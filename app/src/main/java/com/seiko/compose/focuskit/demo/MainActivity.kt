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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.seiko.compose.focuskit.Logger
import com.seiko.compose.focuskit.TvLazyColumn
import com.seiko.compose.focuskit.demo.ui.foundation.TvTabBar
import com.seiko.compose.focuskit.demo.ui.foundation.TvTitleGroup
import com.seiko.compose.focuskit.demo.ui.theme.AnimeTvTheme
import com.seiko.compose.focuskit.rememberContainerTvFocusItem
import com.seiko.compose.focuskit.tvFocusable

class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<MainViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Logger.setLogger(object : Logger {
      override fun log(level: Int, msg: String) {
        Log.println(level, "Focuskit", msg)
      }
    })

    setContent {
      AnimeTvTheme {
        val tabList by viewModel.tabList.collectAsState(emptyList())
        val animeGroup by viewModel.animeGroup.collectAsState()
        Surface(
          modifier = Modifier
            .fillMaxSize()
            .tvFocusable(),
          color = MaterialTheme.colors.background
        ) {
          MainScreen(
            tabList = tabList,
            animeGroup = animeGroup
          )
        }
      }
    }
  }
}

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
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
        tabList = tabList,
        container = tabContainer,
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
        title = title,
        list = animes,
        modifier = Modifier.tvFocusable(groupContainer),
        container = groupContainer,
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  AnimeTvTheme {
    MainScreen()
  }
}