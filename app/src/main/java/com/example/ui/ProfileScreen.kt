package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.auth.AuthUiState
import com.example.auth.AuthViewModel
import com.example.model.Comment
import com.example.model.Post

fun getInitialUserPosts(userName: String, userHandle: String): List<Post> = listOf(
    Post(
        id = "user_post_desk",
        authorName = userName,
        authorHandle = userHandle,
        timeAgo = "1d ago",
        text = "Finally dialed in my creative desk setup! Clean lines, lots of natural light, and good focus vibes. Let's make this week count! 🪴✨💻",
        imageResId = R.drawable.img_profile_post_1788454175879,
        initialLikeCount = 18,
        initialCommentCount = 2,
        initialComments = listOf(
            Comment(
                id = "user_c_1",
                authorName = "Sophia Lin",
                authorHandle = "@sophialin",
                text = "Such a clean and calming aesthetic! 🌿",
                timeAgo = "18h ago"
            ),
            Comment(
                id = "user_c_2",
                authorName = "Marcus Vance",
                authorHandle = "@marcusv",
                text = "Succulent + keyboard combo is peak focus 🙌",
                timeAgo = "12h ago"
            )
        )
    ),
    Post(
        id = "user_post_welcome",
        authorName = userName,
        authorHandle = userHandle,
        timeAgo = "3d ago",
        text = "Just stepped into Social Vibe! Loving the clean energy and inspiring community here. Looking forward to connecting and sharing daily updates. 👋🚀",
        imageResId = null,
        initialLikeCount = 25,
        initialCommentCount = 1,
        initialComments = listOf(
            Comment(
                id = "user_c_3",
                authorName = "Elena Gomez",
                authorHandle = "@elenag",
                text = "Welcome aboard! Thrilled to have you in the vibe family ✨",
                timeAgo = "2d ago"
            )
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: AuthUiState,
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    userPosts: androidx.compose.runtime.snapshots.SnapshotStateList<Post>? = null
) {
    val context = LocalContext.current

    val currentUserName = uiState.currentUserName?.ifBlank { null }
        ?: uiState.currentUserEmail?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
        ?: "Alex"

    val currentUserHandle = uiState.currentUserHandle
        ?: ("@" + (uiState.currentUserEmail?.substringBefore("@") ?: "user"))
    val currentUserBio = uiState.userBio
    val userAvatarStyle = uiState.userAvatarStyle

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // User's own posts list - either provided or fallback separated state
    val actualUserPosts = userPosts ?: remember(currentUserName) {
        mutableStateListOf<Post>().apply {
            addAll(getInitialUserPosts(currentUserName, currentUserHandle))
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar with back navigation to Home / Feed
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateToHome() },
                        modifier = Modifier.testTag("back_to_feed_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_to_feed),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(2)
                            }
                        },
                        modifier = Modifier.testTag("dashboard_nav_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Dashboard,
                            contentDescription = stringResource(id = R.string.dashboard_overview_title),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateToSettings() },
                        modifier = Modifier.testTag("profile_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(id = R.string.settings_title),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("profile_logout_button")
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

            // Scrollable Content
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
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 1 & 2. Profile Header & User Information Card
                    item {
                        ProfileHeaderCard(
                            userName = currentUserName,
                            userHandle = currentUserHandle,
                            userBio = currentUserBio,
                            postsCount = actualUserPosts.size,
                            followersCount = uiState.userFollowersCount,
                            followingCount = uiState.userFollowingCount,
                            avatarStyle = userAvatarStyle,
                            onEditProfileClick = { showEditProfileDialog = true },
                            onDashboardClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(2)
                                }
                            }
                        )
                    }

                    // Simple Dashboard Overview with 6 cards:
                    // Step 1:
                    // - Total Posts
                    // - Followers
                    // - Following
                    // - Profile Views
                    // Step 2:
                    // - Engagement
                    // - Followers Gained
                    item {
                        DashboardOverviewSection(
                            totalPosts = actualUserPosts.size,
                            followersCount = uiState.userFollowersCount,
                            followingCount = uiState.userFollowingCount,
                            profileViewsCount = 1240,
                            engagementRate = "4.8%",
                            followersGained = 36,
                            onCardClick = { title, value ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("$title: $value")
                                }
                            }
                        )
                    }

                    // 3. User's Posts Section Heading
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.my_posts_heading),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${actualUserPosts.size}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Feed/List of User's Own Posts
                    if (actualUserPosts.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.no_user_posts_yet),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(
                            items = actualUserPosts,
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
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }

    // Edit Profile Modal Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            initialName = currentUserName,
            initialUsername = currentUserHandle,
            initialBio = currentUserBio,
            initialAvatarStyle = userAvatarStyle,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updatedName, updatedUsername, updatedBio, updatedAvatarStyle ->
                viewModel.updateUserProfile(
                    name = updatedName,
                    username = updatedUsername,
                    bio = updatedBio,
                    avatarStyle = updatedAvatarStyle
                )
                showEditProfileDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Profile updated successfully")
                }
            }
        )
    }
}

