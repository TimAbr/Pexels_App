package com.example.pexelsapp.domain.features.home.usecases

import com.example.pexelsapp.domain.features.home.repositories.CategoriesRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {
    operator fun invoke() = categoriesRepository.getCategories()
}