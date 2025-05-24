package ru.nativespeakers.feature.resume.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.core.model.CandidateNetwork
import ru.nativespeakers.core.model.ResumeNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.data.candidate.CandidateRepository

@HiltViewModel(assistedFactory = ResumeViewModel.Factory::class)
internal class ResumeViewModel @AssistedInject constructor(
    @Assisted private val resumeId: Long,
    private val candidateRepository: CandidateRepository,
) : ViewModel() {
    var resume by mutableStateOf(BasicUiState<ResumeNetwork?>(null))
        private set

    var candidate by mutableStateOf(BasicUiState<CandidateNetwork?>(null))
        private set

    val isLoading
        get() = resume.isLoading || candidate.isLoading
    val isError
        get() = resume.isError || candidate.isError
    val isLoaded
        get() = resume.isLoaded && candidate.isLoaded

    init {
        loadData()
    }

    fun loadData() {
        loadResume().invokeOnCompletion { loadCandidate() }
    }

    private fun loadCandidate() {
        viewModelScope.launch {
            resume.value?.let {
                candidate = candidate.copy(isLoading = true)

                val result = candidateRepository.findById(it.candidateId)
                candidate = candidate.copy(
                    value = result.getOrNull(),
                    isLoading = false,
                    isError = result.isFailure,
                    isLoaded = candidate.isLoaded || result.isSuccess,
                )
            }
        }
    }

    private fun loadResume() = viewModelScope.launch {
        resume = resume.copy(isLoading = true)

        val result = candidateRepository.resumeById(resumeId)
        resume = resume.copy(
            value = result.getOrNull(),
            isLoaded = resume.isLoaded || result.isSuccess,
            isLoading = false,
            isError = result.isFailure,
        )
    }

    @AssistedFactory
    internal interface Factory {
        fun create(resumeId: Long): ResumeViewModel
    }
}