package org.example.project.domain.feature.auth.models

sealed interface AuthMethod {
    object Google : AuthMethod
}
