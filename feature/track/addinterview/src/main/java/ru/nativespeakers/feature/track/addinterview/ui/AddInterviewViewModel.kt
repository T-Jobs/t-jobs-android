package ru.nativespeakers.feature.track.addinterview.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import ru.nativespeakers.core.model.AppRole
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.core.ui.interview.PersonAndPhotoUiState
import ru.nativespeakers.data.interview.InterviewRepository
import ru.nativespeakers.data.user.UserRepository
import ru.nativespeakers.feature.track.common.InterviewCreateState
import javax.inject.Inject

@HiltViewModel
internal class AddInterviewViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val interviewRepository: InterviewRepository,
) : ViewModel() {
    var candidate by mutableStateOf(PersonAndPhotoUiState())
        private set

    var interviewCreateUiState by mutableStateOf(InterviewCreateState())
        private set

    var searchInterviewers by mutableStateOf(BasicUiState(emptyList<StaffNetwork>()))
        private set

    var searchInterviewTypes by mutableStateOf(BasicUiState(emptyList<InterviewTypeNetwork>()))
        private set

    init {
        loadInterviewers()
        loadAllInterviewTypes()
    }

    fun setAutoInterviewer() {
        interviewCreateUiState = interviewCreateUiState.copy(
            interviewer = null
        )
    }

    fun updateLink(link: String?) {
        if (link == null) return

        interviewCreateUiState = interviewCreateUiState.copy(
            link = link
        )
    }

    fun updateDate(date: LocalDateTime?) {
        interviewCreateUiState = interviewCreateUiState.copy(
            date = date
        )
    }

    fun updateInterviewer(interviewer: StaffNetwork) {
        interviewCreateUiState = interviewCreateUiState.copy(
            interviewer = interviewer
        )
    }

    fun updateInterviewType(interviewType: InterviewTypeNetwork) {
        interviewCreateUiState = interviewCreateUiState.copy(
            interviewType = interviewType
        )
    }

    fun updateSearchInterviewTypes(query: String) {
        searchInterviewTypes = searchInterviewTypes.copy(isLoading = true)

        viewModelScope.launch {
            val result = interviewRepository.searchInterviewTypeByName(query)
            val newValue = if (result.isSuccess) result.getOrThrow() else searchInterviewTypes.value

            searchInterviewTypes = searchInterviewTypes.copy(
                value = newValue,
                isLoading = false,
                isError = result.isFailure,
                isLoaded = searchInterviewTypes.isLoaded || result.isSuccess
            )
        }
    }

    fun updateCandidate(candidate: PersonAndPhotoUiState) {
        this.candidate = candidate
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
                isLoaded = searchInterviewers.isLoaded || result.isSuccess,
            )
        }
    }

    fun loadInterviewers() {
        viewModelScope.launch {
            val result = userRepository.findUsersByQuery("")
            if (result.isSuccess) {
                val staff = result.getOrThrow()
                val interviewers = staff.filter { AppRole.INTERVIEWER in it.roles }
                searchInterviewers = searchInterviewers.copy(value = interviewers)
            }
        }
    }

    fun loadAllInterviewTypes() {
        viewModelScope.launch {
            val result = interviewRepository.searchInterviewTypeByName("")
            if (result.isSuccess) {
                searchInterviewTypes = searchInterviewTypes.copy(
                    value = result.getOrThrow()
                )
            }
        }
    }
}