package com.example.todolistapp.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// define color scheme guna warna dari Color.kt tadi
private val AppColorScheme = lightColorScheme(  //bagitau compose, app ni warna warni
    primary = PrimaryBlue, //warna utama auto guna
    onPrimary = Color.White,  //warna text ATAS primary color

    primaryContainer = PrimaryBlueDark,
    secondary = TaskDoneGreen,
    background = Color.White,
    surface = Color.White, //background card
    surfaceVariant = CardBackground,
    onSurface = Color.Black,
    onSurfaceVariant = TextGray
)

@Composable
fun ToDoListTheme(content: @Composable () -> Unit) {
    val colorScheme = AppColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
