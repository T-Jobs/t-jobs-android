package ru.nativespeakers.core.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class VacancyNetwork(
    val id: Long,
    val name: String,
    val description: String,
    @JsonNames("salary_min")
    val salaryMin: Int?,
    @JsonNames("salary_max")
    val salaryMax: Int?,
    val town: String? = null,
    @JsonNames("interviews")
    val interviewsBaseIds: List<Long>,
    val tags: List<TagNetwork>,
    @JsonNames("staff")
    val staffIds: List<Long>,
    @JsonNames("tracks")
    val trackIds: List<Long>,
    @JsonNames("applied_candidates")
    val appliedCandidatesIds: List<Long>,
)
