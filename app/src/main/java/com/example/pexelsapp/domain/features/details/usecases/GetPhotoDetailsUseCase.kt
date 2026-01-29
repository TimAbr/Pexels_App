package com.example.pexelsapp.domain.features.details.usecases

import com.example.pexelsapp.domain.common.repositories.PhotosRepository
import javax.inject.Inject

class GetPhotoDetailsUseCase @Inject constructor(
    private val photosRepository: PhotosRepository
) {
    suspend operator fun invoke(photoId: Long) =
        photosRepository.getPhoto(photoId)
}