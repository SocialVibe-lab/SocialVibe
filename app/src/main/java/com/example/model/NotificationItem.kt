package com.example.model

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW
}

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val authorName: String,
    val authorHandle: String,
    val actionText: String,
    val commentPreview: String? = null,
    val postPreviewText: String? = null,
    val relatedPostId: String? = null,
    val timeAgo: String,
    val isRead: Boolean = false
)

fun getInitialNotifications(): List<NotificationItem> {
    return listOf(
        NotificationItem(
            id = "notif_1",
            type = NotificationType.LIKE,
            authorName = "Sophia Lin",
            authorHandle = "@sophialin",
            actionText = "liked your post",
            postPreviewText = "Catching the golden hour vibes today! ✨🌅",
            relatedPostId = "post_1",
            timeAgo = "15m ago",
            isRead = false
        ),
        NotificationItem(
            id = "notif_2",
            type = NotificationType.COMMENT,
            authorName = "Marcus Vance",
            authorHandle = "@marcusv",
            actionText = "commented on your post",
            commentPreview = "Succulent + keyboard combo is peak focus 🙌",
            postPreviewText = "Sunday study and focus session with a refreshing iced matcha latte.",
            relatedPostId = "post_2",
            timeAgo = "1h ago",
            isRead = false
        ),
        NotificationItem(
            id = "notif_3",
            type = NotificationType.FOLLOW,
            authorName = "Elena Gomez",
            authorHandle = "@elenag",
            actionText = "started following you",
            timeAgo = "3h ago",
            isRead = false
        ),
        NotificationItem(
            id = "notif_4",
            type = NotificationType.LIKE,
            authorName = "Jackson",
            authorHandle = "@jackson",
            actionText = "liked your post",
            postPreviewText = "Big things happening soon on Social Vibe! Keep chasing your vision. 🚀💡",
            relatedPostId = "post_3",
            timeAgo = "5h ago",
            isRead = true
        ),
        NotificationItem(
            id = "notif_5",
            type = NotificationType.COMMENT,
            authorName = "Sophia Lin",
            authorHandle = "@sophialin",
            actionText = "commented on your post",
            commentPreview = "Love the warm sunset colors, where was this taken? 🌅",
            postPreviewText = "Catching the golden hour vibes today! ✨🌅",
            relatedPostId = "post_1",
            timeAgo = "1d ago",
            isRead = true
        ),
        NotificationItem(
            id = "notif_6",
            type = NotificationType.FOLLOW,
            authorName = "David Kim",
            authorHandle = "@davidk",
            actionText = "started following you",
            timeAgo = "2d ago",
            isRead = true
        )
    )
}
