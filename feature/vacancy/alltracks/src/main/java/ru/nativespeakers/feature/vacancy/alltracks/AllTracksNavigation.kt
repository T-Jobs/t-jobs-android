package ru.nativespeakers.feature.vacancy.alltracks

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.vacancy.alltracks.ui.AllTracksScreen

@Serializable
data class AllTracksRoute(val vacancyId: Long)

fun NavController.navigateToAllTracks(
    vacancyId: Long,
    navOptions: NavOptions? = null,
) = navigate(route = AllTracksRoute(vacancyId), navOptions)

fun NavGraphBuilder.allTracksScreen(
    navigateBack: () -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
) {
    composable<AllTracksRoute> { backStackEntry ->
        val vacancyDetailsRoute = backStackEntry.toRoute<AllTracksRoute>()
        AllTracksScreen(
            vacancyId = vacancyDetailsRoute.vacancyId,
            navigateBack = navigateBack,
            navigateToTrackWithId = navigateToTrackWithId,
        )
    }
}