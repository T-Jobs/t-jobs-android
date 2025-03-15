package ru.nativespeakers.data.vacancy

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.nativespeakers.core.common.IoDispatcher
import ru.nativespeakers.core.model.VacancyNetwork
import ru.nativespeakers.data.vacancy.dto.EditOrCreateVacancyDto
import javax.inject.Inject

internal class VacancyRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : VacancyDataSource {
    override suspend fun findVacancyById(id: Long): Result<VacancyNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.get(Vacancy.Id(id = id))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findVacancyById(ids: List<Long>): Result<List<VacancyNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(VacanciesByIds(ids = ids))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun searchForVacancies(
        page: Int,
        pageSize: Int,
        salaryLowerBound: Int
    ): Result<List<VacancyNetwork>> = withContext(ioDispatcher) {
        val url = Vacancy.Search(
            page = page,
            pageSize = pageSize,
            salaryLowerBound = salaryLowerBound
        )

        val response = httpClient.get(url)
        when (response.status) {
            HttpStatusCode.OK -> Result.success(response.body())
            else -> Result.failure(Exception())
        }
    }

    override suspend fun editVacancyById(
        id: Long,
        editOrCreateVacancyDto: EditOrCreateVacancyDto
    ): Result<VacancyNetwork> = withContext(ioDispatcher) {
        val response = httpClient.post(Vacancy.EditById(id = id)) {
            contentType(ContentType.Application.Json)
            setBody(editOrCreateVacancyDto)
        }

        when (response.status) {
            HttpStatusCode.OK -> Result.success(response.body())
            else -> Result.failure(Exception())
        }
    }

    override suspend fun createVacancy(editOrCreateVacancyDto: EditOrCreateVacancyDto): Result<VacancyNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.post(Vacancy.Create())
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }
}