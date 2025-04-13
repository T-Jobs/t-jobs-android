package ru.nativespeakers.feature.vacancy.edit

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.vacancy.edit.ui.EditVacancyScreen

@Serializable
data class EditVacancyRoute(val vacancyId: Long)

fun NavController.navigateToEditVacancy(
    vacancyId: Long,
    navOptions: NavOptions? = null,
) = navigate(route = EditVacancyRoute(vacancyId), navOptions)

fun NavGraphBuilder.editVacancyScreen(
    navigateBack: () -> Unit,
) {
    composable<EditVacancyRoute> { backStackEntry ->
        val vacancyDetailsRoute = backStackEntry.toRoute<EditVacancyRoute>()
        EditVacancyScreen(
            vacancyId = vacancyDetailsRoute.vacancyId,
            navigateBack = navigateBack,
        )
    }
}