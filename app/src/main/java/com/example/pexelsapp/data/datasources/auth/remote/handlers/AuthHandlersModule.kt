package com.example.pexelsapp.data.datasources.auth.remote.handlers

import com.example.pexelsapp.domain.features.auth.models.AuthMethod
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
interface AuthHandlersModule {

    @Binds
    @IntoMap
    @AuthMethodKey(AuthMethod.Google::class)
    fun bindGoogleLoginHandler(
        handler: GoogleLoginHandler
    ): AuthLoginHandler<AuthMethod<*>, AuthLoginError>
}
