package ru.nativespeakers.data.interview

import kotlinx.datetime.LocalDateTime
import ru.nativespeakers.core.model.InterviewBaseNetwork
import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.data.interview.dto.CreateInterviewDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterviewRepository @Inject constructor(
    private val interviewDataSource: InterviewDataSource,
) {
    suspend fun baseInterviewById(id: Long): Result<InterviewBaseNetwork> {
        return interviewDataSource.baseInterviewById(id)
    }

    suspend fun baseInterviewById(ids: List<Long>): Result<List<InterviewBaseNetwork>> {
        if (ids.isEmpty()) return Result.success(emptyList())

        return interviewDataSource.baseInterviewById(ids)
    }

    suspend fun findById(id: Long): Result<InterviewNetwork> {
        return interviewDataSource.findById(id)
    }

    suspend fun findById(ids: List<Long>): Result<List<InterviewNetwork>> {
        return interviewDataSource.findById(ids)
    }

    suspend fun searchInterviewTypeByName(name: String): Result<List<InterviewTypeNetwork>> {
        return interviewDataSource.searchInterviewTypeByName(name)
    }

    suspend fun deleteById(id: Long): Result<Unit> {
        return interviewDataSource.deleteById(id)
    }

    suspend fun createInterview(createInterviewDto: CreateInterviewDto): Result<Unit> {
        return interviewDataSource.createInterview(createInterviewDto)
    }

    suspend fun setInterviewer(interviewId: Long, interviewerId: Long): Result<Unit> {
        return interviewDataSource.setInterviewer(interviewId, interviewerId)
    }

    suspend fun setAutoInterviewer(interviewId: Long): Result<Unit> {
        return interviewDataSource.setAutoInterviewer(interviewId)
    }

    suspend fun setDate(interviewId: Long, date: LocalDateTime): Result<Unit> {
        return interviewDataSource.setDate(interviewId, date)
    }

    suspend fun setAutoDate(interviewId: Long): Result<Unit> {
        return interviewDataSource.setAutoDate(interviewId)
    }

    suspend fun setLink(interviewId: Long, link: String): Result<Unit> {
        return interviewDataSource.setLink(interviewId, link)
    }

    suspend fun setFeedback(interviewId: Long, feedback: String, success: Boolean): Result<Unit> {
        return interviewDataSource.setFeedback(interviewId, feedback, success)
    }

    suspend fun approveTime(interviewId: Long): Result<Unit> {
        return interviewDataSource.approveTime(interviewId)
    }

    suspend fun declineTime(interviewId: Long): Result<Unit> {
        return interviewDataSource.declineTime(interviewId)
    }
}