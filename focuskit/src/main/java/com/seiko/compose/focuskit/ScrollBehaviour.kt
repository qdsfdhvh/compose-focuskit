package com.seiko.compose.focuskit

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seiko.compose.focuskit.internal.DefaultScrollBehaviour

data class TvPaddingValues(val start: Dp = 0.dp, val end: Dp = 0.dp) {
  constructor(padding: Dp) : this(padding, padding)
}

interface ScrollBehaviour {
  fun calculateScrollBy(
    state: LazyListState, focusItem: LazyListItemInfo,
    density: Density, padding: TvPaddingValues
  ): Float

  companion object {
    val Default: ScrollBehaviour = DefaultScrollBehaviour
  }
}