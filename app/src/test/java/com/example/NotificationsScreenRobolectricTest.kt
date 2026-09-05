package com.example

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.example.auth.AuthUiState
import com.example.auth.AuthViewModel
import com.example.model.NotificationItem
import com.example.model.NotificationType
import com.example.model.getInitialNotifications
import com.example.ui.NotificationsScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NotificationsScreenRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNotificationsFlowAndInteractions() {
        val viewModel = AuthViewModel()
        val uiState = AuthUiState(
            isLoggedIn = true,
            currentUserEmail = "test@example.com",
            currentUserName = "Test User"
        )

        val notificationsList = mutableStateListOf<NotificationItem>().apply {
            addAll(getInitialNotifications())
        }

        composeTestRule.setContent {
            MyApplicationTheme {
                NotificationsScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    notifications = notificationsList
                )
            }
        }

        // Screen and title are displayed
        composeTestRule.onNodeWithTag("notifications_screen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notifications").assertIsDisplayed()

        // Verify Like notification (Sophia Lin)
        composeTestRule.onNodeWithTag("notif_item_notif_1").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Sophia Lin")[0].assertIsDisplayed()

        // Verify Comment notification (Marcus Vance)
        composeTestRule.onNodeWithTag("notif_item_notif_2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Marcus Vance").assertIsDisplayed()

        // Verify Follow notification (Elena Gomez)
        composeTestRule.onNodeWithTag("notif_item_notif_3").assertIsDisplayed()
        composeTestRule.onNodeWithText("Elena Gomez").assertIsDisplayed()

        // Verify unread indicator exists on notif_1
        composeTestRule.onNodeWithTag("unread_dot_notif_1", useUnmergedTree = true).assertExists()

        // Tap on Mark all as read
        composeTestRule.onNodeWithTag("mark_all_as_read_button").performClick()
        composeTestRule.waitForIdle()

        // All unread dots should be gone
        composeTestRule.onNodeWithTag("unread_dot_notif_1", useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNodeWithTag("unread_dot_notif_2", useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNodeWithTag("unread_dot_notif_3", useUnmergedTree = true).assertDoesNotExist()

        // All caught up card should now appear
        composeTestRule.onNodeWithTag("notifications_list").performScrollToNode(hasTestTag("all_caught_up_card"))
        composeTestRule.onNodeWithTag("all_caught_up_card").assertIsDisplayed()
    }

    @Test
    fun testEmptyNotificationsState() {
        val viewModel = AuthViewModel()
        val uiState = AuthUiState(
            isLoggedIn = true,
            currentUserEmail = "test@example.com",
            currentUserName = "Test User"
        )

        val emptyNotifications = mutableStateListOf<NotificationItem>()

        composeTestRule.setContent {
            MyApplicationTheme {
                NotificationsScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    notifications = emptyNotifications
                )
            }
        }

        // Verify empty view is displayed with expected text
        composeTestRule.onNodeWithTag("empty_notifications_view").assertIsDisplayed()
        composeTestRule.onNodeWithText("No notifications yet").assertIsDisplayed()
    }
}
