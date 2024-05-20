package com.example.crashcontrol.ui.screens.addcrash

import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.data.remote.FBCrash
import com.example.crashcontrol.data.remote.FBDataSource
import com.example.crashcontrol.data.remote.OSMPlace
import com.example.crashcontrol.utils.AccountService
import com.example.crashcontrol.utils.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AddCrashState(
    val position: OSMPlace? = null,
    val exclamation: String = "",
    val favourite: Boolean = false,
    val date: String = "",
    val time: String = "",
    val face: String = ""

) {
    fun canSubmit() = exclamation.isNotEmpty()
            && date.isNotEmpty()
            && time.isNotEmpty()
            && face.isNotEmpty()

    fun toCrash() = Crash(
        latitude = position?.latitude,
        longitude = position?.longitude,
        exclamation = exclamation,
        favourite = favourite,
        date = date,
        time = time,
        face = face
    )

    fun toFBCrash() = FBCrash(
        latitude = position?.latitude ?: 0.0,
        longitude = position?.longitude ?: 0.0,
        exclamation = exclamation,
        date = date,
        time = time,
        face = face
    )
}

interface AddCrashActions {
    fun setPosition(position: OSMPlace)
    fun setExclamation(exclamation: String)
    fun setFavourite(favorite: Boolean)
    fun setDate(date: String)
    fun setTime(time: String)
    fun setFace(face: String)
    fun saveFBCrash()
}

class AddCrashViewModel(
    private val accountService: AccountService,
    private val fbDataSource: FBDataSource
) : AuthViewModel() {
    private val _state = MutableStateFlow(AddCrashState())
    val state = _state.asStateFlow()

    private val position
        get() = _state.value.position

    val actions = object : AddCrashActions {
        override fun setPosition(position: OSMPlace) =
            _state.update { it.copy(position = position) }

        override fun setExclamation(exclamation: String) =
            _state.update { it.copy(exclamation = exclamation) }

        override fun setFavourite(favorite: Boolean) =
            _state.update { it.copy(favourite = favorite) }

        override fun setDate(date: String) =
            _state.update { it.copy(date = date) }

        override fun setTime(time: String) =
            _state.update { it.copy(time = time) }

        override fun setFace(face: String) =
            _state.update { it.copy(face = face) }

        override fun saveFBCrash() {
            if (position != null && accountService.hasUser) {
                launchCatching {
                    val user = fbDataSource.loadUser(accountService.currentUserId)
                    val crash = _state.value.toFBCrash()
                    crash.username = user?.username ?: "Unknown"
                    fbDataSource.saveCrash(accountService.currentUserId, crash)
                }
            }
        }

    }
}
