package com.seiko.compose.focuskit

import com.seiko.compose.focuskit.internal.HorizontalNextFocusBehaviour
import com.seiko.compose.focuskit.internal.VerticalNextFocusBehaviour

data class NextFocusState(val index: Int?, val handleKey: Boolean) {
  companion object {
    val True = NextFocusState(null, true)
    val False = NextFocusState(null, false)
  }
}

interface NextFocusBehaviour {
  fun getNext(container: ContainerTvFocusItem, key: TvControllerKey): NextFocusState

  companion object {
    val Horizontal: NextFocusBehaviour = HorizontalNextFocusBehaviour
    val Vertical: NextFocusBehaviour = VerticalNextFocusBehaviour
  }
}
