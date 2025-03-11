package ru.nativespeakers.data.user

import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.model.VacancyNetwork
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDataSource: UserDataSource
) {
    suspend fun findUserById(id: Long): Result<StaffNetwork?> {
        return userDataSource.findUserById(id)
    }

    suspend fun findUserByQuery(query: String): Result<List<StaffNetwork>> {
        return userDataSource.findUserByQuery(query)
    }

    suspend fun findUsersByIds(ids: List<Long>): Result<List<StaffNetwork?>> {
        return userDataSource.findUsersByIds(ids)
    }

    suspend fun userInfo(): Result<StaffNetwork> {
        return userDataSource.userInfo()
    }

    suspend fun userInterviews(onlyActual: Boolean): Result<List<InterviewNetwork>> {
        return userDataSource.userInterviews(onlyActual)
    }

    suspend fun userTracks(onlyActual: Boolean): Result<List<TrackNetwork>> {
        return userDataSource.userTracks(onlyActual)
    }

    suspend fun userVacancies(onlyActual: Boolean): Result<List<VacancyNetwork>> {
        return userDataSource.userVacancies(onlyActual)
    }
}