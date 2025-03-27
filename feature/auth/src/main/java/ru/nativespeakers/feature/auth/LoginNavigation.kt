package ru.nativespeakers.feature.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.auth.ui.LoginScreen

@Serializable
object LoginRoute

fun NavController.navigateToLogin(navOptions: NavOptions? = null) =
    navigate(route = LoginRoute, navOptions)

fun NavGraphBuilder.loginScreen() {
    composable<LoginRoute> {
        LoginScreen()
    }
}