/**
 * Clean Profile Header Card containing avatar, names, bio, edit button, and statistics.
 */
@Composable
fun ProfileHeaderCard(
    userName: String,
    userHandle: String,
    userBio: String,
    postsCount: Int,
    followersCount: Int,
    followingCount: Int,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    avatarStyle: Int = 0,
    onDashboardClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(24.dp))
            .testTag("profile_header_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular profile photo
            UserAvatar(
                name = userName,
                size = 88.dp,
                modifier = Modifier.testTag("profile_avatar"),
                avatarStyle = avatarStyle
            )

            Spacer(modifier = Modifier.height(12.dp))

            // User Name
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("profile_user_name")
            )

            // User Handle
            Text(
                text = userHandle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("profile_user_handle")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Short Bio
            Text(
                text = userBio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .testTag("profile_user_bio")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Statistics (Posts, Followers, Following)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 8.dp)
                    .testTag("profile_stats_row"),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileStatItem(
                    count = postsCount.toString(),
                    label = stringResource(id = R.string.stat_posts),
                    modifier = Modifier.testTag("stat_posts_item")
                )

                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                )

                ProfileStatItem(
                    count = followersCount.toString(),
                    label = stringResource(id = R.string.stat_followers),
                    modifier = Modifier.testTag("stat_followers_item")
                )

                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                )

                ProfileStatItem(
                    count = followingCount.toString(),
                    label = stringResource(id = R.string.stat_following),
                    modifier = Modifier.testTag("stat_following_item")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Edit Profile and Dashboard Overview
            if (onDashboardClick != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onEditProfileClick,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("edit_profile_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.edit_profile),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Button(
                        onClick = onDashboardClick,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("header_dashboard_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Dashboard,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.dashboard_overview_title),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onEditProfileClick,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("edit_profile_button"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.edit_profile),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Complete Edit Profile Dialog:
 * - Profile photo change option with avatar presets
 * - Name input
 * - Username input
 * - Bio input
 * - Save Changes button
 * - Cancel button
 * - Unsaved-changes warning when user tries to leave with unsaved edits
 */
@Composable
fun EditProfileDialog(
    initialName: String,
    initialUsername: String,
    initialBio: String,
    initialAvatarStyle: Int = 0,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int) -> Unit
) {
    var editedName by remember { mutableStateOf(initialName) }
    var editedUsername by remember { mutableStateOf(initialUsername.removePrefix("@")) }
    var editedBio by remember { mutableStateOf(initialBio) }
    var editedAvatarStyle by remember { mutableIntStateOf(initialAvatarStyle) }
    var showAvatarPicker by remember { mutableStateOf(false) }
    var showUnsavedChangesWarning by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }

    fun hasUnsavedChanges(): Boolean {
        val cleanInitialUsername = initialUsername.removePrefix("@").trim()
        val cleanCurrentUsername = editedUsername.removePrefix("@").trim()
        return editedName.trim() != initialName.trim() ||
                cleanCurrentUsername != cleanInitialUsername ||
                editedBio.trim() != initialBio.trim() ||
                editedAvatarStyle != initialAvatarStyle
    }

    fun handleDismissAttempt() {
        if (hasUnsavedChanges()) {
            showUnsavedChangesWarning = true
        } else {
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = { handleDismissAttempt() },
        title = {
            Text(
                text = stringResource(id = R.string.edit_profile_dialog_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Profile photo change option
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .clickable { showAvatarPicker = !showAvatarPicker }
                        .testTag("change_photo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    UserAvatar(
                        name = editedName.ifBlank { "User" },
                        size = 88.dp,
                        avatarStyle = editedAvatarStyle
                    )
                    // Camera icon overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = stringResource(id = R.string.change_photo),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                TextButton(
                    onClick = { showAvatarPicker = !showAvatarPicker },
                    modifier = Modifier.testTag("change_avatar_style_button")
                ) {
                    Text(
                        text = stringResource(id = R.string.change_profile_photo),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Avatar Vibe Selector
                if (showAvatarPicker) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                            .testTag("avatar_picker_palette"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.choose_avatar_vibe),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (styleIndex in 0..5) {
                                val isSelected = editedAvatarStyle == styleIndex
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            editedAvatarStyle = styleIndex
                                            showAvatarPicker = false
                                        }
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .testTag("avatar_style_option_$styleIndex"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    UserAvatar(
                                        name = editedName.ifBlank { "U" },
                                        size = 32.dp,
                                        avatarStyle = styleIndex
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.3f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Name Field
                OutlinedTextField(
                    value = editedName,
                    onValueChange = {
                        editedName = it
                        if (it.isNotBlank()) nameError = null
                    },
                    label = { Text(stringResource(id = R.string.name_label)) },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_profile_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Username Field
                OutlinedTextField(
                    value = editedUsername,
                    onValueChange = {
                        editedUsername = it.filter { char -> char.isLetterOrDigit() || char == '_' }
                        if (it.isNotBlank()) usernameError = null
                    },
                    prefix = { Text("@", color = MaterialTheme.colorScheme.primary) },
                    label = { Text(stringResource(id = R.string.username_label)) },
                    placeholder = { Text(stringResource(id = R.string.username_placeholder)) },
                    isError = usernameError != null,
                    supportingText = usernameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_profile_username_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Bio Field
                OutlinedTextField(
                    value = editedBio,
                    onValueChange = { editedBio = it },
                    label = { Text(stringResource(id = R.string.bio_label)) },
                    placeholder = { Text(stringResource(id = R.string.bio_placeholder)) },
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_profile_bio_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = editedName.trim()
                    val trimmedUser = editedUsername.trim().removePrefix("@")
                    var hasErr = false
                    if (trimmedName.isBlank()) {
                        nameError = "Name cannot be empty."
                        hasErr = true
                    }
                    if (trimmedUser.isBlank()) {
                        usernameError = "Username cannot be empty."
                        hasErr = true
                    }
                    if (!hasErr) {
                        onSave(trimmedName, "@$trimmedUser", editedBio.trim(), editedAvatarStyle)
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text(stringResource(id = R.string.save_changes_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { handleDismissAttempt() },
                modifier = Modifier.testTag("cancel_edit_profile_button")
            ) {
                Text(stringResource(id = R.string.cancel_button))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )

    // Unsaved Changes Warning Dialog
    if (showUnsavedChangesWarning) {
        AlertDialog(
            onDismissRequest = { showUnsavedChangesWarning = false },
            title = {
                Text(
                    text = stringResource(id = R.string.unsaved_changes_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.testTag("unsaved_changes_title")
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.unsaved_changes_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUnsavedChangesWarning = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.testTag("discard_changes_button")
                ) {
                    Text(stringResource(id = R.string.discard_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showUnsavedChangesWarning = false },
                    modifier = Modifier.testTag("keep_editing_button")
                ) {
                    Text(stringResource(id = R.string.keep_editing_button))
                }
            },
            modifier = Modifier.testTag("unsaved_changes_dialog"),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * Simple Dashboard Overview with 6 metric cards:
 * Step 1:
 * - Total Posts
 * - Followers
 * - Following
 * - Profile Views
 * Step 2:
 * - Engagement
 * - Followers Gained
 */
@Composable
fun DashboardOverviewSection(
    totalPosts: Int,
    followersCount: Int,
    followingCount: Int,
    profileViewsCount: Int,
    engagementRate: String = "4.8%",
    followersGained: Int = 36,
    modifier: Modifier = Modifier,
    onCardClick: ((String, String) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_overview_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Dashboard,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = stringResource(id = R.string.dashboard_overview_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Timeframe badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.dashboard_timeframe),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2x2 Grid of 4 Cards
        // Row 1: Total Posts & Followers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardMetricCard(
                title = stringResource(id = R.string.dashboard_stat_total_posts),
                value = totalPosts.toString(),
                badge = stringResource(id = R.string.dashboard_posts_trend),
                icon = Icons.Outlined.Article,
                iconTint = MaterialTheme.colorScheme.primary,
                iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                testTag = "dashboard_card_total_posts",
                modifier = Modifier.weight(1f),
                onClick = {
                    onCardClick?.invoke("Total Posts", totalPosts.toString())
                }
            )

            DashboardMetricCard(
                title = stringResource(id = R.string.dashboard_stat_followers),
                value = followersCount.toString(),
                badge = stringResource(id = R.string.dashboard_followers_trend),
                icon = Icons.Outlined.People,
                iconTint = MaterialTheme.colorScheme.secondary,
                iconBgColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                testTag = "dashboard_card_followers",
                modifier = Modifier.weight(1f),
                onClick = {
                    onCardClick?.invoke("Followers", followersCount.toString())
                }
            )
        }

        // Row 2: Following & Profile Views
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardMetricCard(
                title = stringResource(id = R.string.dashboard_stat_following),
                value = followingCount.toString(),
                badge = stringResource(id = R.string.dashboard_following_trend),
                icon = Icons.Outlined.PersonAdd,
                iconTint = Color(0xFFA78BFA),
                iconBgColor = Color(0xFFA78BFA).copy(alpha = 0.15f),
                testTag = "dashboard_card_following",
                modifier = Modifier.weight(1f),
                onClick = {
                    onCardClick?.invoke("Following", followingCount.toString())
                }
            )

            DashboardMetricCard(
                title = stringResource(id = R.string.dashboard_stat_profile_views),
                value = if (profileViewsCount >= 1000) String.format(java.util.Locale.US, "%,d", profileViewsCount) else profileViewsCount.toString(),
                badge = stringResource(id = R.string.dashboard_views_trend),
                icon = Icons.Outlined.Visibility,
                iconTint = Color(0xFF34D399),
                iconBgColor = Color(0xFF34D399).copy(alpha = 0.15f),
                testTag = "dashboard_card_profile_views",
                modifier = Modifier.weight(1f),
                onClick = {
                    onCardClick?.invoke("Profile Views", String.format(java.util.Locale.US, "%,d", profileViewsCount))
                }
            )
        }

        // Row 3 (Step 2): Engagement & Followers Gained
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardMetricCard(
                title = stringResource(id = R.string.dashboard_stat_engagement),
                value = engagementRate,
                badge = stringResource(id = R.string.dashboard_engagement_trend),
                icon = Icons.Outlined.TrendingUp,
                iconTint = Color(0xFFFB923C),
                iconBgColor = Color(0xFFFB923C).copy(alpha = 0.15f),
                testTag = "dashboard_card_engagement",
                modifier = Modifier.weight(1f),
                onClick = {
                    onCardClick?.invoke("Engagement", engagementRate)
                }
            )

            DashboardMetricCard(
                title = stringResource(id = R.string.dashboard_stat_followers_gained),
                value = if (followersGained > 0) "+$followersGained" else "$followersGained",
                badge = stringResource(id = R.string.dashboard_followers_gained_trend),
                icon = Icons.Outlined.GroupAdd,
                iconTint = Color(0xFF38BDF8),
                iconBgColor = Color(0xFF38BDF8).copy(alpha = 0.15f),
                testTag = "dashboard_card_followers_gained",
                modifier = Modifier.weight(1f),
                onClick = {
                    onCardClick?.invoke("Followers Gained", if (followersGained > 0) "+$followersGained" else "$followersGained")
                }
            )
        }
    }
}

/**
 * Clean, modern Dark-themed Metric Card for Social Vibe Dashboard.
 */
@Composable
fun DashboardMetricCard(
    title: String,
    value: String,
    badge: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBgColor: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .shadow(3.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
        ),
        onClick = { onClick?.invoke() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = iconTint
                    )
                }

                // Trend badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBgColor.copy(alpha = 0.18f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = iconTint
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Metric Value
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Metric Title
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
