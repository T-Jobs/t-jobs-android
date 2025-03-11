package ru.nativespeakers.core.model

import kotlinx.serialization.Serializable

@Serializable
data class InterviewTypeNetwork(
    val id: Long,
    val name: String,
)