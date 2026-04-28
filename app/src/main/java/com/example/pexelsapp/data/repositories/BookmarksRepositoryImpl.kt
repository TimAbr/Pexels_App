package com.example.pexelsapp.data.repositories

import com.example.pexelsapp.data.datasources.bookmarks.local.BookmarksDao
import com.example.pexelsapp.data.datasources.bookmarks.local.CuratedCacheDao
import com.example.pexelsapp.data.datasources.bookmarks.local.PhotosDao
import com.example.pexelsapp.data.datasources.bookmarks.remote.BookmarksRemoteDataSource
import com.example.pexelsapp.data.datasources.bookmarks.remote.RemoteBookmarkSyncEvent
import com.example.pexelsapp.data.mappers.BookmarkedPhotoDtoMapper
import com.example.pexelsapp.data.mappers.PhotoDboMapper
import com.example.pexelsapp.data.models.BookmarkDbo
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.repositories.PhotosRepository
import com.example.pexelsapp.domain.features.bookmarks.models.BookmarkedPhoto
import com.example.pexelsapp.domain.features.bookmarks.models.BookmarksEvent
import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepository
import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepositoryError
import com.example.pexelsapp.domain.features.user.repositories.UserRepository
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = BookmarksRepository::class, component = SingletonComponent::class)
class BookmarksRepositoryImpl @Inject constructor(
    private val bookmarksDao: BookmarksDao,
    private val photosDao: PhotosDao,
    private val curatedCacheDao: CuratedCacheDao,
    private val photoDboMapper: PhotoDboMapper,
    private val remoteDataSource: BookmarksRemoteDataSource,
    private val photosRepository: PhotosRepository,
    private val bookmarkDtoMapper: BookmarkedPhotoDtoMapper,
    private val userRepository: UserRepository,
) : BookmarksRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val _bookmarksEvents = MutableSharedFlow<BookmarksEvent>(extraBufferCapacity = 64)
    override val bookmarksEvents = _bookmarksEvents.asSharedFlow()

    init {
        scope.launch {
            userRepository.user.collect { user ->
                if (user == null) {
                    bookmarksDao.clearAllBookmarks()
                    photosDao.deleteOrphanedPhotos()
                    return@collect
                }

                // Initial sync up: any local bookmarks missing in remote should be uploaded.
                // We'll trust Firestore's onSnapshot for the "down" sync.
                runCatching {
                    val remoteBookmarksOutcome = remoteDataSource.getBookmarks(user.id)
                    if (remoteBookmarksOutcome is Outcome.Success) {
                        val remoteMap = remoteBookmarksOutcome.value.associateBy { it.photoId }
                        val localBookmarks = bookmarksDao.getAllBookmarksWithTimestamps()

                        val missingInRemote = localBookmarks.filter { it.photo.id !in remoteMap }
                        if (missingInRemote.isNotEmpty()) {
                            val dtos = missingInRemote.map { bookmark ->
                                bookmarkDtoMapper.toDto(
                                    BookmarkedPhoto(
                                        photo = photoDboMapper.toDomain(bookmark.photo),
                                        addedAt = bookmark.addedAt,
                                    ),
                                )
                            }
                            remoteDataSource.saveBookmarksBatch(user.id, dtos)
                        }
                    }
                }
            }
        }

        scope.launch {
            remoteDataSource.syncEvents.collect { events ->
                events.forEach { event ->
                    when (event) {
                        is RemoteBookmarkSyncEvent.Added -> {
                            if (!bookmarksDao.isBookmarked(event.dto.photoId)) {
                                val photoOutcome = photosRepository.getPhoto(event.dto.photoId)
                                if (photoOutcome is Outcome.Success) {
                                    val bookmarkedPhoto = bookmarkDtoMapper.toDomain(
                                        dto = event.dto,
                                        photo = photoOutcome.value,
                                    )
                                    saveBookmarkedPhotoLocally(bookmarkedPhoto)
                                }
                            }
                        }
                        is RemoteBookmarkSyncEvent.Deleted -> {
                            if (bookmarksDao.isBookmarked(event.photoId)) {
                                deletePhotoLocally(event.photoId)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getAllBookmarks(): Flow<List<Photo>> = flow {
        val photos = bookmarksDao.getAllBookmarks().map { photoDboMapper.toDomain(it) }
        emit(photos)
    }

    override suspend fun getBookmarksPage(
        page: Int,
        perPage: Int,
    ): Outcome<List<Photo>, BookmarksRepositoryError> {
        return try {
            val offset = (page - 1) * perPage
            val dbPhotos = bookmarksDao.getPagedBookmarks(limit = perPage, offset = offset)
            Outcome.Success(dbPhotos.map { photoDboMapper.toDomain(it) })
        } catch (e: Exception) {
            Outcome.Error(BookmarksRepositoryError.UNKNOWN)
        }
    }

    override suspend fun savePhoto(photo: Photo): Outcome<Unit, BookmarksRepositoryError> {
        return try {
            val addedAt = System.currentTimeMillis()
            savePhotoLocally(photo, addedAt)
            
            val userId = userRepository.user.value?.id
            if (userId != null) {
                scope.launch {
                    remoteDataSource.saveBookmark(
                        userId = userId,
                        bookmark = bookmarkDtoMapper.toDto(BookmarkedPhoto(photo, addedAt)),
                    )
                }
            }
            
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Outcome.Error(BookmarksRepositoryError.UNKNOWN)
        }
    }

    private suspend fun savePhotoLocally(photo: Photo, addedAt: Long = System.currentTimeMillis()) {
        photosDao.insertPhoto(photoDboMapper.toDbo(photo))
        bookmarksDao.insertBookmark(
            BookmarkDbo(
                photoId = photo.id,
                addedAt = addedAt,
            ),
        )
        _bookmarksEvents.tryEmit(BookmarksEvent.Added(photo))
    }

    override suspend fun deletePhoto(photoId: Long): Outcome<Unit, BookmarksRepositoryError> {
        return try {
            deletePhotoLocally(photoId)
            
            val userId = userRepository.user.value?.id
            if (userId != null) {
                scope.launch {
                    remoteDataSource.deleteBookmark(userId, photoId)
                }
            }
            
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Outcome.Error(BookmarksRepositoryError.UNKNOWN)
        }
    }

    private suspend fun deletePhotoLocally(photoId: Long) {
        bookmarksDao.deleteBookmark(photoId)
        photosDao.deleteOrphanedPhotos()
        _bookmarksEvents.tryEmit(BookmarksEvent.Deleted(photoId))
    }

    override fun observeIsBookmarked(photoId: Long): Flow<Boolean> {
        return bookmarksDao.observeIsBookmarked(photoId)
    }

    override suspend fun isBookmarked(photoId: Long): Boolean {
        return bookmarksDao.isBookmarked(photoId)
    }

    override suspend fun getAllBookmarksWithTimestamps(): Outcome<List<BookmarkedPhoto>, BookmarksRepositoryError> {
        return try {
            val dbPhotos = bookmarksDao.getAllBookmarksWithTimestamps()
            val mapped = dbPhotos.map {
                BookmarkedPhoto(
                    photo = photoDboMapper.toDomain(it.photo),
                    addedAt = it.addedAt,
                )
            }
            Outcome.Success(mapped)
        } catch (e: Exception) {
            Outcome.Error(BookmarksRepositoryError.UNKNOWN)
        }
    }

    override suspend fun saveBookmarkedPhoto(
        bookmarkedPhoto: BookmarkedPhoto,
    ): Outcome<Unit, BookmarksRepositoryError> {
        return try {
            saveBookmarkedPhotoLocally(bookmarkedPhoto)
            
            val userId = userRepository.user.value?.id
            if (userId != null) {
                scope.launch {
                    remoteDataSource.saveBookmark(
                        userId = userId,
                        bookmark = bookmarkDtoMapper.toDto(bookmarkedPhoto),
                    )
                }
            }
            
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Outcome.Error(BookmarksRepositoryError.UNKNOWN)
        }
    }

    private suspend fun saveBookmarkedPhotoLocally(bookmarkedPhoto: BookmarkedPhoto) {
        photosDao.insertPhoto(photoDboMapper.toDbo(bookmarkedPhoto.photo))
        bookmarksDao.insertBookmark(
            BookmarkDbo(
                photoId = bookmarkedPhoto.photo.id,
                addedAt = bookmarkedPhoto.addedAt,
            ),
        )
        _bookmarksEvents.tryEmit(BookmarksEvent.Added(bookmarkedPhoto.photo))
    }
}
