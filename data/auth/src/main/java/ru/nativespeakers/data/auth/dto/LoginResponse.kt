package ru.nativespeakers.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
)