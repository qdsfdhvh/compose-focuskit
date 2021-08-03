package com.seiko.compose.focuskit

enum class TvFocusState {
  None, Active, ActiveParent;

  val isFocused: Boolean
    get() = when (this) {
      Active, ActiveParent -> true
      None -> false
    }
}