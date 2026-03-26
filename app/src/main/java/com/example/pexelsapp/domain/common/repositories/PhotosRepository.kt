package com.example.pexelsapp.domain.common.repositories

import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.models.PhotoGroupType
import com.example.pexelsapp.domain.common.models.PhotosPage
import com.example.pexelsapp.utils.models.Outcome

interface PhotosRepository {
    suspend fun getPhoto(photoId: Long): Outcome<Photo, PhotosRepositoryError>

    suspend fun getCachedPhotos(type: PhotoGroupType): Outcome<PhotosPage, PhotosRepositoryError>

    suspend fun getPhotos(
        type: PhotoGroupType,
        page: Int,
        perPage: Int = DEFAULT_PHOTOS_BY_PAGE
    ): Outcome<PhotosPage, PhotosRepositoryError>

    companion object{
        const val DEFAULT_PHOTOS_BY_PAGE = 30
    }
}
