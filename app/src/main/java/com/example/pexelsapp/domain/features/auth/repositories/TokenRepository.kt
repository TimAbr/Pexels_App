package com.example.pexelsapp.domain.features.auth.repositories

import com.example.pexelsapp.domain.features.auth.models.AccessToken
import com.example.pexelsapp.domain.features.auth.models.RefreshToken
import com.example.pexelsapp.domain.features.auth.models.TokenPair

interface TokenRepository {
    fun saveTokens(tokenPair: TokenPair)
    
    fun updateAccessToken(accessToken: AccessToken)
    fun updateRefreshToken(refreshToken: RefreshToken)

    fun getAccessToken(): AccessToken?
    fun getRefreshToken(): RefreshToken?
    fun getTokenPair(): TokenPair?
    
    fun clear()
}
