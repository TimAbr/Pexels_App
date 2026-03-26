package com.example.pexelsapp.data.repositories

import com.example.pexelsapp.data.datasources.photos.remote.RemotePhotosSource
import com.example.pexelsapp.data.mappers.PhotoDtoMapper
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.models.PhotoGroupType
import com.example.pexelsapp.domain.common.models.PhotosPage
import com.example.pexelsapp.domain.common.repositories.PhotosRepository
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import android.util.Log
import com.example.pexelsapp.data.datasources.bookmarks.local.CuratedCacheDao
import com.example.pexelsapp.data.datasources.bookmarks.local.PhotosDao
import com.example.pexelsapp.data.mappers.PhotoDboMapper

@BoundTo(supertype = PhotosRepository::class, component = SingletonComponent::class)
class PhotosRepositoryImpl @Inject constructor(
    private val photosSource: RemotePhotosSource,
    private val photoDtoMapper: PhotoDtoMapper,
    private val photoDboMapper: PhotoDboMapper,
    private val photosDao: PhotosDao,
    private val curatedCacheDao: CuratedCacheDao
) : PhotosRepository {

    private companion object {
        const val TAG = "PhotosRepositoryImpl"
    }

    override suspend fun getPhoto(photoId: Long): Outcome<Photo, PhotosRepositoryError> {
        return try {

            photosDao.getPhotoById(photoId)?.let{
                return Outcome.Success(photoDboMapper(it))
            }

            val response = photosSource.getPhoto(photoId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Outcome.Success(photoDtoMapper(body))
                } else {
                    Log.w(TAG, "getPhoto($photoId): Response body is null")
                    Outcome.Error(PhotosRepositoryError.UNKNOWN)
                }
            } else {
                Log.w(TAG, "getPhoto($photoId): API Error ${response.code()} - ${response.message()}")
                Outcome.Error(mapResponseError(response.code()))
            }
        } catch (e: IOException) {
            Log.w(TAG, "getPhoto($photoId): Network error", e)
            Outcome.Error(PhotosRepositoryError.NETWORK_ERROR)
        } catch (e: Exception) {
            Log.w(TAG, "getPhoto($photoId): Unexpected error", e)
            Outcome.Error(PhotosRepositoryError.UNKNOWN)
        }
    }

    override suspend fun getCachedPhotos(type: PhotoGroupType): Outcome<PhotosPage, PhotosRepositoryError> {
        return withContext(Dispatchers.IO) {
            when (type) {
                is PhotoGroupType.Curated -> {
                    try {
                        val currentTime = System.currentTimeMillis()
                        val cachedDbos = curatedCacheDao.getValidCachedPhotos(currentTime)
                        if (cachedDbos.isNotEmpty()) {
                            val photos = cachedDbos.map { photoDboMapper(it) }
                            curatedCacheDao.deleteExpiredCache(currentTime)
                            Outcome.Success(PhotosPage(photos))
                        } else {
                            curatedCacheDao.deleteExpiredCache(currentTime)
                            Outcome.Success(PhotosPage(emptyList()))
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "getCachedPhotos: Unexpected error", e)
                        Outcome.Error(PhotosRepositoryError.UNKNOWN)
                    }
                }
                else -> Outcome.Error(PhotosRepositoryError.NOT_FOUND)
            }
        }
    }

    override suspend fun getPhotos(
        type: PhotoGroupType,
        page: Int,
        perPage: Int
    ): Outcome<PhotosPage, PhotosRepositoryError> = withContext(Dispatchers.IO) {
        try {
            val response = when (type) {
                is PhotoGroupType.Curated -> photosSource.getCuratedPhotos(page, perPage)
                is PhotoGroupType.Category -> photosSource.getPhotosByQuery(type.category, page, perPage)
                is PhotoGroupType.Query -> photosSource.getPhotosByQuery(type.query, page, perPage)
            }

            if (response.isSuccessful) {
                val downloadedPhotos = response.body()?.photos ?: emptyList()
                val photos = downloadedPhotos.map { photoDtoMapper(it) }

                if (type is PhotoGroupType.Curated && page == 1) {
                    try {
                        val currentTime = System.currentTimeMillis()
                        val expirationOffset = 60 * 60 * 1000L // 1 hour
                        val dbos = photos.map { photoDboMapper(it) }

                        curatedCacheDao.clearCache()
                        photosDao.deleteOrphanedPhotos()

                        photosDao.insertPhotos(dbos)
                        
                        val cacheRecords = dbos.map { 
                            com.example.pexelsapp.data.models.CuratedCacheDbo(
                                photoId = it.id, 
                                cachedAt = currentTime,
                                expiration = currentTime + expirationOffset
                            ) 
                        }
                        curatedCacheDao.insertCacheRecords(cacheRecords)
                    } catch (e: Exception) {
                        Log.e(TAG, "getPhotos($type): Failed to save curated cache to DB", e)
                    }
                }
                Outcome.Success(PhotosPage(photos))
            } else {
                Log.w(TAG, "getPhotos($type): API Error ${response.code()}")
                Outcome.Error(mapResponseError(response.code()))
            }
        } catch (e: IOException) {
            Log.w(TAG, "getPhotos($type): Network error", e)
            Outcome.Error(PhotosRepositoryError.NETWORK_ERROR)
        } catch (e: Exception) {
            Log.e(TAG, "getPhotos($type): Unexpected error", e)
            Outcome.Error(PhotosRepositoryError.UNKNOWN)
        }
    }

    private fun mapResponseError(code: Int): PhotosRepositoryError {
        return when (code) {
            404 -> PhotosRepositoryError.NOT_FOUND
            in 500..599 -> PhotosRepositoryError.SERVER_ERROR
            else -> PhotosRepositoryError.UNKNOWN
        }
    }
}