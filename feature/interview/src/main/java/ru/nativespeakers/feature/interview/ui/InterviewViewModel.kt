package ru.nativespeakers.feature.interview.ui

import androidx.compose.runtime.Immutable
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
import kotlinx.datetime.LocalDateTime
import ru.nativespeakers.core.model.AppRole
import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.model.InterviewNetwork
import ru.nativespeakers.core.model.InterviewStatus
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.data.interview.InterviewRepository
import ru.nativespeakers.data.track.TrackRepository
import ru.nativespeakers.data.user.UserRepository

private fun defaultInterview() = InterviewNetwork(
    id = -1,
    interviewerId = null,
    interviewType = InterviewTypeNetwork(
        id = -1,
        name = "",
    ),
    trackId = -1,
    dateApproved = false,
    status = InterviewStatus.NONE,
    isAbleToSetTime = false,
)

private fun defaultStaff() = StaffNetwork(
    id = -1,
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
    id = -1,
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
data class FeedbackInputUiState(
    val positive: Boolean = true,
    val feedback: String = "",
)

@HiltViewModel(assistedFactory = InterviewViewModel.Factory::class)
internal class InterviewViewModel @AssistedInject constructor(
    @Assisted private val interviewId: Long,
    private val interviewRepository: InterviewRepository,
    private val userRepository: UserRepository,
    private val trackRepository: TrackRepository,
) : ViewModel() {
    var interview by mutableStateOf(BasicUiState(defaultInterview()))
        private set

    var candidate by mutableStateOf(BasicUiState(defaultCandidate()))
        private set

    var interviewer by mutableStateOf(BasicUiState<StaffNetwork?>(null))
        private set

    var user by mutableStateOf(BasicUiState(defaultStaff()))
        private set

    var feedbackUiState by mutableStateOf(FeedbackInputUiState())
        private set

    var searchInterviewers by mutableStateOf(BasicUiState(emptyList<StaffNetwork>()))
        private set

    val isLoading
        get() = interview.isLoading || candidate.isLoading || interviewer.isLoading || user.isLoading

    val isLoaded
        get() = interview.isLoaded && candidate.isLoaded && interviewer.isLoaded && user.isLoaded

    init {
        loadData()
    }

    fun sendFeedback() {
        viewModelScope.launch {
            val result = interviewRepository.setFeedback(
                interviewId,
                feedback = feedbackUiState.feedback,
                success = feedbackUiState.positive,
            )

            if (result.isSuccess) {
                loadInterviewAndMembers()
            }
        }
    }

    fun passCandidate() {
        viewModelScope.launch {
            val result = trackRepository.continueTrack(interview.value.trackId)
            if (result.isSuccess) {
                loadInterviewAndMembers()
            }
        }
    }

    fun finishTrack() {
        viewModelScope.launch {
            val result = trackRepository.finishTrackById(interview.value.trackId)
            if (result.isSuccess) {
                loadInterviewAndMembers()
            }
        }
    }

    fun rejectTime() {
        viewModelScope.launch {
            val result = interviewRepository.declineTime(interviewId)
            if (result.isSuccess) {
                loadInterviewAndMembers()
            }
        }
    }

    fun approveTime() {
        viewModelScope.launch {
            val result = interviewRepository.approveTime(interviewId)
            if (result.isSuccess) {
                loadInterviewAndMembers()
            }
        }
    }

    fun cancelInterview() {
        viewModelScope.launch {
            interviewRepository.deleteById(interviewId)
        }
    }

    fun rejectInterview() {
        viewModelScope.launch {
            val previousInterviewer = interviewer.value

            interviewer = interviewer.copy(value = null)

            val result = interviewRepository.setAutoInterviewer(interviewId)
            if (result.isFailure) {
                interviewer = interviewer.copy(value = previousInterviewer)
            }
        }
    }

    fun updateFeedbackStatus(positive: Boolean) {
        feedbackUiState = feedbackUiState.copy(positive = positive)
    }

    fun updateFeedback(value: String) {
        feedbackUiState = feedbackUiState.copy(feedback = value)
    }

    fun updateLink(link: String) {
        viewModelScope.launch {
            val previousLink = interview.value.link

            interview = interview.copy(
                value = interview.value.copy(link = link)
            )

            val result = interviewRepository.setLink(interviewId, link)
            if (result.isFailure) {
                interview = interview.copy(
                    value = interview.value.copy(link = previousLink)
                )
            }
        }
    }

    fun updateInterviewer(interviewer: StaffNetwork?) {
        viewModelScope.launch {
            val previousInterviewer = this@InterviewViewModel.interviewer

            this@InterviewViewModel.interviewer = this@InterviewViewModel.interviewer.copy(
                value = interviewer
            )

            val result = if (interviewer != null) {
                interviewRepository.setInterviewer(interviewId, interviewer.id)
            } else {
                interviewRepository.setAutoInterviewer(interviewId)
            }

            if (result.isFailure) {
                this@InterviewViewModel.interviewer = previousInterviewer
            }
        }
    }

    fun setAutoDate() {
        viewModelScope.launch {
            val previousDate = interview.value.datePicked

            interview = interview.copy(
                value = interview.value.copy(datePicked = null)
            )

            val result = interviewRepository.setAutoDate(interviewId)
            if (result.isFailure) {
                interview = interview.copy(
                    value = interview.value.copy(datePicked = previousDate)
                )
            }
        }
    }

    fun updateDate(date: LocalDateTime) {
        viewModelScope.launch {
            val previousDate = interview.value.datePicked

            interview = interview.copy(
                value = interview.value.copy(datePicked = date)
            )

            val result = interviewRepository.setDate(interviewId, date)
            if (result.isFailure) {
                interview = interview.copy(
                    value = interview.value.copy(datePicked = previousDate)
                )
            }
        }
    }

    fun updateSearchInterviewers(query: String) {
        searchInterviewers = searchInterviewers.copy(isLoading = true)

        viewModelScope.launch {
            val result = userRepository.findUsersByQuery(query)
            val newValue = if (result.isSuccess) {
                result.getOrThrow().filter { AppRole.INTERVIEWER in it.roles }
            } else {
                searchInterviewers.value
            }

            searchInterviewers = searchInterviewers.copy(
                value = newValue,
                isLoading = false,
                isError = result.isFailure,
                isLoaded = searchInterviewers.isLoaded || result.isSuccess
            )
        }
    }

    fun loadData() {
        loadInterviewAndMembers()
        loadInterviewers()
        loadUser()
    }

    private fun loadInterviewers() {
        viewModelScope.launch {
            val result = userRepository.findUsersByQuery("")
            if (result.isSuccess) {
                val staff = result.getOrThrow()
                val interviewers = staff.filter { AppRole.INTERVIEWER in it.roles }
                searchInterviewers = searchInterviewers.copy(value = interviewers)
            }
        }
    }

    private fun loadInterviewAndMembers() {
        viewModelScope.launch {
            interview = interview.copy(isLoading = true)
            candidate = candidate.copy(isLoading = true)
            interviewer = interviewer.copy(isLoading = true)

            val interviewResult = interviewRepository.findById(interviewId)
            if (interviewResult.isFailure) {
                interview = interview.copy(
                    isLoading = false,
                    isError = true,
                )
                return@launch
            }

            val interview = interviewResult.getOrThrow()

            this@InterviewViewModel.interview = this@InterviewViewModel.interview.copy(
                value = interview,
                isLoading = false,
                isError = false,
                isLoaded = true,
            )

            val trackResult = async { trackRepository.findById(interview.trackId) }
            val interviewerResult = async {
                interview.interviewerId?.let { userRepository.findUserById(it) } ?: Result.success(
                    null
                )
            }

            if (interviewerResult.await().isFailure) {
                interviewer = interviewer.copy(
                    isLoading = false,
                    isError = true,
                )
                return@launch
            }

            interviewer = interviewer.copy(
                value = interviewerResult.await().getOrThrow(),
                isLoading = false,
                isError = false,
                isLoaded = true,
            )

            if (trackResult.await().isFailure) {
                candidate = candidate.copy(
                    isLoading = false,
                    isError = true,
                )
                return@launch
            }

            candidate = candidate.copy(
                value = trackResult.await().getOrThrow().candidate,
                isLoading = false,
                isError = false,
                isLoaded = true,
            )
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            user = user.copy(isLoading = true)

            val userResult = userRepository.userInfo()
            user = user.copy(
                value = userResult.getOrDefault(user.value),
                isLoading = false,
                isError = userResult.isFailure,
                isLoaded = userResult.isSuccess || user.isLoaded,
            )
        }
    }

    @AssistedFactory
    internal interface Factory {
        fun create(interviewId: Long): InterviewViewModel
    }
}