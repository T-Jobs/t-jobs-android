package ru.nativespeakers.feature.track.common

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.InternalSerializationApi
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.data.interview.dto.CreateInterviewDto

@Immutable
data class InterviewCreateState(
    val interviewType: InterviewTypeNetwork? = null,
    val interviewer: StaffNetwork? = null,
    val date: LocalDateTime? = null,
    val link: String? = null,
)

@OptIn(InternalSerializationApi::class)
fun InterviewCreateState.toDto(trackId: Long) = CreateInterviewDto(
    interviewTypeId = interviewType!!.id,
    interviewerId = interviewer?.id,
    trackId = trackId,
    date = date,
    link = link,
)