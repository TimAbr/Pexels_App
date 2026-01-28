package com.example.pexelsapp.domain.features.home.usecases

import com.example.pexelsapp.domain.features.home.repositories.CategoriesRepository

class GetCategoriesUseCase(
    private val categoriesRepository: CategoriesRepository
) {
    operator fun invoke() = categoriesRepository.getCategories()
}