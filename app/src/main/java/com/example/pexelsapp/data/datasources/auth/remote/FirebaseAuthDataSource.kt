package com.example.pexelsapp.data.datasources.auth.remote

import android.util.Log
import com.example.pexelsapp.data.datasources.auth.remote.handlers.AuthLoginHandler
import com.example.pexelsapp.domain.features.auth.models.AuthMethod
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.example.pexelsapp.domain.features.auth.repositories.AuthLogoutError
import com.example.pexelsapp.utils.models.Outcome
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = AuthDataSource::class, component = SingletonComponent::class)
class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val loginHandlers: Map<Class<out AuthMethod<*>>,
            @JvmSuppressWildcards AuthLoginHandler<out AuthMethod<*>>>,
) : AuthDataSource {

    private companion object {
        const val TAG = "FirebaseAuthRemoteDS"
    }

    private val _isAuthorized = MutableStateFlow(firebaseAuth.currentUser != null)
    override val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()

    init {
        firebaseAuth.addAuthStateListener { auth ->
            _isAuthorized.value = auth.currentUser != null
        }
    }

    override suspend fun <E : AuthLoginError> login(method: AuthMethod<E>): Outcome<Unit, E> {
        val handler = loginHandlers[method::class.java]
            ?: return Outcome.Error(AuthLoginError.Common.NotSupported as E)

        val castedHandler = handler as AuthLoginHandler<AuthMethod<E>>

        @Suppress("UNCHECKED_CAST")
        return handler.login(method) as Outcome<Unit, E>
    }

    override suspend fun logout(): Outcome<Unit, AuthLogoutError> {
        return try {
            firebaseAuth.signOut()
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Firebase logout failed", e)
            Outcome.Error(AuthLogoutError.Unknown)
        }
    }
}
