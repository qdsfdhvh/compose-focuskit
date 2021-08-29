package com.seiko.compose.focuskit.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.seiko.compose.focuskit.*
import com.seiko.compose.focuskit.demo.model.AnimeDetail
import com.seiko.compose.focuskit.demo.ui.foundation.*
import com.seiko.compose.focuskit.demo.ui.theme.AnimeTvTheme
import com.seiko.compose.player.TvVideoPlayer
import com.seiko.compose.player.VideoPlayerSource
import com.seiko.compose.player.rememberPlayer
import com.seiko.compose.player.rememberVideoPlayerController

class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<MainViewModel>()

  private val navController by lazy(LazyThreadSafetyMode.NONE) {
    NavHostController(this).apply {
      navigatorProvider.addNavigator(ComposeNavigator())
      navigatorProvider.addNavigator(DialogNavigator())
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AnimeTvTheme {
        CompositionLocalProvider(
          LocalAppNavigator provides navController
        ) {
          Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
          ) {
            Router(navController, viewModel)
          }
        }
      }
    }
  }
}

@Composable
fun Router(
  navController: NavHostController,
  viewModel: MainViewModel,
) {
  NavHost(navController, startDestination = "/home") {
    composable("/home") {
      val tabList by viewModel.tabList.collectAsState(emptyList())
      val animeGroup by viewModel.animeGroup.collectAsState()
      HomeScreen(tabList, animeGroup)
    }
    composable("/show/{animeId}.html") {
      val detail by viewModel.animeDetail.collectAsState()
      DetailScreen(detail)
    }
    composable("/v/{episode}.html") {
      val source by viewModel.animePlayer.collectAsState()
      if (source != null) {
        PlayerScreen(source!!)
      }
    }
  }
}

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
  tabList: List<String> = emptyList(),
  animeGroup: AnimeGroup = emptyList(),
) {
  val size = remember(animeGroup) { 1 + animeGroup.size }
  val focusRequesters = rememberFocusRequesters(size)
  val interactionSource = remember { MutableInteractionSource() }
  val focusIndex by interactionSource.collectFocusIndexAsState()

  TvLazyColumn(
    modifier = Modifier,
    interactionSource = interactionSource
  ) {
    item {
      TvTabBar(
        tabList,
        modifier = Modifier.focusRequester(focusRequesters[0])
      )
    }
    itemsIndexed(animeGroup) { index, pair ->
      val (title, animes) = pair
      TvTitleGroup(
        title, animes,
        modifier = Modifier.focusRequester(focusRequesters[index + 1])
      )
    }
  }

  LaunchedEffect(focusIndex, tabList, animeGroup) {
    focusRequesters.getOrNull(focusIndex)?.requestFocus()
  }
}

@Composable
fun DetailScreen(detail: AnimeDetail) {
  val navController = LocalAppNavigator.current
  val focusRequesters = rememberFocusRequesters(3)
  val interactionSource = remember(detail) { MutableInteractionSource() }
  val focusIndex by interactionSource.collectFocusIndexAsState()

  TvLazyColumn(
    modifier = Modifier
      .handleTvKey(TvKeyEvent.Back) {
        navController.popBackStack()
      },
    interactionSource = interactionSource,
  ) {
    item {
      TvMovieInfo(
        modifier = Modifier.focusRequester(focusRequesters[0]),
        title = detail.title,
        cover = detail.cover,
        releaseTime = detail.releaseTime,
        state = detail.state,
        tags = detail.tags,
        description = detail.description,
      )
    }

    item {
      TvEpisodeList(
        modifier = Modifier.focusRequester(focusRequesters[1]),
        title = "播放列表",
        list = detail.episodeList,
      )
    }

    item {
      TvTitleGroup(
        modifier = Modifier.focusRequester(focusRequesters[2]),
        title = "相关推荐",
        list = detail.relatedList
      )
    }
  }

  LaunchedEffect(focusIndex) {
    focusRequesters.getOrNull(focusIndex)?.requestFocus()
  }
}

@Composable
fun PlayerScreen(source: VideoPlayerSource) {
  var openDialog by remember { mutableStateOf(false) }

  val player = rememberPlayer(source)
  val controller = rememberVideoPlayerController(player)

  var isPlaying by remember(source) { mutableStateOf(false) }

  fun savePlayState() {
    isPlaying = controller.isPlaying
  }

  fun restorePlayState() {
    if (isPlaying) {
      controller.play()
    }
  }

  Box(
    modifier = Modifier
      .handleTvKey(TvKeyEvent.Back) {
        if (!openDialog) {
          openDialog = true
          savePlayState()
          player.pause()
        }
        true
      }
  ) {
    TvVideoPlayer(
      player = player,
      controller = controller
    )

    if (openDialog) {
      val navController = LocalAppNavigator.current
      TvSelectDialog(
        text = "是否退出播放？",
        onCenterClick = {
          openDialog = false
          navController.popBackStack()
        },
        onCancelClick = {
          openDialog = false
          restorePlayState()
        },
      )
    }
  }
}
