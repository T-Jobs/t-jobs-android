package ru.nativespeakers.feature.home.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import ru.nativespeakers.core.model.AppRole
import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.model.TagNetwork
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.model.VacancyNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.core.ui.candidate.CandidateCardUiState
import ru.nativespeakers.core.ui.interview.InterviewCardUiState
import ru.nativespeakers.core.ui.interview.PersonAndPhotoUiState
import ru.nativespeakers.core.ui.paging.PagingDataUiState
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.core.ui.track.TrackCardUiState
import ru.nativespeakers.core.ui.vacancy.VacancyCardUiState
import ru.nativespeakers.data.candidate.CandidateRepository
import ru.nativespeakers.data.tag.TagRepository
import ru.nativespeakers.data.track.TrackRepository
import ru.nativespeakers.data.user.UserRepository
import ru.nativespeakers.data.vacancy.VacancyRepository
import javax.inject.Inject

@Immutable
data class FiltersUiState(
    val salary: Int? = null,
    val tags: List<TagNetwork> = emptyList(),
)

enum class AvailableSearchTab(val index: Int) {
    CANDIDATES(0), VACANCIES(1)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val trackRepository: TrackRepository,
    private val candidateRepository: CandidateRepository,
    private val tagRepository: TagRepository,
    private val vacancyRepository: VacancyRepository,
) : ViewModel() {
    var personAndPhotoUiState by mutableStateOf(BasicUiState(PersonAndPhotoUiState()))
        private set

    var relevantInterviewsUiState by mutableStateOf(BasicUiState(emptyList<InterviewCardUiState>()))
        private set

    var relevantVacanciesUiState by mutableStateOf(BasicUiState(emptyList<VacancyCardUiState>()))
        private set

    var relevantTracksUiState by mutableStateOf(BasicUiState(emptyList<TrackCardUiState>()))
        private set

    var availableFiltersUiState by mutableStateOf(BasicUiState(FiltersUiState()))
        private set

    var selectedFiltersUiState by mutableStateOf(BasicUiState(FiltersUiState()))
        private set

    var currentSearchTabSelected by mutableStateOf(AvailableSearchTab.CANDIDATES)
        private set

    private var searchQuery by mutableStateOf("")

    val searchCandidatesUiState = PagingDataUiState<CandidateCardUiState>()
    val searchVacanciesUiState = PagingDataUiState<VacancyCardUiState>()

    init {
        viewModelScope.launch {
            loadUserInfo().join()
            if (personAndPhotoUiState.isError) return@launch

            loadRelevantInterviews()
        }

        observeSearchCandidates()
        observeSearchVacancies()
    }

    fun updateSearchQuery(value: String) {
        searchQuery = value
    }

    fun updateSearchTabCategory(value: AvailableSearchTab) {
        currentSearchTabSelected = value
    }

    fun updateCurrentSearchFilters(value: FiltersUiState) {
        selectedFiltersUiState = selectedFiltersUiState.copy(value = value)
    }

    private fun observeSearchCandidates() {
        viewModelScope.launch {
            snapshotFlow {
                if (currentSearchTabSelected != AvailableSearchTab.CANDIDATES) {
                    return@snapshotFlow flowOf(PagingData.empty())
                }

                candidateRepository.searchByQuery(
                    query = searchQuery,
                    salaryUpperBound = selectedFiltersUiState.value.salary,
                    tagIds = selectedFiltersUiState.value.tags.map { it.id }
                )
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
            }
                .flatMapLatest { it }
                .map { pagingData -> pagingData.map { it.toCandidateCardUiState() } }
                .collectLatest { searchCandidatesUiState.updateData(it) }
        }
    }

    private fun observeSearchVacancies() {
        viewModelScope.launch {
            snapshotFlow {
                if (currentSearchTabSelected != AvailableSearchTab.VACANCIES) {
                    return@snapshotFlow flowOf(PagingData.empty())
                }

                vacancyRepository.searchForVacancies(
                    query = searchQuery,
                    salaryLowerBound = selectedFiltersUiState.value.salary,
                    tagIds = selectedFiltersUiState.value.tags.map { it.id }
                )
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
            }
                .flatMapLatest { it }
                .mapLatest { pagingData ->
                    pagingData.map { vacancy ->
                        val candidatesIds = vacancy.appliedCandidatesIds
                        val staffIds = vacancy.staffIds

                        val staffResultFuture = async {
                            userRepository.findUsersByIds(staffIds)
                        }
                        val candidatesResultFuture = async {
                            candidateRepository.findById(candidatesIds)
                        }

                        val staffResult = staffResultFuture.await()
                        val candidatesResult = candidatesResultFuture.await()
                        if (staffResult.isFailure || candidatesResult.isFailure) {
                            return@map VacancyCardUiState()
                        }

                        val staff = staffResult.getOrThrow()
                        val hrs = staff.filter { AppRole.HR in it.roles }
                        val teamLeads = staff.filter { AppRole.TEAM_LEAD in it.roles }

                        mapToVacancyCardUiState(
                            vacancyNetwork = vacancy,
                            candidates = candidatesResult.getOrThrow(),
                            hrs = hrs,
                            teamLeads = teamLeads
                        )
                    }
                }
                .collectLatest { searchVacanciesUiState.updateData(it) }
        }
    }

    fun loadUserInfo() = viewModelScope.launch {
        personAndPhotoUiState = personAndPhotoUiState.copy(isLoading = true)

        val userInfoResult = userRepository.userInfo()
        val newValue = if (userInfoResult.isSuccess) {
            val userInfo = userInfoResult.getOrThrow()
            PersonAndPhotoUiState(
                name = userInfo.name,
                surname = userInfo.surname,
                photoUrl = userInfo.photoUrl
            )
        } else personAndPhotoUiState.value

        personAndPhotoUiState = personAndPhotoUiState.copy(
            value = newValue,
            isLoading = false,
            isLoaded = personAndPhotoUiState.isLoaded || userInfoResult.isSuccess,
            isError = userInfoResult.isFailure
        )
    }

    fun loadRelevantInterviews() {
        viewModelScope.launch {
            relevantInterviewsUiState = relevantInterviewsUiState.copy(isLoading = true)

            val interviewsResult = userRepository.userInterviews(onlyRelevant = true)
            if (interviewsResult.isFailure) {
                relevantInterviewsUiState = relevantInterviewsUiState.copy(
                    isLoading = false,
                    isError = true
                )
                return@launch
            }

            val relevantInterviews = interviewsResult.getOrThrow()

            val tracksIds = relevantInterviews.map { it.trackId }
            val tracksResult = trackRepository.findById(tracksIds)
            if (tracksResult.isFailure) {
                relevantInterviewsUiState = relevantInterviewsUiState.copy(
                    isLoading = false,
                    isError = true
                )
                return@launch
            }

            val tracks = tracksResult.getOrThrow()

            relevantInterviewsUiState = relevantInterviewsUiState.copy(
                value = relevantInterviews.zip(tracks) { interview, track ->
                    mapToInterviewCardUiState(personAndPhotoUiState.value, interview, track)
                },
                isLoading = false,
                isLoaded = true,
                isError = false
            )
        }
    }

    fun loadRelevantVacancies() {
        viewModelScope.launch {
            relevantVacanciesUiState = relevantVacanciesUiState.copy(isLoading = true)

            val relevantVacanciesResult = userRepository.userVacancies(onlyRelevant = true)
            if (relevantVacanciesResult.isFailure) {
                relevantVacanciesUiState = relevantVacanciesUiState.copy(
                    isError = true,
                    isLoading = false
                )
                return@launch
            }

            val relevantVacancies = relevantVacanciesResult.getOrThrow()
            val candidatesIds = relevantVacancies.map { it.appliedCandidatesIds }
            val staffIds = relevantVacancies.map { it.staffIds }

            val candidatesResultDeferred = async {
                candidatesIds.map { candidateRepository.findById(it) }
            }
            val staffResultDeferred = async {
                staffIds.map { userRepository.findUsersByIds(it) }
            }

            val candidatesResult = candidatesResultDeferred.await()
            val staffResult = staffResultDeferred.await()
            if (candidatesResult.any { it.isFailure } || staffResult.any { it.isFailure }) {
                relevantVacanciesUiState = relevantVacanciesUiState.copy(
                    isError = true,
                    isLoading = false
                )
                return@launch
            }

            val candidates = candidatesResult.map { it.getOrThrow() }
            val staff = staffResult.map { it.getOrThrow() }
            val hrs = staff.map { it.filter { staff -> AppRole.HR in staff.roles } }
            val teamLeads = staff.map { it.filter { staff -> AppRole.TEAM_LEAD in staff.roles } }

            relevantVacanciesUiState = relevantVacanciesUiState.copy(
                value = relevantVacancies.mapIndexed { i, vacancyNetwork ->
                    mapToVacancyCardUiState(
                        vacancyNetwork = vacancyNetwork,
                        candidates = candidates[i],
                        teamLeads = teamLeads[i],
                        hrs = hrs[i]
                    )
                },
                isLoading = false,
                isLoaded = true,
                isError = false
            )
        }
    }

    fun loadRelevantTracks() {
        viewModelScope.launch {
            relevantTracksUiState = relevantTracksUiState.copy(isLoading = true)

            val relevantTracksResult = userRepository.userTracks(onlyRelevant = true)
            if (relevantTracksResult.isFailure) {
                relevantTracksUiState = relevantTracksUiState.copy(
                    isLoading = false,
                    isError = true
                )
                return@launch
            }

            val relevantTracks = relevantTracksResult.getOrThrow()
            relevantTracksUiState = relevantTracksUiState.copy(
                value = relevantTracks.map { it.toTrackCardUiState() },
                isLoaded = true,
                isLoading = false,
                isError = false
            )
        }
    }

    fun loadAvailableTags() {
        viewModelScope.launch {
            availableFiltersUiState = availableFiltersUiState.copy(isLoading = true)

            val availableTagsResult = tagRepository.availableTags()
            val newValue = if (availableTagsResult.isSuccess) {
                availableTagsResult.getOrThrow()
            } else {
                availableFiltersUiState.value.tags
            }

            availableFiltersUiState = availableFiltersUiState.copy(
                value = FiltersUiState(
                    tags = newValue
                ),
                isLoading = false,
                isLoaded = availableFiltersUiState.isLoaded || availableTagsResult.isSuccess,
                isError = availableTagsResult.isFailure
            )
        }
    }
}

