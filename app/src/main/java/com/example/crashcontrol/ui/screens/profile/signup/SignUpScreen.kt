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
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.crashcontrol.ui.composables.BasicField
import com.example.crashcontrol.ui.composables.EmailField
import com.example.crashcontrol.ui.composables.PasswordField
import com.example.crashcontrol.ui.composables.RepeatPasswordField

@Composable
fun SignUpScreen(
    navController: NavHostController,
    state: SignUpState,
    actions: SignUpActions
) {
    val ctx = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var snackBarMessage by remember { mutableIntStateOf(R.string.email_error) }
    var showWrongInputAlert by remember { mutableStateOf(false) }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val fieldModifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)

            EmailField(state.email, actions::setEmail, fieldModifier)
            BasicField(R.string.username, state.username, actions::setUsername, fieldModifier)
            BasicField(R.string.name, state.name, actions::setName, fieldModifier)
            BasicField(R.string.surname, state.surname, actions::setSurname, fieldModifier)
            BasicField(R.string.birthday, state.birthday, actions::setBirthday, fieldModifier)
            BasicField(R.string.picture, state.picture, actions::setPicture, fieldModifier)
            PasswordField(state.password, actions::setPassword, fieldModifier)
            RepeatPasswordField(state.repeatPassword, actions::setRepeatedPassword, fieldModifier)

            BasicButton(
                R.string.create_account,
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp)
            ) {
                val objection = state.canSubmit()
                if (objection != null) {
                    snackBarMessage = objection
                    showWrongInputAlert = true
                } else {
                    actions.signUp()
                    navController.navigate(CrashControlRoute.Profile.route)
                }
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