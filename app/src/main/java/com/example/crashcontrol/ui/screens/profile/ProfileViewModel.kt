package com.example.crashcontrol.ui.screens.profile

import com.example.crashcontrol.data.remote.FBDataSource
import com.example.crashcontrol.data.remote.FBUser
import com.example.crashcontrol.utils.AccountService
import com.example.crashcontrol.utils.AuthViewModel
import kotlinx.coroutines.flow.map

data class ProfileState(
    val isAnonymousAccount: Boolean = true,
)

interface ProfileActions {
    fun signOut()
    fun deleteAccount()
    suspend fun loadCurrentUser(): FBUser?
}

class ProfileViewModel(
    private val accountService: AccountService,
    private val fbDataSource: FBDataSource
) : AuthViewModel() {
    val state = accountService.currentAccount.map {
        ProfileState(it.isAnonymous)
    }

    val actions = object : ProfileActions {

        override fun signOut() {
            launchCatching {
                accountService.signOut()
            }
        }

        override fun deleteAccount() {
            launchCatching {
                fbDataSource.deleteUser(accountService.currentUserId)
                fbDataSource.deleteCrash(accountService.currentUserId)
                accountService.deleteAccount()
            }
        }

        override suspend fun loadCurrentUser(): FBUser? {
            return fbDataSource.loadUser(accountService.currentUserId)
        }
    }
}