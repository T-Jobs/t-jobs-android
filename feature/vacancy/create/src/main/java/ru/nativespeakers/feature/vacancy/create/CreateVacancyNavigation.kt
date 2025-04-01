package ru.nativespeakers.feature.vacancy.create

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.vacancy.create.ui.CreateVacancyScreen

@Serializable
object CreateVacancyRoute

fun NavController.navigateToCreateVacancy(navOptions: NavOptions? = null) =
    navigate(route = CreateVacancyRoute, navOptions)

fun NavGraphBuilder.createVacancyScreen(
    navigateBack: () -> Unit,
) {
    composable<CreateVacancyRoute> {
        CreateVacancyScreen(
            navigateBack = navigateBack
        )
    }
}