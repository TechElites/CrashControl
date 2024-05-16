package com.example.crashcontrol.ui.screens.profile.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.crashcontrol.R
import com.example.crashcontrol.ui.CrashControlRoute
import com.example.crashcontrol.ui.composables.BasicButton
import com.example.crashcontrol.ui.composables.BasicTextButton
import com.example.crashcontrol.ui.composables.EmailField
import com.example.crashcontrol.ui.composables.PasswordField
import com.example.crashcontrol.utils.isValidEmail

@Composable
fun SignInScreen(
    navController: NavHostController,
    state: SignInState,
    actions: SignInActions
) {
    val ctx = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var snackBarMessage by remember { mutableIntStateOf(R.string.email_error) }
    var showWrongInputAlert by remember { mutableStateOf(false) }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        val fieldModifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
            .padding(16.dp, 4.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmailField(state.email, actions::setEmail, fieldModifier)
            PasswordField(state.password, actions::setPassword, fieldModifier)

            BasicButton(
                R.string.sign_in,
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp)
            ) {
                if (!state.email.isValidEmail()) {
                    showWrongInputAlert = true
                    snackBarMessage = R.string.email_error
                }

                if (state.password.isBlank()) {
                    showWrongInputAlert = true
                    snackBarMessage = R.string.empty_password_error
                }

                if (!showWrongInputAlert) {
                    actions.signIn()
                    navController.navigate(CrashControlRoute.Profile.route)
                }
            }

            BasicTextButton(
                R.string.forgot_password,
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp, 16.dp, 0.dp)
            ) {
                snackBarMessage = if (state.email.isValidEmail()) {
                    actions.forgotPassword()
                    R.string.recovery_email_sent
                } else {
                    R.string.email_error
                }
                showWrongInputAlert = true
            }
        }
    }

    if (showWrongInputAlert) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                ContextCompat.getString(ctx, snackBarMessage),
                duration = SnackbarDuration.Long
            )
            showWrongInputAlert = false
        }
    }
}