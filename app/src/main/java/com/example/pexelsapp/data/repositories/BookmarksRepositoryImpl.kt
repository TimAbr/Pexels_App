package com.example.pexelsapp.data.repositories

import com.example.pexelsapp.data.datasources.bookmarks.local.SavedPhotosDao
import com.example.pexelsapp.data.mappers.PhotoDboMapper
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepository
import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepositoryError
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@BoundTo(supertype = BookmarksRepository::class, component = SingletonComponent::class)
class BookmarksRepositoryImpl @Inject constructor(
    private val dao: SavedPhotosDao,
    private val photoDboMapper: PhotoDboMapper
) : BookmarksRepository {

    override fun getAllBookmarks(): Flow<List<Photo>> = flow {
        val photos = dao.getAllPhotos().map { photoDboMapper(it) }
        emit(photos)
    }

    override suspend fun getBookmarksPage(
        page: Int,
        perPage: Int
    ): Outcome<List<Photo>, BookmarksRepositoryError> {
        return try {
            val offset = (page - 1) * perPage
            val dbPhotos = dao.getPagedBookmarks(limit = perPage, offset = offset)
            Outcome.Success(dbPhotos.map { photoDboMapper(it) })
        } catch (e: Exception) {
            Outcome.Error(BookmarksRepositoryError.UNKNOWN)
        }
    }

    override suspend fun savePhoto(photo: Photo): Outcome<Unit, BookmarksRepositoryError> {
        return try {
            dao.insertPhoto(photoDboMapper(photo))
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Outcome.Error(BookmarksRepositoryError.UNKNOWN)
        }
    }

    override suspend fun deletePhoto(photoId: Long): Outcome<Unit, BookmarksRepositoryError> {
        return try {
            dao.deletePhotoById(photoId)
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Outcome.Error(BookmarksRepositoryError.UNKNOWN)
        }
    }

    override fun observeIsBookmarked(photoId: Long): Flow<Boolean> {
        return dao.observePhotoExists(photoId)
    }

    override suspend fun isBookmarked(photoId: Long): Boolean {
        return dao.photoExists(photoId)
    }
}