package com.example.crashcontrol.ui.screens.profile.signup

import com.example.crashcontrol.utils.AccountService
import com.example.crashcontrol.utils.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = ""
)

interface SignUpActions {
    fun setEmail(email: String)
    fun setPassword(newValue: String)
    fun setRepeatedPassword(newValue: String)
    fun signUp()

}

class SignUpViewModel(
    private val accountService: AccountService,
) : AuthViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val email
        get() = _state.value.email
    private val password
        get() = _state.value.password

    val actions = object : SignUpActions {
        override fun setEmail(email: String) {
            _state.update { it.copy(email = email) }
        }

        override fun setPassword(newValue: String) {
            _state.update { it.copy(password = newValue) }
        }

        override fun setRepeatedPassword(newValue: String) {
            _state.update { it.copy(repeatPassword = newValue) }
        }

        override fun signUp() {
            launchCatching {
                accountService.linkAccount(email, password)
            }
        }
    }
}