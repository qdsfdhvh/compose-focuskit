package com.seiko.compose.focuskit

interface TvFocusHandler {
  fun handleKey(key: TvControllerKey, rootItem: RootTvFocusItem): Boolean
  fun getFocus(): TvFocusItem? = null
}

interface TvFocusHandlerOwner {
  var focusHandler: TvFocusHandler
}