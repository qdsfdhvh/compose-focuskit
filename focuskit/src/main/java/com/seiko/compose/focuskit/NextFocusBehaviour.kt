package com.seiko.compose.focuskit

import androidx.compose.foundation.lazy.LazyListState

interface NextFocusBehaviour {
  fun getNext(key: TvKeyEvent, state: LazyListState, index: Int): Int?

  companion object {
    val Horizontal: NextFocusBehaviour = object : NextFocusBehaviour {
      override fun getNext(key: TvKeyEvent, state: LazyListState, index: Int): Int? {
        return when (key) {
          TvKeyEvent.Left -> index - 1
          TvKeyEvent.Right -> index + 1
          else -> null
        }
      }
    }

    val Vertical: NextFocusBehaviour = object : NextFocusBehaviour {
      override fun getNext(key: TvKeyEvent, state: LazyListState, index: Int): Int? {
        return when (key) {
          TvKeyEvent.Up -> index - 1
          TvKeyEvent.Down -> index + 1
          else -> null
        }
      }
    }
  }
}
