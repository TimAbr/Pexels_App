package com.example.pexelsapp.domain.common.models

import kotlinx.serialization.Serializable

@Serializable
data class PhotoSource(
    val original: String,
    val large: String,
    val medium: String,
    val small: String,
    val tiny: String
)
