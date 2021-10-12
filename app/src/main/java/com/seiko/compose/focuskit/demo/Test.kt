package com.seiko.compose.focuskit.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppScreen() {
  val (focus1, focus2) = remember { FocusRequester.createRefs() }

  Row(
    modifier = Modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box1(Modifier.focusOrder(focus1) { right = focus2; left = focus2 })
    Box2(Modifier.focusOrder(focus2) { left = focus1; right = focus1  })
  }

  SideEffect {
    focus1.requestFocus()
  }
}

@Composable
fun Box1(modifier: Modifier = Modifier) {
  var isParentFocused by remember { mutableStateOf(false) }
  var focusIndex by remember { mutableStateOf(0) }

  LazyColumn(
    modifier = modifier
      .onFocusChanged { isParentFocused = it.isFocused }
      // .focusTarget(),
  ) {
    items(10) { index ->
      val focusRequester = remember { FocusRequester() }
      var isFocused by remember { mutableStateOf(false) }
      Text(
        if (isFocused) "Focused" else "",
        color = Color.Black,
        style = MaterialTheme.typography.h5,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .padding(10.dp)
          .background(Color.Green)
          .width(120.dp)
          .padding(vertical = 10.dp)
          .onFocusChanged {
            isFocused = it.isFocused
            if (isFocused) focusIndex = index
          }
          .focusOrder(focusRequester)
          .focusTarget(),
      )

      if (isParentFocused && focusIndex == index) {
        SideEffect {
          focusRequester.requestFocus()
        }
      }
    }
  }
}

@Composable
fun Box2(modifier: Modifier = Modifier) {
  var isParentFocused by remember { mutableStateOf(false) }
  Box(
    modifier = modifier
      .background(Color.Blue)
      .size(200.dp)
      .onFocusChanged { isParentFocused = it.isFocused }
      .focusTarget(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      if (isParentFocused) "Focused" else "",
      color = Color.White,
      style = MaterialTheme.typography.h3
    )

  }
}