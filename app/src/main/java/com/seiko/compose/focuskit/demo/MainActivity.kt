package com.seiko.compose.focuskit.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.onFocusChanged
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.seiko.compose.focuskit.ScrollBehaviour
import com.seiko.compose.focuskit.demo.model.AnimeDetail
import com.seiko.compose.focuskit.demo.ui.foundation.DetailAnimeInfo
import com.seiko.compose.focuskit.demo.ui.foundation.TvEpisodeList
import com.seiko.compose.focuskit.demo.ui.foundation.TvSelectDialog
import com.seiko.compose.focuskit.demo.ui.foundation.TvTabBar
import com.seiko.compose.focuskit.demo.ui.foundation.TvTitleGroup
import com.seiko.compose.focuskit.demo.ui.theme.AnimeTvTheme
import com.seiko.compose.focuskit.handleBack
import com.seiko.compose.focuskit.handleBackReturn
import com.seiko.compose.focuskit.scrollToIndex
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
        AppScreen()
        // CompositionLocalProvider(
        //   LocalAppNavigator provides navController
        // ) {
        //   Surface(
        //     modifier = Modifier
        //       .fillMaxSize()
        //       .handleBackReturn { navController.popBackStack() },
        //     color = MaterialTheme.colors.background
        //   ) {
        //     Router(navController, viewModel)
        //   }
        // }
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
  val state = rememberLazyListState()

  var focusIndex by rememberSaveable(stateSaver = autoSaver()) { mutableStateOf(0) }

  LazyColumn(state = state) {
    item {
      val focusRequester = remember { FocusRequester() }
      TvTabBar(
        tabList,
        modifier = Modifier
          .onFocusChanged {
            if (it.isFocused) {
              focusIndex = 0
              Log.d("Focuskit", "home focusIndex=0")
            }
          }
          .focusOrder(focusRequester)
      )

      if (focusIndex == 0) {
        SideEffect { focusRequester.requestFocus() }
      }
    }
    itemsIndexed(animeGroup) { index, pair ->
      val focusRequester = remember { FocusRequester() }
      val (title, animes) = pair
      TvTitleGroup(
        title, animes,
        modifier = Modifier
          .onFocusChanged {
            if (it.isFocused) {
              focusIndex = 1 + index
              Log.d("Focuskit", "home focusIndex=${1 + index}")
            }
          }
          .focusOrder(focusRequester)
      )

      if (focusIndex == 1 + index) {
        SideEffect { focusRequester.requestFocus() }
      }
    }
  }

  LaunchedEffect(focusIndex) {
    Log.d("Focuskit", "home move to focusIndex=$focusIndex")
    state.scrollToIndex(focusIndex, ScrollBehaviour.Vertical)
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DetailScreen(detail: AnimeDetail) {
  val state = rememberLazyListState()
  var focusIndex by rememberSaveable(stateSaver = autoSaver()) { mutableStateOf(0) }

  LazyColumn(state = state) {
    item {
      val focusRequester = remember { FocusRequester() }
      DetailAnimeInfo(
        modifier = Modifier
          .onFocusChanged { if (it.isFocused) focusIndex = 0 }
          .focusOrder(focusRequester),
        title = detail.title,
        cover = detail.cover,
        releaseTime = detail.releaseTime,
        state = detail.state,
        types = detail.types,
        tags = detail.tags,
        indexes = detail.indexes,
        description = detail.description,
      )

      if (focusIndex == 0) {
        SideEffect { focusRequester.requestFocus() }
      }
    }

    item {
      val focusRequester = remember { FocusRequester() }
      TvEpisodeList(
        modifier = Modifier
          .onFocusChanged { if (it.isFocused) focusIndex = 1 }
          .focusOrder(focusRequester),
        title = "播放列表",
        list = detail.episodeList,
      )

      if (focusIndex == 1) {
        SideEffect { focusRequester.requestFocus() }
      }
    }

    item {
      val focusRequester = remember { FocusRequester() }
      TvTitleGroup(
        modifier = Modifier
          .onFocusChanged { if (it.isFocused) focusIndex = 2 }
          .focusOrder(focusRequester),
        title = "相关推荐",
        list = detail.relatedList
      )

      if (focusIndex == 2) {
        SideEffect { focusRequester.requestFocus() }
      }
    }
  }

  LaunchedEffect(focusIndex) {
    state.scrollToIndex(focusIndex, ScrollBehaviour.Vertical)
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
      .handleBack {
        if (!openDialog) {
          openDialog = true
          savePlayState()
          player.pause()
        }
      }
  ) {
    TvVideoPlayer(
      player = player,
      controller = controller,
      modifier = Modifier.fillMaxSize()
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
