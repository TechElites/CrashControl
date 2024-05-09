package com.example.crashcontrol.ui.screens.addcrash

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.data.remote.OSMDataSource
import com.example.crashcontrol.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.compose.koinInject

data class AddCrashState(
    val position: String? = "",
    val date: String = "",
    val exclamation: String = "",
    val height: Double = 0.0,

    val showLocationDisabledAlert: Boolean = false,
    val showLocationPermissionDeniedAlert: Boolean = false,
    val showLocationPermissionPermanentlyDeniedSnackbar: Boolean = false,
    val showNoInternetConnectivitySnackbar: Boolean = false
) {
    val canSubmit get() = date.isNotBlank() && date.isNotBlank() && date.isNotBlank()

    fun toCrash() = Crash(
        position = position,
        date = date,
        exclamation = exclamation,
        height = height,
        favourite = false,
        impactTime = null,
        duration = null,
        impactAccelleration = 0.0
    )
}

interface AddCrashActions {
    fun setPosition(position: String?)
    fun setDate(date: String)
    fun setExclamation(exclamation: String)
    fun setHeight(height: Double)

    fun setShowLocationDisabledAlert(show: Boolean)
    fun setShowLocationPermissionDeniedAlert(show: Boolean)
    fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean)
    fun setShowNoInternetConnectivitySnackbar(show: Boolean)
}

class AddCrashViewModel() : ViewModel() {
    private val _state = MutableStateFlow(AddCrashState())
    val state = _state.asStateFlow()

    val actions = object : AddCrashActions {
        override fun setPosition(position: String?) =
            _state.update { it.copy(position = position) }

        override fun setDate(date: String) =
            _state.update { it.copy(date = date) }

        override fun setExclamation(exclamation: String) =
            _state.update { it.copy(exclamation = exclamation) }

        override fun setHeight(height: Double) =
            _state.update { it.copy(height = height) }

        override fun setShowLocationDisabledAlert(show: Boolean) =
            _state.update { it.copy(showLocationDisabledAlert = show) }

        override fun setShowLocationPermissionDeniedAlert(show: Boolean) =
            _state.update { it.copy(showLocationPermissionDeniedAlert = show) }

        override fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean) =
            _state.update { it.copy(showLocationPermissionPermanentlyDeniedSnackbar = show) }

        override fun setShowNoInternetConnectivitySnackbar(show: Boolean) =
            _state.update { it.copy(showNoInternetConnectivitySnackbar = show) }
    }
}
