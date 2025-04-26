package ru.nativespeakers.feature.track

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.track.ui.TrackScreen

@Serializable
data class TrackRoute(val trackId: Long)

fun NavController.navigateToTrack(
    trackId: Long,
    navOptions: NavOptions? = null,
) = navigate(route = TrackRoute(trackId), navOptions)

fun NavGraphBuilder.trackScreen(
    navigateBack: () -> Unit,
    navigateToVacancyWithId: (Long) -> Unit,
    navigateToInterviewWithId: (Long) -> Unit,
) {
    composable<TrackRoute> { backStackEntry ->
        val trackRoute = backStackEntry.toRoute<TrackRoute>()
        val trackId = trackRoute.trackId
        TrackScreen(
            trackId = trackId,
            navigateBack = navigateBack,
            navigateToVacancyWithId = navigateToVacancyWithId,
            navigateToInterviewWithId = navigateToInterviewWithId,
        )
    }
}