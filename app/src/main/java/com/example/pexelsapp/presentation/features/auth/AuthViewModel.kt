package com.example.pexelsapp.presentation.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.domain.features.auth.models.GoogleIdToken
import com.example.pexelsapp.domain.features.auth.usecases.GetAuthStateUseCase
import com.example.pexelsapp.domain.features.auth.usecases.LoginWithGoogleUseCase
import com.example.pexelsapp.domain.features.auth.usecases.ObserveAuthStateUseCase
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<AuthScreenState>(
        if (getAuthStateUseCase()) {
            AuthScreenState.Authorized
        } else {
            AuthScreenState.LogIn
        },
    )
    val state: StateFlow<AuthScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeAuthStateUseCase().collect { isAuthorized ->
                if (isAuthorized) {
                    _state.value = AuthScreenState.Authorized
                } else {
                    _state.value = AuthScreenState.LogIn
                }
            }
        }
    }

    fun loginWithGoogle(idToken: GoogleIdToken) {
        viewModelScope.launch {
            _state.value = AuthScreenState.Loading
            val loginOutcome = loginWithGoogleUseCase(idToken)
            _state.value = when (loginOutcome) {
                is Outcome.Success -> AuthScreenState.Authorized
                is Outcome.Error -> AuthScreenState.Error(loginOutcome.type)
            }
        }
    }

    fun onError(error: AuthLoginError) {
        _state.value = AuthScreenState.Error(error)
    }

    fun clearError() {
        _state.update { currentState ->
            if (currentState is AuthScreenState.Error) {
                AuthScreenState.LogIn
            } else {
                currentState
            }
        }
    }
}
