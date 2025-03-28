package ru.nativespeakers.feature.competencies

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.competencies.ui.CompetenciesScreen
import ru.nativespeakers.feature.profile.ui.ProfileViewModel

@Serializable
object CompetenciesRoute

fun NavController.navigateToCompetencies(navOptions: NavOptions? = null) =
    navigate(route = CompetenciesRoute, navOptions)

fun NavGraphBuilder.competenciesScreen(navController: NavController) {
    composable<CompetenciesRoute> {
        val viewModel = navController.previousBackStackEntry?.let {
            hiltViewModel<ProfileViewModel>(it)
        } ?: hiltViewModel()

        CompetenciesScreen(
            profileViewModel = viewModel,
            navigateBack = navController::popBackStack
        )
    }
}