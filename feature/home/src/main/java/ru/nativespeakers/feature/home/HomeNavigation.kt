package ru.nativespeakers.feature.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.home.ui.HomeScreen

@Serializable
object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) =
    navigate(route = HomeRoute, navOptions)

fun NavGraphBuilder.homeScreen(
    navigateToProfile: () -> Unit,
    navigateToVacancyWithId: (Long) -> Unit,
    navigateToInterviewWithId: (Long) -> Unit,
    navigateToCandidateWithId: (Long) -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
    navigateToFilters: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            navigateToProfile = navigateToProfile,
            navigateToVacancyWithId = navigateToVacancyWithId,
            navigateToInterviewWithId = navigateToInterviewWithId,
            navigateToCandidateWithId = navigateToCandidateWithId,
            navigateToTrackWithId = navigateToTrackWithId,
            navigateToFilters = navigateToFilters
        )
    }
}