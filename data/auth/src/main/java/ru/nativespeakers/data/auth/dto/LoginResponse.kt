package ru.nativespeakers.data.auth.dto

import kotlinx.serialization.Serializable
import ru.nativespeakers.data.auth.AppRole

@Serializable
data class LoginResponse(
    val token: String,
    val roles: List<AppRole>,
)