package ru.nativespeakers.data.interview.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateInterviewDto(
    @SerialName("interview_type_id")
    val interviewTypeId: Long,
    @SerialName("interviewer_id")
    val interviewerId: Long?,
    @SerialName("track_id")
    val trackId: Long,
)