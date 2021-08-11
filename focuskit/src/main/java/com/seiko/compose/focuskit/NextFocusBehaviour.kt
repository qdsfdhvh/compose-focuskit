package com.seiko.compose.focuskit

import androidx.compose.foundation.lazy.LazyListState

interface NextFocusBehaviour {
  fun getNext(key: TvControllerKey, state: LazyListState, index: Int): Int?

  companion object {
    val Horizontal: NextFocusBehaviour = object : NextFocusBehaviour {
      override fun getNext(key: TvControllerKey, state: LazyListState, index: Int): Int? {
        return when (key) {
          TvControllerKey.Left -> index - 1
          TvControllerKey.Right -> index + 1
          else -> null
        }
      }
    }

    val Vertical: NextFocusBehaviour = object : NextFocusBehaviour {
      override fun getNext(key: TvControllerKey, state: LazyListState, index: Int): Int? {
        return when (key) {
          TvControllerKey.Up -> index - 1
          TvControllerKey.Down -> index + 1
          else -> null
        }
      }
    }
  }
}
