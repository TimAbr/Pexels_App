package com.example.pexelsapp.data.datasources.bookmarks.remote

import android.util.Log
import com.example.pexelsapp.data.models.BookmarkedPhotoDto
import com.example.pexelsapp.utils.models.Outcome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = BookmarksRemoteDataSource::class, component = SingletonComponent::class)
class FirebaseBookmarksRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    firebaseAuth: FirebaseAuth,
) : BookmarksRemoteDataSource {

    private companion object {
        const val USERS_COLLECTION = "users"
        const val BOOKMARKS_COLLECTION = "bookmarks"
        const val TAG = "FirebaseBookmarksDataSource"
    }

    private val _syncEvents = MutableSharedFlow<List<RemoteBookmarkSyncEvent>>(extraBufferCapacity = 64)
    override val syncEvents = _syncEvents.asSharedFlow()

    private var bookmarksListener: ListenerRegistration? = null

    init {
        firebaseAuth.addAuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                observeBookmarks(firebaseUser.uid)
            } else {
                stopObservingBookmarks()
            }
        }
    }

    private fun observeBookmarks(userId: String) {
        stopObservingBookmarks()

        bookmarksListener = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(BOOKMARKS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing bookmarks", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val events = snapshot.documentChanges.mapNotNull { change ->
                        val dto = change.document.toObject(BookmarkedPhotoDto::class.java)
                        when (change.type) {
                            DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                                RemoteBookmarkSyncEvent.Added(dto)
                            }
                            DocumentChange.Type.REMOVED -> {
                                RemoteBookmarkSyncEvent.Deleted(dto.photoId)
                            }
                        }
                    }

                    if (events.isNotEmpty()) {
                        val sortedEvents = events.sortedBy { event ->
                            when (event) {
                                is RemoteBookmarkSyncEvent.Added -> event.dto.addedAt
                                is RemoteBookmarkSyncEvent.Deleted -> 0L
                            }
                        }
                        _syncEvents.tryEmit(sortedEvents)
                    }
                }
            }
    }

    private fun stopObservingBookmarks() {
        bookmarksListener?.remove()
        bookmarksListener = null
    }

    override suspend fun getBookmarks(
        userId: String,
    ): Outcome<List<BookmarkedPhotoDto>, Exception> {
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(BOOKMARKS_COLLECTION)
                .get()
                .await()
            val bookmarks = snapshot.toObjects(BookmarkedPhotoDto::class.java)
            Outcome.Success(bookmarks)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bookmarks", e)
            Outcome.Error(e)
        }
    }

    override suspend fun saveBookmark(
        userId: String,
        bookmark: BookmarkedPhotoDto,
    ): Outcome<Unit, Exception> {
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(BOOKMARKS_COLLECTION)
                .document(bookmark.photoId.toString())
                .set(bookmark)
                .await()
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bookmark", e)
            Outcome.Error(e)
        }
    }

    override suspend fun deleteBookmark(
        userId: String,
        photoId: Long,
    ): Outcome<Unit, Exception> {
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(BOOKMARKS_COLLECTION)
                .document(photoId.toString())
                .delete()
                .await()
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting bookmark", e)
            Outcome.Error(e)
        }
    }

    override suspend fun saveBookmarksBatch(
        userId: String,
        bookmarks: List<BookmarkedPhotoDto>,
    ): Outcome<Unit, Exception> {
        return try {
            val writeBatch = firestore.batch()
            val collectionRef = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(BOOKMARKS_COLLECTION)

            bookmarks.forEach { bookmark ->
                val docRef = collectionRef.document(bookmark.photoId.toString())
                writeBatch.set(docRef, bookmark)
            }
            writeBatch.commit().await()
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bookmarks batch", e)
            Outcome.Error(e)
        }
    }
}
