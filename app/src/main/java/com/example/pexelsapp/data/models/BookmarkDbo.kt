package com.example.pexelsapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = PhotoDbo::class,
            parentColumns = ["id"],
            childColumns = ["photo_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookmarkDbo(
    @PrimaryKey
    @ColumnInfo(name = "photo_id")
    val photoId: Long,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)
