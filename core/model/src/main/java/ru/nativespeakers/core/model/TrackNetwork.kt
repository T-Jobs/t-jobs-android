package ru.nativespeakers.core.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class TrackNetwork(
    val id: Long,
    val hr: StaffNetwork,
    val candidate: CandidateNetwork,
    val vacancy: VacancyNetwork,
    val finished: Boolean,
    @JsonNames("interviews")
    val interviewsIds: List<Long>,
    @JsonNames("last_status")
    val lastStatus: InterviewStatus,
)