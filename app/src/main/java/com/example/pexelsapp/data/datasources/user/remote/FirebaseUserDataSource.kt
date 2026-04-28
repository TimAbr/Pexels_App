package com.example.pexelsapp.data.datasources.user.remote

import android.util.Log
import com.example.pexelsapp.data.mappers.UserMapper
import com.example.pexelsapp.domain.features.user.models.User
import com.example.pexelsapp.domain.features.user.repositories.UserError
import com.example.pexelsapp.utils.models.Outcome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userMapper: UserMapper,
) {
    private companion object {
        const val TAG = "FirebaseUserDataSource"
        const val USERS_COLLECTION = "users"
    }

    private val _user = MutableStateFlow<User?>(firebaseAuth.currentUser?.let { userMapper.toDomain(it) })
    val user: StateFlow<User?> = _user.asStateFlow()

    private var userSnapshotListener: ListenerRegistration? = null

    init {
        firebaseAuth.addAuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                _user.value = userMapper.toDomain(firebaseUser)
                observeUserDocument(firebaseUser.uid)
            } else {
                stopObservingUserDocument()
                _user.value = null
            }
        }
    }

    private fun observeUserDocument(userId: String) {
        stopObservingUserDocument()
        
        userSnapshotListener = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing user document", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    _user.value = userMapper.toDomain(snapshot)
                } else {
                    Log.d(TAG, "User document does not exist in Firestore yet")
                }
            }
    }

    private fun stopObservingUserDocument() {
        userSnapshotListener?.remove()
        userSnapshotListener = null
    }

    suspend fun getUserById(userId: String): Outcome<User, UserError.Get> {
        return try {
            val document = firestore.collection(USERS_COLLECTION).document(userId).get().await()
            val user = userMapper.toDomain(document)
            if (user != null) {
                Outcome.Success(user)
            } else {
                Outcome.Error(UserError.Get.NotFound)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user by id $userId", e)
            Outcome.Error(UserError.Common.Unknown)
        }
    }

    suspend fun updateProfile(user: User): Outcome<Unit, UserError.Update> {
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(user.id)
                .set(userMapper.toDto(user))
                .await()
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update profile for user ${user.id}", e)
            Outcome.Error(UserError.Common.Unknown)
        }
    }

    fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid
}
