package com.example.pexelsapp.domain.features.auth.models

sealed interface AuthMethod {
    object Google : AuthMethod
}
