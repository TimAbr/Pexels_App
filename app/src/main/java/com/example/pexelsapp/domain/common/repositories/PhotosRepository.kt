package com.example.pexelsapp.domain.common.repositories

import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.Flow

interface PhotosRepository{
    suspend fun getPhoto(photoId: Long): Outcome<Photo, PhotosRepositoryError>

    fun getCuratedPhotos(
        page: Int,
        perPage: Int = DEFAULT_PHOTOS_BY_PAGE
    ): Flow<Outcome<List<Photo>, PhotosRepositoryError>>

    fun getPhotosByQuery(
        query: String,
        page: Int,
        perPage: Int = DEFAULT_PHOTOS_BY_PAGE
    ): Flow<Outcome<List<Photo>, PhotosRepositoryError>>

    companion object{
        const val DEFAULT_PHOTOS_BY_PAGE = 30
    }
}
