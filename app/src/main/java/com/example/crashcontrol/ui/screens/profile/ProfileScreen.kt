package com.example.crashcontrol.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.crashcontrol.R
import com.example.crashcontrol.data.remote.FBUser
import com.example.crashcontrol.ui.CrashControlRoute
import com.example.crashcontrol.ui.composables.BasicAlertDialog
import com.example.crashcontrol.ui.composables.BasicTextButton
import com.example.crashcontrol.ui.composables.DialogConfirmButton
import com.example.crashcontrol.ui.composables.RegularCardEditor
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavHostController,
    state: ProfileState,
    actions: ProfileActions,
    totalCrashes: Int
) {
    val ctx = LocalContext.current
    var user by remember { mutableStateOf<FBUser?>(null) }
    var infoDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    fun loadUser() = coroutineScope.launch {
        user = actions.loadCurrentUser()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
                ImageVector.vectorResource(id = R.drawable.baseline_person_add_alt_1_24),
                "",
                Modifier.padding(16.dp, 0.dp, 16.dp, 8.dp)
            ) {
                navController.navigate(CrashControlRoute.SignUp.route)
            }

            BasicTextButton(
                R.string.why_signup,
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp, 16.dp, 0.dp)
            ) {
                infoDialog = true
            }
        } else {
            loadUser()
            if (user != null) {
                if (user?.picture?.isNotEmpty() == true) {
                    Card(
                        modifier = Modifier
                            .size(150.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(80.dp),
                    ) {
                        AsyncImage(
                            ImageRequest.Builder(ctx).data(user!!.picture.toUri())
                                .crossfade(true).build(),
                            "Captured image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                user?.username?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                user?.email?.let { Text(text = it) }
                Text(text = user?.name + " " + user?.surname)
                user?.birthday?.let { Text(text = it) }
                Text(text = "Total crashes: $totalCrashes")
            } else {
                Text(text = stringResource(R.string.loading))
            }
            Spacer(modifier = Modifier.weight(1f))
            SignOutCard { actions.signOut() }
        }
    }

    if (infoDialog) {
        AlertDialog(
            onDismissRequest = { infoDialog = false },
            icon = { Icon(Icons.Default.Info, contentDescription = "Info") },
            title = { Text(stringResource(R.string.why_signup)) },
            text = { Text(stringResource(R.string.why_signup_info)) },
            confirmButton = {
                DialogConfirmButton(R.string.close) {
                    infoDialog = false
                }
            }
        )
    }
}

@Composable
private fun SignOutCard(signOut: () -> Unit) {
    var showWarningDialog by remember { mutableStateOf(false) }

    RegularCardEditor(
        R.string.sign_out, Icons.Outlined.ExitToApp, "",
        Modifier.padding(16.dp, 0.dp, 16.dp, 8.dp)
    ) {
        showWarningDialog = true
    }

    if (showWarningDialog) {
        BasicAlertDialog(
            onDismissRequest = { showWarningDialog = false },
            onConfirmation = {
                signOut()
                showWarningDialog = false
            },
            dialogTitle = stringResource(R.string.sign_out),
            dialogText = stringResource(R.string.sign_out_description),
            confimationText = R.string.cancel,
            icon = Icons.Outlined.ExitToApp
        )
    }
}