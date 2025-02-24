package ru.nativespeakers.data.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AppRole {
    @SerialName("hr") HR,
    @SerialName("team_lead") TEAM_LEAD,
    @SerialName("interviewer") INTERVIEWER,
}