package com.example

import com.example.auth.AuthScreen
import com.example.auth.AuthViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        viewModel = AuthViewModel()
    }

    @Test
    fun initialScreen_isLogin() {
        assertEquals(AuthScreen.LOGIN, viewModel.uiState.value.currentScreen)
        assertFalse(viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun navigateToCreateAccount_andBackToLogin() {
        viewModel.navigateToCreateAccount()
        assertEquals(AuthScreen.CREATE_ACCOUNT, viewModel.uiState.value.currentScreen)

        viewModel.navigateToLogin()
        assertEquals(AuthScreen.LOGIN, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun createAccount_validationErrors_emptyFields() {
        viewModel.navigateToCreateAccount()
        viewModel.createAccount()

        val state = viewModel.uiState.value
        assertNotNull(state.createAccountNameError)
        assertNotNull(state.createAccountEmailError)
        assertNotNull(state.createAccountPasswordError)
        assertNotNull(state.createAccountConfirmPasswordError)
        assertNotNull(state.createAccountErrorMessage)
        assertFalse(state.isLoggedIn)
    }

    @Test
    fun createAccount_validationErrors_mismatchedPassword() {
        viewModel.navigateToCreateAccount()
        viewModel.onCreateAccountNameChange("Alex Morgan")
        viewModel.onCreateAccountEmailChange("alex@example.com")
        viewModel.onCreateAccountPasswordChange("password123")
        viewModel.onCreateAccountConfirmPasswordChange("differentPassword")

        viewModel.createAccount()

        val state = viewModel.uiState.value
        assertNull(state.createAccountNameError)
        assertNull(state.createAccountEmailError)
        assertNull(state.createAccountPasswordError)
        assertEquals("Passwords do not match.", state.createAccountConfirmPasswordError)
        assertFalse(state.isLoggedIn)
    }

    @Test
    fun createAccount_passwordToggles() {
        assertFalse(viewModel.uiState.value.isCreatePasswordVisible)
        assertFalse(viewModel.uiState.value.isCreateConfirmPasswordVisible)

        viewModel.toggleCreatePasswordVisibility()
        assertTrue(viewModel.uiState.value.isCreatePasswordVisible)

        viewModel.toggleCreateConfirmPasswordVisibility()
        assertTrue(viewModel.uiState.value.isCreateConfirmPasswordVisible)
    }

    @Test
    fun profileNavigation_andUpdateProfile() {
        assertEquals(com.example.auth.MainScreen.HOME, viewModel.uiState.value.currentMainScreen)

        viewModel.navigateToProfile()
        assertEquals(com.example.auth.MainScreen.PROFILE, viewModel.uiState.value.currentMainScreen)

        viewModel.updateUserProfile("Jordan River", "Tech enthusiast and coffee lover ☕")
        assertEquals("Jordan River", viewModel.uiState.value.currentUserName)
        assertEquals("Tech enthusiast and coffee lover ☕", viewModel.uiState.value.userBio)

        viewModel.navigateToHome()
        assertEquals(com.example.auth.MainScreen.HOME, viewModel.uiState.value.currentMainScreen)
    }

    @Test
    fun bottomNavigation_switchesAllTabs() {
        assertEquals(com.example.auth.MainScreen.HOME, viewModel.uiState.value.currentMainScreen)

        viewModel.navigateToMainScreen(com.example.auth.MainScreen.SEARCH)
        assertEquals(com.example.auth.MainScreen.SEARCH, viewModel.uiState.value.currentMainScreen)

        viewModel.navigateToMainScreen(com.example.auth.MainScreen.CREATE_POST)
        assertEquals(com.example.auth.MainScreen.CREATE_POST, viewModel.uiState.value.currentMainScreen)

        viewModel.navigateToMainScreen(com.example.auth.MainScreen.NOTIFICATIONS)
        assertEquals(com.example.auth.MainScreen.NOTIFICATIONS, viewModel.uiState.value.currentMainScreen)

        viewModel.navigateToMainScreen(com.example.auth.MainScreen.PROFILE)
        assertEquals(com.example.auth.MainScreen.PROFILE, viewModel.uiState.value.currentMainScreen)

        viewModel.navigateToMainScreen(com.example.auth.MainScreen.HOME)
        assertEquals(com.example.auth.MainScreen.HOME, viewModel.uiState.value.currentMainScreen)
    }
}
