package com.seiko.compose.focuskit.demo.ui.foundation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seiko.compose.focuskit.ContainerTvFocusItem
import com.seiko.compose.focuskit.TvLazyRow
import com.seiko.compose.focuskit.demo.model.Anime
import com.seiko.compose.focuskit.demo.ui.theme.AnimeTvTheme
import com.seiko.compose.focuskit.demo.ui.theme.backgroundColor
import com.seiko.compose.focuskit.onTvFocusChanged
import com.seiko.compose.focuskit.rememberContainerTvFocusItem
import com.seiko.compose.focuskit.rememberTvFocusItem

@Composable
fun TvTitleGroup(
  title: String,
  list: List<Anime>,
  modifier: Modifier = Modifier,
  container: ContainerTvFocusItem? = null
) {
  val containerItem = container ?: rememberContainerTvFocusItem()

  Column(modifier = modifier) {
    Text(
      text = title,
      style = MaterialTheme.typography.h6,
      modifier = Modifier.padding(start = 15.dp, top = 10.dp),
    )
    TvLazyRow(containerItem) {
      itemsIndexed(list) { index, item ->
        val focusItem = rememberTvFocusItem(
          key = item,
          container = containerItem,
          index = index
        )
        var isFocused by remember { mutableStateOf(false) }
        GroupItem(
          modifier = Modifier
            .onFocusChanged { }
            .onTvFocusChanged(focusItem) {
              isFocused = it.isFocused
            },
          item = item,
          isFocused = isFocused,
        )
      }
    }
  }
}

@Composable
private fun GroupItem(
  item: Anime,
  isFocused: Boolean,
  modifier: Modifier = Modifier,
) {
  val scale by animateFloatAsState(if (isFocused) 1.1f else 1f)
  Box(
    modifier = modifier
      .scale(scale)
      .padding(horizontal = 15.dp, vertical = 10.dp)
      .shadow(if (isFocused) 5.dp else 0.dp)
      .background(MaterialTheme.colors.surface)
      .border(1.dp, if (isFocused) MaterialTheme.colors.surface else Color.Transparent)
      .size(140.dp, 200.dp)
  ) {
    NetworkImage(
      data = item.imageUrl,
      modifier = Modifier.fillMaxSize()
    )
    Text(
      text = item.title,
      color = Color.White,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.caption,
      modifier = Modifier
        .background(Color.Gray.copy(alpha = 0.5f))
        .padding(5.dp)
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
    )
  }
}

@Preview(showBackground = true)
@Composable
fun GroupItemPreview() {
  AnimeTvTheme {
    Surface(color = backgroundColor) {
      Row(Modifier.padding(5.dp)) {
        GroupItem(
          Anime(
            title = "妖精的尾巴",
            imageUrl = "http://css.njhzmxx.com/comic/focus/2018/10/201810070913.jpg",
          ),
          isFocused = true
        )
        GroupItem(
          Anime(
            title = "妖精的尾巴",
            imageUrl = "http://css.njhzmxx.com/comic/focus/2018/10/201810070913.jpg",
          ),
          isFocused = false
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun TvTitleGroupPreview() {
  AnimeTvTheme {
    Surface(color = backgroundColor) {
      TvTitleGroup(
        title = "最新更新",
        list = listOf(
          Anime(
            title = "妖精的尾巴",
            imageUrl = "http://css.njhzmxx.com/comic/focus/2018/10/201810070913.jpg",
          ),
          Anime(
            title = "妖精的尾巴",
            imageUrl = "http://css.njhzmxx.com/comic/focus/2018/10/201810070913.jpg",
          )
        )
      )
    }
  }
}