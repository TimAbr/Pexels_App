package com.example.pexelsapp.domain.features.user.repositories

import com.example.pexelsapp.domain.features.user.models.Uri
import com.example.pexelsapp.domain.features.user.models.Url
import com.example.pexelsapp.domain.features.user.models.User
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val user: StateFlow<User?>

    suspend fun getUserById(userId: String): Outcome<User, UserError.Get>
    suspend fun updateProfile(user: User): Outcome<Unit, UserError.Update>

    suspend fun uploadUserPhoto(uri: Uri): Outcome<Url, UserError.Update>
}

sealed interface UserError {
    sealed interface Common : Get, Update {
        object Network : Common
        object Server : Common
        object Unknown : Common
        object Unathorized : Common
    }

    sealed interface Get : UserError {
        object NotFound : Get
    }

    sealed interface Update : UserError{
        object InvalidData : Update
    }
}
