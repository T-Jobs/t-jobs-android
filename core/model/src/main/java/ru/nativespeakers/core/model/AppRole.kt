package ru.nativespeakers.core.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
enum class AppRole {
    @JsonNames("HR") HR,
    @JsonNames("TL") TEAM_LEAD,
    @JsonNames("INTERVIEWER") INTERVIEWER,
}