package ru.nativespeakers.feature.profile.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.core.model.StaffNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.data.auth.AuthRepository
import ru.nativespeakers.data.user.UserRepository
import javax.inject.Inject

private val EmptyUser = StaffNetwork(
    id = 0,
    name = "",
    surname = "",
    photoUrl = null,
    tracksIds = emptyList(),
    interviewTypeNetworks = emptyList(),
    vacanciesIds = emptyList(),
    roles = emptyList(),
    interviewsIds = emptyList(),
    isInterviewModeOn = false,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    var userInfo by mutableStateOf(BasicUiState(EmptyUser))
        private set

    init {
        loadUserInfo()
    }

    fun updateInterviewMode(value: Boolean) {
        userInfo = userInfo.copy(
            value = userInfo.value.copy(isInterviewModeOn = value)
        )

        viewModelScope.launch {
            val result = userRepository.toggleInterviewerMode(value)
            if (result.isFailure) {
                userInfo = userInfo.copy(
                    value = userInfo.value.copy(isInterviewModeOn = !value)
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            userInfo = userInfo.copy(isLoading = true)

            val result = userRepository.userInfo()
            val newValue = if (result.isSuccess) {
                result.getOrThrow()
            } else {
                userInfo.value
            }

            userInfo = userInfo.copy(
                value = newValue,
                isLoading = false,
                isLoaded = result.isSuccess || userInfo.isLoaded,
                isError = result.isFailure
            )
        }
    }
}