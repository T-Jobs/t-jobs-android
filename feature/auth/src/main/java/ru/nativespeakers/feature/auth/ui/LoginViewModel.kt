package ru.nativespeakers.feature.auth.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.data.auth.AuthRepository
import ru.nativespeakers.data.auth.dto.LoginDto
import ru.nativespeakers.data.auth.exception.UnauthorizedException
import javax.inject.Inject

@Immutable
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val credentialsInvalid: Boolean = false,
    val isLoggedIn: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun updateEmail(email: String) {
        loginUiState = loginUiState.copy(email = email)
    }

    fun updatePassword(password: String) {
        loginUiState = loginUiState.copy(password = password)
    }

    fun credentialsInvalidMessageShown() {
        loginUiState = loginUiState.copy(credentialsInvalid = false)
    }

    fun login() {
        viewModelScope.launch {
            loginUiState = loginUiState.copy(isLoading = true)

            val result = authRepository.login(loginUiState.toLoginDto())
            when {
                result.isSuccess -> {
                    loginUiState = loginUiState.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                }
                result.isFailure -> {
                    loginUiState = loginUiState.copy(
                        isLoading = false,
                        credentialsInvalid = result.exceptionOrNull()!! is UnauthorizedException,
                    )
                }
            }
        }
    }
}

private fun LoginUiState.toLoginDto() =
    LoginDto(
        email = email,
        password = password,
    )