package ru.nativespeakers.feature.candidate.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.nativespeakers.core.model.AppRole
import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.core.ui.resume.ResumeCardUiState
import ru.nativespeakers.core.ui.resume.toResumeCardUiState
import ru.nativespeakers.core.ui.track.TrackCardUiState
import ru.nativespeakers.core.ui.track.toTrackCardUiState
import ru.nativespeakers.core.ui.vacancy.VacancyCardUiState
import ru.nativespeakers.core.ui.vacancy.vacancyCardUiState
import ru.nativespeakers.data.candidate.CandidateRepository
import ru.nativespeakers.data.track.TrackRepository
import ru.nativespeakers.data.user.UserRepository
import ru.nativespeakers.data.vacancy.VacancyRepository

@HiltViewModel(assistedFactory = CandidateViewModel.Factory::class)
internal class CandidateViewModel @AssistedInject constructor(
    @Assisted private val candidateId: Long,
    private val candidateRepository: CandidateRepository,
    private val vacancyRepository: VacancyRepository,
    private val userRepository: UserRepository,
    private val trackRepository: TrackRepository,
) : ViewModel() {
    var candidate by mutableStateOf(BasicUiState<CandidateNetwork?>(null))
        private set

    var resumes by mutableStateOf(BasicUiState<List<ResumeCardUiState>>(emptyList()))
        private set

    var briefs by mutableStateOf(BasicUiState<List<VacancyCardUiState>>(emptyList()))
        private set

    var tracks by mutableStateOf(BasicUiState<List<TrackCardUiState>>(emptyList()))
        private set

    var vacanciesToInviteCandidate by mutableStateOf(BasicUiState<List<VacancyCardUiState>>(emptyList()))
        private set

    init {
        loadData()
    }

    fun inviteCandidateOnVacancy(vacancyId: Long) {
        viewModelScope.launch {
            val result: Result<Unit> = Result.success(Unit)
            if (result.isSuccess) {
                vacanciesToInviteCandidate = vacanciesToInviteCandidate.copy(
                    value = vacanciesToInviteCandidate.value.filterNot { it.id == vacancyId }
                )
            }
        }
    }

    fun applyCandidate(vacancyId: Long) {
        viewModelScope.launch {
            val result = trackRepository.approveApplication(candidateId, vacancyId)
            if (result.isSuccess) {
                loadBriefs()
            }
        }
    }

    fun rejectCandidate(vacancyId: Long) {
        viewModelScope.launch {


        }
    }

    fun loadData() {
        loadCandidate().invokeOnCompletion { loadResumes() }
    }

    fun loadTracks() {
        viewModelScope.launch {
            candidate.value?.let {
                tracks = tracks.copy(isLoading = true)

                val trackIds = it.tracks
                if (trackIds.isEmpty()) {
                    tracks = tracks.copy(
                        value = emptyList(),
                        isLoading = false,
                        isLoaded = true,
                        isError = false,
                    )
                    return@launch
                }

                val tracksResult = trackRepository.findById(trackIds)
                if (tracksResult.isFailure) {
                    tracks = tracks.copy(
                        isLoading = false,
                        isLoaded = tracks.isLoaded,
                        isError = true,
                    )
                    return@launch
                }

                val trackCards = tracksResult.getOrThrow().map { it.toTrackCardUiState() }
                tracks = tracks.copy(
                    value = trackCards,
                    isLoading = false,
                    isError = false,
                    isLoaded = true,
                )
            }
        }
    }

    fun loadVacanciesToInviteCandidate() {
        viewModelScope.launch {
            candidate.value?.let {
                vacanciesToInviteCandidate = vacanciesToInviteCandidate.copy(isLoading = true)

                val hrVacancies = userRepository.userVacancies(true)
                if (hrVacancies.isFailure) {
                    vacanciesToInviteCandidate = vacanciesToInviteCandidate.copy(
                        isLoading = false,
                        isLoaded = vacanciesToInviteCandidate.isLoaded,
                        isError = true,
                    )
                    return@launch
                }

                val vacancies = hrVacancies.getOrThrow()
                val actualVacanciesToInvite = vacancies.filter {
                    hrVacancy -> hrVacancy.id !in it.appliedVacancies
                }

                val candidateIds = actualVacanciesToInvite.flatMap { it.appliedCandidatesIds }
                val staffIds = actualVacanciesToInvite.flatMap { it.staffIds }

                val staffResult = async { userRepository.findUsersByIds(staffIds) }
                val candidateResult = async { candidateRepository.findById(candidateIds) }
                if (staffResult.await().isFailure || candidateResult.await().isFailure) {
                    vacanciesToInviteCandidate = vacanciesToInviteCandidate.copy(
                        isLoading = false,
                        isLoaded = briefs.isLoaded,
                        isError = true,
                    )
                    return@launch
                }

                val staffs = staffResult.await().getOrThrow()
                val hrs = staffs
                    .filter { AppRole.HR in it.roles }
                    .associateBy { it.id }

                val teamLeads = staffs
                    .filter { AppRole.TEAM_LEAD in it.roles }
                    .associateBy { it.id }

                val candidates = candidateResult.await().getOrThrow().associateBy { it.id }

                val vacancyCards = actualVacanciesToInvite.map { vacancy ->
                    vacancyCardUiState(
                        vacancyNetwork = vacancy,
                        candidates = vacancy.appliedCandidatesIds.map { candidates[it]!! },
                        teamLeads = vacancy.staffIds
                            .mapNotNull { teamLeads.getOrElse(it) { null } },
                        hrs = vacancy.staffIds
                            .mapNotNull { hrs.getOrElse(it) { null } },
                    )
                }

                vacanciesToInviteCandidate = vacanciesToInviteCandidate.copy(
                    value = vacancyCards,
                    isLoading = false,
                    isLoaded = true,
                    isError = false,
                )
            }
        }
    }

    fun loadBriefs() {
        viewModelScope.launch {
            candidate.value?.let {
                briefs = briefs.copy(isLoading = true)

                val appliedVacancyIds = it.appliedVacancies
                if (appliedVacancyIds.isEmpty()) {
                    briefs = briefs.copy(
                        value = emptyList(),
                        isLoading = false,
                        isLoaded = true,
                        isError = false,
                    )
                    return@launch
                }

                val result = vacancyRepository.findVacancyById(appliedVacancyIds)
                if (result.isFailure) {
                    briefs = briefs.copy(
                        isLoading = false,
                        isLoaded = briefs.isLoaded,
                        isError = true,
                    )
                    return@launch
                }

                val vacancies = result.getOrThrow()
                val candidateIds = vacancies.flatMap { it.appliedCandidatesIds }
                val staffIds = vacancies.flatMap { it.staffIds }

                val staffResult = async { userRepository.findUsersByIds(staffIds) }
                val candidateResult = async { candidateRepository.findById(candidateIds) }
                if (staffResult.await().isFailure || candidateResult.await().isFailure) {
                    briefs = briefs.copy(
                        isLoading = false,
                        isLoaded = briefs.isLoaded,
                        isError = true,
                    )
                    return@launch
                }

                val staffs = staffResult.await().getOrThrow()
                val hrs = staffs
                    .filter { AppRole.HR in it.roles }
                    .associateBy { it.id }

                val teamLeads = staffs
                    .filter { AppRole.TEAM_LEAD in it.roles }
                    .associateBy { it.id }

                val candidates = candidateResult.await().getOrThrow().associateBy { it.id }

                val vacancyCards = vacancies.map { vacancy ->
                    vacancyCardUiState(
                        vacancyNetwork = vacancy,
                        candidates = vacancy.appliedCandidatesIds.map { candidates[it]!! },
                        teamLeads = vacancy.staffIds
                            .mapNotNull { teamLeads.getOrElse(it) { null } },
                        hrs = vacancy.staffIds
                            .mapNotNull { hrs.getOrElse(it) { null } },
                    )
                }

                briefs = briefs.copy(
                    value = vacancyCards,
                    isLoading = false,
                    isLoaded = true,
                    isError = false,
                )
            }
        }
    }

    fun loadCandidate() = viewModelScope.launch {
        candidate = candidate.copy(isLoading = true)

        val result = candidateRepository.findById(candidateId)
        val newValue = if (result.isSuccess) result.getOrThrow() else candidate.value

        candidate = candidate.copy(
            value = newValue,
            isLoading = false,
            isLoaded = candidate.isLoaded || result.isSuccess,
            isError = result.isFailure,
        )
    }

    fun loadResumes() {
        viewModelScope.launch {
            candidate.value?.let {
                resumes = resumes.copy(isLoading = true)

                val resumeIds = it.resumesIds
                if (resumeIds.isEmpty()) {
                    resumes = resumes.copy(
                        isLoading = false,
                        isLoaded = true,
                        isError = false,
                    )
                    return@launch
                }

                val result = candidateRepository.resumeById(resumeIds)
                val newValue = if (result.isSuccess) {
                    result.getOrThrow().map { it.toResumeCardUiState() }
                } else {
                    resumes.value
                }

                resumes = resumes.copy(
                    value = newValue,
                    isLoading = false,
                    isLoaded = result.isSuccess || resumes.isLoaded,
                    isError = result.isFailure,
                )
            }
        }
    }

    @AssistedFactory
    internal interface Factory {
        fun create(candidateId: Long): CandidateViewModel
    }
}