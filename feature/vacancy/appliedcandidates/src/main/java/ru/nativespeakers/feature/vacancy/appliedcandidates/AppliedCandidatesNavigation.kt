package ru.nativespeakers.feature.vacancy.appliedcandidates

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.vacancy.appliedcandidates.ui.AppliedCandidatesScreen

@Serializable
data class AppliedCandidatesRoute(val vacancyId: Long)

fun NavController.navigateToAppliedCandidates(
    vacancyId: Long,
    navOptions: NavOptions? = null,
) = navigate(route = AppliedCandidatesRoute(vacancyId), navOptions)

fun NavGraphBuilder.appliedCandidatesScreen(
    navigateBack: () -> Unit,
) {
    composable<AppliedCandidatesRoute> { backStackEntry ->
        val vacancyDetailsRoute = backStackEntry.toRoute<AppliedCandidatesRoute>()
        AppliedCandidatesScreen(
            vacancyId = vacancyDetailsRoute.vacancyId,
            navigateBack = navigateBack,
        )
    }
}