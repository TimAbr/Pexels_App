package com.example.pexelsapp.domain.features.home.usecases

import com.example.pexelsapp.domain.common.models.PhotoGroupType
import com.example.pexelsapp.domain.common.models.PhotosPage
import com.example.pexelsapp.domain.common.repositories.PhotosRepository
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.utils.models.Outcome
import javax.inject.Inject

class GetPhotosUseCase @Inject constructor(
    private val repository: PhotosRepository
) {
    suspend operator fun invoke(
        type: PhotoGroupType,
        page: Int,
        perPage: Int = PhotosRepository.DEFAULT_PHOTOS_BY_PAGE
    ): Outcome<PhotosPage, PhotosRepositoryError> {
        if (type is PhotoGroupType.Query && type.query.isBlank()) {
            return Outcome.Success(PhotosPage(emptyList()))
        }
        return repository.getPhotos(type = type, page = page, perPage = perPage)
    }
}
