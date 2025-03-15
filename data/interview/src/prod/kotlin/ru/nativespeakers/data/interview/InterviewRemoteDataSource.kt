package ru.nativespeakers.data.interview

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.forms.submitForm
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import ru.nativespeakers.core.common.IoDispatcher
import ru.nativespeakers.core.model.InterviewBaseNetwork
import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.data.interview.dto.CreateInterviewDto
import javax.inject.Inject

class InterviewRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : InterviewDataSource {
    override suspend fun baseInterviewById(id: Long): Result<InterviewBaseNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.get(Interview.BaseById(id = id))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun baseInterviewById(ids: List<Long>): Result<List<InterviewBaseNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(Interview.BasesByIds(ids = ids))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findById(id: Long): Result<InterviewNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.get(Interview.Id(id = id))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findById(ids: List<Long>): Result<List<InterviewNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(InterviewsByIds(ids))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun searchInterviewTypeByName(name: String): Result<List<InterviewTypeNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(Interview.SearchTypeByName(name = name))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun deleteById(id: Long): Result<Unit> = withContext(ioDispatcher) {
        val response = httpClient.delete(Interview.Id)
        when (response.status) {
            HttpStatusCode.OK -> Result.success(Unit)
            else -> Result.failure(Exception())
        }
    }

    override suspend fun createInterview(createInterviewDto: CreateInterviewDto): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post(Interview.AddToTrack)
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setInterviewer(interviewId: Long, interviewerId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val url = Interview.SetInterviewer(
                interviewId = interviewId,
                interviewerId = interviewerId
            )

            val response = httpClient.post(url)
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setAutoInterviewer(interviewId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post(Interview.SetAutoInterviewer(interviewId = interviewId))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setDate(interviewId: Long, date: LocalDateTime): Result<Unit> =
        withContext(ioDispatcher) {
            val url = Interview.SetDate(
                interviewId = interviewId,
                date = date
            )

            val response = httpClient.post(url)
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setAutoDate(interviewId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post(Interview.SetAutoDate(interviewId = interviewId))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setLink(interviewId: Long, link: String): Result<Unit> =
        withContext(ioDispatcher) {
            val url = Interview.SetLink(
                interviewId = interviewId,
                link = link
            )

            val response = httpClient.post(url)
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setFeedback(
        interviewId: Long,
        feedback: String,
        success: Boolean
    ): Result<Unit> = withContext(ioDispatcher) {
        val response = httpClient.submitForm(
            formParameters = parameters { append("feedback", feedback) }
        ) {
            url {
                parameters.append("interview_id", interviewId.toString())
                parameters.append("success", success.toString())
            }
        }
        when (response.status) {
            HttpStatusCode.OK -> Result.success(Unit)
            else -> Result.failure(Exception())
        }
    }

    override suspend fun approveTime(interviewId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post(Interview.ApproveTime(interviewId = interviewId))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun declineTime(interviewId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post(Interview.DeclineTime(interviewId = interviewId))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }
}