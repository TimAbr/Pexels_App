package com.example.pexelsapp.data.repositories

import com.example.pexelsapp.data.datasources.bookmarks.local.BookmarksDao
import com.example.pexelsapp.data.datasources.bookmarks.local.CuratedCacheDao
import com.example.pexelsapp.data.datasources.bookmarks.local.PhotosDao
import com.example.pexelsapp.data.mappers.PhotoDboMapper
import com.example.pexelsapp.data.models.BookmarkDbo
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.features.bookmarks.models.BookmarksEvent
import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepository
import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepositoryError
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = BookmarksRepository::class, component = SingletonComponent::class)
class BookmarksRepositoryImpl @Inject constructor(
    private val bookmarksDao: BookmarksDao,
    private val photosDao: PhotosDao,
    private val curatedCacheDao: CuratedCacheDao,
    private val photoDboMapper: PhotoDboMapper
) : BookmarksRepository {

    private val _bookmarksEvents = MutableSharedFlow<BookmarksEvent>(extraBufferCapacity = 64)
    override val bookmarksEvents = _bookmarksEvents.asSharedFlow()

    override fun getAllBookmarks(): Flow<List<Photo>> = flow {
        val photos = bookmarksDao.getAllBookmarks().map { photoDboMapper(it) }
        emit(photos)
    }

    override suspend fun getBookmarksPage(
        page: Int,
        perPage: Int
    ): Outcome<List<Photo>, BookmarksRepositoryError> {
        return try {
            val offset = (page - 1) * perPage
            val dbPhotos = bookmarksDao.getPagedBookmarks(limit = perPage, offset = offset)
            Outcome.Success(dbPhotos.map { photoDboMapper(it) })
        } catch (e: Exception) {
            Outcome.Error(BookmarksRepositoryError.UNKNOWN)
        }
    }

    override suspend fun savePhoto(photo: Photo): Outcome<Unit, BookmarksRepositoryError> {
        return try {
            photosDao.insertPhoto(photoDboMapper(photo))
            bookmarksDao.insertBookmark(BookmarkDbo(photoId = photo.id))
            _bookmarksEvents.tryEmit(BookmarksEvent.Added(photo))
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Outcome.Error(BookmarksRepositoryError.UNKNOWN)
        }
    }

    override suspend fun deletePhoto(photoId: Long): Outcome<Unit, BookmarksRepositoryError> {
        return try {
            bookmarksDao.deleteBookmark(photoId)
            photosDao.deleteOrphanedPhotos()
            _bookmarksEvents.tryEmit(BookmarksEvent.Deleted(photoId))
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Outcome.Error(BookmarksRepositoryError.UNKNOWN)
        }
    }

    override fun observeIsBookmarked(photoId: Long): Flow<Boolean> {
        return bookmarksDao.observeIsBookmarked(photoId)
    }

    override suspend fun isBookmarked(photoId: Long): Boolean {
        return bookmarksDao.isBookmarked(photoId)
    }
}