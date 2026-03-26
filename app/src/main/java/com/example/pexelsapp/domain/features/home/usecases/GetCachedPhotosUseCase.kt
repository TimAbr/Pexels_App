package com.example.pexelsapp.domain.features.home.usecases

import com.example.pexelsapp.domain.common.models.PhotoGroupType
import com.example.pexelsapp.domain.common.models.PhotosPage
import com.example.pexelsapp.domain.common.repositories.PhotosRepository
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.utils.models.Outcome
import javax.inject.Inject

class GetCachedPhotosUseCase @Inject constructor(
    private val repository: PhotosRepository
) {
    suspend operator fun invoke(type: PhotoGroupType): Outcome<PhotosPage, PhotosRepositoryError> {
        return repository.getCachedPhotos(type = type)
    }
}
