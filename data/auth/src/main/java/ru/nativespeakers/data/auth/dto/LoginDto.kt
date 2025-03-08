package ru.nativespeakers.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    val login: String,
    val password: String,
)