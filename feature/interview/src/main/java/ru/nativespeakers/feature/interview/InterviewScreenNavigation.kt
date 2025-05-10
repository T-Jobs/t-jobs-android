package ru.nativespeakers.feature.interview

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.interview.ui.InterviewScreen

@Serializable
data class Interview(val interviewId: Long)

fun NavController.navigateToInterview(
    interviewId: Long,
    navOptions: NavOptions? = null,
) = navigate(route = Interview(interviewId), navOptions)

fun NavGraphBuilder.interviewScreen(
    navigateBack: () -> Unit,
) {
    composable<Interview> { backStackEntry ->
        val route = backStackEntry.toRoute<Interview>()
        val interviewId = route.interviewId
        InterviewScreen(
            interviewId = interviewId,
            navigateBack = navigateBack,
        )
    }
}