package ru.nativespeakers.core.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterviewNetwork(
    val id: Long,
    @SerialName("interviewer_id")
    val interviewerId: Long? = null,
    @SerialName("interview_type")
    val interviewType: InterviewTypeNetwork,
    @SerialName("track_id")
    val trackId: Long,
    @SerialName("date_picked")
    val datePicked: LocalDateTime? = null,
    @SerialName("date_approved")
    val dateApproved: Boolean,
    val feedback: String? = null,
    val status: InterviewStatus,
    @SerialName("able_set_time")
    val isAbleToSetTime: Boolean,
)
