package ru.nativespeakers.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ru.nativespeakers.core.designsystem.Primary2
import ru.nativespeakers.core.designsystem.Primary4
import ru.nativespeakers.ui.setStatusBarMode
import ru.nativespeakers.feature.auth.R as authRes

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    navigateToHomeScreen: () -> Unit,
) {
    val view = LocalView.current
    SideEffect {
        setStatusBarMode(view, true)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        Box {
            Image(
                painter = painterResource(authRes.drawable.header),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Image(
                    painter = painterResource(authRes.drawable.feature_auth_app_logo),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(authRes.string.feature_auth_welcome_back),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(authRes.string.feature_auth_enter_login_and_password),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.weight(1f))

                EmailTextField(
                    value = loginViewModel.loginUiState.email,
                    onValueChange = loginViewModel::updateEmail,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                PasswordTextField(
                    value = loginViewModel.loginUiState.password,
                    placeholder = stringResource(authRes.string.feature_auth_password),
                    onValueChange = loginViewModel::updatePassword,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = loginViewModel::login,
                    shape = MaterialTheme.shapes.small,
                    enabled = !loginViewModel.loginUiState.isLoading && loginViewModel.loginUiState.email.isNotBlank() && loginViewModel.loginUiState.password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = Primary2,
                        disabledContentColor = Primary4
                    ),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (loginViewModel.loginUiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(authRes.string.feature_auth_enter),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    ObserveLoginStateChanges(
        loginUiState = loginViewModel.loginUiState,
        snackbarHostState = snackbarHostState,
        onCredentialsInvalidShown = loginViewModel::credentialsInvalidMessageShown,
        onLoggedIn = navigateToHomeScreen
    )
}

@Composable
private fun ObserveLoginStateChanges(
    loginUiState: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onCredentialsInvalidShown: () -> Unit,
    onLoggedIn: () -> Unit,
) {
    ObserveIsLoggedIn(loginUiState, onLoggedIn)
    ObserveCredentialsInvalid(
        loginUiState = loginUiState,
        snackbarHostState = snackbarHostState,
        onCredentialsInvalidShown = onCredentialsInvalidShown,
    )
}

@Composable
private fun ObserveIsLoggedIn(
    loginUiState: LoginUiState,
    onLoggedIn: () -> Unit,
) {
    val currentOnLoggedIn by rememberUpdatedState(onLoggedIn)
    LaunchedEffect(loginUiState) {
        if (loginUiState.isLoggedIn) {
            currentOnLoggedIn()
        }
    }
}

@Composable
private fun ObserveCredentialsInvalid(
    loginUiState: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onCredentialsInvalidShown: () -> Unit,
) {
    val loginErrorMessage = stringResource(authRes.string.feature_auth_incorrect_login_or_password)
    val currentOnCredentialsInvalidShown by rememberUpdatedState(onCredentialsInvalidShown)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(loginUiState) {
        if (loginUiState.credentialsInvalid) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = loginErrorMessage,
                    withDismissAction = true
                )
                currentOnCredentialsInvalidShown()
            }
        }
    }
}