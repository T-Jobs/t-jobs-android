package ru.nativespeakers.core.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class InterviewNetwork(
    val id: Long,
    @JsonNames("interviewer_id")
    val interviewerId: Long? = null,
    @JsonNames("interview_type")
    val interviewType: InterviewTypeNetwork,
    @JsonNames("track_id")
    val trackId: Long,
    @JsonNames("date_picked")
    val datePicked: LocalDateTime? = null,
    @JsonNames("date_approved")
    val dateApproved: Boolean,
    val feedback: String? = null,
    val status: InterviewStatus,
    @JsonNames("able_set_time")
    val isAbleToSetTime: Boolean,
)
