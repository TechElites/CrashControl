package com.example.crashcontrol.ui.screens.profile

import com.example.crashcontrol.utils.AccountService
import com.example.crashcontrol.utils.AuthViewModel
import kotlinx.coroutines.flow.map

data class ProfileState(val isAnonymousAccount: Boolean = true)

interface ProfileActions {
    fun onSignOutClick()
    fun onDeleteMyAccountClick()
}

class ProfileViewModel(
    private val accountService: AccountService
) : AuthViewModel() {
    val state = accountService.currentUser.map {
        ProfileState(it.isAnonymous)
    }

    val actions = object : ProfileActions {

        override fun onSignOutClick() {
            launchCatching {
                accountService.signOut()
            }
        }

        override fun onDeleteMyAccountClick() {
            launchCatching {
                accountService.deleteAccount()
            }
        }
    }
}