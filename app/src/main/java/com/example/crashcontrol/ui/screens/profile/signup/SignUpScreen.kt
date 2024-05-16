/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.crashcontrol.ui.screens.profile.signup

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
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import com.example.crashcontrol.R
import com.example.crashcontrol.ui.CrashControlRoute
import com.example.crashcontrol.ui.composables.BasicButton
import com.example.crashcontrol.ui.composables.EmailField
import com.example.crashcontrol.ui.composables.PasswordField
import com.example.crashcontrol.ui.composables.RepeatPasswordField
import com.example.crashcontrol.utils.isValidEmail
import com.example.crashcontrol.utils.isValidPassword
import com.example.crashcontrol.utils.passwordMatches

@Composable
fun SignUpScreen(
    navController: NavHostController,
    state: SignUpState,
    actions: SignUpActions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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
            EmailField(state.email, actions::setEmail, fieldModifier)
            PasswordField(state.password, actions::setPassword, fieldModifier)
            RepeatPasswordField(state.repeatPassword, actions::setRepeatedPassword, fieldModifier)

            BasicButton(
                R.string.create_account,
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp)
            ) {
                if (!state.email.isValidEmail()) {
                    snackBarMessage = R.string.email_error
                    showWrongInputAlert = true
                }
                if (!state.password.isValidPassword()) {
                    snackBarMessage = R.string.password_error
                    showWrongInputAlert = true
                }
                if (!state.password.passwordMatches(state.repeatPassword)) {
                    snackBarMessage = R.string.password_match_error
                    showWrongInputAlert = true
                }
                if (!showWrongInputAlert) {
                    actions.signUp()
                    navController.navigate(CrashControlRoute.Profile.route)
                }
            }
        }

        if (showWrongInputAlert) {
            LaunchedEffect(snackbarHostState) {
                snackbarHostState.showSnackbar(
                    getString(ctx, snackBarMessage),
                    duration = SnackbarDuration.Long
                )
                showWrongInputAlert = false
            }
        }
    }
}