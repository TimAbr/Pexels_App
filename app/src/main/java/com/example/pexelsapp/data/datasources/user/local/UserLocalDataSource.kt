package com.example.pexelsapp.data.datasources.user.local

import android.content.SharedPreferences
import com.example.pexelsapp.domain.features.user.models.User
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import javax.inject.Inject
import javax.inject.Singleton

interface UserLocalDataSource {
    suspend fun saveUser(user: User)
    suspend fun getUser(): User?
    suspend fun clearUser()
}

@Singleton
@BoundTo(supertype = UserLocalDataSource::class, component = SingletonComponent::class)
class UserLocalDataSourceImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : UserLocalDataSource {

    private companion object {
        const val KEY_ID = "user_id"
        const val KEY_NAME = "user_name"
        const val KEY_EMAIL = "user_email"
        const val KEY_PHOTO_URL = "user_photo_url"
    }

    override suspend fun saveUser(user: User) {
        sharedPreferences.edit()
            .putString(KEY_ID, user.id)
            .putString(KEY_NAME, user.name)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_PHOTO_URL, user.photoUrl)
            .apply()
    }

    override suspend fun getUser(): User? {
        val id = sharedPreferences.getString(KEY_ID, null) ?: return null
        return User(
            id = id,
            name = sharedPreferences.getString(KEY_NAME, "") ?: "",
            email = sharedPreferences.getString(KEY_EMAIL, "") ?: "",
            photoUrl = sharedPreferences.getString(KEY_PHOTO_URL, "") ?: ""
        )
    }

    override suspend fun clearUser() {
        sharedPreferences.edit()
            .remove(KEY_ID)
            .remove(KEY_NAME)
            .remove(KEY_EMAIL)
            .remove(KEY_PHOTO_URL)
            .apply()
    }
}

