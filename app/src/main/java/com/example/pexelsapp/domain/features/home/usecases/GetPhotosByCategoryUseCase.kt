package com.example.pexelsapp.domain.features.home.usecases

import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.repositories.PhotosRepository
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.domain.features.home.models.Category
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetPhotosByCategoryUseCase (
    private val repository: PhotosRepository
) {
    operator fun invoke(
        category: Category,
        page: Int, perPage:
        Int = PhotosRepository.DEFAULT_PHOTOS_BY_PAGE
    ): Flow<Outcome<List<Photo>, PhotosRepositoryError>> =
        repository.getPhotosByQuery(query = category.name, page = page, perPage = perPage)
}