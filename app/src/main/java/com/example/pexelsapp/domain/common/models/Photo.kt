package com.example.pexelsapp.domain.common.models

import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val photographer: String,
    val description: String,
    val avgColor: String,
    val source: PhotoSource
)
