package ru.nativespeakers.feature.filters

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.home.ui.HomeViewModel

@Serializable
object FiltersRoute

fun NavController.navigateToFilters(navOptions: NavOptions? = null) =
    navigate(route = FiltersRoute, navOptions)

fun NavGraphBuilder.filtersScreen(navController: NavController) {
    composable<FiltersRoute> {
        val viewModel = navController.previousBackStackEntry?.let {
            hiltViewModel<HomeViewModel>(it)
        } ?: hiltViewModel()

        FiltersScreen(
            homeViewModel = viewModel,
            navigateBack = navController::popBackStack
        )
    }
}