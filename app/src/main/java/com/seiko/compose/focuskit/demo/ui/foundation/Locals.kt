package com.seiko.compose.focuskit.demo.ui.foundation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

val LocalAppNavigator = staticCompositionLocalOf<NavController> { error("No Navigator") }