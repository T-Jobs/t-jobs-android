package ru.nativespeakers.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.profile.ui.ProfileScreen

@Serializable
object ProfileRoute

fun NavController.navigateToProfile(navOptions: NavOptions? = null) =
    navigate(route = ProfileRoute, navOptions)

fun NavGraphBuilder.profileScreen(
    navigateBack: () -> Unit,
    navigateToCompetenciesScreen: () -> Unit,
    navigateToCreateVacancyClick: () -> Unit,
) {
    composable<ProfileRoute> {
        ProfileScreen(
            navigateBack = navigateBack,
            navigateToCompetenciesScreen = navigateToCompetenciesScreen,
            navigateToCreateVacancyClick = navigateToCreateVacancyClick
        )
    }
}