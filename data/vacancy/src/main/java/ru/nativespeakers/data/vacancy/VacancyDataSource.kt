package ru.nativespeakers.data.vacancy

import ru.nativespeakers.core.model.VacancyNetwork
import ru.nativespeakers.data.vacancy.dto.EditOrCreateVacancyDto

interface VacancyDataSource {
    suspend fun findVacancyById(id: Long): Result<VacancyNetwork>
    suspend fun findVacancyById(ids: List<Long>): Result<List<VacancyNetwork>>
    suspend fun searchForVacancies(
        page: Int,
        pageSize: Int,
        salaryLowerBound: Int?,
        query: String,
        tagIds: List<Long>
    ): Result<List<VacancyNetwork>>
    suspend fun editVacancyById(id: Long, editOrCreateVacancyDto: EditOrCreateVacancyDto): Result<VacancyNetwork>
    suspend fun createVacancy(editOrCreateVacancyDto: EditOrCreateVacancyDto): Result<VacancyNetwork>
}