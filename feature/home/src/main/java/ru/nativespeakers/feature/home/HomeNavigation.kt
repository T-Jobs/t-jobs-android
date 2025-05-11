package ru.nativespeakers.feature.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.home.ui.HomeScreen

data class InitialSearchCandidatesFilters(
    val maxSalary: Int?,
    val tagIds: List<Long>,
)

@Serializable
data class HomeRoute(
    val maxSalary: Int?,
    val tagIds: List<Long>,
)

fun NavController.navigateToHome(
    initialSearchCandidatesFilters: InitialSearchCandidatesFilters? = null,
    navOptions: NavOptions? = null
) = navigate(
    route = HomeRoute(
        maxSalary = initialSearchCandidatesFilters?.maxSalary,
        tagIds = initialSearchCandidatesFilters?.tagIds.orEmpty(),
    ),
    navOptions
)

fun NavGraphBuilder.homeScreen(
    navigateToProfile: () -> Unit,
    navigateToVacancyWithId: (Long) -> Unit,
    navigateToInterviewWithId: (Long) -> Unit,
    navigateToCandidateWithId: (Long) -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
    navigateToFilters: () -> Unit,
) {
    composable<HomeRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<HomeRoute>()
        val filters = if (route.tagIds.isEmpty()) {
            null
        } else InitialSearchCandidatesFilters(
            maxSalary = route.maxSalary,
            tagIds = route.tagIds,
        )

        HomeScreen(
            navigateToProfile = navigateToProfile,
            navigateToVacancyWithId = navigateToVacancyWithId,
            navigateToInterviewWithId = navigateToInterviewWithId,
            navigateToCandidateWithId = navigateToCandidateWithId,
            navigateToTrackWithId = navigateToTrackWithId,
            navigateToFilters = navigateToFilters,
            initialFilters = filters,
        )
    }
}