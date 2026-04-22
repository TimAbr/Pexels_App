package com.example.pexelsapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ImgBBResponse(
    val data: ImgBBData,
    val success: Boolean,
    val status: Int
)

@Serializable
data class ImgBBData(
    val url: String,
    val display_url: String,
    val delete_url: String
)
