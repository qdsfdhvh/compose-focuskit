package com.seiko.compose.focuskit

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collect

class FocusIndexInteraction(val index: Int) : Interaction

@Composable
fun InteractionSource.collectFocusIndexAsState(): State<Int> {
  val focusIndex = remember { mutableStateOf(-1) }
  LaunchedEffect(this) {
    interactions.collect { interaction ->
      if (interaction is FocusIndexInteraction) {
        focusIndex.value = interaction.index
      }
    }
  }
  return focusIndex
}