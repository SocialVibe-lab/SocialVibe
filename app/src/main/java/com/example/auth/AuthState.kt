package com.example.auth

enum class AuthScreen {
    LOGIN,
    CREATE_ACCOUNT
}

enum class MainScreen {
    HOME,
    SEARCH,
    CREATE_POST,
    NOTIFICATIONS,
    PROFILE,
    SETTINGS
}

data class AuthUiState(
    // Current active auth screen
    val currentScreen: AuthScreen = AuthScreen.LOGIN,
    val currentMainScreen: MainScreen = MainScreen.HOME,

    // Login screen fields
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,

    // Create New Account screen fields
    val createAccountName: String = "",
    val createAccountEmail: String = "",
    val createAccountPassword: String = "",
    val createAccountConfirmPassword: String = "",
    val isCreatePasswordVisible: Boolean = false,
    val isCreateConfirmPasswordVisible: Boolean = false,
    val isCreateAccountLoading: Boolean = false,
    val createAccountNameError: String? = null,
    val createAccountEmailError: String? = null,
    val createAccountPasswordError: String? = null,
    val createAccountConfirmPasswordError: String? = null,
    val createAccountErrorMessage: String? = null,
    val createAccountSuccessMessage: String? = null,

    // App Session / General
    val isLoggedIn: Boolean = false,
    val currentUserEmail: String? = null,
    val currentUserName: String? = null,
    val currentUserHandle: String? = null,
    val userBio: String = "Digital creator & vibe explorer ✨ | Coffee lover & coding enthusiast 💻",
    val userAvatarStyle: Int = 0,
    val userFollowersCount: Int = 348,
    val userFollowingCount: Int = 192,
    val infoMessage: String? = null,

    // Privacy Settings (Session)
    val isProfilePrivate: Boolean = false,
    val whoCanFollowMe: String = "Everyone",
    val whoCanMessageMe: String = "People I Follow",

    // Forgot password dialog
    val isForgotPasswordDialogOpen: Boolean = false,
    val forgotPasswordEmail: String = "",
    val forgotPasswordSuccess: String? = null,
    val forgotPasswordError: String? = null,
    val isForgotPasswordLoading: Boolean = false
)
