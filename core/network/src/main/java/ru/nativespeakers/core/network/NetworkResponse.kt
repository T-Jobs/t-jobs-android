package ru.nativespeakers.core.network

import kotlinx.serialization.Serializable

@Serializable
sealed class NetworkResponse<out T> {
    @Serializable
    class Failure(val error: String, val message: String) : NetworkResponse<Nothing>()
    @Serializable
    class Success<out T>(val data: T) : NetworkResponse<T>()
}