package ru.nativespeakers.data.user

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.nativespeakers.core.common.IoDispatcher
import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.model.VacancyNetwork
import javax.inject.Inject

internal class UserRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : UserDataSource {
    override suspend fun findUserById(id: Long): Result<StaffNetwork?> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/user/${id}")
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findUsersByIds(ids: List<Long>): Result<List<StaffNetwork?>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/user") {
                url {
                    for (id in ids)
                        parameters.append("ids", id.toString())
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findUserByQuery(query: String): Result<List<StaffNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/user/search") {
                url {
                    parameters.append("text", query)
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun userInfo(): Result<StaffNetwork> = withContext(ioDispatcher) {
        val response = httpClient.get("/user/info")
        when (response.status) {
            HttpStatusCode.OK -> Result.success(response.body())
            else -> Result.failure(Exception())
        }
    }

    override suspend fun userInterviews(onlyActual: Boolean): Result<List<InterviewNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/user/interviews") {
                url {
                    parameters.append("onlyActual", onlyActual.toString())
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun userTracks(onlyActual: Boolean): Result<List<TrackNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/user/tracks") {
                url {
                    parameters.append("onlyActual", onlyActual.toString())
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun userVacancies(onlyActual: Boolean): Result<List<VacancyNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/user/vacancies")
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }
}