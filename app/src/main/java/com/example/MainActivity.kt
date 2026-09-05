package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.auth.AuthScreen
import com.example.auth.AuthViewModel
import com.example.auth.MainScreen
import com.example.model.NotificationItem
import com.example.model.Post
import com.example.model.getInitialNotifications
import com.example.ui.CreateAccountScreen
import com.example.ui.CreatePostScreen
import com.example.ui.HomeScreen
import com.example.ui.LoginScreen
import com.example.ui.NotificationsScreen
import com.example.ui.ProfileScreen
import com.example.ui.SearchScreen
import com.example.ui.SettingsScreen
import com.example.ui.SocialVibeBottomBar
import com.example.ui.getInitialPosts
import com.example.ui.getInitialUserPosts
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private val authViewModel: AuthViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val context = LocalContext.current
      LaunchedEffect(Unit) {
        authViewModel.initialize(context)
      }

      val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
          Crossfade(
            targetState = uiState.isLoggedIn,
            label = "auth_state_transition"
          ) { isLoggedIn ->
            if (isLoggedIn) {
              MainAppContainer(
                uiState = uiState,
                viewModel = authViewModel
              )
            } else {
              Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Crossfade(
                  targetState = uiState.currentScreen,
                  label = "auth_screen_transition",
                  modifier = Modifier.padding(innerPadding)
                ) { screen ->
                  when (screen) {
                    AuthScreen.CREATE_ACCOUNT -> CreateAccountScreen(
                      uiState = uiState,
                      viewModel = authViewModel
                    )
                    AuthScreen.LOGIN -> LoginScreen(
                      uiState = uiState,
                      viewModel = authViewModel
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun MainAppContainer(
  uiState: com.example.auth.AuthUiState,
  viewModel: AuthViewModel,
  modifier: Modifier = Modifier
) {
  val samplePosts = remember {
    mutableStateListOf<Post>().apply { addAll(getInitialPosts()) }
  }

  val sampleNotifications = remember {
    mutableStateListOf<NotificationItem>().apply { addAll(getInitialNotifications()) }
  }

  val unreadNotificationsCount = sampleNotifications.count { !it.isRead }

  val currentUserName = uiState.currentUserName?.ifBlank { null }
    ?: uiState.currentUserEmail?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
    ?: "Alex"

  val currentUserHandle = "@" + (uiState.currentUserEmail?.substringBefore("@") ?: "user")

  val userPosts = remember(currentUserName) {
    mutableStateListOf<Post>().apply {
      addAll(getInitialUserPosts(currentUserName, currentUserHandle))
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    bottomBar = {
      SocialVibeBottomBar(
        currentScreen = uiState.currentMainScreen,
        onSelectScreen = { screen ->
          viewModel.navigateToMainScreen(screen)
        },
        unreadNotificationCount = unreadNotificationsCount
      )
    }
  ) { innerPadding ->
    Crossfade(
      targetState = uiState.currentMainScreen,
      label = "main_screen_transition",
      modifier = Modifier.padding(innerPadding)
    ) { screen ->
      when (screen) {
        MainScreen.HOME -> HomeScreen(
          uiState = uiState,
          viewModel = viewModel,
          samplePosts = samplePosts,
          userPosts = userPosts
        )
        MainScreen.SEARCH -> SearchScreen(
          uiState = uiState,
          viewModel = viewModel,
          samplePosts = samplePosts
        )
        MainScreen.CREATE_POST -> CreatePostScreen(
          uiState = uiState,
          viewModel = viewModel,
          samplePosts = samplePosts,
          userPosts = userPosts
        )
        MainScreen.NOTIFICATIONS -> NotificationsScreen(
          uiState = uiState,
          viewModel = viewModel,
          notifications = sampleNotifications,
          samplePosts = samplePosts
        )
        MainScreen.PROFILE -> ProfileScreen(
          uiState = uiState,
          viewModel = viewModel,
          userPosts = userPosts
        )
        MainScreen.SETTINGS -> SettingsScreen(
          uiState = uiState,
          viewModel = viewModel
        )
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}
