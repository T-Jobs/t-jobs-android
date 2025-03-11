package ru.nativespeakers.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CandidateNetwork(
    val id: Long,
    val name: String,
    val surname: String,
    @SerialName("photo_url")
    val photoUrl: String?,
    @SerialName("tg_id")
    val tgId: String,
    val town: String,
    @SerialName("resumes")
    val resumesIds: List<Long>,
    val tracks: List<Long>,
    @SerialName("applied_vacancies")
    val appliedVacancies: List<Long>,
)