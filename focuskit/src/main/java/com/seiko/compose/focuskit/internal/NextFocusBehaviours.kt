package com.seiko.compose.focuskit.internal

import com.seiko.compose.focuskit.ContainerTvFocusItem
import com.seiko.compose.focuskit.NextFocusBehaviour
import com.seiko.compose.focuskit.NextFocusState
import com.seiko.compose.focuskit.TvControllerKey

internal object HorizontalNextFocusBehaviour : NextFocusBehaviour {
  override fun getNext(container: ContainerTvFocusItem, key: TvControllerKey): NextFocusState {
    val focusIndex = container.focusIndex
    val lastIndex = container.getLastIndex()
    return when {
      key === TvControllerKey.Left && focusIndex > 0 -> {
        NextFocusState(focusIndex - 1, true)
      }
      key === TvControllerKey.Right && (lastIndex == null || focusIndex < lastIndex) -> {
        NextFocusState(focusIndex + 1, true)
      }
      else -> NextFocusState.False
    }
  }
}

internal object VerticalNextFocusBehaviour : NextFocusBehaviour {
  override fun getNext(container: ContainerTvFocusItem, key: TvControllerKey): NextFocusState {
    val focusIndex = container.focusIndex
    val lastIndex = container.getLastIndex()
    return when {
      key === TvControllerKey.Up && focusIndex > 0 -> {
        NextFocusState(focusIndex - 1, true)
      }
      key === TvControllerKey.Down && (lastIndex == null || focusIndex < lastIndex) -> {
        NextFocusState(focusIndex + 1, true)
      }
      else -> NextFocusState.False
    }
  }
}