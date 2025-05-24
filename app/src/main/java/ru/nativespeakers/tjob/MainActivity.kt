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
import ru.nativespeakers.feature.candidate.candidateScreen
import ru.nativespeakers.feature.candidate.navigateToCandidate
import ru.nativespeakers.feature.competencies.competenciesScreen
import ru.nativespeakers.feature.competencies.navigateToCompetencies
import ru.nativespeakers.feature.filters.filtersScreen
import ru.nativespeakers.feature.filters.navigateToFilters
import ru.nativespeakers.feature.home.HomeRoute
import ru.nativespeakers.feature.home.homeScreen
import ru.nativespeakers.feature.home.navigateToHome
import ru.nativespeakers.feature.interview.interviewScreen
import ru.nativespeakers.feature.interview.navigateToInterview
import ru.nativespeakers.feature.profile.navigateToProfile
import ru.nativespeakers.feature.profile.profileScreen
import ru.nativespeakers.feature.track.addinterview.addInterviewScreen
import ru.nativespeakers.feature.track.addinterview.navigateToAddInterview
import ru.nativespeakers.feature.track.details.navigateToTrack
import ru.nativespeakers.feature.track.details.trackScreen
import ru.nativespeakers.feature.vacancy.alltracks.allTracksScreen
import ru.nativespeakers.feature.vacancy.alltracks.navigateToAllTracks
import ru.nativespeakers.feature.vacancy.appliedcandidates.appliedCandidatesScreen
import ru.nativespeakers.feature.vacancy.appliedcandidates.navigateToAppliedCandidates
import ru.nativespeakers.feature.vacancy.create.createVacancyScreen
import ru.nativespeakers.feature.vacancy.create.navigateToCreateVacancy
import ru.nativespeakers.feature.vacancy.details.navigateToVacancyDetails
import ru.nativespeakers.feature.vacancy.details.vacancyDetailsScreen
import ru.nativespeakers.feature.vacancy.edit.editVacancyScreen
import ru.nativespeakers.feature.vacancy.edit.navigateToEditVacancy

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
fun TJobApp() = TJobTheme {
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
                navigateToVacancyWithId = navController::navigateToVacancyDetails,
                navigateToInterviewWithId = navController::navigateToInterview,
                navigateToCandidateWithId = navController::navigateToCandidate,
                navigateToTrackWithId = navController::navigateToTrack,
                navigateToFilters = navController::navigateToFilters
            )

            filtersScreen(navController)

            profileScreen(
                navigateBack = navController::popBackStack,
                navigateToCompetenciesScreen = navController::navigateToCompetencies,
                navigateToCreateVacancyClick = navController::navigateToCreateVacancy
            )

            competenciesScreen(navController)

            createVacancyScreen(
                navigateBack = navController::popBackStack
            )

            vacancyDetailsScreen(
                navigateBack = navController::popBackStack,
                navigateToShowAllAppliedCandidatesScreen = navController::navigateToAppliedCandidates,
                navigateToTrackWithId = navController::navigateToTrack,
                navigateToShowAllTracksScreen = navController::navigateToAllTracks,
                navigateToEditVacancyScreenWithId = navController::navigateToEditVacancy,
                navigateToRelevantResumeScreen = {
                    navController.navigateToHome(
                        initialSearchCandidatesFilters = it,
                        navOptions = NavOptions.Builder()
                            .setPopUpTo(HomeRoute::class, inclusive = true)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                },
            )

            editVacancyScreen(navController::popBackStack)

            allTracksScreen(
                navigateBack = navController::popBackStack,
                navigateToTrackWithId = navController::navigateToTrack,
            )

            appliedCandidatesScreen(
                navigateBack = navController::popBackStack,
            )

            trackScreen(
                navigateBack = navController::popBackStack,
                navigateToVacancyWithId = navController::navigateToVacancyDetails,
                navigateToInterviewWithId = navController::navigateToInterview,
                navigateToAddInterviewScreen = navController::navigateToAddInterview,
            )

            addInterviewScreen(navController)

            interviewScreen(navigateBack = navController::popBackStack)

            candidateScreen(
                navigateToVacancyWithId = navController::navigateToVacancyDetails,
                navigateToResumeWithId = {},
                navigateToTrackWithId = navController::navigateToTrack,
                navigateBack = navController::popBackStack,
            )
        }
    }
}