package com.seiko.compose.focuskit.internal

import com.seiko.compose.focuskit.ContainerTvFocusItem
import com.seiko.compose.focuskit.NextFocusBehaviour
import com.seiko.compose.focuskit.RootTvFocusItem
import com.seiko.compose.focuskit.TvControllerKey
import com.seiko.compose.focuskit.TvFocusHandler
import com.seiko.compose.focuskit.TvFocusItem

internal class ContainerTvFocusHandler(
  private val container: ContainerTvFocusItem,
  private val nextFocus: NextFocusBehaviour,
  private val onFocusChange: (RootTvFocusItem, Int) -> Unit
) : TvFocusHandler {
  override fun handleKey(key: TvControllerKey, rootItem: RootTvFocusItem): Boolean {
    val nextFocusState = nextFocus.getNext(container, key)
    if (nextFocusState.index != null) {
      onFocusChange(rootItem, nextFocusState.index)
    }
    return nextFocusState.handleKey
  }

  override fun getFocus(): TvFocusItem? {
    return container.getChild(container.focusIndex)
  }
}
