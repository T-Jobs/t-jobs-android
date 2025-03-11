package ru.nativespeakers.core.model

import kotlinx.serialization.Serializable

@Serializable
data class TagNetwork(
    val id: Long,
    val category: TagCategoryNetwork,
    val name: String,
)

@Serializable
data class TagCategoryNetwork(
    val id: Long,
    val name: String
)