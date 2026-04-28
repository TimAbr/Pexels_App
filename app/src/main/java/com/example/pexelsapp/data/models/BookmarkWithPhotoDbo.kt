package com.example.pexelsapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class BookmarkWithPhotoDbo(
    @ColumnInfo(name = "added_at") val addedAt: Long,
    @Embedded val photo: PhotoDbo
)
