package ru.nativespeakers.core.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackNetwork(
    val id: Long,
    val hr: StaffNetwork,
    val candidate: CandidateNetwork,
    val vacancy: VacancyNetwork,
    val finished: Boolean,
    val interviewsIds: List<Long>,
    val lastStatus: InterviewStatus,
)