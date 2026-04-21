package com.example.pexelsapp.data.datasources.auth.remote.handlers

import com.example.pexelsapp.domain.features.auth.models.AuthMethod
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.example.pexelsapp.utils.models.Outcome
import dagger.MapKey
import kotlin.reflect.KClass


interface AuthLoginHandler<in M : AuthMethod<*>, out E : AuthLoginError> {
    suspend fun login(method: M): Outcome<Unit, E>
}

@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class AuthMethodKey(val value: KClass<out AuthMethod<*>>)
