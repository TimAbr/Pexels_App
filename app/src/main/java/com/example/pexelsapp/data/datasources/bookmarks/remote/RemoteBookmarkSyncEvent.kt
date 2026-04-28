package com.example.pexelsapp.data.datasources.bookmarks.remote

import com.example.pexelsapp.data.models.BookmarkedPhotoDto

sealed interface RemoteBookmarkSyncEvent {
    data class Added(
        val dto: BookmarkedPhotoDto,
    ) : RemoteBookmarkSyncEvent

    data class Deleted(
        val photoId: Long,
    ) : RemoteBookmarkSyncEvent
}
