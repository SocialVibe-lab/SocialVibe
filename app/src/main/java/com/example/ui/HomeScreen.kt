package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import coil.compose.AsyncImage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.auth.AuthUiState
import com.example.auth.AuthViewModel
import com.example.model.Comment
import com.example.model.CommentReply
import com.example.model.Post

fun getInitialPosts(): List<Post> = listOf(
    Post(
        id = "post_1",
        authorName = "Sophia Lin",
        authorHandle = "@sophialin",
        timeAgo = "2h ago",
        text = "Catching the golden hour vibes today! Nothing beats a fresh breeze and good energy. ✨🌅 What are you all working on this week?",
        imageResId = R.drawable.img_sunset_vibe_1788453440646,
        initialLikeCount = 42,
        initialCommentCount = 2,
        initialComments = listOf(
            Comment(
                id = "c1_1",
                authorName = "Marcus Vance",
                authorHandle = "@marcusv",
                text = "Stunning view Sophia! Have a productive week! 🔥",
                timeAgo = "1h ago",
                replies = listOf(
                    CommentReply(
                        id = "rep_c1_1_1",
                        authorName = "Sophia Lin",
                        authorHandle = "@sophialin",
                        text = "Thanks Marcus! You too! ✨",
                        timeAgo = "45m ago"
                    )
                )
            ),
            Comment(
                id = "c1_2",
                authorName = "Elena Gomez",
                authorHandle = "@elenag",
                text = "Golden hour never fails. Love this vibe ✨",
                timeAgo = "30m ago"
            )
        )
    ),
    Post(
        id = "post_2",
        authorName = "Marcus Vance",
        authorHandle = "@marcusv",
        timeAgo = "4h ago",
        text = "Sunday study and focus session with a refreshing iced matcha latte. Loving the community vibe here! ☕📚",
        imageResId = R.drawable.img_coffee_study_1788453460368,
        initialLikeCount = 27,
        initialCommentCount = 1,
        initialComments = listOf(
            Comment(
                id = "c2_1",
                authorName = "David Kim",
                authorHandle = "@dkim",
                text = "Matcha latte is unmatched for morning coding sessions 🙌",
                timeAgo = "2h ago"
            )
        )
    ),
    Post(
        id = "post_3",
        authorName = "Elena Gomez",
        authorHandle = "@elenag",
        timeAgo = "6h ago",
        text = "Just wrapped up a creative brainstorming session. Big things happening soon on Social Vibe! Keep chasing your vision. 🚀💡",
        imageResId = null,
        initialLikeCount = 19,
        initialCommentCount = 0,
        initialComments = emptyList()
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: AuthUiState,
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    samplePosts: androidx.compose.runtime.snapshots.SnapshotStateList<Post> = remember {
        mutableStateListOf<Post>().apply { addAll(getInitialPosts()) }
    },
    userPosts: androidx.compose.runtime.snapshots.SnapshotStateList<Post>? = null
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var newPostText by remember { mutableStateOf("") }

    val currentUserName = uiState.currentUserName?.ifBlank { null }
        ?: uiState.currentUserEmail?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
        ?: "Alex"

    val currentUserHandle = "@" + (uiState.currentUserEmail?.substringBefore("@") ?: "user")

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar / Header
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        // Social Vibe Logo
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_app_logo),
                                contentDescription = stringResource(id = R.string.app_logo_desc),
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Social Vibe Brand Name
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    // Profile navigation button in header
                    IconButton(
                        onClick = { viewModel.navigateToProfile() },
                        modifier = Modifier.testTag("profile_nav_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(id = R.string.profile_title),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Sign out icon button in header
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(id = R.string.sign_out),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(2.dp)
            )

            // Feed Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                            )
                        )
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // User Profile Header Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(20.dp))
                                .clickable { viewModel.navigateToProfile() }
                                .testTag("user_profile_area"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Profile Avatar
                                UserAvatar(
                                    name = currentUserName,
                                    size = 50.dp
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentUserName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = currentUserHandle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // View Profile chip
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("view_profile_chip")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = stringResource(id = R.string.view_profile),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Create Post Section
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(3.dp, RoundedCornerShape(20.dp))
                                .testTag("create_post_section"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    UserAvatar(
                                        name = currentUserName,
                                        size = 42.dp
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // "What's on your mind?" Input
                                    OutlinedTextField(
                                        value = newPostText,
                                        onValueChange = { newPostText = it },
                                        placeholder = {
                                            Text(
                                                text = stringResource(id = R.string.whats_on_your_mind),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("whats_on_your_mind_input"),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                        ),
                                        maxLines = 4,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Primary "Post" Button
                                    Button(
                                        onClick = {
                                            val trimmed = newPostText.trim()
                                            if (trimmed.isNotEmpty()) {
                                                val newPost = Post(
                                                    id = "user_post_${System.currentTimeMillis()}",
                                                    authorName = currentUserName,
                                                    authorHandle = currentUserHandle,
                                                    timeAgo = "Just now",
                                                    text = trimmed,
                                                    imageResId = null,
                                                    initialLikeCount = 0,
                                                    initialCommentCount = 0,
                                                    initialComments = emptyList()
                                                )
                                                samplePosts.add(0, newPost)
                                                userPosts?.add(0, newPost)
                                                newPostText = ""
                                                focusManager.clearFocus()
                                            }
                                        },
                                        enabled = newPostText.trim().isNotEmpty(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier
                                            .height(42.dp)
                                            .testTag("create_post_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = stringResource(id = R.string.create_post_button),
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Feed Section Title
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Vibes",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Feed Items
                    items(
                        items = samplePosts,
                        key = { it.id }
                    ) { post ->
                        PostCard(
                            post = post,
                            currentUserName = currentUserName,
                            currentUserHandle = currentUserHandle,
                            onSharePost = { postToShare ->
                                sharePostContent(context, postToShare)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

/**
 * Triggers native Android share sheet with fallback to clipboard copying.
 */
fun sharePostContent(context: Context, post: Post) {
    val shareText = "Check out this vibe from ${post.authorName} on Social Vibe:\n\n\"${post.text}\"\n\nhttps://socialvibe.app/posts/${post.id}"
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Social Vibe Post by ${post.authorName}")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val chooser = Intent.createChooser(sendIntent, "Share Vibe via")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    try {
        context.startActivity(chooser)
    } catch (e: Exception) {
        // Fallback: Copy to clipboard if no target activity found
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText("Social Vibe Post", shareText)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(context, "Post copied to clipboard!", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to share post at this time.", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    currentUserName: String,
    currentUserHandle: String,
    onSharePost: (Post) -> Unit,
    modifier: Modifier = Modifier
) {
    // Like state preserved exactly as before
    var isLiked by remember(post.id) { mutableStateOf(post.isLiked) }
    var likeCount by remember(post.id) { mutableStateOf(post.initialLikeCount) }

    // Comment state & list - independent per post
    var isCommentSectionExpanded by remember(post.id) { mutableStateOf(false) }
    var commentInputText by remember(post.id) { mutableStateOf("") }
    val postComments = remember(post.id) {
        mutableStateListOf<Comment>().apply {
            addAll(post.initialComments)
        }
    }

    val likeTint by animateColorAsState(
        targetValue = if (isLiked) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "like_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Profile picture, User name, Handle, Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    name = post.authorName,
                    size = 44.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorHandle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = post.timeAgo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post Text
            Text(
                text = post.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            // Optional Image Area
            if (post.imageUri != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    AsyncImage(
                        model = post.imageUri,
                        contentDescription = "Post image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else if (post.imageResId != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Image(
                        painter = painterResource(id = post.imageResId),
                        contentDescription = "Post image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 0.8.dp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Action Row: Like, Comment, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button & Count (UNCHANGED logic & feel)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (isLiked) {
                                isLiked = false
                                likeCount = maxOf(0, likeCount - 1)
                            } else {
                                isLiked = true
                                likeCount += 1
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("like_button_${post.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(id = R.string.like_button),
                        tint = likeTint,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (likeCount > 0) "$likeCount" else stringResource(id = R.string.like_button),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isLiked) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = likeTint
                    )
                }

                // Comment Button & Count (Expands comment panel)
                val commentActionColor = if (isCommentSectionExpanded) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isCommentSectionExpanded = !isCommentSectionExpanded
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("comment_button_${post.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Chat,
                        contentDescription = stringResource(id = R.string.comment_button),
                        tint = commentActionColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (postComments.isNotEmpty()) "${postComments.size}" else stringResource(id = R.string.comment_button),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isCommentSectionExpanded) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = commentActionColor
                    )
                }

                // Share Button (Triggers native Android share sheet with fallback)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onSharePost(post)
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("share_button_${post.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = stringResource(id = R.string.share_button),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(id = R.string.share_button),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expandable Comment Section for this post
            if (isCommentSectionExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .testTag("comment_section_${post.id}")
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        thickness = 0.6.dp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Comment Input Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            name = currentUserName,
                            size = 34.dp
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = commentInputText,
                            onValueChange = { commentInputText = it },
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.write_comment_placeholder),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_${post.id}"),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                            )
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Submit / Post Comment Button
                        IconButton(
                            onClick = {
                                val trimmed = commentInputText.trim()
                                if (trimmed.isNotEmpty()) {
                                    val newComment = Comment(
                                        id = "comm_${post.id}_${System.currentTimeMillis()}",
                                        authorName = currentUserName,
                                        authorHandle = currentUserHandle,
                                        text = trimmed,
                                        timeAgo = "Just now"
                                    )
                                    postComments.add(newComment)
                                    commentInputText = ""
                                }
                            },
                            enabled = commentInputText.trim().isNotEmpty(),
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (commentInputText.trim().isNotEmpty()) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .testTag("submit_comment_button_${post.id}")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = stringResource(id = R.string.post_comment_button),
                                tint = if (commentInputText.trim().isNotEmpty()) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                },
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Existing Comments List
                    if (postComments.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.no_comments_yet),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            postComments.forEach { comment ->
                                CommentItem(
                                    comment = comment,
                                    currentUserName = currentUserName,
                                    currentUserHandle = currentUserHandle,
                                    onAddReply = { commentId, replyText ->
                                        val commentIdx = postComments.indexOfFirst { it.id == commentId }
                                        if (commentIdx != -1) {
                                            val targetComment = postComments[commentIdx]
                                            val newReply = CommentReply(
                                                id = "rep_${targetComment.id}_${System.currentTimeMillis()}",
                                                authorName = currentUserName,
                                                authorHandle = currentUserHandle,
                                                text = replyText,
                                                timeAgo = "Just now"
                                            )
                                            postComments[commentIdx] = targetComment.copy(
                                                replies = targetComment.replies + newReply
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    currentUserName: String = "User",
    currentUserHandle: String = "@user",
    onAddReply: (commentId: String, text: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var isReplying by remember(comment.id) { mutableStateOf(false) }
    var replyInputText by remember(comment.id) { mutableStateOf("") }
    var showReplies by remember(comment.id) { mutableStateOf(true) }
    val commentReplies = remember(comment.id) {
        mutableStateListOf<CommentReply>().apply {
            addAll(comment.replies)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("comment_item_${comment.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            UserAvatar(
                name = comment.authorName,
                size = 32.dp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Comment bubble
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = comment.authorName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = comment.timeAgo,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = comment.text,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Actions below comment: Reply button & View/Hide replies toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reply Button
                    Text(
                        text = stringResource(id = R.string.reply_button),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                isReplying = !isReplying
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("reply_button_${comment.id}")
                    )

                    // View replies / Hide replies toggle option
                    if (commentReplies.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    showReplies = !showReplies
                                }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("toggle_replies_button_${comment.id}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (showReplies) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val repliesLabel = if (showReplies) {
                                stringResource(id = R.string.hide_replies)
                            } else {
                                if (commentReplies.size == 1) {
                                    stringResource(id = R.string.view_replies_singular)
                                } else {
                                    stringResource(id = R.string.view_replies_plural, commentReplies.size)
                                }
                            }
                            Text(
                                text = repliesLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Tapping Reply opens a reply input for that specific comment
        if (isReplying) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 8.dp, bottom = 4.dp)
                    .testTag("reply_input_container_${comment.id}")
            ) {
                // Header showing selected comment author being replied to + cancel button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Reply,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.replying_to, comment.authorName),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.cancel_reply_button),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                isReplying = false
                                replyInputText = ""
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("cancel_reply_button_${comment.id}")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Reply input & Send button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(
                        name = currentUserName,
                        size = 28.dp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = replyInputText,
                        onValueChange = { replyInputText = it },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.write_reply_placeholder),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reply_input_${comment.id}"),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            val trimmed = replyInputText.trim()
                            if (trimmed.isNotEmpty()) {
                                val newReply = CommentReply(
                                    id = "rep_${comment.id}_${System.currentTimeMillis()}",
                                    authorName = currentUserName,
                                    authorHandle = currentUserHandle,
                                    text = trimmed,
                                    timeAgo = "Just now"
                                )
                                commentReplies.add(newReply)
                                onAddReply(comment.id, trimmed)
                                replyInputText = ""
                                isReplying = false
                                showReplies = true
                            }
                        },
                        enabled = replyInputText.trim().isNotEmpty(),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (replyInputText.trim().isNotEmpty()) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .testTag("submit_reply_button_${comment.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(id = R.string.send_reply_button),
                            tint = if (replyInputText.trim().isNotEmpty()) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        // Display replies underneath parent comment, visually indented
        if (showReplies && commentReplies.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 8.dp)
                    .testTag("replies_container_${comment.id}"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                commentReplies.forEach { reply ->
                    ReplyItem(reply = reply)
                }
            }
        }
    }
}

@Composable
fun ReplyItem(
    reply: CommentReply,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("reply_item_${reply.id}"),
        verticalAlignment = Alignment.Top
    ) {
        UserAvatar(
            name = reply.authorName,
            size = 28.dp
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = reply.authorName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = reply.timeAgo,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = reply.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun UserAvatar(
    name: String,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    avatarStyle: Int = 0
) {
    val initial = name.firstOrNull()?.uppercaseChar() ?: 'U'
    val avatarGradient = remember(name, avatarStyle) {
        val styles = listOf(
            listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)), // Indigo Purple
            listOf(Color(0xFF0EA5E9), Color(0xFF06B6D4)), // Cyan Sky
            listOf(Color(0xFFEC4899), Color(0xFFF43F5E)), // Sunset Pink
            listOf(Color(0xFF10B981), Color(0xFF059669)), // Emerald Mint
            listOf(Color(0xFFF59E0B), Color(0xFFEA580C)), // Amber Sunset
            listOf(Color(0xFF8B5CF6), Color(0xFFD946EF))  // Cosmic Fuchsia
        )
        if (avatarStyle in styles.indices) {
            styles[avatarStyle]
        } else {
            val hash = name.hashCode()
            if (hash % 2 == 0) {
                listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
            } else {
                listOf(Color(0xFF0EA5E9), Color(0xFF6366F1))
            }
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(2.dp, CircleShape)
            .clip(CircleShape)
            .background(Brush.linearGradient(avatarGradient))
            .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial.toString(),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.42f).sp
            )
        )
    }
}
