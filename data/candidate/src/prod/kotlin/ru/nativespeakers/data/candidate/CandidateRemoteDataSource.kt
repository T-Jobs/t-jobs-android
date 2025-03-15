package ru.nativespeakers.data.candidate

import androidx.annotation.IntRange
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
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
        @IntRange(from = 1)
        page: Int,
        pageSize: Int,
        query: String?,
        salaryUpperBound: Int,
        tagIds: List<Long>
    ): Result<List<CandidateNetwork>> = withContext(ioDispatcher) {
        val url = Candidate.Search(
            page = page,
            pageSize = pageSize,
            text = query,
            salaryUpperBound = salaryUpperBound,
            tagIds = tagIds
        )

        val response = httpClient.get(url)
        when (response.status) {
            HttpStatusCode.OK -> Result.success(response.body())
            else -> Result.failure(Exception())
        }
    }

    override suspend fun findById(id: Long): Result<CandidateNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.get(Candidate.Id(id = id))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun findById(ids: List<Long>): Result<List<CandidateNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(CandidatesByIds(ids))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun resumeById(id: Long): Result<ResumeNetwork> =
        withContext(ioDispatcher) {
            val response = httpClient.get(Candidate.ResumeById(id = id))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }

    override suspend fun resumeById(ids: List<Long>): Result<List<ResumeNetwork>> =
        withContext(ioDispatcher) {
            val response = httpClient.get(Candidate.ResumesByIds(ids = ids))
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception())
            }
        }
}