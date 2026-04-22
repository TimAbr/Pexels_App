package com.example.pexelsapp.data.datasources.auth.remote

import com.example.pexelsapp.domain.features.auth.models.AuthMethod
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.example.pexelsapp.domain.features.auth.repositories.AuthLogoutError
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.StateFlow

interface AuthDataSource {
    val isAuthorized: StateFlow<Boolean>
    suspend fun <E : AuthLoginError> login(method: AuthMethod<E>): Outcome<Unit, E>
    suspend fun logout(): Outcome<Unit, AuthLogoutError>
}
