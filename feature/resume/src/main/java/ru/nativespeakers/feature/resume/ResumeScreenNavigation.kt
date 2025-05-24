package ru.nativespeakers.feature.resume

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.nativespeakers.feature.resume.ui.ResumeScreen

@Serializable
data class ResumeRoute(val resumeId: Long)

fun NavController.navigateToResume(
    resumeId: Long,
    navOptions: NavOptions? = null,
) = navigate(route = ResumeRoute(resumeId), navOptions)

fun NavGraphBuilder.resumeScreen(
    navigateBack: () -> Unit,
) {
    composable<ResumeRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ResumeRoute>()
        val interviewId = route.resumeId
        ResumeScreen(
            resumeId = interviewId,
            navigateBack = navigateBack,
        )
    }
}