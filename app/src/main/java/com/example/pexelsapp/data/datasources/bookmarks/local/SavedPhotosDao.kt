package com.example.pexelsapp.data.datasources.bookmarks.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pexelsapp.data.models.PhotoDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPhotosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoDbo)

    @Query("SELECT * FROM saved_photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: Long): PhotoDbo?

    @Query("SELECT EXISTS(SELECT 1 FROM saved_photos WHERE id = :photoId)")
    suspend fun photoExists(photoId: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM saved_photos WHERE id = :photoId)")
    fun observePhotoExists(photoId: Long): Flow<Boolean>

    @Query("DELETE FROM saved_photos WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: Long)
}