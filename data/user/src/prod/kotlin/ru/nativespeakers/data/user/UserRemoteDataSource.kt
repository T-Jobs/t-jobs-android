package ru.nativespeakers.data.user

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
    override suspend fun findUserById(id: Long): Result<StaffNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.get(User.Id(id = id))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findUsersByIds(ids: List<Long>): Result<List<StaffNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(UsersByIds(ids = ids))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findUsersByQuery(query: String): Result<List<StaffNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(User.Search(text = query))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun userInfo(): Result<StaffNetwork> = withContext(ioDispatcher) {
        val response = httpClient.get(User.Info())
        when (response.status) {
            HttpStatusCode.OK -> Result.success(response.body())
            else -> Result.failure(Exception())
        }
    }

    override suspend fun userInterviews(onlyRelevant: Boolean): Result<List<InterviewNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(User.Interviews(onlyRelevant = onlyRelevant))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun userTracks(onlyRelevant: Boolean): Result<List<TrackNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(User.Tracks(onlyRelevant = onlyRelevant))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun userVacancies(onlyRelevant: Boolean): Result<List<VacancyNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(User.Vacancies(onlyRelevant = onlyRelevant))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun setInterviewerMode(value: Boolean): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.submitForm(
                url = "/user/set-interviewer-mode",
                formParameters = parameters { append("value", value.toString()) }
            )

            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun addCompetency(interviewTypeId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.submitForm(
                url = "/user/competencies",
                formParameters = parameters {
                    append("interview_type_id", interviewTypeId.toString())
                }
            )

            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun deleteCompetency(interviewTypeId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.delete(User.Competencies(interviewTypeId = interviewTypeId))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }

    override suspend fun followVacancy(vacancyId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            val response = httpClient.post(User.FollowVacancyById(id = vacancyId))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                else -> Result.failure(Exception())
            }
        }
}