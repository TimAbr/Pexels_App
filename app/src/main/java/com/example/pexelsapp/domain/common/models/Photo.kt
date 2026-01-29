package com.example.pexelsapp.domain.common.models

data class Photo(
    val id: Long,
    val width: Int,
    val height: Int,
    val photographer: Photographer,
    val description: String,
    val avgColor: String,
    val source: PhotoSource
)
