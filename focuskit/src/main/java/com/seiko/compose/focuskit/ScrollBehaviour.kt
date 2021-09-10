package com.seiko.compose.focuskit

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

interface ScrollBehaviour {
  suspend fun onScroll(state: LazyListState, focusItem: LazyListItemInfo, density: Density)

  companion object {
    val Horizontal: ScrollBehaviour get() = HorizontalImpl
    val Vertical: ScrollBehaviour get() = VerticalImpl
  }
}

private object HorizontalImpl : ScrollBehaviour {
  override suspend fun onScroll(
    state: LazyListState,
    focusItem: LazyListItemInfo,
    density: Density
  ) {
    val viewStart = state.layoutInfo.viewportStartOffset
    val viewEnd = state.layoutInfo.viewportEndOffset
    val viewSize = viewEnd - viewStart

    val itemStart = focusItem.offset
    val itemEnd = focusItem.offset + focusItem.size
    val itemSize = focusItem.size

    val leftLine = viewStart + viewSize * 0.3
    val rightLine = viewStart + viewSize * 0.7

    val value = when {
      itemStart > rightLine -> itemSize.toFloat()
      itemEnd < leftLine -> (-itemSize).toFloat()
      else -> return
    }
    state.startScroll(value)
  }
}

private object VerticalImpl : ScrollBehaviour {
  override suspend fun onScroll(
    state: LazyListState,
    focusItem: LazyListItemInfo,
    density: Density
  ) {
    val viewStart = state.layoutInfo.viewportStartOffset
    val viewEnd = state.layoutInfo.viewportEndOffset
    val viewSize = viewEnd - viewStart

    val itemStart = focusItem.offset
    val itemEnd = focusItem.offset + focusItem.size

    val offSect = density.run { 20.dp.roundToPx() }

    val value = when {
      itemStart < viewStart -> itemStart.toFloat() - offSect
      itemEnd > viewStart + viewSize -> (itemEnd - viewSize - viewStart).toFloat() + offSect
      else -> return
    }
    state.startScroll(value)
  }
}

private suspend fun LazyListState.startScroll(value: Float) {
  stopScroll()
  animateScrollBy(value, tween(SCROLL_ANIMATION_DURATION, 0, LinearEasing))
}

private const val SCROLL_ANIMATION_DURATION = 150
