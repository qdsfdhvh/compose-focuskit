package com.seiko.compose.focuskit

import androidx.compose.ui.focus.FocusRequester

fun FocusRequester.Companion.createRefs(size: Int): Array<FocusRequester> {
  return Array(size) { FocusRequester() }
}

fun Array<FocusRequester>.requestFocus(index: Int): Result<Unit>? {
  return getOrNull(index)?.runCatching { requestFocus() }
}
