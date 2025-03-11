package ru.nativespeakers.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaffNetwork(
    val id: Long,
    val name: String,
    val surname: String,
    @SerialName("photo_url")
    val photoUrl: String? = null,
    @SerialName("tracks")
    val tracksIds: List<Long>,
    @SerialName("interview_types")
    val interviewTypeNetworks: List<InterviewTypeNetwork>,
    @SerialName("vacancies")
    val vacanciesIds: List<Long>,
    val roles: List<AppRole>,
    @SerialName("interviews")
    val interviewsIds: List<Long>,
    @SerialName("interviewer_mode")
    val isInterviewModeOn: Boolean,
)