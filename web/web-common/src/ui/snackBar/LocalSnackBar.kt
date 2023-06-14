package org.solvo.web.ui.snackBar

import androidx.compose.runtime.staticCompositionLocalOf

val LocalTopSnackBar = staticCompositionLocalOf<SolvoSnackbar> { error("LocalSnackBar not found") }