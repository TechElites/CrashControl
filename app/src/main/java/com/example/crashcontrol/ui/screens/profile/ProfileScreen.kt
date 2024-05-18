package com.example.crashcontrol.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.crashcontrol.R
import com.example.crashcontrol.data.remote.FBUser
import com.example.crashcontrol.ui.CrashControlRoute
import com.example.crashcontrol.ui.composables.DangerousCardEditor
import com.example.crashcontrol.ui.composables.DialogCancelButton
import com.example.crashcontrol.ui.composables.DialogConfirmButton
import com.example.crashcontrol.ui.composables.RegularCardEditor
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavHostController,
    state: ProfileState,
    actions: ProfileActions
) {
    var user by remember { mutableStateOf<FBUser?>(null) }
    val coroutineScope = rememberCoroutineScope()
    fun loadUser() = coroutineScope.launch {
        user = actions.loadCurrentUser()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        if (state.isAnonymousAccount) {
            RegularCardEditor(
                R.string.sign_in,
                Icons.Filled.Person,
                "",
                Modifier.padding(16.dp, 0.dp, 16.dp, 8.dp)
            ) {
                navController.navigate(CrashControlRoute.SignIn.route)
            }

            RegularCardEditor(
                R.string.create_account,
                Icons.Filled.Add,
                "",
                Modifier.padding(16.dp, 0.dp, 16.dp, 8.dp)
            ) {
                navController.navigate(CrashControlRoute.SignUp.route)
            }
        } else {
            loadUser()
            if (user != null) {
                user?.email?.let { Text(text = "Email: $it") }
                user?.username?.let { Text(text = "Username: $it") }
                user?.name?.let { Text(text = "Name: $it") }
                user?.surname?.let { Text(text = "Surname: $it") }
                user?.birthday?.let { Text(text = "Birthday: $it") }
                user?.picture?.let { Text(text = "Picture: $it") }
            } else {
                Text(text = stringResource(R.string.loading))
            }
            SignOutCard { actions.onSignOutClick() }
            DeleteMyAccountCard { actions.onDeleteMyAccountClick() }
        }
    }
}

@Composable
private fun SignOutCard(signOut: () -> Unit) {
    var showWarningDialog by remember { mutableStateOf(false) }

    RegularCardEditor(
        R.string.sign_out,
        Icons.Outlined.ExitToApp,
        "",
        Modifier.padding(16.dp, 0.dp, 16.dp, 8.dp)
    ) {
        showWarningDialog = true
    }

    if (showWarningDialog) {
        AlertDialog(
            title = { Text(stringResource(R.string.sign_out)) },
            text = { Text(stringResource(R.string.sign_out_description)) },
            dismissButton = { DialogCancelButton(R.string.cancel) { showWarningDialog = false } },
            confirmButton = {
                DialogConfirmButton(R.string.sign_out) {
                    signOut()
                    showWarningDialog = false
                }
            },
            onDismissRequest = { showWarningDialog = false }
        )
    }
}

@Composable
private fun DeleteMyAccountCard(deleteMyAccount: () -> Unit) {
    var showWarningDialog by remember { mutableStateOf(false) }

    DangerousCardEditor(
        R.string.delete_account,
        Icons.Outlined.Close,
        "",
        Modifier.padding(16.dp, 0.dp, 16.dp, 8.dp)
    ) {
        showWarningDialog = true
    }

    if (showWarningDialog) {
        AlertDialog(
            title = { Text(stringResource(R.string.delete_account)) },
            text = { Text(stringResource(R.string.delete_account_description)) },
            dismissButton = { DialogCancelButton(R.string.cancel) { showWarningDialog = false } },
            confirmButton = {
                DialogConfirmButton(R.string.delete_account) {
                    deleteMyAccount()
                    showWarningDialog = false
                }
            },
            onDismissRequest = { showWarningDialog = false }
        )
    }
}