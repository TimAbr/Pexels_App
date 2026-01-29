package com.example.pexelsapp.domain.features.home.usecases

import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.repositories.PhotosRepository
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCuratedPhotosUseCase @Inject constructor(
    private val repository: PhotosRepository
) {
    operator fun invoke(
        page: Int,
        perPage: Int = PhotosRepository.DEFAULT_PHOTOS_BY_PAGE
    ): Flow<Outcome<List<Photo>, PhotosRepositoryError>> {
        return repository.getCuratedPhotos(page = page, perPage = perPage)
    }
}