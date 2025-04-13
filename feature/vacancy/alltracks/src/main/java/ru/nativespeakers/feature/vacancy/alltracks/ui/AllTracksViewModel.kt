package ru.nativespeakers.feature.vacancy.alltracks.ui

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
import ru.nativespeakers.core.model.TrackNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.data.track.TrackRepository
import ru.nativespeakers.data.vacancy.VacancyRepository

@HiltViewModel(assistedFactory = AllTracksViewModel.Factory::class)
internal class AllTracksViewModel @AssistedInject constructor(
    @Assisted private val vacancyId: Long,
    private val vacancyRepository: VacancyRepository,
    private val trackRepository: TrackRepository,
): ViewModel() {
    var tracks by mutableStateOf(BasicUiState(emptyList<TrackNetwork>()))
        private set

    init {
        loadTracks()
    }

    fun loadTracks() {
        viewModelScope.launch {
            tracks = tracks.copy(isLoading = true)

            val vacancyResult = vacancyRepository.findVacancyById(vacancyId)
            if (vacancyResult.isFailure) {
                tracks = tracks.copy(
                    isLoading = false,
                    isLoaded = tracks.isLoaded,
                    isError = true,
                )
                return@launch
            }

            val vacancy = vacancyResult.getOrThrow()

            val tracksResult = trackRepository.findById(vacancy.trackIds)
            if (tracksResult.isFailure) {
                tracks = tracks.copy(
                    isLoading = false,
                    isLoaded = tracks.isLoaded,
                    isError = true,
                )
                return@launch
            }

            tracks = tracks.copy(
                value = tracksResult.getOrThrow(),
                isLoading = false,
                isLoaded = true,
                isError = false,
            )
        }
    }

    @AssistedFactory
    internal interface Factory {
        fun create(vacancyId: Long): AllTracksViewModel
    }
}