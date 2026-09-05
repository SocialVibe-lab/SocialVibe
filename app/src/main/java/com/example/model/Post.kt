package com.example.model

data class CommentReply(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val text: String,
    val timeAgo: String = "Just now"
)

data class Comment(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val text: String,
    val timeAgo: String = "Just now",
    val replies: List<CommentReply> = emptyList()
)

data class Post(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val timeAgo: String,
    val text: String,
    val imageResId: Int? = null,
    val imageUri: String? = null,
    val initialLikeCount: Int = 0,
    val initialCommentCount: Int = 0,
    val isLiked: Boolean = false,
    val initialComments: List<Comment> = emptyList()
)
