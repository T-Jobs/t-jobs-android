package ru.nativespeakers.data.tag

import ru.nativespeakers.core.model.TagNetwork

interface TagDataSource {
    suspend fun availableTags(): Result<List<TagNetwork>>
}