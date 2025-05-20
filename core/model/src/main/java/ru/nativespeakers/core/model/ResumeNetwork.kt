package ru.nativespeakers.core.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ResumeNetwork(
    val id: Long,
    @JsonNames("candidate_id")
    val candidateId: Long,
    @JsonNames("salary_min")
    val salaryMin: Int,
    val name: String,
    val description: String,
    val tags: List<TagNetwork>,
    val date: LocalDateTime,
)