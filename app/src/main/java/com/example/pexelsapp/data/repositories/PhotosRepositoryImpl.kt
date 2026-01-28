package com.example.pexelsapp.data.repositories

import com.example.pexelsapp.data.datasources.photos.remote.RemotePhotosSource
import com.example.pexelsapp.data.mappers.PhotoDtoMapper
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.repositories.PhotosRepository
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

class PhotosRepositoryImpl constructor(
    private val photosSource: RemotePhotosSource,
    private val photoDtoMapper: PhotoDtoMapper
) : PhotosRepository {

    override suspend fun getPhoto(photoId: Int): Outcome<Photo, PhotosRepositoryError> {
        return try {
            val response = photosSource.getPhoto(photoId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Outcome.Success(photoDtoMapper(body))
                } else {
                    Outcome.Error(PhotosRepositoryError.UNKNOWN)
                }
            } else {
                val error = mapResponseError(response.code())
                Outcome.Error(error)
            }
        } catch (e: IOException) {
            Outcome.Error(PhotosRepositoryError.NETWORK_ERROR)
        } catch (e: Exception) {
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
                emit(Outcome.Error(mapResponseError(response.code())))
            }
        } catch (e: IOException) {
            emit(Outcome.Error(PhotosRepositoryError.NETWORK_ERROR))
        } catch (e: Exception) {
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
                emit(Outcome.Error(mapResponseError(response.code())))
            }
        } catch (e: IOException) {
            emit(Outcome.Error(PhotosRepositoryError.NETWORK_ERROR))
        } catch (e: Exception) {
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