package com.example.crashcontrol.ui.screens.crashdetails

import com.example.crashcontrol.data.database.Crash
import com.example.crashcontrol.data.remote.FBDataSource
import com.example.crashcontrol.data.remote.OSMPlace
import com.example.crashcontrol.utils.AccountService
import com.example.crashcontrol.utils.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CrashDetailsState(
    val id: Int = 0,
    val position: OSMPlace? = null,
    val exclamation: String = "",
    val favourite: Boolean = false,
    val date: String = "",
    val time: String = "",
    val face: String = ""

) {
    fun toCrash() = Crash(
        id = id,
        latitude = position?.latitude,
        longitude = position?.longitude,
        exclamation = exclamation,
        favourite = favourite,
        date = date,
        time = time,
        face = face
    )
}

interface CrashDetailsActions {
    fun setId(id: Int)
    fun setPosition(position: OSMPlace)
    fun setExclamation(exclamation: String)
    fun setFavourite(favorite: Boolean)
    fun setDate(date: String)
    fun setTime(time: String)
    fun setFace(face: String)
    fun deleteFBCrash()

}

class CrashDetailsViewModel(
    private val accountService: AccountService,
    private val fbDataSource: FBDataSource
) : AuthViewModel() {
    private val _state = MutableStateFlow(CrashDetailsState())
    val state = _state.asStateFlow()

    private val latitude
        get() = _state.value.position?.latitude

    private val longitude
        get() = _state.value.position?.longitude

    val actions = object : CrashDetailsActions {

        override fun setId(id: Int) =
            _state.update { it.copy(id = id) }

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

        override fun deleteFBCrash() {
            if (accountService.hasUser) {
                launchCatching {
                    val latestCrash = fbDataSource.loadCrash(accountService.currentUserId)
                    if (latestCrash != null
                        && latestCrash.latitude == latitude
                        && latestCrash.longitude == longitude
                    ) {
                        fbDataSource.deleteCrash(accountService.currentUserId)
                    }
                }
            }
        }

    }
}
