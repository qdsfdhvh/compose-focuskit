package com.seiko.compose.focuskit.internal

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Density
import com.seiko.compose.focuskit.ScrollBehaviour
import com.seiko.compose.focuskit.TvPaddingValues

internal object DefaultScrollBehaviour : ScrollBehaviour {
  override fun calculateScrollBy(
    state: LazyListState,
    focusItem: LazyListItemInfo,
    density: Density,
    padding: TvPaddingValues
  ): Float {
    val actualViewportStart = state.layoutInfo.viewportStartOffset
    val actualViewportEnd = state.layoutInfo.viewportEndOffset
    val actualViewportSize = actualViewportEnd - actualViewportStart

    val itemStart = focusItem.offset
    val itemEnd = focusItem.offset + focusItem.size

    val contentStartPaddingPx = density.run { padding.start.roundToPx() }
    val contentEndPaddingPx = density.run { padding.end.roundToPx() }

    return when {
      itemStart < actualViewportStart + contentStartPaddingPx -> {
        itemStart.toFloat()
      }
      itemEnd > actualViewportStart + actualViewportSize - contentEndPaddingPx -> {
        (itemEnd - actualViewportSize + contentEndPaddingPx - actualViewportStart).toFloat()
      }
      else -> 0f
    }
  }
}