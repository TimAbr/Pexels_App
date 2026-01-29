package com.example.pexelsapp.data.datasources.photos.remote

import com.example.pexelsapp.data.models.PhotoDto
import com.example.pexelsapp.data.models.PhotosResponseDto
import retrofit2.Response
import javax.inject.Inject

class RemotePhotosSource @Inject constructor(
    private val api: PexelsApi
) {
    suspend fun getCuratedPhotos(page: Int, perPage: Int): Response<PhotosResponseDto> {
        return api.getCuratedPhotos(page, perPage)
    }

    suspend fun getPhotosByQuery(query: String, page: Int, perPage: Int): Response<PhotosResponseDto> {
        return api.getPhotosByQuery(query, page, perPage)
    }

    suspend fun getPhoto(id: Long): Response<PhotoDto> {
        return api.getPhoto(id)
    }
}