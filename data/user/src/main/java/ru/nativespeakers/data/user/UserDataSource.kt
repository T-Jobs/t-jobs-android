package ru.nativespeakers.data.user

import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.model.VacancyNetwork

interface UserDataSource {
    suspend fun findUserById(id: Long): Result<StaffNetwork?>
    suspend fun findUserByQuery(query: String): Result<List<StaffNetwork>>
    suspend fun findUsersByIds(ids: List<Long>): Result<List<StaffNetwork?>>
    suspend fun userInfo(): Result<StaffNetwork>
    suspend fun userInterviews(onlyActual: Boolean): Result<List<InterviewNetwork>>
    suspend fun userTracks(onlyActual: Boolean): Result<List<TrackNetwork>>
    suspend fun userVacancies(onlyActual: Boolean): Result<List<VacancyNetwork>>
}