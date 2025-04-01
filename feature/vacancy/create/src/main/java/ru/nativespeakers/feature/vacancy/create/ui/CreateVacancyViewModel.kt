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
import ru.nativespeakers.core.model.TagCategoryNetwork
import ru.nativespeakers.core.model.TagNetwork
import ru.nativespeakers.core.ui.BasicUiState
import ru.nativespeakers.data.interview.InterviewRepository
import ru.nativespeakers.data.tag.TagRepository
import ru.nativespeakers.data.vacancy.VacancyRepository
import ru.nativespeakers.data.vacancy.dto.EditOrCreateVacancyDto
import javax.inject.Inject

@HiltViewModel
class CreateVacancyViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val vacancyRepository: VacancyRepository,
    private val interviewRepository: InterviewRepository,
) : ViewModel() {
    var name by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var city by mutableStateOf("")
        private set

    var availableTags by mutableStateOf(BasicUiState(emptyMap<TagCategoryNetwork, List<TagNetwork>>()))
        private set

    var selectedTags = mutableStateListOf<Long>()
        private set

    val selectedInterviews = mutableStateListOf<InterviewTypeNetwork>()

    var searchInterviewTypes by mutableStateOf(BasicUiState(emptyList<InterviewTypeNetwork>()))
        private set

    var vacancyCreated by mutableStateOf(false)
        private set

    init {
        loadAvailableTags()
        loadAllInterviewTypes()
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
        name = value
    }

    fun updateDescription(value: String) {
        description = value
    }

    fun updateCity(value: String) {
        city = value
    }

    fun addTag(tagId: Long) {
        selectedTags.add(tagId)
    }

    fun removeTag(tagId: Long) {
        selectedTags.remove(tagId)
    }

    fun addInterview(interview: InterviewTypeNetwork) {
        selectedInterviews.add(interview)
    }

    fun createVacancy(salaryLowerBound: Float, salaryHigherBound: Float) {
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

    fun loadAvailableTags() {
        availableTags = availableTags.copy(isLoading = true)

        viewModelScope.launch {
            val tagsResult = tagRepository.availableTags()
            if (tagsResult.isFailure) {
                availableTags = availableTags.copy(
                    isLoading = false,
                    isLoaded = availableTags.isLoaded,
                    isError = true
                )
                return@launch
            }

            val tags = tagsResult.getOrThrow()
            availableTags = availableTags.copy(
                value = tags.groupBy { it.category },
                isLoading = false,
                isLoaded = true,
                isError = false
            )
        }
    }

    private fun toDto(salaryLowerBound: Float, salaryHigherBound: Float) = EditOrCreateVacancyDto(
        name = name,
        description = description,
        salaryMin = salaryLowerBound.toInt(),
        salaryMax = salaryHigherBound.toInt(),
        town = city,
        interviews = selectedInterviews.map { it.id },
        tags = selectedTags
    )
}