package ru.nativespeakers.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterviewBaseNetwork(
    val id: Long,
    @SerialName("interview_type")
    val interviewType: InterviewTypeNetwork,
    @SerialName("vacancy_id")
    val vacancyId: Long,
)