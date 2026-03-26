package com.example.pexelsapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "curated_cache",
    foreignKeys = [
        ForeignKey(
            entity = PhotoDbo::class,
            parentColumns = ["id"],
            childColumns = ["photo_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CuratedCacheDbo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "photo_id")
    val photoId: Long,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "expiration")
    val expiration: Long
)
