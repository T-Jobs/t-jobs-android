package ru.nativespeakers.data.candidate

import io.ktor.resources.Resource
import kotlinx.serialization.SerialName

@Resource("/candidate")
internal class Candidate {
    @Resource("/{id}")
    internal class Id(val parent: Candidate = Candidate(), val id: Long)

    @Resource("/search")
    internal class Search(
        val parent: Candidate = Candidate(),
        val page: Int,
        @SerialName("page_size")
        val pageSize: Int,
        @SerialName("salary_upper_bound")
        val salaryUpperBound: Int,
        val text: String?,
        val tagIds: List<Long>,
    )

    @Resource("/resume/{id}")
    internal class ResumeById(val parent: Candidate = Candidate(), val id: Long)

    @Resource("/resume")
    internal class ResumesByIds(val parent: Candidate = Candidate(), val ids: List<Long>)
}

@Resource("/candidate")
internal class CandidatesByIds(val ids: List<Long>)