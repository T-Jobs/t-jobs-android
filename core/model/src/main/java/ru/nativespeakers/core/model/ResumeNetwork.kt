package ru.nativespeakers.core.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResumeNetwork(
    val id: Long,
    @SerialName("candidate_id")
    val candidateId: Long,
    @SerialName("salary_min")
    val salaryMin: Int,
    val description: String,
    val tags: List<TagNetwork>,
    val date: LocalDateTime,
)