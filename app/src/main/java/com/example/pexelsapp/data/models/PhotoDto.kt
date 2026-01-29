package com.example.pexelsapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    @SerialName("id") val id: Long,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("url") val url: String,
    @SerialName("photographer") val photographer: String,
    @SerialName("photographer_url") val photographerUrl: String,
    @SerialName("photographer_id") val photographerId: Long,
    @SerialName("avg_color") val avgColor: String,
    @SerialName("src") val src: PhotoSourcesDto,
    @SerialName("liked") val liked: Boolean,
    @SerialName("alt") val description: String
)

