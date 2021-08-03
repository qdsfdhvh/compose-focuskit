package com.seiko.compose.focuskit.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.seiko.compose.focuskit.TvLazyColumn
import com.seiko.compose.focuskit.demo.ui.foundation.TvTabBar
import com.seiko.compose.focuskit.demo.ui.foundation.TvTitleGroup
import com.seiko.compose.focuskit.demo.ui.theme.AnimeTvTheme
import com.seiko.compose.focuskit.rememberContainerTvFocusItem
import com.seiko.compose.focuskit.rememberTvFocusItem
import com.seiko.compose.focuskit.tvFocusable

class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<MainViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AnimeTvTheme {
        val focusRequester = remember { FocusRequester() }

        val tabList by viewModel.tabList.collectAsState(emptyList())
        val animeGroup by viewModel.animeGroup.collectAsState()
        Surface(
          modifier = Modifier
            .fillMaxSize()
            .tvFocusable(focusRequester),
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
    stickyHeader {
      TvTabBar(
        tabList = tabList,
        parentContainer = container,
      )
    }
    items(animeGroup) { pair ->
      val focusItem = rememberTvFocusItem()
      val (title, animes) = pair
      TvTitleGroup(
        title = title,
        list = animes,
        modifier = Modifier.tvFocusable(focusItem),
        parentContainer = container,
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