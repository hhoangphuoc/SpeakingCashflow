package com.hhoangphuoc.speakingcashflow.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.hhoangphuoc.speakingcashflow.data.TransactionType
import com.hhoangphuoc.speakingcashflow.ui.theme.LocalIsDarkMode
import com.hhoangphuoc.speakingcashflow.ui.theme.getPastelColorForDarkMode
import com.hhoangphuoc.speakingcashflow.ui.theme.pastelBlue
import com.hhoangphuoc.speakingcashflow.ui.theme.pastelBrown
import com.hhoangphuoc.speakingcashflow.ui.theme.pastelGray
import com.hhoangphuoc.speakingcashflow.ui.theme.pastelGreen
import com.hhoangphuoc.speakingcashflow.ui.theme.pastelOrange
import com.hhoangphuoc.speakingcashflow.ui.theme.pastelPink
import com.hhoangphuoc.speakingcashflow.ui.theme.pastelPurple
import com.hhoangphuoc.speakingcashflow.ui.theme.pastelRed
import com.hhoangphuoc.speakingcashflow.ui.theme.pastelTeal
import com.hhoangphuoc.speakingcashflow.ui.theme.pastelYellow
import kotlin.math.abs
import androidx.core.graphics.toColorInt

fun String.toColor(): Color {
    return try {
        Color(this.toColorInt())
    } catch (e: Exception) {
        Color.Gray
    }
}

fun Color.toPastelColor(isDarkMode: Boolean): Color {
    val hsl = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsl)

    // Make it more pastel for light mode
    if (!isDarkMode) {
        hsl[1] = (hsl[1] * 0.65f).coerceAtMost(0.6f)  // Lower saturation
        hsl[2] = (hsl[2] * 1.2f).coerceAtMost(0.95f)  // Higher brightness
    } else {
        // For dark mode, keep some saturation but make it darker
        hsl[1] = (hsl[1] * 0.8f).coerceAtMost(0.7f)
        hsl[2] = (hsl[2] * 0.8f).coerceAtMost(0.7f)
    }

    return Color(android.graphics.Color.HSVToColor(hsl))
}

@Composable
fun getTransactionTypeColor(type: TransactionType): Color {
    val isDarkMode = LocalIsDarkMode.current

    return when (type) {
        TransactionType.INCOME -> {
            if (isDarkMode) pastelGreen.copy(alpha = 0.8f)
            else pastelGreen
        }
        TransactionType.EXPENSE -> {
            if (isDarkMode) pastelRed.copy(alpha = 0.8f)
            else pastelRed
        }
    }
}

@Composable
fun getCategoryPastelColor(categoryName: String): Color {
    val isDarkMode = LocalIsDarkMode.current

    val baseColor = when {
        categoryName.contains("food", ignoreCase = true) -> pastelOrange
        categoryName.contains("rent", ignoreCase = true) -> pastelPurple
        categoryName.contains("transport", ignoreCase = true) -> pastelBlue
        categoryName.contains("entertainment", ignoreCase = true) -> pastelPink
        categoryName.contains("shopping", ignoreCase = true) -> pastelYellow
        categoryName.contains("health", ignoreCase = true) -> pastelRed
        categoryName.contains("utilities", ignoreCase = true) -> pastelTeal
        categoryName.contains("education", ignoreCase = true) -> pastelGreen
        categoryName.contains("travel", ignoreCase = true) -> pastelBrown
        else -> getCategoryColorByString(categoryName)
    }

    return if (isDarkMode) getPastelColorForDarkMode(baseColor) else baseColor
}

@Composable
fun getCategoryPastelColor(colorHex: String): Color {
    val isDarkMode = LocalIsDarkMode.current
    val color = colorHex.toColor()

    return if (isDarkMode) {
        color.toPastelColor(true)
    } else {
        color.toPastelColor(false)
    }
}

private fun getCategoryColorByString(text: String): Color {
    // Generate a hash code from the string and use it to select a pastel color
    val pastelColors = listOf(
        pastelRed, pastelOrange, pastelYellow, pastelGreen,
        pastelBlue, pastelPurple, pastelPink, pastelTeal,
        pastelBrown, pastelGray
    )

    val index = abs(text.hashCode()) % pastelColors.size
    return pastelColors[index]
}

fun Color.toHsl(): FloatArray {
    val hsl = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsl)
    return hsl
}