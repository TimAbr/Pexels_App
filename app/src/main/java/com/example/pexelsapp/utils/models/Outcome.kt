package com.example.pexelsapp.utils.models

sealed interface Outcome<out ValueType, out ErrorType> {
    data class Success<out ValueType>(val value: ValueType) : Outcome<ValueType, Nothing>
    data class Error<out ErrorType>(val type: ErrorType, val message: String? = null) : Outcome<Nothing, ErrorType>

    object InProgress
}
