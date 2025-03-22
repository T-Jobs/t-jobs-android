package ru.nativespeakers.core.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CandidateNetwork(
    val id: Long,
    val name: String,
    val surname: String,
    @JsonNames("photo_url")
    val photoUrl: String?,
    @JsonNames("tg_id")
    val tgId: String,
    val town: String,
    @JsonNames("resumes")
    val resumesIds: List<Long>,
    val tracks: List<Long>,
    @JsonNames("applied_vacancies")
    val appliedVacancies: List<Long>,
)