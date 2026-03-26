package com.example.pexelsapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoDbo(
    @PrimaryKey val id: Long,
    val width: Int,
    val height: Int,
    val description: String,
    val avgColor: String,

    @Embedded(prefix = "photographer_")
    val photographer: PhotographerDbo,

    @Embedded(prefix = "source_")
    val source: PhotoSourceDbo
)

