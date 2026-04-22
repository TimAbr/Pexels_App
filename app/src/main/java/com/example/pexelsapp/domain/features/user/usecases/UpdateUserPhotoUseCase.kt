package com.example.pexelsapp.domain.features.user.usecases

import com.example.pexelsapp.domain.features.user.models.Uri
import com.example.pexelsapp.domain.features.user.repositories.UserError
import com.example.pexelsapp.domain.features.user.repositories.UserRepository
import com.example.pexelsapp.utils.models.Outcome
import javax.inject.Inject

class UpdateUserPhotoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(imageUri: Uri): Outcome<Unit, UserError.Update> {
        return when (
            val uploadResult = userRepository.uploadUserPhoto(imageUri)
        ) {
            is Outcome.Success -> {
                val photoUrl = uploadResult.value
                val user = userRepository.user.value
                if(user != null){
                    userRepository.updateProfile(
                        user.copy(photoUrl = photoUrl.value)
                    )
                } else {
                    Outcome.Error(UserError.Common.Unathorized)
                }

            }
            is Outcome.Error -> uploadResult

        }
    }
}