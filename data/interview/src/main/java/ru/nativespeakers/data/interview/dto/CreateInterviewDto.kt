package ru.nativespeakers.data.interview.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.nativespeakers.core.network.LocalDateTimeSerializer

@InternalSerializationApi
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class CreateInterviewDto(
    @SerialName("interview_type_id")
    val interviewTypeId: Long,
    @SerialName("interviewer_id")
    val interviewerId: Long?,
    @SerialName("track_id")
    val trackId: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val date: LocalDateTime? = null,
    val link: String? = null,
)