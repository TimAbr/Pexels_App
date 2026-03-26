package com.example.pexelsapp.domain.common.models

sealed interface PhotoGroupType {
    object Curated : PhotoGroupType
    @JvmInline value class Category(val category: String) : PhotoGroupType
    @JvmInline value class Query(val query: String) : PhotoGroupType
}
