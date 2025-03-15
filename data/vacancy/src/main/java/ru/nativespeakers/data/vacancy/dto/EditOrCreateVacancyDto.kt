package ru.nativespeakers.data.vacancy.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditOrCreateVacancyDto(
    val name: String,
    val description: String,
    @SerialName("salary_min")
    val salaryMin: Int?,
    @SerialName("salary_max")
    val salaryMax: Int?,
    val town: String?,
    val interviews: List<Long>,
    val tags: List<Long>,
)