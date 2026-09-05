package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToString
import com.example.model.Comment
import com.example.model.CommentReply
import com.example.model.Post
import com.example.ui.PostCard
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CommentReplyRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCommentReplyFlowAndSeparation() {
        val testPost = Post(
            id = "test_post_1",
            authorName = "Sophia Lin",
            authorHandle = "@sophialin",
            timeAgo = "1h ago",
            text = "Testing comment reply feature!",
            initialLikeCount = 10,
            initialCommentCount = 2,
            initialComments = listOf(
                Comment(
                    id = "comment_a",
                    authorName = "Marcus Vance",
                    authorHandle = "@marcusv",
                    text = "First comment by Marcus"
                ),
                Comment(
                    id = "comment_b",
                    authorName = "Elena Gomez",
                    authorHandle = "@elenag",
                    text = "Second comment by Elena"
                )
            )
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    PostCard(
                        post = testPost,
                        currentUserName = "Alex Morgan",
                        currentUserHandle = "@alexm",
                        onSharePost = {}
                    )
                }
            }
        }

        // 1. Post is displayed
        composeTestRule.onNodeWithTag("post_card_test_post_1").assertIsDisplayed()

        // 2. Open comments section
        composeTestRule.onNodeWithTag("comment_button_test_post_1").performClick()
        composeTestRule.onNodeWithTag("comment_section_test_post_1").assertIsDisplayed()

        // Both comments are visible
        composeTestRule.onNodeWithTag("comment_item_comment_a").assertIsDisplayed()
        composeTestRule.onNodeWithTag("comment_item_comment_b").assertIsDisplayed()

        // 3. Tap Reply on comment A
        composeTestRule.onNodeWithTag("reply_button_comment_a").performClick()

        // Verify reply input is shown for comment A and displays "Replying to Marcus Vance"
        composeTestRule.onNodeWithTag("reply_input_container_comment_a").assertIsDisplayed()
        composeTestRule.onNodeWithText("Replying to Marcus Vance").assertIsDisplayed()

        // Verify send button is initially disabled when input is empty
        composeTestRule.onNodeWithTag("submit_reply_button_comment_a").assertIsNotEnabled()

        // 4. Write a reply to comment A and submit
        composeTestRule.onNodeWithTag("reply_input_comment_a").performTextInput("Great point Marcus!")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("submit_reply_button_comment_a").assertIsEnabled()
        composeTestRule.onNodeWithTag("submit_reply_button_comment_a").performClick()
        composeTestRule.waitForIdle()

        // 5. Confirm the reply appears directly under comment A
        composeTestRule.onNodeWithTag("replies_container_comment_a").assertExists()
        composeTestRule.onNodeWithText("Great point Marcus!").assertExists()
        composeTestRule.onNodeWithText("Alex Morgan").assertExists()

        // Verify replies container for comment B does NOT exist yet
        composeTestRule.onNodeWithTag("replies_container_comment_b").assertDoesNotExist()

        // 6. Add a second reply to the same comment A
        composeTestRule.onNodeWithTag("reply_button_comment_a").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("reply_input_comment_a").performTextInput("Second reply to Marcus!")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("submit_reply_button_comment_a").performClick()
        composeTestRule.waitForIdle()

        // 7. Confirm both replies appear correctly under comment A
        composeTestRule.onNodeWithText("Great point Marcus!").assertExists()
        composeTestRule.onNodeWithText("Second reply to Marcus!").assertExists()

        // Toggle button shows "Hide replies"
        composeTestRule.onNodeWithTag("toggle_replies_button_comment_a").assertExists()
        composeTestRule.onNodeWithText("Hide replies").assertExists()

        // 8. Test Reply on comment B and confirm it stays separate
        composeTestRule.onNodeWithTag("reply_button_comment_b").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("reply_input_container_comment_b").assertExists()
        composeTestRule.onNodeWithText("Replying to Elena Gomez").assertExists()

        composeTestRule.onNodeWithTag("reply_input_comment_b").performScrollTo().performTextInput("Hello Elena!")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("submit_reply_button_comment_b").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Confirm Elena's reply is displayed under comment B
        composeTestRule.onNodeWithTag("replies_container_comment_b").assertExists()
        composeTestRule.onNodeWithText("Hello Elena!").assertExists()

        // Confirm Comment B's replies does NOT contain Marcus's replies
        // Test that toggling "Hide replies" on Comment A works
        composeTestRule.onNodeWithTag("toggle_replies_button_comment_a").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("replies_container_comment_a").assertDoesNotExist()
        composeTestRule.onNodeWithText("View 2 replies").assertExists()

        // Comment B's replies are still visible
        composeTestRule.onNodeWithTag("replies_container_comment_b").assertExists()
        composeTestRule.onNodeWithText("Hello Elena!").assertExists()

        // Re-expand Comment A replies
        composeTestRule.onNodeWithTag("toggle_replies_button_comment_a").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("replies_container_comment_a").assertExists()
        composeTestRule.onNodeWithText("Great point Marcus!").assertExists()
    }

    @Test
    fun testRegularCommentAndLikeStillWork() {
        val testPost = Post(
            id = "test_post_2",
            authorName = "David Kim",
            authorHandle = "@dkim",
            timeAgo = "3h ago",
            text = "Testing like and normal comments",
            initialLikeCount = 5,
            initialCommentCount = 0,
            initialComments = emptyList()
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                PostCard(
                    post = testPost,
                    currentUserName = "Alex Morgan",
                    currentUserHandle = "@alexm",
                    onSharePost = {}
                )
            }
        }

        // Like/Unlike test
        composeTestRule.onNodeWithTag("like_button_test_post_2").performClick()
        composeTestRule.onNodeWithText("6").assertIsDisplayed()

        // Open comments
        composeTestRule.onNodeWithTag("comment_button_test_post_2").performClick()
        composeTestRule.onNodeWithTag("comment_input_test_post_2").performTextInput("Brand new top-level comment")
        composeTestRule.onNodeWithTag("submit_comment_button_test_post_2").performClick()

        // Verify top-level comment appears
        composeTestRule.onNodeWithText("Brand new top-level comment").assertIsDisplayed()
    }
}
