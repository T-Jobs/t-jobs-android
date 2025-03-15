package ru.nativespeakers.data.candidate

import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.model.ResumeNetwork

interface CandidateDataSource {
    suspend fun searchByQuery(
        page: Int,
        pageSize: Int,
        query: String?,
        salaryUpperBound: Int,
        tagIds: List<Long>,
    ): Result<List<CandidateNetwork>>
    suspend fun findById(id: Long): Result<CandidateNetwork>
    suspend fun findById(ids: List<Long>): Result<List<CandidateNetwork>>
    suspend fun resumeById(id: Long): Result<ResumeNetwork>
    suspend fun resumeById(ids: List<Long>): Result<List<ResumeNetwork>>
}