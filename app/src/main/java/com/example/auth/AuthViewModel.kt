package com.example.auth

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    @Suppress("UNUSED_PARAMETER")
    fun initialize(context: Context) {
        // Firebase Authentication integration is disabled for now.
    }

    // Navigation between Auth screens
    fun navigateToCreateAccount() {
        _uiState.update {
            it.copy(
                currentScreen = AuthScreen.CREATE_ACCOUNT,
                createAccountErrorMessage = null,
                createAccountSuccessMessage = null,
                createAccountNameError = null,
                createAccountEmailError = null,
                createAccountPasswordError = null,
                createAccountConfirmPasswordError = null
            )
        }
    }

    fun navigateToLogin() {
        _uiState.update {
            it.copy(
                currentScreen = AuthScreen.LOGIN,
                errorMessage = null,
                createAccountSuccessMessage = null
            )
        }
    }

    // Login Screen Form Handlers
    fun onEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                emailError = null,
                errorMessage = null
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                passwordError = null,
                errorMessage = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun login() {
        val currentState = _uiState.value
        val email = currentState.email.trim()
        val password = currentState.password

        var hasError = false
        var emailErr: String? = null
        var passwordErr: String? = null

        if (email.isEmpty()) {
            emailErr = "Email address is required."
            hasError = true
        } else if (!isValidEmail(email)) {
            emailErr = "Please enter a valid email address."
            hasError = true
        }

        if (password.isEmpty()) {
            passwordErr = "Password is required."
            hasError = true
        } else if (password.length < 6) {
            passwordErr = "Password must be at least 6 characters."
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    emailError = emailErr,
                    passwordError = passwordErr,
                    errorMessage = "Please correct the highlighted fields."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    emailError = null,
                    passwordError = null
                )
            }
            delay(500)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    currentUserEmail = email,
                    errorMessage = null
                )
            }
        }
    }

    // Create New Account Form Handlers
    fun onCreateAccountNameChange(newName: String) {
        _uiState.update {
            it.copy(
                createAccountName = newName,
                createAccountNameError = null,
                createAccountErrorMessage = null
            )
        }
    }

    fun onCreateAccountEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(
                createAccountEmail = newEmail,
                createAccountEmailError = null,
                createAccountErrorMessage = null
            )
        }
    }

    fun onCreateAccountPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                createAccountPassword = newPassword,
                createAccountPasswordError = null,
                createAccountConfirmPasswordError = null,
                createAccountErrorMessage = null
            )
        }
    }

    fun onCreateAccountConfirmPasswordChange(newConfirmPassword: String) {
        _uiState.update {
            it.copy(
                createAccountConfirmPassword = newConfirmPassword,
                createAccountConfirmPasswordError = null,
                createAccountErrorMessage = null
            )
        }
    }

    fun toggleCreatePasswordVisibility() {
        _uiState.update { it.copy(isCreatePasswordVisible = !it.isCreatePasswordVisible) }
    }

    fun toggleCreateConfirmPasswordVisibility() {
        _uiState.update { it.copy(isCreateConfirmPasswordVisible = !it.isCreateConfirmPasswordVisible) }
    }

    fun clearCreateAccountError() {
        _uiState.update { it.copy(createAccountErrorMessage = null) }
    }

    fun createAccount() {
        val state = _uiState.value
        val name = state.createAccountName.trim()
        val email = state.createAccountEmail.trim()
        val password = state.createAccountPassword
        val confirmPassword = state.createAccountConfirmPassword

        var hasError = false
        var nameErr: String? = null
        var emailErr: String? = null
        var passwordErr: String? = null
        var confirmPasswordErr: String? = null

        // Validate name
        if (name.isEmpty()) {
            nameErr = "Please enter your name."
            hasError = true
        }

        // Validate email
        if (email.isEmpty()) {
            emailErr = "Email address is required."
            hasError = true
        } else if (!isValidEmail(email)) {
            emailErr = "Please enter a valid email address."
            hasError = true
        }

        // Validate password
        if (password.isEmpty()) {
            passwordErr = "Password is required."
            hasError = true
        } else if (password.length < 6) {
            passwordErr = "Password must be at least 6 characters."
            hasError = true
        }

        // Validate confirm password and match
        if (confirmPassword.isEmpty()) {
            confirmPasswordErr = "Please confirm your password."
            hasError = true
        } else if (password != confirmPassword) {
            confirmPasswordErr = "Passwords do not match."
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    createAccountNameError = nameErr,
                    createAccountEmailError = emailErr,
                    createAccountPasswordError = passwordErr,
                    createAccountConfirmPasswordError = confirmPasswordErr,
                    createAccountErrorMessage = "Please check the highlighted fields."
                )
            }
            return
        }

        // Local / Mock Submission (No Firebase connection)
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCreateAccountLoading = true,
                    createAccountErrorMessage = null,
                    createAccountNameError = null,
                    createAccountEmailError = null,
                    createAccountPasswordError = null,
                    createAccountConfirmPasswordError = null
                )
            }
            delay(650)
            _uiState.update {
                it.copy(
                    isCreateAccountLoading = false,
                    createAccountSuccessMessage = "Account created successfully for $name!",
                    isLoggedIn = true,
                    currentUserEmail = email,
                    currentUserName = name,
                    email = email // prefill login email for convenience
                )
            }
        }
    }

    // Forgot Password Handlers
    fun openForgotPasswordDialog() {
        _uiState.update {
            it.copy(
                isForgotPasswordDialogOpen = true,
                forgotPasswordEmail = it.email.trim(),
                forgotPasswordSuccess = null,
                forgotPasswordError = null
            )
        }
    }

    fun closeForgotPasswordDialog() {
        _uiState.update {
            it.copy(
                isForgotPasswordDialogOpen = false,
                forgotPasswordSuccess = null,
                forgotPasswordError = null
            )
        }
    }

    fun onForgotPasswordEmailChange(email: String) {
        _uiState.update {
            it.copy(
                forgotPasswordEmail = email,
                forgotPasswordError = null
            )
        }
    }

    fun sendPasswordReset() {
        val email = _uiState.value.forgotPasswordEmail.trim()
        if (email.isEmpty() || !isValidEmail(email)) {
            _uiState.update {
                it.copy(forgotPasswordError = "Please enter a valid email address.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isForgotPasswordLoading = true, forgotPasswordError = null) }
            delay(500)
            _uiState.update {
                it.copy(
                    isForgotPasswordLoading = false,
                    forgotPasswordSuccess = "Password reset instructions sent to $email."
                )
            }
        }
    }

    fun logout() {
        _uiState.update {
            AuthUiState(
                email = it.email,
                currentScreen = AuthScreen.LOGIN,
                currentMainScreen = MainScreen.HOME
            )
        }
    }

    fun navigateToProfile() {
        _uiState.update { it.copy(currentMainScreen = MainScreen.PROFILE) }
    }

    fun navigateToHome() {
        _uiState.update { it.copy(currentMainScreen = MainScreen.HOME) }
    }

    fun navigateToSettings() {
        _uiState.update { it.copy(currentMainScreen = MainScreen.SETTINGS) }
    }

    fun navigateToMainScreen(screen: MainScreen) {
        _uiState.update { it.copy(currentMainScreen = screen) }
    }

    fun updateUserProfile(
        name: String,
        username: String,
        bio: String,
        avatarStyle: Int = _uiState.value.userAvatarStyle
    ) {
        val cleanUsername = if (username.startsWith("@")) username else "@$username"
        _uiState.update {
            it.copy(
                currentUserName = name.ifBlank { it.currentUserName },
                currentUserHandle = cleanUsername.ifBlank { it.currentUserHandle },
                userBio = bio,
                userAvatarStyle = avatarStyle
            )
        }
    }

    fun updateUserProfile(name: String, bio: String) {
        _uiState.update {
            it.copy(
                currentUserName = name.ifBlank { it.currentUserName },
                userBio = bio
            )
        }
    }

    fun setProfileVisibility(isPrivate: Boolean) {
        _uiState.update {
            it.copy(isProfilePrivate = isPrivate)
        }
    }

    fun setWhoCanFollowMe(option: String) {
        _uiState.update {
            it.copy(whoCanFollowMe = option)
        }
    }

    fun setWhoCanMessageMe(option: String) {
        _uiState.update {
            it.copy(whoCanMessageMe = option)
        }
    }

    fun changePassword(newPassword: String) {
        _uiState.update {
            it.copy(password = newPassword)
        }
    }

    fun changeEmail(newEmail: String): Boolean {
        val trimmed = newEmail.trim()
        if (!isValidEmail(trimmed)) {
            return false
        }
        _uiState.update {
            it.copy(currentUserEmail = trimmed, email = trimmed)
        }
        return true
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return EMAIL_REGEX.matches(email)
    }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    }
}

