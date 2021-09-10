package com.seiko.compose.focuskit.demo

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

val LocalAppNavigator = staticCompositionLocalOf<NavController> { error("No Navigator") }
