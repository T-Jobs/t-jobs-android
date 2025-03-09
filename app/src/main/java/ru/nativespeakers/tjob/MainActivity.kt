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
import ru.nativespeakers.feature.auth.ui.AuthViewModel
import ru.nativespeakers.feature.auth.ui.LocalAppRoles
import ru.nativespeakers.feature.auth.ui.LoginRoute
import ru.nativespeakers.feature.auth.ui.loginScreen
import ru.nativespeakers.feature.auth.ui.navigateToLogin

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
                // navigate to home screen
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
            }
        }
    }
}