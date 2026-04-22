package com.example.pexelsapp.domain.features.auth.repositories

import kotlinx.coroutines.flow.StateFlow
import com.example.pexelsapp.domain.features.auth.models.AuthMethod
import com.example.pexelsapp.domain.features.user.models.User
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isAuthorized: StateFlow<Boolean>
    suspend fun <E : AuthLoginError> login(
        method: AuthMethod<E>
    ): Outcome<Unit, E>
    suspend fun logout(): Outcome<Unit, AuthLogoutError>
}

sealed interface AuthLoginError {

    sealed interface Common: GoogleAuthError{
        object NetworkError : Common
        object ServerError : Common
        object Unknown : Common
        object Canceled : Common
        object NotSupported : Common
    }
    
    interface GoogleAuthError : AuthLoginError {
        object NoCredentials : GoogleAuthError
        object InvalidToken : GoogleAuthError
    }
}

sealed interface AuthLogoutError {
    object NetworkError : AuthLogoutError
    object Unknown : AuthLogoutError
}
