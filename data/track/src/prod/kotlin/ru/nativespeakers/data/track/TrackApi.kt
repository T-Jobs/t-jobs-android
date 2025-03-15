package ru.nativespeakers.data.track

import io.ktor.resources.Resource
import kotlinx.serialization.SerialName

@Resource("/track")
internal class Track {
    @Resource("/{id}")
    internal class Id(val parent: Track = Track(), val id: Long)
    @Resource("/approve-application")
    internal class ApproveApplication(val parent: Track = Track())
    @Resource("/set-hr")
    internal class SetHr(
        val parent: Track = Track(),
        @SerialName("track_id") val trackId: Long,
        @SerialName("hr_id") val hrId: Long,
    )
    @Resource("/finish")
    internal class Finish(val parent: Track = Track(), val id: Long)
    @Resource("/create")
    internal class Create(
        val parent: Track = Track(),
        @SerialName("candidate_id") val candidateId: Long,
        @SerialName("vacancy_id") val vacancyId: Long,
    )
}

@Resource("/track")
internal class TracksByIds(val ids: List<Long>)