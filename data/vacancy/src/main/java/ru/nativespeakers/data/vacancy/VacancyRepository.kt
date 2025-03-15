package ru.nativespeakers.data.vacancy

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.nativespeakers.core.model.VacancyNetwork
import ru.nativespeakers.data.paging.PagingDataSource
import ru.nativespeakers.data.vacancy.dto.EditOrCreateVacancyDto
import javax.inject.Inject
import javax.inject.Singleton

private object VacancyDefaults {
    val PagingConfig = PagingConfig(pageSize = 20)
}

@Singleton
class VacancyRepository @Inject constructor(
    private val vacancyDataSource: VacancyDataSource,
) {
    suspend fun findVacancyById(id: Long): Result<VacancyNetwork> {
        return vacancyDataSource.findVacancyById(id)
    }

    suspend fun findVacancyById(ids: List<Long>): Result<List<VacancyNetwork>> {
        return vacancyDataSource.findVacancyById(ids)
    }

    fun searchForVacancies(salaryLowerBound: Int): Flow<PagingData<VacancyNetwork>> {
        val pagingConfig = VacancyDefaults.PagingConfig
        return Pager(config = pagingConfig) {
            val pageSize = pagingConfig.pageSize
            PagingDataSource(vacancyDataSource) { page ->
                searchForVacancies(
                    page = page,
                    pageSize = pageSize,
                    salaryLowerBound = salaryLowerBound
                )
            }
        }.flow
    }

    suspend fun editVacancyById(id: Long, editOrCreateVacancyDto: EditOrCreateVacancyDto): Result<VacancyNetwork> {
        return vacancyDataSource.editVacancyById(id, editOrCreateVacancyDto)
    }

    suspend fun createVacancy(editOrCreateVacancyDto: EditOrCreateVacancyDto): Result<VacancyNetwork> {
        return vacancyDataSource.createVacancy(editOrCreateVacancyDto)
    }
}