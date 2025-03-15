package ru.nativespeakers.data.track

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.forms.submitForm
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
        val response = httpClient.get(Track.Id(id = id))
        when (response.status) {
            HttpStatusCode.OK -> Result.success(response.body())
            else -> Result.failure(Exception())
        }
    }

    override suspend fun findById(ids: List<Long>): Result<List<TrackNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(TracksByIds(ids = ids))
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
            val url = Track.SetHr(
                trackId = trackId,
                hrId = hrId
            )

            val response = httpClient.post(url)
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun finishTrackById(id: Long): Result<Unit> = withContext(ioDispatcher) {
        val response = httpClient.post(Track.Finish(id = id))
        when (response.status) {
            HttpStatusCode.OK -> Result.success(Unit)
            else -> Result.failure(Exception())
        }
    }

    override suspend fun createTrack(candidateId: Long, vacancyId: Long): Result<TrackNetwork> =
        withContext(ioDispatcher) {
            val url = Track.Create(
                candidateId = candidateId,
                vacancyId = vacancyId
            )

            val response = httpClient.post(url)
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }
}