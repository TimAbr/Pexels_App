package com.example.pexelsapp.domain.features.bookmarks.models

import com.example.pexelsapp.domain.common.models.Photo

data class BookmarkedPhoto(
    val photo: Photo,
    val addedAt: Long
)
