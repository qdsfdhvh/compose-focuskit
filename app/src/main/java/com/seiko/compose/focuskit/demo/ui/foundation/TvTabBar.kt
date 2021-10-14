package com.seiko.compose.focuskit.demo.ui.foundation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seiko.compose.focuskit.ScrollBehaviour
import com.seiko.compose.focuskit.demo.ui.theme.AnimeTvTheme
import com.seiko.compose.focuskit.demo.ui.theme.backgroundColor
import com.seiko.compose.focuskit.animateScrollToItem

@Composable
fun TvTabBar(
  tabList: List<String>,
  modifier: Modifier = Modifier,
) {
  val state = rememberLazyListState()

  var isParentFocused by remember { mutableStateOf(false) }
  var focusIndex by rememberSaveable(stateSaver = autoSaver()) { mutableStateOf(0) }

  LazyRow(
    state = state,
    modifier = modifier
      .onFocusChanged { isParentFocused = it.hasFocus || it.isFocused }
      .focusTarget(),
  ) {
    itemsIndexed(tabList) { index, title ->
      val focusRequester = remember { FocusRequester() }
      var isFocused by remember { mutableStateOf(false) }
      TvTabBarItem(
        modifier = Modifier
          .onFocusChanged {
            isFocused = it.isFocused
            if (isFocused) focusIndex = index
          }
          .focusOrder(focusRequester)
          .focusTarget(),
        title = title,
        isFocused = isFocused,
        isSelected = focusIndex == index,
      )

      if (isParentFocused && focusIndex == index) {
        SideEffect { focusRequester.requestFocus() }
      }
    }
  }

  if (isParentFocused) {
    LaunchedEffect(focusIndex) {
      state.animateScrollToItem(focusIndex, ScrollBehaviour.Horizontal)
    }
  }
}

@Composable
private fun TvTabBarItem(
  title: String,
  isFocused: Boolean,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
) {
  val scale by animateFloatAsState(if (isFocused) 1.1f else 1f)
  val background = if (isSelected) MaterialTheme.colors.surface else Color.Transparent
  Text(
    text = title,
    color = if (isFocused) Color.Black else Color.Unspecified,
    style = MaterialTheme.typography.body1,
    modifier = modifier
      .scale(scale)
      .padding(5.dp)
      .shadow(if (isFocused) 5.dp else 0.dp, CircleShape)
      .background(background, CircleShape)
      .padding(15.dp, 5.dp)
  )
}

@Preview(showBackground = true)
@Composable
fun TvTabItemPreview() {
  AnimeTvTheme {
    Surface(color = backgroundColor) {
      Row(
        modifier = Modifier.padding(5.dp)
      ) {
        TvTabBarItem("首页", isFocused = true, isSelected = true)
        TvTabBarItem("日本动漫", isFocused = false, isSelected = true)
        TvTabBarItem("国产动漫", isFocused = false, isSelected = false)
      }
    }
  }
}
