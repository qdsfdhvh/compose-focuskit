package com.seiko.compose.focuskit

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TvLazyColumn(
  modifier: Modifier = Modifier,
  interactionSource: MutableInteractionSource? = null,
  state: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  scrollBehaviour: ScrollBehaviour = ScrollBehaviour.Vertical,
  nextFocusBehaviour: NextFocusBehaviour = NextFocusBehaviour.Vertical,
  spaceBetween: Dp = 0.dp,
  content: LazyListScope.() -> Unit
) {
  LazyColumn(
    modifier = modifier
      .onFocusScroll(
        state = state,
        nextFocusBehaviour = nextFocusBehaviour,
        scrollBehaviour = scrollBehaviour,
        interactionSource = interactionSource,
      )
      .focusable(interactionSource = interactionSource),
    state = state,
    contentPadding = contentPadding,
    verticalArrangement = Arrangement.spacedBy(spaceBetween, Alignment.Top),
    content = content
  )
}

@Composable
fun TvLazyRow(
  modifier: Modifier = Modifier,
  interactionSource: MutableInteractionSource? = null,
  state: LazyListState = rememberLazyListState(),
  contentPadding: PaddingValues = PaddingValues(0.dp),
  scrollBehaviour: ScrollBehaviour = ScrollBehaviour.Horizontal,
  nextFocusBehaviour: NextFocusBehaviour = NextFocusBehaviour.Horizontal,
  spaceBetween: Dp = 0.dp,
  content: LazyListScope.() -> Unit
) {
  LazyRow(
    modifier = modifier
      .onFocusScroll(
        state = state,
        nextFocusBehaviour = nextFocusBehaviour,
        scrollBehaviour = scrollBehaviour,
        interactionSource = interactionSource,
      )
      .focusable(interactionSource = interactionSource),
    state = state,
    contentPadding = contentPadding,
    horizontalArrangement = Arrangement.spacedBy(spaceBetween, Alignment.Start),
    content = content
  )
}
