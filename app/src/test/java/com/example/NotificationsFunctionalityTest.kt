package com.example

import com.example.model.NotificationItem
import com.example.model.NotificationType
import com.example.model.getInitialNotifications
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationsFunctionalityTest {

    @Test
    fun `initial notifications have like, comment, and follow types`() {
        val notifications = getInitialNotifications()
        assertTrue("Notifications list should not be empty", notifications.isNotEmpty())

        val likeNotifications = notifications.filter { it.type == NotificationType.LIKE }
        val commentNotifications = notifications.filter { it.type == NotificationType.COMMENT }
        val followNotifications = notifications.filter { it.type == NotificationType.FOLLOW }

        assertTrue("Should include like notifications", likeNotifications.isNotEmpty())
        assertTrue("Should include comment notifications", commentNotifications.isNotEmpty())
        assertTrue("Should include follow notifications", followNotifications.isNotEmpty())
    }

    @Test
    fun `unread notifications count and mark as read functionality`() {
        val notifications = getInitialNotifications().toMutableList()
        val initialUnreadCount = notifications.count { !it.isRead }
        assertTrue("Initial list should contain unread notifications", initialUnreadCount > 0)

        // Find first unread notification
        val unreadIndex = notifications.indexOfFirst { !it.isRead }
        assertTrue(unreadIndex != -1)

        val unreadNotif = notifications[unreadIndex]
        assertFalse(unreadNotif.isRead)

        // Mark it as read
        notifications[unreadIndex] = unreadNotif.copy(isRead = true)
        val newUnreadCount = notifications.count { !it.isRead }
        assertEquals(initialUnreadCount - 1, newUnreadCount)
    }

    @Test
    fun `mark all as read clears all unread notifications`() {
        val notifications = getInitialNotifications().toMutableList()
        assertTrue("Should have unread items", notifications.any { !it.isRead })

        // Mark all as read
        for (i in notifications.indices) {
            if (!notifications[i].isRead) {
                notifications[i] = notifications[i].copy(isRead = true)
            }
        }

        val unreadCountAfter = notifications.count { !it.isRead }
        assertEquals(0, unreadCountAfter)
        assertTrue(notifications.all { it.isRead })
    }

    @Test
    fun `comment notification contains comment preview text`() {
        val commentNotifications = getInitialNotifications().filter { it.type == NotificationType.COMMENT }
        assertTrue(commentNotifications.isNotEmpty())

        for (notif in commentNotifications) {
            assertNotNull("Comment notification must have a comment preview", notif.commentPreview)
            assertTrue("Comment preview must not be blank", notif.commentPreview!!.isNotBlank())
        }
    }

    @Test
    fun `like notification links to related post`() {
        val likeNotifications = getInitialNotifications().filter { it.type == NotificationType.LIKE }
        assertTrue(likeNotifications.isNotEmpty())

        for (notif in likeNotifications) {
            assertNotNull("Like notification must have relatedPostId", notif.relatedPostId)
            assertNotNull("Like notification should have post preview text", notif.postPreviewText)
        }
    }
}
