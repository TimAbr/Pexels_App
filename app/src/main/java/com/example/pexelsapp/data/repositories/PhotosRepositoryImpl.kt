package com.example.pexelsapp.data.repositories

import com.example.pexelsapp.data.datasources.photos.remote.RemotePhotosSource
import com.example.pexelsapp.data.mappers.PhotoDtoMapper
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.repositories.PhotosRepository
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.domain.features.home.repositories.CategoriesRepository
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject
import android.util.Log


@BoundTo(supertype = PhotosRepository::class, component = SingletonComponent::class)
class PhotosRepositoryImpl @Inject constructor(
    private val photosSource: RemotePhotosSource,
    private val photoDtoMapper: PhotoDtoMapper
) : PhotosRepository {

    private companion object {
        const val TAG = "PhotosRepositoryImpl"
    }

    override suspend fun getPhoto(photoId: Long): Outcome<Photo, PhotosRepositoryError> {
        return try {
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

    override fun getCuratedPhotos(
        page: Int,
        perPage: Int
    ): Flow<Outcome<List<Photo>, PhotosRepositoryError>> = flow {
        try {
            val response = photosSource.getCuratedPhotos(page, perPage)

            if (response.isSuccessful) {
                val photos = response.body()
                    ?.photos
                    ?.map { photoDtoMapper(it) } ?: emptyList()
                emit(Outcome.Success(photos))
            } else {
                Log.w(TAG, "getCuratedPhotos: API Error ${response.code()}")
                emit(Outcome.Error(mapResponseError(response.code())))
            }
        } catch (e: IOException) {
            Log.w(TAG, "getCuratedPhotos: Network error", e)
            emit(Outcome.Error(PhotosRepositoryError.NETWORK_ERROR))
        } catch (e: Exception) {
            Log.w(TAG, "getCuratedPhotos: Unexpected error", e)
            emit(Outcome.Error(PhotosRepositoryError.UNKNOWN))
        }
    }.flowOn(Dispatchers.IO)

    override fun getPhotosByQuery(
        query: String,
        page: Int,
        perPage: Int
    ): Flow<Outcome<List<Photo>, PhotosRepositoryError>> = flow {
        try {
            val response = photosSource.getPhotosByQuery(query, page, perPage)
            if (response.isSuccessful) {
                val photos = response.body()
                    ?.photos
                    ?.map { photoDtoMapper(it) } ?: emptyList()
                emit(Outcome.Success(photos))
            } else {
                Log.w(TAG, "getPhotosByQuery(query=$query): API Error ${response.code()}")
                emit(Outcome.Error(mapResponseError(response.code())))
            }
        } catch (e: IOException) {
            Log.w(TAG, "getPhotosByQuery(query=$query): Network error", e)
            emit(Outcome.Error(PhotosRepositoryError.NETWORK_ERROR))
        } catch (e: Exception) {
            Log.w(TAG, "getPhotosByQuery(query=$query): Unexpected error", e)
            emit(Outcome.Error(PhotosRepositoryError.UNKNOWN))
        }
    }.flowOn(Dispatchers.IO)

    private fun mapResponseError(code: Int): PhotosRepositoryError {
        return when (code) {
            404 -> PhotosRepositoryError.NOT_FOUND
            in 500..599 -> PhotosRepositoryError.SERVER_ERROR
            else -> PhotosRepositoryError.UNKNOWN
        }
    }
}