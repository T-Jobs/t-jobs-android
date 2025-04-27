package ru.nativespeakers.data.interview

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.InternalSerializationApi
import ru.nativespeakers.core.model.InterviewBaseNetwork
import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.data.interview.dto.CreateInterviewDto

interface InterviewDataSource {
    suspend fun baseInterviewById(id: Long): Result<InterviewBaseNetwork>
    suspend fun baseInterviewById(ids: List<Long>): Result<List<InterviewBaseNetwork>>
    suspend fun findById(id: Long): Result<InterviewNetwork>
    suspend fun findById(ids: List<Long>): Result<List<InterviewNetwork>>
    suspend fun searchInterviewTypeByName(name: String): Result<List<InterviewTypeNetwork>>
    suspend fun deleteById(id: Long): Result<Unit>
    @OptIn(InternalSerializationApi::class)
    suspend fun createInterview(createInterviewDto: CreateInterviewDto): Result<InterviewNetwork>
    suspend fun setInterviewer(interviewId: Long, interviewerId: Long): Result<Unit>
    suspend fun setAutoInterviewer(interviewId: Long): Result<Unit>
    suspend fun setDate(interviewId: Long, date: LocalDateTime): Result<Unit>
    suspend fun setAutoDate(interviewId: Long): Result<Unit>
    suspend fun setLink(interviewId: Long, link: String): Result<Unit>
    suspend fun setFeedback(interviewId: Long, feedback: String, success: Boolean): Result<Unit>
    suspend fun approveTime(interviewId: Long): Result<Unit>
    suspend fun declineTime(interviewId: Long): Result<Unit>
}