package com.example.pexelsapp.data.repositories

import android.content.Context
import android.content.res.Resources
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.features.home.models.Category
import com.example.pexelsapp.domain.features.home.repositories.CategoriesRepository
import com.example.pexelsapp.domain.features.home.repositories.CategoriesRepositoryError
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import javax.inject.Inject

@BoundTo(supertype = CategoriesRepository::class, component = SingletonComponent::class)
class CategoriesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CategoriesRepository {

    override fun getCategories(): Outcome<List<Category>, CategoriesRepositoryError> {
        return try {
            Outcome.Success(
                context.resources.getStringArray(R.array.photo_categories)
                    .toList()
                    .map {
                        Category(it)
                    }
            )
        } catch (e: Resources.NotFoundException) {
            Outcome.Error(CategoriesRepositoryError.UNKNOWN)
        }
    }
}