package com.example.pexelsapp.domain.features.auth.repositories

import kotlinx.coroutines.flow.StateFlow
import com.example.pexelsapp.domain.features.auth.models.AuthMethod
import com.example.pexelsapp.utils.models.Outcome

interface AuthRepository {
    val isAuthorized: StateFlow<Boolean>
    suspend fun login(method: AuthMethod): Outcome<Unit, AuthLoginError>
    suspend fun logout(): Outcome<Unit, AuthLogoutError>
}

sealed interface AuthLoginError {
    object NetworkError : AuthLoginError
    object ServerError : AuthLoginError
    object Unknown : AuthLoginError
    object Canceled : AuthLoginError
    
    interface GoogleAuthError : AuthLoginError {
        object NoCredentials : GoogleAuthError
        object Cancelled : GoogleAuthError
        object InvalidToken : GoogleAuthError
    }
}

sealed interface AuthLogoutError {
    object NetworkError : AuthLogoutError
    object Unknown : AuthLogoutError
}
