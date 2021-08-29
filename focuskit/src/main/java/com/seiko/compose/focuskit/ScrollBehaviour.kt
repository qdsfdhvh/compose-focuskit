package com.seiko.compose.focuskit

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

interface ScrollBehaviour {
  fun calculateScrollBy(state: LazyListState, focusItem: LazyListItemInfo, density: Density): Float

  companion object {
    val Horizontal: ScrollBehaviour = object : ScrollBehaviour {
      override fun calculateScrollBy(
        state: LazyListState,
        focusItem: LazyListItemInfo,
        density: Density,
      ): Float {
        val viewStart = state.layoutInfo.viewportStartOffset
        val viewEnd = state.layoutInfo.viewportEndOffset
        val viewSize = viewEnd - viewStart

        val itemStart = focusItem.offset
        val itemEnd = focusItem.offset + focusItem.size
        val itemSize = focusItem.size

        val leftLine = viewStart + viewSize * 0.3
        val rightLine = viewStart + viewSize * 0.7

        return when {
          itemStart > rightLine -> itemSize.toFloat()
          itemEnd < leftLine -> (-itemSize).toFloat()
          else -> 0f
        }
      }
    }

    val Vertical: ScrollBehaviour = object : ScrollBehaviour {
      override fun calculateScrollBy(
        state: LazyListState,
        focusItem: LazyListItemInfo,
        density: Density
      ): Float {
        val viewStart = state.layoutInfo.viewportStartOffset
        val viewEnd = state.layoutInfo.viewportEndOffset
        val viewSize = viewEnd - viewStart

        val itemStart = focusItem.offset
        val itemEnd = focusItem.offset + focusItem.size

        val offSect= density.run { 20.dp.roundToPx() }

        return when {
          itemStart < viewStart -> itemStart.toFloat() - offSect
          itemEnd > viewStart + viewSize -> (itemEnd - viewSize - viewStart).toFloat() + offSect
          else -> 0f
        }
      }
    }
  }
}
