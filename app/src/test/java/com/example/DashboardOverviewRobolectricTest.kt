package com.example

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.auth.AuthUiState
import com.example.auth.AuthViewModel
import com.example.model.Post
import com.example.ui.DashboardOverviewSection
import com.example.ui.ProfileScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DashboardOverviewRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDashboardOverviewCardsDisplayCorrectly() {
        var clickedStat: String? = null

        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardOverviewSection(
                    totalPosts = 14,
                    followersCount = 420,
                    followingCount = 180,
                    profileViewsCount = 1240,
                    onCardClick = { title, _ -> clickedStat = title }
                )
            }
        }

        // Verify section header
        composeTestRule.onNodeWithTag("dashboard_overview_section").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dashboard Overview").assertIsDisplayed()

        // Card 1: Total Posts
        composeTestRule.onNodeWithTag("dashboard_card_total_posts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Posts").assertIsDisplayed()
        composeTestRule.onNodeWithText("14").assertIsDisplayed()

        // Card 2: Followers
        composeTestRule.onNodeWithTag("dashboard_card_followers").assertIsDisplayed()
        composeTestRule.onNodeWithText("Followers").assertIsDisplayed()
        composeTestRule.onNodeWithText("420").assertIsDisplayed()

        // Card 3: Following
        composeTestRule.onNodeWithTag("dashboard_card_following").assertIsDisplayed()
        composeTestRule.onNodeWithText("Following").assertIsDisplayed()
        composeTestRule.onNodeWithText("180").assertIsDisplayed()

        // Card 4: Profile Views
        composeTestRule.onNodeWithTag("dashboard_card_profile_views").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile Views").assertIsDisplayed()
        composeTestRule.onNodeWithText("1,240").assertIsDisplayed()

        // Card 5: Engagement (Step 2)
        composeTestRule.onNodeWithTag("dashboard_card_engagement").assertIsDisplayed()
        composeTestRule.onNodeWithText("Engagement").assertIsDisplayed()
        composeTestRule.onNodeWithText("4.8%").assertIsDisplayed()

        // Card 6: Followers Gained (Step 2)
        composeTestRule.onNodeWithTag("dashboard_card_followers_gained").assertIsDisplayed()
        composeTestRule.onNodeWithText("Followers Gained").assertIsDisplayed()
        composeTestRule.onNodeWithText("+36").assertIsDisplayed()

        // Verify card clicking triggers callback
        composeTestRule.onNodeWithTag("dashboard_card_total_posts").performClick()
        assertEquals("Total Posts", clickedStat)

        composeTestRule.onNodeWithTag("dashboard_card_profile_views").performClick()
        assertEquals("Profile Views", clickedStat)

        composeTestRule.onNodeWithTag("dashboard_card_engagement").performClick()
        assertEquals("Engagement", clickedStat)

        composeTestRule.onNodeWithTag("dashboard_card_followers_gained").performClick()
        assertEquals("Followers Gained", clickedStat)
    }

    @Test
    fun testProfileScreenContainsDashboardNavigationAndOverview() {
        val authUiState = AuthUiState(
            isLoggedIn = true,
            currentUserEmail = "alex@socialvibe.io",
            currentUserName = "Alex Rivera",
            userBio = "Digital creator & vibe curator ✨",
            userFollowersCount = 348,
            userFollowingCount = 192
        )
        val viewModel = AuthViewModel()
        val posts = mutableStateListOf<Post>()

        composeTestRule.setContent {
            MyApplicationTheme {
                ProfileScreen(
                    uiState = authUiState,
                    viewModel = viewModel,
                    userPosts = posts
                )
            }
        }

        // Verify TopAppBar navigation button exists and is displayed
        composeTestRule.onNodeWithTag("dashboard_nav_button").assertIsDisplayed()

        // Verify Header Dashboard button exists and is displayed
        composeTestRule.onNodeWithTag("header_dashboard_button").assertIsDisplayed()

        // Verify existing profile elements are preserved
        composeTestRule.onNodeWithTag("edit_profile_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_avatar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_user_name").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_user_handle").assertIsDisplayed()

        // Click the dashboard navigation button to scroll to Dashboard Overview
        composeTestRule.onNodeWithTag("dashboard_nav_button").performClick()
        composeTestRule.waitForIdle()

        // Verify Dashboard overview section and cards exist in ProfileScreen
        composeTestRule.onNodeWithTag("dashboard_overview_section").assertExists()
        composeTestRule.onNodeWithTag("dashboard_card_total_posts").assertExists()
        composeTestRule.onNodeWithTag("dashboard_card_followers").assertExists()
        composeTestRule.onNodeWithTag("dashboard_card_following").assertExists()
        composeTestRule.onNodeWithTag("dashboard_card_profile_views").assertExists()
        composeTestRule.onNodeWithTag("dashboard_card_engagement").assertExists()
        composeTestRule.onNodeWithTag("dashboard_card_followers_gained").assertExists()
    }
}
