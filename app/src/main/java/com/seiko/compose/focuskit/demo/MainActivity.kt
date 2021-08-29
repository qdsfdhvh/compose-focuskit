package com.seiko.compose.focuskit.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.seiko.compose.focuskit.*
import com.seiko.compose.focuskit.demo.model.AnimeDetail
import com.seiko.compose.focuskit.demo.ui.foundation.*
import com.seiko.compose.focuskit.demo.ui.theme.AnimeTvTheme
import com.seiko.compose.player.TvVideoPlayer
import com.seiko.compose.player.VideoPlayerSource
import com.seiko.compose.player.rememberPlayer
import com.seiko.compose.player.rememberVideoPlayerController
import java.io.PrintWriter
import java.io.StringWriter

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
  val navController = LocalAppNavigator.current
  val rootFocusItem = rememberRootTvFocusItem()

  TvLazyColumn(
    container = rootFocusItem,
    modifier = Modifier
      .handleTvKey(TvControllerKey.Back) {
        navController.popBackStack()
      }
  ) {
    item {
      val tabContainer = rememberContainerTvFocusItem(
        key = Unit,
        container = rootFocusItem,
        index = 0
      )

      TvTabBar(
        parent = tabContainer,
        tabList = tabList,
      )
    }
    itemsIndexed(animeGroup) { index, pair ->
      val groupContainer = rememberContainerTvFocusItem(
        key = pair,
        container = rootFocusItem,
        index = index + 1
      )

      val (title, animes) = pair
      TvTitleGroup(
        parent = groupContainer,
        title = title,
        list = animes,
      )
    }
  }

  LaunchedEffect(tabList) {
    Log.d("Demo", "HomeScreen onActive1")
    rootFocusItem.refocus(true)
  }

  DisposableEffect(Unit) {
    Log.d("Demo", "HomeScreen onActive2")
    onDispose {
      Log.d("Demo", "HomeScreen onDispose")
    }
  }
}

@Composable
fun DetailScreen(detail: AnimeDetail) {
  val navController = LocalAppNavigator.current
  val rootFocusItem = rememberRootTvFocusItem()

  TvLazyColumn(
    container = rootFocusItem,
    modifier = Modifier
      .handleTvKey(TvControllerKey.Back) {
        navController.popBackStack()
      }
  ) {
    item {
      val infoContainer = rememberContainerTvFocusItem(
        key = Unit,
        container = rootFocusItem,
        index = 0
      )

      TvMovieInfo(
        title = detail.title,
        cover = detail.cover,
        releaseTime = detail.releaseTime,
        state = detail.state,
        tags = detail.tags,
        description = detail.description,
        focusParent = infoContainer,
      )
    }

    item {
      val episodeContainer = rememberContainerTvFocusItem(
        key = Unit,
        container = rootFocusItem,
        index = 1
      )

      TvEpisodeList(
        title = "播放列表",
        list = detail.episodeList,
        parent = episodeContainer,
      )
    }

    item {
      val relatedContainer = rememberContainerTvFocusItem(
        key = Unit,
        container = rootFocusItem,
        index = 2
      )

      TvTitleGroup(
        parent = relatedContainer,
        title = "相关推荐",
        list = detail.relatedList
      )
    }
  }

  LaunchedEffect(detail) {
    rootFocusItem.refocus(true)
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
      .handleTvKey(TvControllerKey.Back) {
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
