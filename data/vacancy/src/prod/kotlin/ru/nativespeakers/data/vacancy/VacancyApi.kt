package ru.nativespeakers.data.vacancy

import io.ktor.resources.Resource
import kotlinx.serialization.SerialName

@Resource("/vacancy")
internal class Vacancy {
    @Resource("/{id}")
    internal class Id(val parent: Vacancy = Vacancy(), val id: Long)
    @Resource("/create")
    internal class Create(val parent: Vacancy = Vacancy())
    @Resource("/search")
    internal class Search(
        val parent: Vacancy = Vacancy(),
        val page: Int,
        @SerialName("page_size") val pageSize: Int,
        @SerialName("salary_lower_bound") val salaryLowerBound: Int,
    )
    @Resource("/edit/{id}")
    internal class EditById(val parent: Vacancy = Vacancy(), val id: Long)
}

@Resource("/vacancy")
internal class VacanciesByIds(val ids: List<Long>)