package com.example.pexelsapp.domain.common.models

data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val photographer: String,
    val description: String,
    val avgColor: String
)
