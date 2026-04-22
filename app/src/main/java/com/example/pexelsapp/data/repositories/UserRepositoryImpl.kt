package com.example.pexelsapp.data.repositories

import com.example.pexelsapp.data.datasources.user.local.UserLocalDataSource
import com.example.pexelsapp.data.datasources.user.remote.UserRemoteDataSource
import com.example.pexelsapp.domain.features.user.models.User
import com.example.pexelsapp.domain.features.user.repositories.UserError
import com.example.pexelsapp.domain.features.user.repositories.UserRepository
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = UserRepository::class, component = SingletonComponent::class)
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
) : UserRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val _user = MutableStateFlow<User?>(null)
    override val user: StateFlow<User?> = _user.asStateFlow()

    init {
        scope.launch {
            val cachedUser = localDataSource.getUser()
            if (_user.value == null && cachedUser != null) {
                _user.value = cachedUser
            }

            remoteDataSource.user.collect { remoteUser ->
                _user.value = remoteUser
                if (remoteUser != null) {
                    localDataSource.saveUser(remoteUser)
                } else {
                    localDataSource.clearUser()
                }
            }
        }
    }

    override suspend fun getUserById(userId: String): Outcome<User, UserError.Get> {
        return remoteDataSource.getUserById(userId)
    }

    override suspend fun updateProfile(user: User): Outcome<Unit, UserError.Update> {
        return remoteDataSource.updateProfile(user)
    }
}
