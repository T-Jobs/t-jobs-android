package ru.nativespeakers.tjob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.nativespeakers.core.designsystem.TJobTheme
import ru.nativespeakers.feature.auth.ui.LoginRoute
import ru.nativespeakers.feature.auth.ui.loginScreen

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
        NavHost(
            navController = navController,
            startDestination = LoginRoute
        ) {
            loginScreen(
                navigateToHome = {}
            )
        }
    }
}