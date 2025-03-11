package ru.nativespeakers.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import ru.nativespeakers.data.auth.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    authRepository: AuthRepository,
) : ViewModel() {
    val roles = authRepository.isAuthenticated
        .distinctUntilChanged()
        .mapLatest { authenticated ->
            if (authenticated) {
                val userRolesResult = authRepository.roles()
                if (userRolesResult.isSuccess) {
                    userRolesResult.getOrThrow()
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}