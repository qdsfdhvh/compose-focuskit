package com.seiko.compose.focuskit

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch

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

  this
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

        scrollBehaviour.onScroll(state, foundItem, density)
      }
      true
    }
}

private val Saver = mapSaver(
  save = { mapOf("index" to it.index) },
  restore = { FocusIndexInteraction(it["index"] as? Int ?: 0) }
)

@Composable
fun rememberFocusIndexInteraction(key: Any? = null): MutableState<FocusIndexInteraction> {
  return rememberSaveable(key, stateSaver = Saver) {
    mutableStateOf(FocusIndexInteraction(0))
  }
}

private const val SCROLL_ANIMATION_DURATION = 150