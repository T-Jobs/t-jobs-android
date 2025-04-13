package ru.nativespeakers.feature.vacancy.details

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.vacancy.details.ui.VacancyDetailsScreen

@Serializable
data class VacancyDetailsRoute(val vacancyId: Long)

fun NavController.navigateToVacancyDetails(
    vacancyId: Long,
    navOptions: NavOptions? = null,
) = navigate(route = VacancyDetailsRoute(vacancyId), navOptions)

fun NavGraphBuilder.vacancyDetailsScreen(
    navigateBack: () -> Unit,
    navigateToShowAllTracksScreen: (Long) -> Unit,
    navigateToShowAllAppliedCandidatesScreen: (Long) -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
    navigateToRelevantResumeScreen: () -> Unit,
    navigateToEditVacancyScreenWithId: (Long) -> Unit,
) {
    composable<VacancyDetailsRoute> { backStackEntry ->
        val vacancyDetailsRoute = backStackEntry.toRoute<VacancyDetailsRoute>()
        val vacancyId = vacancyDetailsRoute.vacancyId
        VacancyDetailsScreen(
            vacancyId = vacancyId,
            navigateBack = navigateBack,
            navigateToShowAllTracksScreen = { navigateToShowAllTracksScreen(vacancyId) },
            navigateToShowAllAppliedCandidatesScreen = {
                navigateToShowAllAppliedCandidatesScreen(vacancyId)
            },
            navigateToTrackWithId = navigateToTrackWithId,
            navigateToRelevantResumeScreen = navigateToRelevantResumeScreen,
            navigateToEditVacancyScreen = { navigateToEditVacancyScreenWithId(vacancyId) },
        )
    }
}