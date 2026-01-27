package com.example.pexelsapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosResponseDto(
    @SerialName("page") val page: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("photos") val photos: List<PhotoDto>,
    @SerialName("next_page") val nextPage: String? = null
)