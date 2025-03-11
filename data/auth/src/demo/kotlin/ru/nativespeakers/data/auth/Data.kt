package ru.nativespeakers.data.auth

import ru.nativespeakers.core.model.AppRole

internal val UserCredentials = mapOf(
    "login" to "qwerty123@mail.ru",
    "password" to "12345!"
)

internal val UserRoles = listOf(
    AppRole.HR,
    AppRole.TEAM_LEAD,
    AppRole.INTERVIEWER
)