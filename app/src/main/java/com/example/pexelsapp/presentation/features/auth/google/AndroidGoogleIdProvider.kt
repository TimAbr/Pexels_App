package com.example.pexelsapp.presentation.features.auth.google

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.example.pexelsapp.BuildConfig
import com.example.pexelsapp.data.mappers.toGoogleAuthLoginError
import com.example.pexelsapp.domain.features.auth.models.GoogleIdToken
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.example.pexelsapp.utils.models.Outcome
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
class AndroidGoogleIdProvider @Inject constructor(
    @ActivityContext private val context: Context,
    private val credentialManager: CredentialManager,
) : GoogleIdProvider {

    private val webClientId: String = BuildConfig.GOOGLE_WEB_CLIENT_ID

    override suspend fun getId(): Outcome<GoogleIdToken, AuthLoginError> =
        withContext(Dispatchers.Main) {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = context,
                    request = request,
                )

                handleSignIn(result)
            } catch (e: Exception) {
                Outcome.Error(e.toGoogleAuthLoginError())
            }
        }

    private fun handleSignIn(result: GetCredentialResponse): Outcome<GoogleIdToken, AuthLoginError> {
        val credential = result.credential

        return if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Outcome.Success(GoogleIdToken(googleIdTokenCredential.idToken))
            } catch (e: Exception) {
                Outcome.Error(AuthLoginError.Common.Unknown)
            }
        } else {
            Outcome.Error(AuthLoginError.Common.Unknown)
        }
    }
}
