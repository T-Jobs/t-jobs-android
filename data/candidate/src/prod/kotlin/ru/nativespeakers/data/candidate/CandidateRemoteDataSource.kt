package ru.nativespeakers.data.candidate

import androidx.annotation.IntRange
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.nativespeakers.core.common.IoDispatcher
import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.model.ResumeNetwork
import javax.inject.Inject

internal class CandidateRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CandidateDataSource {
    override suspend fun searchByQuery(
        @IntRange(from = 0)
        page: Int,
        pageSize: Int,
        query: String,
        salaryUpperBound: Int?,
        tagIds: List<Long>
    ): Result<List<CandidateNetwork>> = withContext(ioDispatcher) {
        val response = httpClient.get("/candidate/search") {
            url {
                with(parameters) {
                    append("page", page.toString())
                    append("page_size", pageSize.toString())

                    if (query.isNotBlank()) {
                        append("text", query)
                    }

                    if (salaryUpperBound != null) {
                        append("salary_upper_bound", salaryUpperBound.toString())
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

    override suspend fun findById(id: Long): Result<CandidateNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/candidate/${id}")
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findById(ids: List<Long>): Result<List<CandidateNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/candidate") {
                url {
                    if (ids.isNotEmpty()) {
                        parameters.append("ids", ids.joinToString(separator = ","))
                    }
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun resumeById(id: Long): Result<ResumeNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/candidate/${id}")
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun resumeById(ids: List<Long>): Result<List<ResumeNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get("/candidate/resume") {
                url {
                    if (ids.isNotEmpty()) {
                        parameters.append("ids", ids.joinToString(separator = ","))
                    }
                }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }
}