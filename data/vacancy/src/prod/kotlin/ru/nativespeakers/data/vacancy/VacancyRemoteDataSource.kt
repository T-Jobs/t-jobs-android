package ru.nativespeakers.data.vacancy

import androidx.annotation.IntRange
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
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
            val response = httpClient.get("/vacancy/$id")
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findVacancyById(ids: List<Long>): Result<List<VacancyNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/vacancy") {
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

    override suspend fun searchForVacancies(
        @IntRange(from = 0) page: Int,
        pageSize: Int,
        salaryLowerBound: Int?,
        query: String,
        tagIds: List<Long>
    ): Result<List<VacancyNetwork>> = withContext(ioDispatcher) {
        val response = httpClient.get("/vacancy/search") {
            url {
                with(parameters) {
                    append("page", page.toString())
                    append("page_size", pageSize.toString())

                    if (salaryLowerBound != null) {
                        append("salary_lower_bound", salaryLowerBound.toString())
                    }

                    if (query.isNotBlank()) {
                        append("text", query)
                    }

                    if (tagIds.isNotEmpty()) {
                        append("tag_ids", tagIds.joinToString(separator = ","))
                    }
                }
            }
        }

        when (response.status) {
            HttpStatusCode.OK -> Result.success(response.body())
            else -> Result.failure(Exception())
        }
    }

    override suspend fun editVacancyById(
        id: Long,
        editOrCreateVacancyDto: EditOrCreateVacancyDto
    ): Result<VacancyNetwork> = withContext(ioDispatcher) {
        val response = httpClient.post("/vacancy/edit/$id") {
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
            val response = httpClient.post("/vacancy/create") {
                contentType(ContentType.Application.Json)
                setBody(editOrCreateVacancyDto)
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }
}