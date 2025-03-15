package ru.nativespeakers.data.user

import io.ktor.resources.Resource
import kotlinx.serialization.SerialName

@Resource("/user")
internal class User {
    @Resource("/info")
    internal class Info(val parent: User = User())
    @Resource("/{id}")
    internal class Id(val parent: User = User(), val id: Long)
    @Resource("/interviews")
    internal class Interviews(val parent: User = User(), val onlyRelevant: Boolean)
    @Resource("/vacancies")
    internal class Vacancies(val parent: User = User(), val onlyRelevant: Boolean)
    @Resource("/tracks")
    internal class Tracks(val parent: User = User(), val onlyRelevant: Boolean)
    @Resource("/search")
    internal class Search(val parent: User = User(), val text: String)
    @Resource("/competencies")
    internal class Competencies(
        val parent: User = User(),
        @SerialName("interview_type_id") val interviewTypeId: Long
    )
    @Resource("/follow-vacancy/{id}")
    internal class FollowVacancyById(val parent: User = User(), val id: Long)
}

@Resource("/user")
internal class UsersByIds(val ids: List<Long>)