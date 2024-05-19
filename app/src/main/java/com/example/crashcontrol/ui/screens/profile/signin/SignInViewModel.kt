package com.example.crashcontrol.ui.screens.profile.signin

import com.example.crashcontrol.R
import com.example.crashcontrol.utils.AccountService
import com.example.crashcontrol.utils.AuthViewModel
import com.example.crashcontrol.utils.isValidEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SignInState(
    val email: String = "",
    val password: String = ""
) {
    fun canSubmit(): Int? {
        return when {
            email.isEmpty() || !email.isValidEmail() -> R.string.email_error
            password.isEmpty() -> R.string.empty_password_error
            else -> null
        }
    }

}

interface SignInActions {
    fun setEmail(newValue: String)
    fun setPassword(newValue: String)
    fun signIn()
    fun forgotPassword()
}

class SignInViewModel(
    private val accountService: AccountService,
) : AuthViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val email
        get() = _state.value.email
    private val password
        get() = _state.value.password

    val actions = object : SignInActions {

        override fun setEmail(newValue: String) {
            _state.update { it.copy(email = newValue) }
        }

        override fun setPassword(newValue: String) {
            _state.update { it.copy(password = newValue) }
        }

        override fun signIn() {
            launchCatching {
                accountService.authenticate(email, password)
            }
        }

        override fun forgotPassword() {
            launchCatching {
                accountService.sendRecoveryEmail(email)
            }
        }
    }
}
