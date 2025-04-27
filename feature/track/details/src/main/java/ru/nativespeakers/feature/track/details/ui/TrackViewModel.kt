package ru.nativespeakers.feature.track.details.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import kotlinx.serialization.InternalSerializationApi
import ru.nativespeakers.core.model.AppRole
import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.core.ui.interview.InterviewCardUiState
import ru.nativespeakers.core.ui.interview.interviewCardUiState
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.core.ui.vacancy.VacancyCardUiState
import ru.nativespeakers.core.ui.vacancy.vacancyCardUiState
import ru.nativespeakers.data.candidate.CandidateRepository
import ru.nativespeakers.data.interview.InterviewRepository
import ru.nativespeakers.data.track.TrackRepository
import ru.nativespeakers.data.user.UserRepository
import ru.nativespeakers.feature.track.common.InterviewCreateState
import ru.nativespeakers.feature.track.common.toDto

private fun defaultStaff() = StaffNetwork(
    id = 0,
    name = "",
    surname = "",
    tracksIds = emptyList(),
    interviewTypeNetworks = emptyList(),
    vacanciesIds = emptyList(),
    roles = emptyList(),
    interviewsIds = emptyList(),
    isInterviewModeOn = false,
)

private fun defaultCandidate() = CandidateNetwork(
    id = 0,
    name = "",
    surname = "",
    photoUrl = null,
    tgId = "",
    town = "",
    resumesIds = emptyList(),
    tracks = emptyList(),
    appliedVacancies = emptyList(),
)

@Immutable
data class TrackDetailsUiState(
    val id: Long = 0,
    val candidateNetwork: CandidateNetwork = defaultCandidate(),
    val hr: StaffNetwork = defaultStaff(),
    val vacancy: VacancyCardUiState = VacancyCardUiState(),
    val interviews: List<InterviewCardUiState> = emptyList(),
    val finished: Boolean = false,
)

