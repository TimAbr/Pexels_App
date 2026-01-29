package com.example.pexelsapp.domain.features.home.usecases

import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.repositories.PhotosRepository
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetPhotosByQueryUseCase @Inject constructor(
    private val repository: PhotosRepository
) {
    operator fun invoke(
        query: String,
        page: Int, perPage:
        Int = PhotosRepository.DEFAULT_PHOTOS_BY_PAGE
    ): Flow<Outcome<List<Photo>, PhotosRepositoryError>> {
        if (query.isBlank()) {
            return flowOf(Outcome.Success(emptyList()))
        }
        return repository.getPhotosByQuery(query = query, page = page, perPage = perPage)
    }
}