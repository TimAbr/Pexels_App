package org.example.project.domain.feature.auth.repositories

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.feature.auth.models.AccessToken
import org.example.project.domain.feature.auth.models.RefreshToken
import org.example.project.domain.feature.auth.models.TokenPair

interface TokenRepository {
    fun saveTokens(tokenPair: TokenPair)
    
    fun updateAccessToken(accessToken: AccessToken)
    fun updateRefreshToken(refreshToken: RefreshToken)

    fun getAccessToken(): AccessToken?
    fun getRefreshToken(): RefreshToken?
    fun getTokenPair(): TokenPair?
    
    fun clear()
}
