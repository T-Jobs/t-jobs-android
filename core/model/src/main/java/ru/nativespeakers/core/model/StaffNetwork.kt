package ru.nativespeakers.core.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class StaffNetwork(
    val id: Long,
    val name: String,
    val surname: String,
    @JsonNames("photo_url")
    val photoUrl: String? = null,
    @JsonNames("tracks")
    val tracksIds: List<Long>,
    @JsonNames("interview_types")
    val interviewTypeNetworks: List<InterviewTypeNetwork>,
    @JsonNames("vacancies")
    val vacanciesIds: List<Long>,
    val roles: List<AppRole>,
    @JsonNames("interviews")
    val interviewsIds: List<Long>,
    @JsonNames("interviewer_mode")
    val isInterviewModeOn: Boolean,
)