package com.example.pexelsapp.data.datasources.bookmarks.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.pexelsapp.data.models.CuratedCacheDbo
import com.example.pexelsapp.data.models.PhotoDbo

@Dao
interface CuratedCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheRecords(records: List<CuratedCacheDbo>)

    @Query("DELETE FROM curated_cache")
    suspend fun clearCache()

    @Transaction
    @Query("""
        SELECT p.* FROM photos p
        INNER JOIN curated_cache c ON p.id = c.photo_id
        WHERE c.expiration > :currentTime
        ORDER BY c.id ASC
    """)
    suspend fun getValidCachedPhotos(currentTime: Long): List<PhotoDbo>

    @Query("DELETE FROM curated_cache WHERE expiration <= :currentTime")
    suspend fun deleteExpiredCache(currentTime: Long)
}
