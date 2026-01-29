package com.example.pexelsapp.domain.features.bookmarks.models

import com.example.pexelsapp.domain.common.models.Photo

sealed class BookmarksEvent {
    data class Deleted(val photoId: Long) : BookmarksEvent()
    data class Added(val photo: Photo) : BookmarksEvent()
}
