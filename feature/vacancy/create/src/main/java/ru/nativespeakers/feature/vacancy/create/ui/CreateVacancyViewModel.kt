package ru.nativespeakers.feature.vacancy.create.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.core.model.InterviewTypeNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.data.interview.InterviewRepository
import ru.nativespeakers.data.tag.TagRepository
import ru.nativespeakers.data.vacancy.VacancyRepository
import ru.nativespeakers.data.vacancy.dto.EditOrCreateVacancyDto
import ru.nativespeakers.feature.vacancy.common.EditVacancyUiState
import javax.inject.Inject

@HiltViewModel
class CreateVacancyViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val vacancyRepository: VacancyRepository,
    private val interviewRepository: InterviewRepository,
) : ViewModel() {
    var editVacancyUiState by mutableStateOf(BasicUiState(EditVacancyUiState()))
        private set

    var selectedTags = mutableStateListOf<Long>()
        private set

    val selectedInterviews = mutableStateListOf<InterviewTypeNetwork>()

    var searchInterviewTypes by mutableStateOf(BasicUiState(emptyList<InterviewTypeNetwork>()))
        private set

    var vacancyCreated by mutableStateOf(false)
        private set

    init {
        loadData()
    }

    fun removeInterview(id: Long) {
        selectedInterviews.removeIf { it.id == id }
    }

    fun reorderInterviews(from: Int, to: Int) {
        with(selectedInterviews) {
            val fromNormalized = from.coerceIn(indices)
            val toNormalized = to.coerceIn(indices)
            add(toNormalized, removeAt(fromNormalized))
        }
    }

    fun updateName(value: String) {
        editVacancyUiState = editVacancyUiState.copy(
            value = editVacancyUiState.value.copy(name = value)
        )
    }

    fun updateDescription(value: String) {
        editVacancyUiState = editVacancyUiState.copy(
            value = editVacancyUiState.value.copy(description = value)
        )
    }

    fun updateCity(value: String) {
        editVacancyUiState = editVacancyUiState.copy(
            value = editVacancyUiState.value.copy(city = value)
        )
    }

    fun onTagClick(tagId: Long) {
        if (tagId in selectedTags) {
            selectedTags -= tagId
        } else {
            selectedTags += tagId
        }
    }

    fun onSearchInterviewTypeClick(interview: InterviewTypeNetwork) {
        if (interview in selectedInterviews) {
            selectedInterviews -= interview
        } else {
            selectedInterviews += interview
        }
    }

    fun createVacancy(salaryLowerBound: Int, salaryHigherBound: Int) {
        viewModelScope.launch {
            val result = vacancyRepository.createVacancy(toDto(salaryLowerBound, salaryHigherBound))
            vacancyCreated = result.isSuccess
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

    fun loadData() {
        loadAvailableTags()
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

    private fun loadAvailableTags() {
        editVacancyUiState = editVacancyUiState.copy(isLoading = true)

        viewModelScope.launch {
            val tagsResult = tagRepository.availableTags()
            if (tagsResult.isFailure) {
                editVacancyUiState = editVacancyUiState.copy(
                    isLoading = false,
                    isLoaded = editVacancyUiState.isLoaded,
                    isError = true
                )
                return@launch
            }

            val tags = tagsResult.getOrThrow()
            editVacancyUiState = editVacancyUiState.copy(
                value = editVacancyUiState.value.copy(
                    tags = tags.groupBy { it.category },
                ),
                isLoading = false,
                isLoaded = true,
                isError = false,
            )
        }
    }

    private fun toDto(salaryLowerBound: Int, salaryHigherBound: Int) = EditOrCreateVacancyDto(
        name = editVacancyUiState.value.name,
        description = editVacancyUiState.value.description,
        salaryMin = salaryLowerBound,
        salaryMax = salaryHigherBound,
        town = editVacancyUiState.value.city,
        interviews = selectedInterviews.map { it.id },
        tags = selectedTags
    )
}