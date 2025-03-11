package ru.nativespeakers.data.auth.dto

import kotlinx.serialization.Serializable
import ru.nativespeakers.core.model.AppRole

@Serializable
data class UserRolesResponse(
    val roles: List<AppRole>,
)