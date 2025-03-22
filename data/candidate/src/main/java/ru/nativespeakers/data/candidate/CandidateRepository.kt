package ru.nativespeakers.data.candidate

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.model.ResumeNetwork
import ru.nativespeakers.data.paging.PagingDataSource
import javax.inject.Inject
import javax.inject.Singleton

private object PagingDefaults {
    val PagingConfig = PagingConfig(pageSize = 20)
}

@Singleton
class CandidateRepository @Inject constructor(
    private val candidateDataSource: CandidateDataSource,
) {
    fun searchByQuery(
        query: String,
        salaryUpperBound: Int?,
        tagIds: List<Long>,
    ): Flow<PagingData<CandidateNetwork>> {
        val pagingConfig = PagingDefaults.PagingConfig
        return Pager(pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(candidateDataSource) { page ->
                searchByQuery(
                    page = page,
                    pageSize = pageSize,
                    query = query,
                    salaryUpperBound = salaryUpperBound,
                    tagIds = tagIds
                )
            }
        }.flow
    }

    suspend fun findById(id: Long): Result<CandidateNetwork> {
        return candidateDataSource.findById(id)
    }

    suspend fun findById(ids: List<Long>): Result<List<CandidateNetwork>> {
        return candidateDataSource.findById(ids)
    }

    suspend fun resumeById(id: Long): Result<ResumeNetwork> {
        return candidateDataSource.resumeById(id)
    }

    suspend fun resumeById(ids: List<Long>): Result<List<ResumeNetwork>> {
        return candidateDataSource.resumeById(ids)
    }
}