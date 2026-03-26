package com.example.pexelsapp.data.datasources.bookmarks.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.pexelsapp.data.models.BookmarkDbo
import com.example.pexelsapp.data.models.PhotoDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkDbo)

    @Query("DELETE FROM bookmarks WHERE photo_id = :photoId")
    suspend fun deleteBookmark(photoId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE photo_id = :photoId)")
    suspend fun isBookmarked(photoId: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE photo_id = :photoId)")
    fun observeIsBookmarked(photoId: Long): Flow<Boolean>

    @Transaction
    @Query("""
        SELECT p.* FROM photos p
        INNER JOIN bookmarks b ON p.id = b.photo_id
        ORDER BY b.added_at DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getPagedBookmarks(limit: Int, offset: Int): List<PhotoDbo>

    @Transaction
    @Query("""
        SELECT p.* FROM photos p
        INNER JOIN bookmarks b ON p.id = b.photo_id
        ORDER BY b.added_at DESC
    """)
    suspend fun getAllBookmarks(): List<PhotoDbo>
}
