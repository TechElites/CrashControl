package com.example.crashcontrol.ui.screens.profile.signup

import com.example.crashcontrol.R
import com.example.crashcontrol.data.remote.FBDataSource
import com.example.crashcontrol.data.remote.FBUser
import com.example.crashcontrol.utils.AccountService
import com.example.crashcontrol.utils.AuthViewModel
import com.example.crashcontrol.utils.isValidEmail
import com.example.crashcontrol.utils.isValidPassword
import com.example.crashcontrol.utils.passwordMatches
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SignUpState(
    val email: String = "",
    val username: String = "",
    val name: String = "",
    val surname: String = "",
    val birthday: String = "",
    val picture: String = "",
    val password: String = "",
    val repeatPassword: String = "",
) {
    fun canSubmit(): Int? {
        return when {
            email.isEmpty() || !email.isValidEmail() -> R.string.email_error
            username.isEmpty()
                    ||name.isEmpty()
                    || surname.isEmpty()
                    || birthday.isEmpty()
                    || picture.isEmpty() -> R.string.empty_fields_error
            password.isEmpty() || !password.isValidPassword() -> R.string.password_error
            repeatPassword.isEmpty() || !password.passwordMatches(repeatPassword) -> R.string.password_match_error
            else -> null
        }
    }

    fun toFBUser() = FBUser(
        email = email,
        username = username,
        name = name,
        surname = surname,
        birthday = birthday,
        picture = picture,
    )
}

interface SignUpActions {
    fun setEmail(email: String)
    fun setUsername(username: String)
    fun setName(name: String)
    fun setSurname(surname: String)
    fun setBirthday(birthday: String)
    fun setPicture(picture: String)
    fun setPassword(newValue: String)
    fun setRepeatedPassword(newValue: String)
    fun signUp()
}

class SignUpViewModel(
    private val accountService: AccountService,
    private val fbDataSource: FBDataSource
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

        override fun setUsername(username: String) {
            _state.update { it.copy(username = username) }
        }

        override fun setName(name: String) {
            _state.update { it.copy(name = name) }
        }

        override fun setSurname(surname: String) {
            _state.update { it.copy(surname = surname) }
        }

        override fun setBirthday(birthday: String) {
            _state.update { it.copy(birthday = birthday) }
        }

        override fun setPicture(picture: String) {
            _state.update { it.copy(picture = picture) }
        }

        override fun setPassword(newValue: String) {
            _state.update { it.copy(password = newValue) }
        }

        override fun setRepeatedPassword(newValue: String) {
            _state.update { it.copy(repeatPassword = newValue) }
        }

        override fun signUp() {
            launchCatching {
                accountService.createAccount(email, password)
                fbDataSource.saveUser(accountService.currentUserId, _state.value.toFBUser())
            }
        }
    }
}