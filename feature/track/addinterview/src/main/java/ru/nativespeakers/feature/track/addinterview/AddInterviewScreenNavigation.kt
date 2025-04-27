package ru.nativespeakers.feature.track.addinterview

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.feature.track.addinterview.ui.AddInterviewScreen
import ru.nativespeakers.feature.track.addinterview.ui.AddInterviewViewModel
import ru.nativespeakers.feature.track.details.ui.TrackViewModel

@Serializable
data class AddInterview(val trackId: Long)

fun NavController.navigateToAddInterview(
    trackId: Long,
    navOptions: NavOptions? = null,
) = navigate(route = AddInterview(trackId), navOptions)

fun NavGraphBuilder.addInterviewScreen(
    navController: NavController,
) {
    composable<AddInterview> { backStackEntry ->
        val route = backStackEntry.toRoute<AddInterview>()
        val trackId = route.trackId

        val trackViewModel = navController.previousBackStackEntry?.let {
            hiltViewModel(
                viewModelStoreOwner = it,
                creationCallback = { factory: TrackViewModel.Factory ->
                    factory.create(trackId)
                }
            )
        }

        val viewModel = hiltViewModel<AddInterviewViewModel>()

        LaunchedEffect(Unit) {
            trackViewModel?.let {
                viewModel.updateCandidate(it.trackDetailsUiState.value.candidateNetwork.toPersonAndPhotoUiState())
            }
        }

        AddInterviewScreen(
            onCreateClick = { trackViewModel?.createInterview(it) },
            navigateBack = navController::popBackStack
        )
    }
}