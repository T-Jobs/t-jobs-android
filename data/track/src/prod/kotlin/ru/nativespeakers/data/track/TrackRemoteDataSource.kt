package ru.nativespeakers.data.track

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.nativespeakers.core.common.IoDispatcher
import ru.nativespeakers.core.model.TrackNetwork
import javax.inject.Inject

internal class TrackRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : TrackDataSource {
    override suspend fun findById(id: Long): Result<TrackNetwork> = withContext(ioDispatcher) {
        val response = httpClient.get("/track/$id")
        when (response.status) {
            HttpStatusCode.OK -> Result.success(response.body())
            else -> Result.failure(Exception())
        }
    }

    override suspend fun findById(ids: List<Long>): Result<List<TrackNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/track") {
                url {
                    parameters.append("ids", ids.joinToString(separator = ","))
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun approveApplication(candidateId: Long, vacancyId: Long): Result<TrackNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.submitForm(
                url = "/track/approve-application",
                formParameters = parameters {
                    append("candidate_id", candidateId.toString())
                    append("vacancy_id", vacancyId.toString())
                }
            )

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setHrForTrack(trackId: Long, hrId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post("/track/set-hr") {
                url {
                    with(parameters) {
                        append("track_id", trackId.toString())
                        append("hr_id", hrId.toString())
                    }
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun finishTrackById(id: Long): Result<Unit> = withContext(ioDispatcher) {
        val response = httpClient.post("/track/finish") {
            url { parameters.append("id", id.toString()) }
        }

        when (response.status) {
            HttpStatusCode.OK -> Result.success(Unit)
            else -> Result.failure(Exception())
        }
    }

    override suspend fun createTrack(candidateId: Long, vacancyId: Long): Result<TrackNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.post("/track/create") {
                url {
                    with(parameters) {
                        append("candidate_id", candidateId.toString())
                        append("vacancy_id", vacancyId.toString())
                    }
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun continueTrack(trackId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post("/track/continue") {
                url {
                    parameters.append("id", trackId.toString())
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }
}