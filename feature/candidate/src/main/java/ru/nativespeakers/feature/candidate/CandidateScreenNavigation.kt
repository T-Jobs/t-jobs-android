package ru.nativespeakers.feature.candidate

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.candidate.ui.CandidateScreen

@Serializable
data class CandidateRoute(val candidateId: Long)

fun NavController.navigateToCandidate(
    candidateId: Long,
    navOptions: NavOptions? = null
) = navigate(CandidateRoute(candidateId), navOptions)

fun NavGraphBuilder.candidateScreen(
    navigateToVacancyWithId: (Long) -> Unit,
    navigateToResumeWithId: (Long) -> Unit,
    navigateToTrackWithId: (Long) -> Unit,
    navigateBack: () -> Unit,
) {
    composable<CandidateRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<CandidateRoute>()
        val candidateId = route.candidateId

        CandidateScreen(
            candidateId = candidateId,
            navigateToResumeWithId = navigateToResumeWithId,
            navigateToVacancyWithId = navigateToVacancyWithId,
            navigateToTrackWithId = navigateToTrackWithId,
            navigateBack = navigateBack,
        )
    }
}