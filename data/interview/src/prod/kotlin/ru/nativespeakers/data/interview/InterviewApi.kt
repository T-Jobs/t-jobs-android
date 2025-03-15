package ru.nativespeakers.data.interview

import io.ktor.resources.Resource
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName

@Resource("/interview")
internal class Interview {
    @Resource("/{id}")
    internal class Id(val parent: Interview = Interview(), val id: Long)
    @Resource("/base/{id}")
    internal class BaseById(val parent: Interview = Interview(), val id: Long)
    @Resource("/base")
    internal class BasesByIds(val parent: Interview = Interview(), val ids: List<Long>)
    @Resource("/type/search")
    internal class SearchTypeByName(val parent: Interview = Interview(), val name: String)
    @Resource("/add-to-track")
    internal class AddToTrack(val parent: Interview = Interview())
    @Resource("/set-interviewer")
    internal class SetInterviewer(
        val parent: Interview = Interview(),
        @SerialName("interview_id") val interviewId: Long,
        @SerialName("interviewer_id") val interviewerId: Long,
    )
    @Resource("/set-auto-interviewer")
    internal class SetAutoInterviewer(
        val parent: Interview = Interview(),
        @SerialName("interview_id") val interviewId: Long,
    )
    @Resource("/set-date")
    internal class SetDate(
        val parent: Interview = Interview(),
        @SerialName("interview_id") val interviewId: Long,
        val date: LocalDateTime,
    )
    @Resource("/set-auto-date")
    internal class SetAutoDate(
        val parent: Interview = Interview(),
        @SerialName("interview_id") val interviewId: Long,
    )
    @Resource("/set-link")
    internal class SetLink(
        val parent: Interview = Interview(),
        @SerialName("interview_id") val interviewId: Long,
        val link: String,
    )
    @Resource("/set-feedback")
    internal class SetFeedback(
        val parent: Interview = Interview(),
        @SerialName("interview_id") val interviewId: Long,
        val success: Boolean,
    )
    @Resource("/approve-time")
    internal class ApproveTime(
        val parent: Interview = Interview(),
        @SerialName("interview_id") val interviewId: Long,
    )
    @Resource("/decline-time")
    internal class DeclineTime(
        val parent: Interview = Interview(),
        @SerialName("interview_id") val interviewId: Long,
    )
}

@Resource("/interview")
internal class InterviewsByIds(val ids: List<Long>)