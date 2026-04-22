package com.example.pexelsapp.presentation.features.auth

import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError

sealed interface AuthScreenState {
    object Loading : AuthScreenState
    object LogIn : AuthScreenState
    object Authorized : AuthScreenState
    data class Error(val error: AuthLoginError) : AuthScreenState
}
