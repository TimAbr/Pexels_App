package com.example.pexelsapp.presentation.features.auth.google

import android.content.Context
import androidx.credentials.CredentialManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.Binds

@Module
@InstallIn(ActivityComponent::class)
interface GoogleAuthModule {

    @Binds
    @ActivityScoped
    fun bindGoogleIdProvider(
        androidGoogleIdProvider: AndroidGoogleIdProvider
    ): GoogleIdProvider
}
