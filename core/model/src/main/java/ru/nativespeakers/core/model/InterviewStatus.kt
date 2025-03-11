package ru.nativespeakers.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class InterviewStatus {
    @SerialName("FAILED") FAILED,
    @SerialName("SUCCESS") PASSED,
    @SerialName("WAITING_FEEDBACK") WAITING_FOR_FEEDBACK,
    @SerialName("TIME_APPROVAL") WAITING_FOR_TIME_APPROVAL,
    @SerialName("NONE") NONE,
}