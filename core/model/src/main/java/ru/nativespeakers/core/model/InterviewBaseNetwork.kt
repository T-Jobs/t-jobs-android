package ru.nativespeakers.core.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class InterviewBaseNetwork(
    val id: Long,
    @JsonNames("interview_type")
    val interviewType: InterviewTypeNetwork,
    @JsonNames("vacancy_id")
    val vacancyId: Long,
)