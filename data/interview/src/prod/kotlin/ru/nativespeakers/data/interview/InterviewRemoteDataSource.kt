package ru.nativespeakers.data.interview

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
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

internal class InterviewRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : InterviewDataSource {
    override suspend fun baseInterviewById(id: Long): Result<InterviewBaseNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/interview/base/$id")
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun baseInterviewById(ids: List<Long>): Result<List<InterviewBaseNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/interview/base") {
                if (ids.isNotEmpty()) {
                    url {
                        parameters.append("ids", ids.joinToString(separator = ","))
                    }
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findById(id: Long): Result<InterviewNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/interview/$id")
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findById(ids: List<Long>): Result<List<InterviewNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/interview") {
                if (ids.isNotEmpty()) {
                    url {
                        parameters.append("ids", ids.joinToString(separator = ","))
                    }
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun searchInterviewTypeByName(name: String): Result<List<InterviewTypeNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/interview/type/search") {
                url { parameters.append("name", name) }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun deleteById(id: Long): Result<Unit> = withContext(ioDispatcher) {
        val response = httpClient.delete("/interview/$id")
        when (response.status) {
            HttpStatusCode.OK -> Result.success(Unit)
            else -> Result.failure(Exception())
        }
    }

    override suspend fun createInterview(createInterviewDto: CreateInterviewDto): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post("/interview/add-to-track")
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setInterviewer(interviewId: Long, interviewerId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post("/interview/set-interviewer") {
                url {
                    with(parameters) {
                        append("interview_id", interviewId.toString())
                        append("interviewer_id", interviewerId.toString())
                    }
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setAutoInterviewer(interviewId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post("/interview/set-auto-interviewer") {
                url {
                    parameters.append("interview_id", interviewId.toString())
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setDate(interviewId: Long, date: LocalDateTime): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post("/interview/set-date") {
                url {
                    with(parameters) {
                        append("interview_id", interviewId.toString())
                        append("date", "${date.year}-${date.month}-${date.hour} ${date.hour}:${date.minute}:${date.second}")
                    }
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setAutoDate(interviewId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post("/interview/set-auto-date") {
                url {
                    parameters.append("interview_id", interviewId.toString())
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setLink(interviewId: Long, link: String): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post("/interview/set-link") {
                url {
                    with(parameters) {
                        append("interview_id", interviewId.toString())
                        append("link", link)
                    }
                }
            }

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
            val response = httpClient.post("/interview/approve-time") {
                url {
                    parameters.append("interview_id", interviewId.toString())
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun declineTime(interviewId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post("/interview/decline-time") {
                url {
                    parameters.append("interview_id", interviewId.toString())
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }
}