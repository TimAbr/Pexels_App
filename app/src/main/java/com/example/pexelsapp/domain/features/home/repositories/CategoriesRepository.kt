package com.example.pexelsapp.domain.features.home.repositories

import com.example.pexelsapp.domain.common.models.Category
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategories(): Outcome<List<Category>, CategoriesRepositoryError>
}