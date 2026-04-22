package com.example.pexelsapp.data.datasources.user.remote

import com.example.pexelsapp.domain.features.user.models.Uri
import com.example.pexelsapp.domain.features.user.models.Url
import com.example.pexelsapp.domain.features.user.models.User
import com.example.pexelsapp.domain.features.user.repositories.UserError
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = UserRemoteDataSource::class, component = SingletonComponent::class)
class UserRemoteDataSourceImpl @Inject constructor(
    private val firebaseDataSource: FirebaseUserDataSource,
    private val imgBBDataSource: ImgBBUserDataSource,
) : UserRemoteDataSource {

    override val user: StateFlow<User?> = firebaseDataSource.user

    override suspend fun getUserById(userId: String): Outcome<User, UserError.Get> {
        return firebaseDataSource.getUserById(userId)
    }

    override suspend fun updateProfile(user: User): Outcome<Unit, UserError.Update> {
        return firebaseDataSource.updateProfile(user)
    }

    override suspend fun uploadUserPhoto(uri: Uri): Outcome<Url, UserError.Update> {
        val userId = firebaseDataSource.getCurrentUserId() ?: return Outcome.Error(UserError.Common.Unathorized)
        return imgBBDataSource.uploadUserPhoto(userId, uri)
    }
}
