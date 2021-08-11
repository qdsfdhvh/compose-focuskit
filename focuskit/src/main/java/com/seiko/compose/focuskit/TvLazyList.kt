package com.seiko.compose.focuskit

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val SCROLL_ANIMATION_DURATION = 150

@Composable
fun TvLazyColumn(
  container: ContainerTvFocusItem,
  modifier: Modifier = Modifier,
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
  state: LazyListState = rememberLazyListState(),
  contentPadding: TvPaddingValues = TvPaddingValues(0.dp),
  scrollBehaviour: ScrollBehaviour = ScrollBehaviour.Default,
  nextFocusBehaviour: NextFocusBehaviour = NextFocusBehaviour.Vertical,
  spaceBetween: Dp = 0.dp,
  content: LazyListScope.() -> Unit
) {
  val density = LocalDensity.current

  LazyColumn(
    modifier = modifier
      .handleTvScroll(
        container = container,
        state = state,
        contentPadding = contentPadding,
        nextFocusBehaviour = nextFocusBehaviour,
        scrollBehaviour = scrollBehaviour,
        density = density,
        coroutineScope = coroutineScope
      ),
    state = state,
    contentPadding = PaddingValues(top = contentPadding.start, bottom = contentPadding.end),
    verticalArrangement = Arrangement.spacedBy(spaceBetween, Alignment.Top),
    content = content
  )
}

@Composable
fun TvLazyRow(
  container: ContainerTvFocusItem,
  modifier: Modifier = Modifier,
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
  state: LazyListState = rememberLazyListState(),
  contentPadding: TvPaddingValues = TvPaddingValues(0.dp),
  scrollBehaviour: ScrollBehaviour = ScrollBehaviour.Default,
  nextFocusBehaviour: NextFocusBehaviour = NextFocusBehaviour.Horizontal,
  spaceBetween: Dp = 0.dp,
  content: LazyListScope.() -> Unit
) {
  val density = LocalDensity.current
  LazyRow(
    modifier = modifier
      .handleTvScroll(
        container = container,
        state = state,
        contentPadding = contentPadding,
        nextFocusBehaviour = nextFocusBehaviour,
        scrollBehaviour = scrollBehaviour,
        density = density,
        coroutineScope = coroutineScope
      ),
    state = state,
    contentPadding = PaddingValues(start = contentPadding.start, end = contentPadding.end),
    horizontalArrangement = Arrangement.spacedBy(spaceBetween, Alignment.Start),
    content = content
  )
}

private fun Modifier.handleTvScroll(
  container: ContainerTvFocusItem,
  state: LazyListState,
  contentPadding: TvPaddingValues,
  nextFocusBehaviour: NextFocusBehaviour,
  scrollBehaviour: ScrollBehaviour,
  density: Density,
  coroutineScope: CoroutineScope
) = this
  .onTvKeyEvent { key ->
    if (state.isScrollInProgress) {
      return@onTvKeyEvent true
    }

    val nextFocusIndex = nextFocusBehaviour.getNext(key, state, container.focusIndex)
      ?: return@onTvKeyEvent false

    val foundItem = state.layoutInfo.visibleItemsInfo.find { it.index == nextFocusIndex }
      ?: return@onTvKeyEvent false

    container.run {
      focusIndex = nextFocusIndex
      getFocus()?.requestFocus()
    }

    coroutineScope.launch {
      state.scrollAndFocusTv(foundItem, scrollBehaviour, density, contentPadding)
    }
    true
  }
  .tvFocusTarget(container)

private suspend fun LazyListState.scrollAndFocusTv(
  foundItem: LazyListItemInfo,
  scrollBehaviour: ScrollBehaviour,
  density: Density,
  padding: TvPaddingValues
) {
  stopScroll()

  var value = scrollBehaviour.calculateScrollBy(this, foundItem, density, padding)
  val offset = density.run { 20.dp.roundToPx() }
  if (value > 0) {
    value += offset
  } else if (value < 0) {
    value -= offset
  }

  if (value != 0f) {
    animateScrollBy(value, tween(SCROLL_ANIMATION_DURATION, 0, LinearEasing))
  }
}
