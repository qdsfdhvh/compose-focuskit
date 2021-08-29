package com.seiko.compose.focuskit

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.launch

private val Saver = mapSaver(
  save = { mapOf("index" to it.index) },
  restore = {  FocusIndexInteraction(it["index"] as? Int ?: 0) }
)

@Composable
fun rememberFocusIndexInteraction(key: Any? = null): MutableState<FocusIndexInteraction> {
  return rememberSaveable(key, stateSaver = Saver) {
    mutableStateOf(FocusIndexInteraction(0))
  }
}

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.onFocusScroll(
  state: LazyListState,
  nextFocusBehaviour: NextFocusBehaviour,
  scrollBehaviour: ScrollBehaviour,
  interactionSource: MutableInteractionSource? = null,
) = composed {
  val density = LocalDensity.current
  val scope = rememberCoroutineScope()
  var focusIndexInteraction by rememberFocusIndexInteraction(state)

  DisposableEffect(state) {
    scope.launch {
      interactionSource?.emit(focusIndexInteraction)
    }
    onDispose { }
  }

  Modifier
    .onTvKeyEvent { key ->
      if (state.isScrollInProgress) {
        return@onTvKeyEvent true
      }

      val nextFocusIndex = nextFocusBehaviour.getNext(key, state, focusIndexInteraction.index)
        ?: return@onTvKeyEvent false

      val foundItem = state.layoutInfo.visibleItemsInfo.find { it.index == nextFocusIndex }
        ?: return@onTvKeyEvent false

      scope.launch {
        val interaction = FocusIndexInteraction(nextFocusIndex)
        interactionSource?.emit(interaction)
        focusIndexInteraction = interaction
        state.scrollAndFocusTv(foundItem, scrollBehaviour, density)
      }
      true
    }
}

private suspend fun LazyListState.scrollAndFocusTv(
  foundItem: LazyListItemInfo,
  scrollBehaviour: ScrollBehaviour,
  density: Density,
) {
  stopScroll()

  val value = scrollBehaviour.calculateScrollBy(this, foundItem, density)
  if (value != 0f) {
    animateScrollBy(value, tween(SCROLL_ANIMATION_DURATION, 0, LinearEasing))
  }
}

private const val SCROLL_ANIMATION_DURATION = 150