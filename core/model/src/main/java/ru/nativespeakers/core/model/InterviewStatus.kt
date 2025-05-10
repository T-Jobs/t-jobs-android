package ru.nativespeakers.core.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
enum class InterviewStatus {
    @JsonNames("FAILED") FAILED,
    @JsonNames("SUCCESS") PASSED,
    @JsonNames("WAITING_FEEDBACK") WAITING_FOR_FEEDBACK,
    @JsonNames("TIME_APPROVAL") WAITING_FOR_TIME_APPROVAL,
    @JsonNames("NONE") NONE,
}