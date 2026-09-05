package com.example

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.auth.AuthViewModel
import com.example.ui.EditProfileDialog
import com.example.ui.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EditProfileAndSettingsRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testEditProfileSaveUpdatesCorrectly() {
        var savedName = ""
        var savedUsername = ""
        var savedBio = ""
        var savedAvatarStyle = -1
        var dismissed = false

        composeTestRule.setContent {
            MyApplicationTheme {
                EditProfileDialog(
                    initialName = "Taylor",
                    initialUsername = "@taylor",
                    initialBio = "Initial Bio",
                    initialAvatarStyle = 0,
                    onDismiss = { dismissed = true },
                    onSave = { name, username, bio, avatarStyle ->
                        savedName = name
                        savedUsername = username
                        savedBio = bio
                        savedAvatarStyle = avatarStyle
                    }
                )
            }
        }

        // Change name
        composeTestRule.onNodeWithTag("edit_profile_name_input").performTextClearance()
        composeTestRule.onNodeWithTag("edit_profile_name_input").performTextInput("Taylor Swift")

        // Change username
        composeTestRule.onNodeWithTag("edit_profile_username_input").performTextClearance()
        composeTestRule.onNodeWithTag("edit_profile_username_input").performTextInput("taylorswift")

        // Change bio
        composeTestRule.onNodeWithTag("edit_profile_bio_input").performTextClearance()
        composeTestRule.onNodeWithTag("edit_profile_bio_input").performTextInput("Music and coffee enthusiast ☕")

        // Open avatar palette and choose style 2
        composeTestRule.onNodeWithTag("change_avatar_style_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("avatar_picker_palette").assertExists()
        composeTestRule.onNodeWithTag("avatar_style_option_2").performScrollTo().performClick()

        // Save
        composeTestRule.onNodeWithTag("save_profile_button").performClick()

        assertEquals("Taylor Swift", savedName)
        assertEquals("@taylorswift", savedUsername)
        assertEquals("Music and coffee enthusiast ☕", savedBio)
        assertEquals(2, savedAvatarStyle)
        assertFalse(dismissed)
    }

    @Test
    fun testEditProfileUnsavedChangesWarningOnCancel() {
        var dismissed = false

        composeTestRule.setContent {
            MyApplicationTheme {
                EditProfileDialog(
                    initialName = "Taylor",
                    initialUsername = "@taylor",
                    initialBio = "Initial Bio",
                    initialAvatarStyle = 0,
                    onDismiss = { dismissed = true },
                    onSave = { _, _, _, _ -> }
                )
            }
        }

        // Make an edit first
        composeTestRule.onNodeWithTag("edit_profile_name_input").performTextInput(" Edited")

        // Click cancel
        composeTestRule.onNodeWithTag("cancel_edit_profile_button").performClick()

        // Warning dialog should appear
        composeTestRule.onNodeWithTag("unsaved_changes_dialog").assertExists()
        composeTestRule.onNodeWithTag("unsaved_changes_title").assertExists()
        assertFalse(dismissed)

        // Click Keep Editing
        composeTestRule.onNodeWithTag("keep_editing_button").performClick()
        composeTestRule.onNodeWithTag("edit_profile_name_input").assertExists()
        assertFalse(dismissed)

        // Click Cancel again and Discard
        composeTestRule.onNodeWithTag("cancel_edit_profile_button").performClick()
        composeTestRule.onNodeWithTag("discard_changes_button").performClick()
        assertTrue(dismissed)
    }

    @Test
    fun testSettingsProfileVisibilityToggle() {
        val viewModel = AuthViewModel()
        viewModel.onEmailChange("alex@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        composeTestRule.setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()
                SettingsScreen(
                    uiState = state,
                    viewModel = viewModel
                )
            }
        }

        // Verify Privacy Card
        composeTestRule.onNodeWithTag("settings_privacy_card").assertExists()
        composeTestRule.onNodeWithTag("setting_profile_visibility_switch").assertExists()

        // Toggle profile visibility to Private
        assertFalse(viewModel.uiState.value.isProfilePrivate)
        composeTestRule.onNodeWithTag("setting_profile_visibility_switch").performClick()
        composeTestRule.waitForIdle()
        assertTrue(viewModel.uiState.value.isProfilePrivate)
    }

    @Test
    fun testSettingsWhoCanFollow() {
        val viewModel = AuthViewModel()
        viewModel.onEmailChange("alex@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        composeTestRule.setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()
                SettingsScreen(
                    uiState = state,
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithTag("setting_who_can_follow_item").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("who_can_follow_dialog").assertExists()
        composeTestRule.onNodeWithText("Followers Only").performClick()
        composeTestRule.onNodeWithTag("confirm_selection_button").performClick()
        composeTestRule.waitForIdle()
        assertEquals("Followers Only", viewModel.uiState.value.whoCanFollowMe)
    }

    @Test
    fun testSettingsWhoCanMessage() {
        val viewModel = AuthViewModel()
        viewModel.onEmailChange("alex@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        composeTestRule.setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()
                SettingsScreen(
                    uiState = state,
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithTag("setting_who_can_message_item").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("who_can_message_dialog").assertExists()
        composeTestRule.onNodeWithText("No One").performClick()
        composeTestRule.onNodeWithTag("confirm_selection_button").performClick()
        composeTestRule.waitForIdle()
        assertEquals("No One", viewModel.uiState.value.whoCanMessageMe)
    }

    @Test
    fun testSettingsChangePassword() {
        val viewModel = AuthViewModel()
        viewModel.onEmailChange("alex@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        composeTestRule.setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()
                SettingsScreen(
                    uiState = state,
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithTag("setting_change_password_item").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("change_password_dialog").assertExists()
        composeTestRule.onNodeWithTag("current_password_input").performTextInput("password123")
        composeTestRule.onNodeWithTag("new_password_input").performTextInput("newSecret456")
        composeTestRule.onNodeWithTag("confirm_new_password_input").performTextInput("newSecret456")
        composeTestRule.onNodeWithTag("save_password_button").performClick()
        composeTestRule.waitForIdle()
        assertEquals("newSecret456", viewModel.uiState.value.password)
    }

    @Test
    fun testSettingsChangeEmail() {
        val viewModel = AuthViewModel()
        viewModel.onEmailChange("alex@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        composeTestRule.setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()
                SettingsScreen(
                    uiState = state,
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithTag("setting_change_email_item").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("change_email_dialog").assertExists()
        composeTestRule.onNodeWithTag("new_email_input").performTextClearance()
        composeTestRule.onNodeWithTag("new_email_input").performTextInput("alex.updated@vibe.io")
        composeTestRule.onNodeWithTag("save_email_button").performClick()
        composeTestRule.waitForIdle()
        assertEquals("alex.updated@vibe.io", viewModel.uiState.value.currentUserEmail)
    }

    @Test
    fun testSettingsPhonePlaceholder() {
        val viewModel = AuthViewModel()
        viewModel.onEmailChange("alex@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        composeTestRule.setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()
                SettingsScreen(
                    uiState = state,
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithTag("setting_change_phone_item").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("phone_placeholder_dialog").assertExists()
        composeTestRule.onNodeWithTag("phone_placeholder_field").assertExists()
        composeTestRule.onNodeWithTag("phone_dialog_dismiss_button").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun testLogoutConfirmationDialog() {
        val viewModel = AuthViewModel()
        viewModel.onEmailChange("alex@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()
        assertTrue(viewModel.uiState.value.isAuthenticated)

        composeTestRule.setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()
                SettingsScreen(
                    uiState = state,
                    viewModel = viewModel
                )
            }
        }

        // Open Logout dialog
        composeTestRule.onNodeWithTag("setting_logout_row").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("logout_confirm_dialog").assertExists()

        // Cancel logout first
        composeTestRule.onNodeWithTag("cancel_logout_button").performClick()
        composeTestRule.waitForIdle()
        assertTrue(viewModel.uiState.value.isAuthenticated)

        // Open again and confirm logout
        composeTestRule.onNodeWithTag("setting_logout_row").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("confirm_logout_button").performClick()
        composeTestRule.waitForIdle()
        assertFalse(viewModel.uiState.value.isAuthenticated)
    }

    @Test
    fun testChangePasswordValidationMismatched() {
        val viewModel = AuthViewModel()
        viewModel.onEmailChange("alex@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        composeTestRule.setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()
                SettingsScreen(
                    uiState = state,
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithTag("setting_change_password_item").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("change_password_dialog").assertExists()

        composeTestRule.onNodeWithTag("current_password_input").performTextInput("password123")
        composeTestRule.onNodeWithTag("new_password_input").performTextInput("newSecret99")
        composeTestRule.onNodeWithTag("confirm_new_password_input").performTextInput("differentSecret")

        composeTestRule.onNodeWithTag("save_password_button").performClick()
        composeTestRule.waitForIdle()

        // Dialog should remain visible and error banner shown
        composeTestRule.onNodeWithTag("change_password_error_banner").assertExists()
        assertEquals("password123", viewModel.uiState.value.password)

        // Now fix confirm password
        composeTestRule.onNodeWithTag("confirm_new_password_input").performTextClearance()
        composeTestRule.onNodeWithTag("confirm_new_password_input").performTextInput("newSecret99")
        composeTestRule.onNodeWithTag("save_password_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals("newSecret99", viewModel.uiState.value.password)
    }

    @Test
    fun testChangeEmailValidation() {
        val viewModel = AuthViewModel()
        viewModel.onEmailChange("alex@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()

        composeTestRule.setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()
                SettingsScreen(
                    uiState = state,
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithTag("setting_change_email_item").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("change_email_dialog").assertExists()

        // Try invalid email format
        composeTestRule.onNodeWithTag("new_email_input").performTextClearance()
        composeTestRule.onNodeWithTag("new_email_input").performTextInput("notanemail")
        composeTestRule.onNodeWithTag("save_email_button").performClick()
        composeTestRule.waitForIdle()

        // Dialog should still exist and show error banner
        composeTestRule.onNodeWithTag("change_email_error_banner").assertExists()
        assertEquals("alex@example.com", viewModel.uiState.value.currentUserEmail)

        // Cancel dialog
        composeTestRule.onNodeWithTag("cancel_email_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("change_email_dialog").assertDoesNotExist()
    }
}
