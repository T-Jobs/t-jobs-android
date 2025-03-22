package ru.nativespeakers.data.tag

import ru.nativespeakers.core.model.TagNetwork
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val tagDataSource: TagDataSource,
) {
    suspend fun availableTags(): Result<List<TagNetwork>> {
        return tagDataSource.availableTags()
    }
}