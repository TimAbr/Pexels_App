package com.example.pexelsapp.data.datasources.user.remote

import com.example.pexelsapp.domain.features.user.models.Uri
import com.example.pexelsapp.domain.features.user.models.Url
import com.example.pexelsapp.domain.features.user.models.User
import com.example.pexelsapp.domain.features.user.repositories.UserError
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.StateFlow

interface UserRemoteDataSource {
    val user: StateFlow<User?>
    suspend fun getUserById(userId: String): Outcome<User, UserError.Get>
    suspend fun updateProfile(user: User): Outcome<Unit, UserError.Update>
    suspend fun uploadUserPhoto(uri: Uri): Outcome<Url, UserError.Update>
}