@HiltViewModel(assistedFactory = TrackViewModel.Factory::class)
class TrackViewModel @AssistedInject constructor(
    @Assisted private val trackId: Long,
    private val trackRepository: TrackRepository,
    private val interviewRepository: InterviewRepository,
    private val candidateRepository: CandidateRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    var trackDetailsUiState by mutableStateOf(BasicUiState(TrackDetailsUiState()))
        private set

    var searchHrs by mutableStateOf(BasicUiState(emptyList<StaffNetwork>()))
        private set

    var currentSelectedHrId by mutableLongStateOf(-1L)

    init {
        loadData()
    }

    @OptIn(InternalSerializationApi::class)
    fun createInterview(createState: InterviewCreateState) {
        viewModelScope.launch {
            val state = trackDetailsUiState.value

            val result = interviewRepository.createInterview(createState.toDto(trackId))
            if (result.isFailure) return@launch

            val interview = result.getOrThrow()
            val trackResult = async { trackRepository.findById(trackId) }
            val interviewerResult = interview.interviewerId?.let {
                async { userRepository.findUserById(it) }
            }

            if (trackResult.await().isFailure || interviewerResult?.await()?.isFailure == true) {
                return@launch
            }

            val interviewer = interviewerResult?.await()?.getOrThrow()
            val track = trackResult.await().getOrThrow()

            val cardState = interviewCardUiState(
                interviewer = interviewer?.toPersonAndPhotoUiState(),
                interview = interview,
                track = track
            )

            trackDetailsUiState = trackDetailsUiState.copy(
                value = state.copy(
                    interviews = state.interviews + cardState
                )
            )
        }
    }

    fun changeHr(hrId: Long) {
        if (hrId == currentSelectedHrId) return

        val previousHrId = currentSelectedHrId
        currentSelectedHrId = hrId

        viewModelScope.launch {
            val result = trackRepository.setHrForTrack(trackId, hrId)
            if (result.isFailure) {
                currentSelectedHrId = previousHrId
            }
        }
    }

    fun updateSearchHrs(query: String) {
        searchHrs = searchHrs.copy(isLoading = true)

        viewModelScope.launch {
            val result = userRepository.findUsersByQuery(query)
            val newValue = if (result.isSuccess) {
                result.getOrThrow().filter { AppRole.HR in it.roles }
            } else {
                searchHrs.value
            }

            searchHrs = searchHrs.copy(
                value = newValue,
                isLoading = false,
                isError = result.isFailure,
                isLoaded = searchHrs.isLoaded || result.isSuccess
            )
        }
    }

    fun removeInterviewById(id: Long) {
        val state = trackDetailsUiState.value

        trackDetailsUiState = trackDetailsUiState.copy(
            value = state.copy(
                interviews = state.interviews.filterNot { it.interviewId == id }
            )
        )

        viewModelScope.launch {
            val result = interviewRepository.deleteById(id)
            if (result.isFailure) {
                trackDetailsUiState = trackDetailsUiState.copy(
                    value = state.copy(
                        interviews = state.interviews
                    )
                )
            }
        }
    }

    fun finishTrack() {
        viewModelScope.launch {
            trackRepository.finishTrackById(trackId)
        }
    }

    fun loadData() {
        loadHrs()
        loadTrack()
    }

    private fun loadHrs() {
        viewModelScope.launch {
            val result = userRepository.findUsersByQuery("")
            if (result.isSuccess) {
                val staff = result.getOrThrow()
                val hrs = staff.filter { AppRole.HR in it.roles }
                searchHrs = searchHrs.copy(value = hrs)
            }
        }
    }

    private fun loadTrack() {
        viewModelScope.launch {
            trackDetailsUiState = trackDetailsUiState.copy(isLoading = true)

            val trackResult = trackRepository.findById(trackId)
            if (trackResult.isFailure) {
                trackDetailsUiState = trackDetailsUiState.copy(
                    isLoading = false,
                    isError = true,
                    isLoaded = trackDetailsUiState.isLoaded
                )
                return@launch
            }

            val track = trackResult.getOrThrow()

            val interviewsResult = async { interviewRepository.findById(track.interviewsIds) }
            val candidatesResult = async {
                candidateRepository.findById(track.vacancy.appliedCandidatesIds)
            }
            val staffResult = async { userRepository.findUsersByIds(track.vacancy.staffIds) }

            if (interviewsResult.await().isFailure || candidatesResult.await().isFailure || staffResult.await().isFailure) {
                trackDetailsUiState = trackDetailsUiState.copy(
                    isLoading = false,
                    isError = true,
                    isLoaded = trackDetailsUiState.isLoaded
                )
                return@launch
            }

            val interviews = interviewsResult.await().getOrThrow()
            val candidates = candidatesResult.await().getOrThrow()
            val staff = staffResult.await().getOrThrow()
            val hrs = staff.filter { AppRole.HR in it.roles }
            val teamLeads = staff.filter { AppRole.TEAM_LEAD in it.roles }

            val interviewersIds = interviews.mapNotNull { it.interviewerId }
            val interviewersResult = userRepository.findUsersByIds(interviewersIds)
            if (interviewersResult.isFailure) {
                trackDetailsUiState = trackDetailsUiState.copy(
                    isLoading = false,
                    isError = true,
                    isLoaded = trackDetailsUiState.isLoaded
                )
                return@launch
            }

            val interviewers = interviewersResult.getOrThrow()

            currentSelectedHrId = track.hr.id

            trackDetailsUiState = trackDetailsUiState.copy(
                value = trackDetailsUiState(
                    track = track,
                    interviews = interviews,
                    vacancyCandidates = candidates,
                    teamLeads = teamLeads,
                    hrs = hrs,
                    interviewers = interviewers,
                ),
                isLoading = false,
                isLoaded = true,
                isError = false,
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(trackId: Long): TrackViewModel
    }

    private fun trackDetailsUiState(
        track: TrackNetwork,
        interviews: List<InterviewNetwork>,
        vacancyCandidates: List<CandidateNetwork>,
        teamLeads: List<StaffNetwork>,
        hrs: List<StaffNetwork>,
        interviewers: List<StaffNetwork>,
    ) = TrackDetailsUiState(
        id = track.id,
        candidateNetwork = track.candidate,
        hr = track.hr,
        vacancy = vacancyCardUiState(
            vacancyNetwork = track.vacancy,
            candidates = vacancyCandidates,
            teamLeads = teamLeads,
            hrs = hrs,
        ),
        interviews = interviews.map { interview ->
            interviewCardUiState(
                interviewer = interviewers
                    .find { it.id == interview.interviewerId }
                    ?.toPersonAndPhotoUiState(),
                interview = interview,
                track = track,
            )
        },
        finished = track.finished,
    )
}