private fun CandidateNetwork.toCandidateCardUiState() =
    CandidateCardUiState(
        id = id,
        candidate = this.toPersonAndPhotoUiState(),
        vacancyCount = tracks.size + appliedVacancies.size
    )

private fun mapToInterviewCardUiState(
    personAndPhotoUiState: PersonAndPhotoUiState,
    interview: InterviewNetwork,
    track: TrackNetwork
) = InterviewCardUiState(
    interviewId = interview.id,
    interviewName = interview.interviewType.name,
    interviewerUiState = personAndPhotoUiState,
    candidateUiState = PersonAndPhotoUiState(
        name = track.candidate.name,
        surname = track.candidate.surname,
        photoUrl = track.candidate.photoUrl
    ),
    status = interview.status,
    date = interview.datePicked
)

private fun mapToVacancyCardUiState(
    vacancyNetwork: VacancyNetwork,
    candidates: List<CandidateNetwork>,
    teamLeads: List<StaffNetwork>,
    hrs: List<StaffNetwork>,
) = VacancyCardUiState(
    id = vacancyNetwork.id,
    name = vacancyNetwork.name,
    city = vacancyNetwork.town,
    firstTwoTags = vacancyNetwork.tags.take(2).map { it.name },
    firstTwoHrs = hrs
        .take(2)
        .map { it.toPersonAndPhotoUiState() },
    hrsCount = hrs.size,
    firstTwoTeamLeads = teamLeads
        .take(2)
        .map { it.toPersonAndPhotoUiState() },
    teamLeadsCount = teamLeads.size,
    firstTwoCandidates = candidates
        .take(2)
        .map { it.toPersonAndPhotoUiState() },
    candidatesCount = candidates.size,
    salaryLowerBoundRub = vacancyNetwork.salaryMin,
    salaryHigherBoundRub = vacancyNetwork.salaryMax
)

private fun TrackNetwork.toTrackCardUiState() =
    TrackCardUiState(
        id = id,
        hr = hr.toPersonAndPhotoUiState(),
        candidate = candidate.toPersonAndPhotoUiState(),
        vacancy = vacancy.name,
        interviewsCount = interviewsIds.size,
        lastInterviewStatus = lastStatus
    )