package com.example.pexelsapp.data.datasources.auth.remote.handlers

import android.util.Log
import com.example.pexelsapp.domain.features.auth.models.AuthMethod
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.example.pexelsapp.utils.models.Outcome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleLoginHandler @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthLoginHandler<AuthMethod.Google> {

    private companion object {
        const val TAG = "GoogleLoginHandler"
    }

    override suspend fun login(
        method: AuthMethod.Google
    ): Outcome<Unit, AuthLoginError.GoogleAuthError> {
        return try {
            val credential = GoogleAuthProvider.getCredential(method.idToken.value, null)
            firebaseAuth.signInWithCredential(credential).await()
            Outcome.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Google login failed", e)
            Outcome.Error(AuthLoginError.GoogleAuthError.InvalidToken)
        }
    }
}
