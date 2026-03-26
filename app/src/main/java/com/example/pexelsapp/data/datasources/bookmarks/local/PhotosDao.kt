package com.example.pexelsapp.data.datasources.bookmarks.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pexelsapp.data.models.PhotoDbo

@Dao
interface PhotosDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhotos(photos: List<PhotoDbo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhoto(photo: PhotoDbo)

    @Query("SELECT * FROM photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: Long): PhotoDbo?
    // Deletes general photos that are neither bookmarked nor cached
    @Query("""
        DELETE FROM photos 
        WHERE id NOT IN (SELECT photo_id FROM bookmarks) 
        AND id NOT IN (SELECT photo_id FROM curated_cache)
    """)
    suspend fun deleteOrphanedPhotos()
}
