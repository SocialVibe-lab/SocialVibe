package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.auth.AuthUiState
import com.example.auth.AuthViewModel
import com.example.model.Comment
import com.example.model.Post

data class MockUser(
    val id: String,
    val name: String,
    val username: String,
    val bio: String
)

fun getMockSearchUsers(): List<MockUser> = listOf(
    MockUser(
        id = "user_sophia",
        name = "Sophia Lin",
        username = "@sophialin",
        bio = "Visual designer & coffee lover 🌿✨ Exploring daily moments."
    ),
    MockUser(
        id = "user_marcus",
        name = "Marcus Vance",
        username = "@marcusv",
        bio = "Tech enthusiast, mechanical keyboards & clean setups 💻⚡"
    ),
    MockUser(
        id = "user_elena",
        name = "Elena Gomez",
        username = "@elenag",
        bio = "Digital nomad, builder & community vibes lover 🚀 Golden hour hunter."
    ),
    MockUser(
        id = "user_jackson",
        name = "Jackson",
        username = "@jackson",
        bio = "Photographer capturing golden hour vibes & sunsets 📸🌅"
    )
)

fun getMockSearchPosts(): List<Post> = listOf(
    Post(
        id = "search_post_golden_1",
        authorName = "Sophia Lin",
        authorHandle = "@sophialin",
        timeAgo = "2h ago",
        text = "Catching the golden hour vibes today! Nothing beats a fresh breeze and good energy. ✨🌅 What are you all working on this week?",
        imageResId = R.drawable.img_sunset_vibe_1788453440646,
        initialLikeCount = 42,
        initialCommentCount = 2,
        initialComments = listOf(
            Comment(
                id = "sc_1_1",
                authorName = "Marcus Vance",
                authorHandle = "@marcusv",
                text = "Stunning view Sophia! Have a productive week! 🔥",
                timeAgo = "1h ago"
            ),
            Comment(
                id = "sc_1_2",
                authorName = "Elena Gomez",
                authorHandle = "@elenag",
                text = "Golden hour never fails. Love this vibe ✨",
                timeAgo = "30m ago"
            )
        )
    ),
    Post(
        id = "search_post_productive_2",
        authorName = "Marcus Vance",
        authorHandle = "@marcusv",
        timeAgo = "3h ago",
        text = "Having a productive week! Knocked out all major design reviews early. Coffee + code session continues. ☕💻",
        imageResId = R.drawable.img_coffee_study_1788453460368,
        initialLikeCount = 31,
        initialCommentCount = 1,
        initialComments = listOf(
            Comment(
                id = "sc_2_1",
                authorName = "Sophia Lin",
                authorHandle = "@sophialin",
                text = "Love that workspace focus! Keep going! 🙌",
                timeAgo = "2h ago"
            )
        )
    ),
    Post(
        id = "search_post_beach_3",
        authorName = "Elena Gomez",
        authorHandle = "@elenag",
        timeAgo = "5h ago",
        text = "Golden hour by the beach. The sound of the waves and warm light make this the best spot to recharge. 🌅🌊",
        imageResId = R.drawable.img_sunset_vibe_1788453440646,
        initialLikeCount = 58,
        initialCommentCount = 2,
        initialComments = listOf(
            Comment(
                id = "sc_3_1",
                authorName = "Jackson",
                authorHandle = "@jackson",
                text = "Unbelievable sunset colors tonight! 📸",
                timeAgo = "3h ago"
            ),
            Comment(
                id = "sc_3_2",
                authorName = "Marcus Vance",
                authorHandle = "@marcusv",
                text = "Pure tranquility. Enjoy it! ✨",
                timeAgo = "1h ago"
            )
        )
    ),
    Post(
        id = "search_post_jackson_4",
        authorName = "Jackson",
        authorHandle = "@jackson",
        timeAgo = "7h ago",
        text = "Chasing golden hour shadows across the downtown rooftops. Never stop looking for new perspectives. 📸🏙️",
        imageResId = null,
        initialLikeCount = 19,
        initialCommentCount = 0,
        initialComments = emptyList()
    )
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    uiState: AuthUiState,
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    samplePosts: SnapshotStateList<Post>? = null
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val currentUserName = uiState.currentUserName?.ifBlank { null }
        ?: uiState.currentUserEmail?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
        ?: "Alex"

    val currentUserHandle = "@" + (uiState.currentUserEmail?.substringBefore("@") ?: "user")

    val mockUsers = remember { getMockSearchUsers() }
    val defaultSearchPosts = remember { getMockSearchPosts() }

    // Combine samplePosts (from Home/CreatePost) and search mock posts, distinct by ID
    val allSearchPosts = remember(samplePosts?.size) {
        val combined = (samplePosts?.toList() ?: emptyList()) + defaultSearchPosts
        combined.distinctBy { it.id }
    }

    val trimmedQuery = searchQuery.trim()
    val isSearching = trimmedQuery.isNotEmpty()

    // Case-insensitive user search: matches name, username, or bio
    val matchingUsers = remember(trimmedQuery, mockUsers) {
        if (!isSearching) {
            emptyList()
        } else {
            mockUsers.filter { user ->
                user.name.contains(trimmedQuery, ignoreCase = true) ||
                    user.username.contains(trimmedQuery, ignoreCase = true) ||
                    user.bio.contains(trimmedQuery, ignoreCase = true)
            }
        }
    }

    // Case-insensitive post search: matches text, author name, or handle
    val matchingPosts = remember(trimmedQuery, allSearchPosts) {
        if (!isSearching) {
            emptyList()
        } else {
            allSearchPosts.filter { post ->
                post.text.contains(trimmedQuery, ignoreCase = true) ||
                    post.authorName.contains(trimmedQuery, ignoreCase = true) ||
                    post.authorHandle.contains(trimmedQuery, ignoreCase = true)
            }
        }
    }

    val trendingTags = listOf(
        "GoldenHour",
        "VibeCheck",
        "CoffeeAndCode",
        "TechLife",
        "CreativeFlow",
        "SunsetMagic",
        "WeekendVibes",
        "MindfulMoments"
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(id = R.string.search_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(2.dp)
            )

            // Content Area
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
                        .padding(horizontal = 16.dp)
                        .testTag("search_results_list"),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Search Input Bar with Search Icon, Placeholder, and Clear (X) Button
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        text = stringResource(id = R.string.search_placeholder),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = { searchQuery = "" },
                                            modifier = Modifier.testTag("clear_search_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = stringResource(id = R.string.clear_search),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .testTag("search_bar_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                )
                            )
                        }
                    }

                    // If NOT searching: Show Trending Topics and Discover Community Placeholder
                    if (!isSearching) {
                        // Trending Topics Section
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(20.dp))
                                    .testTag("trending_topics_section"),
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
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Tag,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(id = R.string.trending_topics),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        trendingTags.forEach { tag ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                                    .border(
                                                        width = 1.dp,
                                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                        shape = RoundedCornerShape(20.dp)
                                                    )
                                                    .clickable { searchQuery = tag }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = "#$tag",
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Placeholder Info Card
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(20.dp))
                                    .testTag("search_placeholder_card"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Explore,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = stringResource(id = R.string.search_placeholder_heading),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = stringResource(id = R.string.search_placeholder_subheading),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        // When searching: Trending and Discover are hidden. Check results:
                        val hasNoResults = matchingUsers.isEmpty() && matchingPosts.isEmpty()

                        if (hasNoResults) {
                            // "No results found"
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(2.dp, RoundedCornerShape(20.dp))
                                        .testTag("no_search_results_card"),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SearchOff,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = stringResource(id = R.string.no_results_found),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = stringResource(id = R.string.no_results_desc),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            // USER RESULTS ("People" Section)
                            if (matchingUsers.isNotEmpty()) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp, bottom = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(id = R.string.search_users_heading),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                        ) {
                                            Text(
                                                text = "${matchingUsers.size}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                items(
                                    items = matchingUsers,
                                    key = { it.id }
                                ) { user ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(2.dp, RoundedCornerShape(16.dp))
                                            .clickable { viewModel.navigateToProfile() }
                                            .testTag("user_result_${user.id}"),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            UserAvatar(
                                                name = user.name,
                                                size = 48.dp
                                            )
                                            Spacer(modifier = Modifier.width(14.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = user.name,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = user.username,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = user.bio,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // POST RESULTS ("Posts" Section)
                            if (matchingPosts.isNotEmpty()) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp, bottom = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.Article,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(id = R.string.search_posts_heading),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                        ) {
                                            Text(
                                                text = "${matchingPosts.size}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                items(
                                    items = matchingPosts,
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
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
