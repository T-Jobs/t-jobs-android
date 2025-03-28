package ru.nativespeakers.tjob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.nativespeakers.core.designsystem.TJobTheme
import ru.nativespeakers.core.ui.LocalAppRoles
import ru.nativespeakers.feature.auth.ui.AuthViewModel
import ru.nativespeakers.feature.auth.LoginRoute
import ru.nativespeakers.feature.auth.loginScreen
import ru.nativespeakers.feature.auth.navigateToLogin
import ru.nativespeakers.feature.competencies.competenciesScreen
import ru.nativespeakers.feature.competencies.navigateToCompetencies
import ru.nativespeakers.feature.filters.filtersScreen
import ru.nativespeakers.feature.filters.navigateToFilters
import ru.nativespeakers.feature.home.homeScreen
import ru.nativespeakers.feature.home.navigateToHome
import ru.nativespeakers.feature.profile.navigateToProfile
import ru.nativespeakers.feature.profile.profileScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TJobApp()
        }
    }
}

@Composable
fun TJobApp() {
    TJobTheme {
        val navController = rememberNavController()
        val authViewModel = hiltViewModel<AuthViewModel>()
        val roles by authViewModel.roles.collectAsStateWithLifecycle()

        LaunchedEffect(roles) {
            if (roles.isNotEmpty()) {
                val currentDestination = navController.currentDestination?.id
                if (currentDestination != null) {
                    navController.navigateToHome(
                        navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setPopUpTo(
                                destinationId = currentDestination,
                                inclusive = true
                            )
                            .build()
                    )
                } else {
                    navController.navigateToHome(
                        navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                    )
                }
            } else {
                val currentDestination = navController.currentDestination?.id
                if (currentDestination != null) {
                    navController.navigateToLogin(
                        navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setPopUpTo(
                                destinationId = currentDestination,
                                inclusive = true
                            )
                            .build()
                    )
                } else {
                    navController.navigateToLogin(
                        navOptions = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .build()
                    )
                }
            }
        }

        CompositionLocalProvider(
            LocalAppRoles provides roles
        ) {
            NavHost(
                navController = navController,
                startDestination = LoginRoute
            ) {
                loginScreen()
                homeScreen(
                    navigateToProfile = navController::navigateToProfile,
                    navigateToVacancyWithId = {},
                    navigateToInterviewWithId = {},
                    navigateToCandidateWithId = {},
                    navigateToTrackWithId = {},
                    navigateToFilters = navController::navigateToFilters
                )
                filtersScreen(navController)
                profileScreen(
                    navigateBack = navController::popBackStack,
                    navigateToCompetenciesScreen = navController::navigateToCompetencies,
                    navigateToCreateVacancyClick = {}
                )
                competenciesScreen(navController)
            }
        }
    }
}