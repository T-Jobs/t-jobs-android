package ru.nativespeakers.feature.vacancy.appliedcandidates.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.data.candidate.CandidateRepository
import ru.nativespeakers.data.track.TrackRepository
import ru.nativespeakers.data.vacancy.VacancyRepository

@HiltViewModel(assistedFactory = AppliedCandidatesViewModel.Factory::class)
internal class AppliedCandidatesViewModel @AssistedInject constructor(
    @Assisted private val vacancyId: Long,
    private val vacancyRepository: VacancyRepository,
    private val candidateRepository: CandidateRepository,
    private val trackRepository: TrackRepository,
) : ViewModel() {
    var candidates by mutableStateOf(BasicUiState(emptyList<CandidateNetwork>()))
        private set

    val candidatesInProcess = mutableStateListOf<Long>()
    var isErrorWhileApplyingCandidate by mutableStateOf(false)
        private set

    init {
        loadCandidates()
    }

    fun applyCandidateWithId(candidateId: Long) {
        viewModelScope.launch {
            candidatesInProcess += candidateId

            val result = trackRepository.approveApplication(candidateId, vacancyId)
            if (result.isFailure) {
                isErrorWhileApplyingCandidate = true
                return@launch
            }

            candidates = candidates.copy(
                value = candidates.value.filterNot { it.id == candidateId },
            )
            candidatesInProcess -= candidateId
        }
    }

    fun onShowErrorWhileApplyingCandidateMessage() {
        isErrorWhileApplyingCandidate = false
    }

    fun loadCandidates() {
        viewModelScope.launch {
            candidates = candidates.copy(isLoading = true)

            val vacancyResult = vacancyRepository.findVacancyById(vacancyId)
            if (vacancyResult.isFailure) {
                candidates = candidates.copy(
                    isLoading = false,
                    isLoaded = candidates.isLoaded,
                    isError = true,
                )
                return@launch
            }

            val vacancy = vacancyResult.getOrThrow()

            val appliedCandidatesResult = candidateRepository.findById(vacancy.appliedCandidatesIds)
            if (appliedCandidatesResult.isFailure) {
                candidates = candidates.copy(
                    isLoading = false,
                    isLoaded = candidates.isLoaded,
                    isError = true,
                )
                return@launch
            }

            candidates = candidates.copy(
                value = appliedCandidatesResult.getOrThrow(),
                isLoading = false,
                isLoaded = true,
                isError = false,
            )
        }
    }

    @AssistedFactory
    internal interface Factory {
        fun create(vacancyId: Long): AppliedCandidatesViewModel
    }
}