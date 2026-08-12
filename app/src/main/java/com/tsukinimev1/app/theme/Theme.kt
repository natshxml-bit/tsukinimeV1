package com.tsukinimev1.app.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val Bg = Color(0xFF0A0A0A)
val Surface = Color(0xFF141414)
val SurfaceAlt = Color(0xFF1C1C1C)
val TextPrimary = Color(0xFFF4F4F4)
val TextSecondary = Color(0xFF9A9A9A)
val AccentRed = Color(0xFFEF4444)
val AccentRedSoft = Color(0x33EF4444)
val Cyan = Color(0xFF22D3EE)
val Green = Color(0xFF34D399)
val Indigo = Color(0xFF818CF8)
val Amber = Color(0xFFEAB308)

private val DarkColors = darkColorScheme(
    primary = AccentRed,
    onPrimary = Color.White,
    secondary = AccentRed,
    background = Bg,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    error = Color(0xFFF87171),
)

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
)

@Composable
fun TsukiNimeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        shapes = AppShapes,
        content = content,
    )
}
