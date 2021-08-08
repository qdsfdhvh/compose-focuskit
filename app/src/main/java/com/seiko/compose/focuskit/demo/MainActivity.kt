package com.seiko.compose.focuskit.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.seiko.compose.focuskit.TvLazyColumn
import com.seiko.compose.focuskit.TvLogger
import com.seiko.compose.focuskit.demo.model.AnimeDetail
import com.seiko.compose.focuskit.demo.ui.foundation.*
import com.seiko.compose.focuskit.demo.ui.theme.AnimeTvTheme
import com.seiko.compose.focuskit.rememberContainerTvFocusItem
import com.seiko.compose.focuskit.tvFocusable
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

    TvLogger.setLogger(object : TvLogger {
      override var level: Int = Log.DEBUG
      override fun log(level: Int, msg: String?, throwable: Throwable?) {
        if (msg != null) {
          Log.println(level, "Focuskit", msg)
        }
        if (throwable != null) {
          val writer = StringWriter()
          throwable.printStackTrace(PrintWriter(writer))
          Log.println(level, "Focuskit", writer.toString())
        }
      }
    })

    setContent {
      AnimeTvTheme {
        CompositionLocalProvider(
          LocalAppNavigator provides navController
        ) {
          Surface(
            modifier = Modifier
              .fillMaxSize()
              .tvFocusable(),
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
  }
}

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
  tabList: List<String> = emptyList(),
  animeGroup: AnimeGroup = emptyList(),
) {
  val container = rememberContainerTvFocusItem()

  TvLazyColumn(container = container) {
    item {
      val tabContainer = rememberContainerTvFocusItem(
        key = Unit,
        container = container,
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
        container = container,
        index = index + 1
      )

      val (title, animes) = pair
      TvTitleGroup(
        parent = groupContainer,
        modifier = Modifier.tvFocusable(groupContainer),
        title = title,
        list = animes,
      )
    }
  }
}

@Composable
fun DetailScreen(detail: AnimeDetail) {
  val container = rememberContainerTvFocusItem()

  TvLazyColumn(
    container = container,
    modifier = Modifier
      .fillMaxSize(),
  ) {
    item {
      TvMovieInfo(
        title = detail.title,
        cover = detail.cover,
        releaseTime = detail.releaseTime,
        state = detail.state,
        tags = detail.tags,
        description = detail.description,
      )
    }

    item {
      val episodeContainer = rememberContainerTvFocusItem(
        key = Unit,
        container = container,
        index = 0
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
        container = container,
        index = 1
      )

      TvTitleGroup(
        parent = relatedContainer,
        title = "相关推荐",
        list = detail.relatedList
      )
    }
  }
}
