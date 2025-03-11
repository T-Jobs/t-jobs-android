package ru.nativespeakers.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VacancyNetwork(
    val id: Long,
    val name: String,
    val description: String,
    @SerialName("salary_min")
    val salaryMin: Int?,
    @SerialName("salary_max")
    val salaryMax: Int?,
    val town: String? = null,
    val interviewsBaseIds: List<Long>,
    val tags: List<TagNetwork>,
    @SerialName("staff")
    val staffIds: List<Long>,
    @SerialName("tracks")
    val trackIds: List<Long>,
    @SerialName("applied_candidates")
    val appliedCandidatesIds: List<Long>,
)
