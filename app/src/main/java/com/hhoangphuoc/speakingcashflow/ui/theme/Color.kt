package com.hhoangphuoc.speakingcashflow.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Light theme colors
val md_theme_light_primary = Color(0xFF006C51)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFA8F2D4) // More pastel
val md_theme_light_onPrimaryContainer = Color(0xFF002117)
val md_theme_light_secondary = Color(0xFF586A60)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFDBE5DB) // More pastel
val md_theme_light_onSecondaryContainer = Color(0xFF151E19)
val md_theme_light_tertiary = Color(0xFF5B6374)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFDEE1F9) // More pastel
val md_theme_light_onTertiaryContainer = Color(0xFF151C2B)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFAF8FF)
val md_theme_light_onBackground = Color(0xFF1A1C1A)
val md_theme_light_surface = Color(0xFFFAF8FF)
val md_theme_light_onSurface = Color(0xFF1A1C1A)
val md_theme_light_surfaceVariant = Color(0xFFE8E0EC)
val md_theme_light_onSurfaceVariant = Color(0xFF404943)
val md_theme_light_outline = Color(0xFF707973)
val md_theme_light_outlineVariant = Color(0xFFC0C9C1)
val md_theme_light_scrim = Color(0xFF000000)

// Dark theme colors
val md_theme_dark_primary = Color(0xFF87DDB9) // Pastel green
val md_theme_dark_onPrimary = Color(0xFF003829)
val md_theme_dark_primaryContainer = Color(0xFF00513D)
val md_theme_dark_onPrimaryContainer = Color(0xFFA8F2D4)
val md_theme_dark_secondary = Color(0xFFBECDC2) // Pastel sage
val md_theme_dark_onSecondary = Color(0xFF293431)
val md_theme_dark_secondaryContainer = Color(0xFF3F4A45)
val md_theme_dark_onSecondaryContainer = Color(0xFFDBE5DB)
val md_theme_dark_tertiary = Color(0xFFB9C4DC) // Pastel blue
val md_theme_dark_onTertiary = Color(0xFF293144)
val md_theme_dark_tertiaryContainer = Color(0xFF404759)
val md_theme_dark_onTertiaryContainer = Color(0xFFDEE1F9)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF121212)
val md_theme_dark_onBackground = Color(0xFFE7E7E7)
val md_theme_dark_surface = Color(0xFF121212)
val md_theme_dark_onSurface = Color(0xFFE7E7E7)
val md_theme_dark_surfaceVariant = Color(0xFF303030)
val md_theme_dark_onSurfaceVariant = Color(0xFFC0C9C1)
val md_theme_dark_outline = Color(0xFF8A938C)
val md_theme_dark_outlineVariant = Color(0xFF404943)
val md_theme_dark_scrim = Color(0xFF000000)

// Pastel category colors
val pastelRed = Color(0xFFF5C2C0)
val pastelOrange = Color(0xFFFFD8B8)
val pastelYellow = Color(0xFFFFF0B8)
val pastelGreen = Color(0xFFD1E9CA)
val pastelBlue = Color(0xFFB8E4FF)
val pastelPurple = Color(0xFFDDCDF5)
val pastelPink = Color(0xFFF7C9E0)
val pastelTeal = Color(0xFFB8EAE0)
val pastelBrown = Color(0xFFE0D1AE)
val pastelGray = Color(0xFFD6D6D6)

// Function to create dark mode versions of pastel colors with reduced saturation
fun getPastelColorForDarkMode(color: Color): Color {
    val hsl = FloatArray(3)
    android.graphics.Color.colorToHSV(
        android.graphics.Color.argb(
            (color.alpha * 255).toInt(),
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        ),
        hsl
    )

    // Adjust saturation and brightness for dark mode
    hsl[1] = hsl[1] * 0.8f  // Reduce saturation slightly
    hsl[2] = hsl[2] * 0.7f  // Reduce brightness for dark mode

    val argb = android.graphics.Color.HSVToColor(
        (color.alpha * 255).toInt(),
        hsl
    )

    return Color(argb)
}