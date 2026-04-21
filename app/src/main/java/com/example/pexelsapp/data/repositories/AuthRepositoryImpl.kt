package com.example.pexelsapp.data.repositories

import com.example.pexelsapp.data.datasources.auth.remote.AuthRemoteDataSource
import com.example.pexelsapp.domain.features.auth.models.AuthMethod
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.example.pexelsapp.domain.features.auth.repositories.AuthLogoutError
import com.example.pexelsapp.domain.features.auth.repositories.AuthRepository
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = AuthRepository::class, component = SingletonComponent::class)
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
) : AuthRepository {

    override val isAuthorized: StateFlow<Boolean> = remoteDataSource.isAuthorized

    override suspend fun <E : AuthLoginError> login(
        method: AuthMethod<E>
    ): Outcome<Unit, E> {
        return remoteDataSource.login(method)
    }

    override suspend fun logout(): Outcome<Unit, AuthLogoutError> {
        return remoteDataSource.logout()
    }
}
