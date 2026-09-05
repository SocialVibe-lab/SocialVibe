package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.auth.AuthUiState
import com.example.auth.AuthViewModel
import com.example.ui.SearchScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SearchScreenRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSearchFlow() {
        val viewModel = AuthViewModel()
        val uiState = AuthUiState(
            isLoggedIn = true,
            currentUserEmail = "test@example.com",
            currentUserName = "Test User"
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                SearchScreen(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }

        // Initially trending topics is displayed
        composeTestRule.onNodeWithTag("search_bar_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("trending_topics_section").assertIsDisplayed()

        // 1. Search for "sophia" -> matches Sophia Lin
        composeTestRule.onNodeWithTag("search_bar_input").performTextInput("sophia")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Sophia Lin").assertIsDisplayed()

        // 2. Clear search using clear button
        composeTestRule.onNodeWithTag("clear_search_button").performClick()
        composeTestRule.waitForIdle()

        // 3. Search for post text directly: "productive" -> matches "Having a productive week!"
        composeTestRule.onNodeWithTag("search_bar_input").performTextInput("productive")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Posts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Having a productive week! Knocked out all major design reviews early. Coffee + code session continues. ☕💻").assertIsDisplayed()

        // 4. Clear and search for "golden"
        composeTestRule.onNodeWithTag("clear_search_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("search_bar_input").performTextInput("golden")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Elena Gomez").assertIsDisplayed()

        // 5. Clear and search for "xyz123" -> no results
        composeTestRule.onNodeWithTag("clear_search_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("search_bar_input").performTextInput("xyz123")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("No results found").assertIsDisplayed()
        composeTestRule.onNodeWithText("No matching people or posts found.").assertIsDisplayed()
    }
}
