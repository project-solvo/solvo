package org.solvo.web.ui.snackBar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf

val LocalTopSnackBar = staticCompositionLocalOf<SnackbarHostState> { error("LocalSnackBar not found") }