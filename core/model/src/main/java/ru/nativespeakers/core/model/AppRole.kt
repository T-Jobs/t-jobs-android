package ru.nativespeakers.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AppRole {
    @SerialName("HR") HR,
    @SerialName("TL") TEAM_LEAD,
    @SerialName("INTERVIEWER") INTERVIEWER,
}