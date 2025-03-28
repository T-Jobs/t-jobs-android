package ru.nativespeakers.feature.competencies.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.data.interview.InterviewRepository
import javax.inject.Inject

@HiltViewModel
internal class CompetenciesViewModel @Inject constructor(
    private val interviewRepository: InterviewRepository,
) : ViewModel() {
    var searchInterviewTypes by mutableStateOf(BasicUiState(emptyList<InterviewTypeNetwork>()))
        private set

    init {
        loadAllInterviewTypes()
    }

    private fun loadAllInterviewTypes() {
        viewModelScope.launch {
            val result = interviewRepository.searchInterviewTypeByName("")
            if (result.isSuccess) {
                searchInterviewTypes = searchInterviewTypes.copy(
                    value = result.getOrThrow()
                )
            }
        }
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
}