package com.seiko.compose.focuskit

data class NextFocusState(val index: Int?, val handleKey: Boolean) {
  companion object {
    val True = NextFocusState(null, true)
    val False = NextFocusState(null, false)
  }
}

interface NextFocusBehaviour {
  fun getNext(container: ContainerTvFocusItem, key: TvControllerKey): NextFocusState
}

object HorizontalNextFocusBehaviour : NextFocusBehaviour {
  override fun getNext(container: ContainerTvFocusItem, key: TvControllerKey): NextFocusState {
    val focusIndex = container.focusIndex
    val lastIndex = container.getLastIndex()
    Logger.d("NextFocus Horizontal focusIndex=$focusIndex lastIndex=$lastIndex")
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

object VerticalNextFocusBehaviour : NextFocusBehaviour {
  override fun getNext(container: ContainerTvFocusItem, key: TvControllerKey): NextFocusState {
    val focusIndex = container.focusIndex
    val lastIndex = container.getLastIndex()
    return when {
      key == TvControllerKey.Up && focusIndex > 0 -> {
        NextFocusState(focusIndex - 1, true)
      }
      key == TvControllerKey.Down && (lastIndex == null || focusIndex < lastIndex) -> {
        NextFocusState(focusIndex + 1, true)
      }
      else -> NextFocusState.False
    }
  }
}