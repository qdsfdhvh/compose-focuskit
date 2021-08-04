package com.seiko.compose.focuskit

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seiko.compose.focuskit.internal.ContainerTvFocusHandler
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
    modifier = modifier.handleTvScroll(
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
    modifier = modifier.handleTvScroll(
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

@SuppressLint("UnnecessaryComposedModifier")
private fun Modifier.handleTvScroll(
  container: ContainerTvFocusItem,
  state: LazyListState,
  contentPadding: TvPaddingValues,
  nextFocusBehaviour: NextFocusBehaviour,
  scrollBehaviour: ScrollBehaviour,
  density: Density,
  coroutineScope: CoroutineScope
) = composed {
  DisposableEffect(container.id) {
    container.listState = state
    onDispose {
      container.listState = null
    }
  }
  handleTvContainerFocus(
    container = container,
    nextFocus = OnlyWithinLazyListVisibleItems(state, nextFocusBehaviour),
    onFocusChange = { rootViewItem, focusIndex ->
      coroutineScope.launch {
        state.scrollAndFocusTv(
          container = container,
          rootItem = rootViewItem,
          focusIndex = focusIndex,
          scrollBehaviour = scrollBehaviour,
          density = density,
          contentPadding = contentPadding
        )
      }
    }
  )
}

private suspend fun LazyListState.scrollAndFocusTv(
  container: ContainerTvFocusItem,
  rootItem: RootTvFocusItem,
  focusIndex: Int,
  scrollBehaviour: ScrollBehaviour,
  density: Density,
  contentPadding: TvPaddingValues
) {
  stopScroll()

  val foundItem = layoutInfo.visibleItemsInfo.find { it.index == focusIndex } ?: return
  var value = scrollBehaviour.calculateScrollBy(this, foundItem, density, contentPadding)

  // 暂时添加些便宜量，确保visibleItems包含下一个item
  val offset = density.run { 20.dp.roundToPx() }
  if (value > 0) {
    value += offset
  } else {
    value -= offset
  }

  val focusItem = container.getChild(focusIndex)
  if (focusItem == null) {
    Logger.w("[scrollAndFocusTV] Failed to focus at index: $focusIndex, focused item is null!")
    return
  }

  val prevFocusIndex = container.focusIndex
  container.focusIndex = focusIndex
  if (!rootItem.refocus()) {
    container.focusIndex = prevFocusIndex
    Logger.w("[scrollAndFocusTV] Failed to focus at index: $focusIndex, $focusItem")
    return
  }

  if (value != 0f) {
    animateScrollBy(value, tween(SCROLL_ANIMATION_DURATION, 0, LinearEasing))
    Logger.d("[scrollAndFocusTV] Scrolled and focused to $focusIndex, $focusItem")
  } else {
    Logger.d("[scrollAndFocusTV] Focused to $focusIndex, $focusItem")
  }
}

private fun Modifier.handleTvContainerFocus(
  container: ContainerTvFocusItem,
  nextFocus: NextFocusBehaviour,
  onFocusChange: (RootTvFocusItem, Int) -> Unit
) = this.then(
  tvFocusable(container) {
    ContainerTvFocusHandler(container, nextFocus, onFocusChange)
  }
)

private class OnlyWithinLazyListVisibleItems(
  val state: LazyListState,
  val nextFocusBehaviour: NextFocusBehaviour
) : NextFocusBehaviour {
  override fun getNext(
    container: ContainerTvFocusItem,
    key: TvControllerKey
  ): NextFocusState {
    val focusState = nextFocusBehaviour.getNext(container, key)
    return if (focusState.index == null) {
      if (state.isScrollInProgress) {
        NextFocusState.True
      } else {
        focusState
      }
    } else if (state.layoutInfo.visibleItemsInfo.any { it.index == focusState.index }) {
      focusState
    } else {
      NextFocusState.True
    }
  }
}
