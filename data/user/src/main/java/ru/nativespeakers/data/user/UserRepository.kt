package ru.nativespeakers.data.user

import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.model.VacancyNetwork
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDataSource: UserDataSource,
) {
    suspend fun findUserById(id: Long): Result<StaffNetwork> {
        return userDataSource.findUserById(id)
    }

    suspend fun findUsersByIds(ids: List<Long>): Result<List<StaffNetwork>> {
        if (ids.isEmpty()) return Result.success(emptyList())

        return userDataSource.findUsersByIds(ids)
    }

    suspend fun findUsersByQuery(query: String): Result<List<StaffNetwork>> {
        return userDataSource.findUsersByQuery(query)
    }

    suspend fun userInfo(): Result<StaffNetwork> {
        return userDataSource.userInfo()
    }

    suspend fun userInterviews(onlyRelevant: Boolean): Result<List<InterviewNetwork>> {
        return userDataSource.userInterviews(onlyRelevant)
    }

    suspend fun userTracks(onlyRelevant: Boolean): Result<List<TrackNetwork>> {
        return userDataSource.userTracks(onlyRelevant)
    }

    suspend fun userVacancies(onlyRelevant: Boolean): Result<List<VacancyNetwork>> {
        return userDataSource.userVacancies(onlyRelevant)
    }

    suspend fun toggleInterviewerMode(value: Boolean): Result<Unit> {
        return userDataSource.setInterviewerMode(value)
    }

    suspend fun addCompetency(interviewTypeId: Long): Result<Unit> {
        return userDataSource.addCompetency(interviewTypeId)
    }

    suspend fun deleteCompetency(interviewTypeId: Long): Result<Unit> {
        return userDataSource.deleteCompetency(interviewTypeId)
    }

    suspend fun followVacancy(vacancyId: Long): Result<Unit> {
        return userDataSource.followVacancy(vacancyId)
    }
}