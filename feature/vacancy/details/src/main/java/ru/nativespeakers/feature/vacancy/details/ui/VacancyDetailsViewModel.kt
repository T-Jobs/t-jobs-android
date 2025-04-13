package ru.nativespeakers.feature.vacancy.details.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.core.model.AppRole
import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.model.InterviewBaseNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.model.TagCategoryNetwork
import ru.nativespeakers.core.model.TagNetwork
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.model.VacancyNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.data.candidate.CandidateRepository
import ru.nativespeakers.data.interview.InterviewRepository
import ru.nativespeakers.data.track.TrackRepository
import ru.nativespeakers.data.user.UserRepository
import ru.nativespeakers.data.vacancy.VacancyRepository

@Immutable
internal data class VacancyDetailsUiState(
    val name: String = "",
    val salaryLowerBound: Int? = null,
    val salaryHigherBound: Int? = null,
    val description: String = "",
    val tags: Map<TagCategoryNetwork, List<TagNetwork>> = emptyMap(),
    val teamLeads: List<StaffNetwork> = emptyList(),
    val hrs: List<StaffNetwork> = emptyList(),
    val tracks: List<TrackNetwork> = emptyList(),
    val appliedCandidates: List<CandidateNetwork> = emptyList(),
    val baseTrack: List<InterviewBaseNetwork> = emptyList(),
    val followedByUser: Boolean = false,
)

@HiltViewModel(assistedFactory = VacancyDetailsViewModel.Factory::class)
internal class VacancyDetailsViewModel @AssistedInject constructor(
    @Assisted private val vacancyId: Long,
    private val vacancyRepository: VacancyRepository,
    private val userRepository: UserRepository,
    private val candidateRepository: CandidateRepository,
    private val trackRepository: TrackRepository,
    private val interviewRepository: InterviewRepository,
) : ViewModel() {
    var vacancyDetailsUiState by mutableStateOf(BasicUiState(VacancyDetailsUiState()))
        private set

    var appliedCandidatesInProgress = mutableStateListOf<Long>()
        private set

    init {
        loadData()
    }

    fun toggleFollowVacancy() {
        viewModelScope.launch {
            val state = vacancyDetailsUiState.value
            val result = if (state.followedByUser) {
                userRepository.unfollowVacancy(vacancyId)
            } else {
                userRepository.followVacancy(vacancyId)
            }

            if (result.isSuccess) {
                vacancyDetailsUiState = vacancyDetailsUiState.copy(
                    value = state.copy(
                        followedByUser = !state.followedByUser
                    )
                )
            }
        }
    }

    fun applyCandidateWithId(candidateId: Long) {
        viewModelScope.launch {
            appliedCandidatesInProgress += candidateId

            val result = trackRepository.approveApplication(
                candidateId = candidateId,
                vacancyId = vacancyId
            )

            if (result.isSuccess) {
                val state = vacancyDetailsUiState.value
                vacancyDetailsUiState = vacancyDetailsUiState.copy(
                    value = state.copy(
                        appliedCandidates = state.appliedCandidates.filterNot {
                            it.id == candidateId
                        }
                    )
                )
            }

            appliedCandidatesInProgress -= candidateId
        }
    }

    fun loadData() {
        viewModelScope.launch {
            vacancyDetailsUiState = vacancyDetailsUiState.copy(isLoading = true)

            val vacancyResult = vacancyRepository.findVacancyById(vacancyId)
            if (vacancyResult.isFailure) {
                vacancyDetailsUiState = vacancyDetailsUiState.copy(
                    isLoading = false,
                    isError = true,
                    isLoaded = vacancyDetailsUiState.isLoaded
                )
                return@launch
            }

            val vacancy = vacancyResult.getOrThrow()

            val staffResult = userRepository.findUsersByIds(vacancy.staffIds)
            if (staffResult.isFailure) {
                vacancyDetailsUiState = vacancyDetailsUiState.copy(
                    isLoading = false,
                    isError = true,
                    isLoaded = vacancyDetailsUiState.isLoaded
                )
                return@launch
            }

            val staff = staffResult.getOrThrow()
            val hrs = staff.filter { AppRole.HR in it.roles }
            val teamLeads = staff.filter { AppRole.TEAM_LEAD in it.roles }

            val candidatesResult = candidateRepository.findById(vacancy.appliedCandidatesIds)
            if (candidatesResult.isFailure) {
                vacancyDetailsUiState = vacancyDetailsUiState.copy(
                    isLoading = false,
                    isError = true,
                    isLoaded = vacancyDetailsUiState.isLoaded
                )
                return@launch
            }

            val candidates = candidatesResult.getOrThrow()

            val tracksResult = trackRepository.findById(vacancy.trackIds)
            if (tracksResult.isFailure) {
                vacancyDetailsUiState = vacancyDetailsUiState.copy(
                    isLoading = false,
                    isError = true,
                    isLoaded = vacancyDetailsUiState.isLoaded
                )
                return@launch
            }

            val tracks = tracksResult.getOrThrow()

            val baseTrackResult = interviewRepository.baseInterviewById(vacancy.interviewsBaseIds)
            if (baseTrackResult.isFailure) {
                vacancyDetailsUiState = vacancyDetailsUiState.copy(
                    isLoading = false,
                    isError = true,
                    isLoaded = vacancyDetailsUiState.isLoaded
                )
                return@launch
            }

            val baseTrack = baseTrackResult.getOrThrow()

            val userVacanciesResult = userRepository.userVacancies(false)
            if (userVacanciesResult.isFailure) {
                vacancyDetailsUiState = vacancyDetailsUiState.copy(
                    isLoading = false,
                    isError = true,
                    isLoaded = vacancyDetailsUiState.isLoaded
                )
                return@launch
            }

            val userVacancies = userVacanciesResult.getOrThrow()

            vacancyDetailsUiState = vacancyDetailsUiState.copy(
                value = toState(
                    vacancy = vacancy,
                    teamLeads = teamLeads,
                    hrs = hrs,
                    candidates = candidates,
                    tracks = tracks,
                    baseTrack = baseTrack,
                    userVacancies = userVacancies
                ),
                isLoading = false,
                isError = false,
                isLoaded = true
            )
        }
    }

    private fun toState(
        vacancy: VacancyNetwork,
        teamLeads: List<StaffNetwork>,
        hrs: List<StaffNetwork>,
        candidates: List<CandidateNetwork>,
        tracks: List<TrackNetwork>,
        baseTrack: List<InterviewBaseNetwork>,
        userVacancies: List<VacancyNetwork>,
    ) = VacancyDetailsUiState(
        name = vacancy.name,
        salaryLowerBound = vacancy.salaryMin,
        salaryHigherBound = vacancy.salaryMax,
        description = vacancy.description,
        tags = vacancy.tags.groupBy { it.category },
        teamLeads = teamLeads,
        hrs = hrs,
        tracks = tracks,
        appliedCandidates = candidates,
        baseTrack = baseTrack,
        followedByUser = userVacancies.any { it.id == vacancy.id },
    )

    @AssistedFactory
    internal interface Factory {
        fun create(vacancyId: Long): VacancyDetailsViewModel
    }
}