package com.example.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val DarkBackground = Color(0xFF09090B)
val SurfaceDark = Color(0xFF18181B)
val SurfaceVariantDark = Color(0xFF27272A)

val PrimaryViolet = Color(0xFF818CF8)
val SecondaryPurple = Color(0xFFC084FC)
val AccentCyan = Color(0xFF38BDF8)
val AccentGreen = Color(0xFF34D399)
val AccentAmber = Color(0xFFFBBF24)
val AccentRed = Color(0xFFF87171)

val TextPrimary = Color(0xFFF4F4F5)
val TextSecondary = Color(0xFFA1A1AA)

val BongoColorScheme = darkColorScheme(
    primary = PrimaryViolet,
    secondary = SecondaryPurple,
    tertiary = AccentCyan,
    background = DarkBackground,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)
