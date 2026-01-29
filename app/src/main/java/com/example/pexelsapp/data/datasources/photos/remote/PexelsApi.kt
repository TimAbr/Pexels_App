package com.example.pexelsapp.data.datasources.photos.remote

import com.example.pexelsapp.data.models.PhotoDto
import com.example.pexelsapp.data.models.PhotosResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PexelsApi {
    @GET("v1/curated")
    suspend fun getCuratedPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<PhotosResponseDto>

    @GET("v1/search")
    suspend fun getPhotosByQuery(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<PhotosResponseDto>

    @GET("v1/photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: Long
    ): Response<PhotoDto>
}