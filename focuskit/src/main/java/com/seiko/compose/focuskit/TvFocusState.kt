package com.seiko.compose.focuskit

import androidx.compose.ui.focus.FocusState

enum class TvFocusState : FocusState {
  None,
  Active,
  ActiveParent;

  override val isFocused: Boolean
    get() = when (this) {
      Active, ActiveParent -> true
      None -> false
    }

  override val hasFocus: Boolean
    get() = when (this) {
      ActiveParent -> true
      Active, None -> false
    }

  override val isCaptured: Boolean
    get() = false
}