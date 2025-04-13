package ru.nativespeakers.data.user

import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.model.VacancyNetwork

interface UserDataSource {
    suspend fun findUserById(id: Long): Result<StaffNetwork>
    suspend fun findUsersByIds(ids: List<Long>): Result<List<StaffNetwork>>
    suspend fun findUsersByQuery(query: String): Result<List<StaffNetwork>>
    suspend fun userInfo(): Result<StaffNetwork>
    suspend fun userInterviews(onlyRelevant: Boolean): Result<List<InterviewNetwork>>
    suspend fun userTracks(onlyRelevant: Boolean): Result<List<TrackNetwork>>
    suspend fun userVacancies(onlyRelevant: Boolean): Result<List<VacancyNetwork>>
    suspend fun setInterviewerMode(value: Boolean): Result<Unit>
    suspend fun addCompetency(interviewTypeId: Long): Result<Unit>
    suspend fun deleteCompetency(interviewTypeId: Long): Result<Unit>
    suspend fun followVacancy(vacancyId: Long): Result<Unit>
    suspend fun unfollowVacancy(vacancyId: Long): Result<Unit>
}