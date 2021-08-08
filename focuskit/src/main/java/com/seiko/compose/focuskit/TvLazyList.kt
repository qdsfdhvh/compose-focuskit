package com.seiko.compose.focuskit

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.key.*
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
  val nextFocusBehaviourWrapper = remember(state) {
    OnlyWithinLazyListVisibleItems(state, nextFocusBehaviour)
  }
  this
    .onKeyEvent {
      if (it.type == KeyEventType.KeyDown) {
        Logger.log(Log.DEBUG) { "$container onKeyEvent=${it}" }
        val key = controllerKey(it) ?: return@onKeyEvent false

        val nextFocusState = nextFocusBehaviourWrapper.getNext(container, key)
        if (nextFocusState.index != null) {
          coroutineScope.launch {
            state.scrollAndFocusTv(
              container = container,
              focusIndex = nextFocusState.index,
              scrollBehaviour = scrollBehaviour,
              density = density,
              contentPadding = contentPadding,
            )
          }
          nextFocusState.handleKey
        }
      }
      false
    }
    .tvFocusTarget(container)
}

private suspend fun LazyListState.scrollAndFocusTv(
  container: ContainerTvFocusItem,
  focusIndex: Int,
  scrollBehaviour: ScrollBehaviour,
  density: Density,
  contentPadding: TvPaddingValues
) {
  stopScroll()

  Logger.log(Log.DEBUG) { "focus to index(${focusIndex}) with $container" }

  val foundItem = layoutInfo.visibleItemsInfo.find { it.index == focusIndex } ?: return
  var value = scrollBehaviour.calculateScrollBy(this, foundItem, density, contentPadding)

  // 暂时添加些便宜量，确保visibleItems包含下一个item
  val offset = density.run { 20.dp.roundToPx() }
  if (value > 0) {
    value += offset
  } else if (value < 0) {
    value -= offset
  }

  val focusItem = container.getChild(focusIndex)
  if (focusItem == null) {
    Logger.log(Log.WARN) { "failed to focus at index($focusIndex) with $container, focused item is null!" }
    return
  }

  val prevFocusIndex = container.focusIndex
  container.focusIndex = focusIndex
  if (!container.refocus()) {
    container.focusIndex = prevFocusIndex
    Logger.log(Log.WARN) { "failed to focus at index($focusIndex) with $container, root refocus failed" }
    return
  }

  if (value != 0f) {
    Logger.log(Log.INFO) { "scroll to index($focusIndex) with $container, value=$value" }
    animateScrollBy(value, tween(SCROLL_ANIMATION_DURATION, 0, LinearEasing))
  }
}

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
