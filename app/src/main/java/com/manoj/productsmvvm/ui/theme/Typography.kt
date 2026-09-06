package com.manoj.productsmvvm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.manoj.productsmvvm.R

/**
 * Lexend is bundled in res/font, so typography works offline and does not depend on a font
 * provider. The variable font maps each requested FontWeight to its `wght` variation axis.
 */
private val Lexend = FontFamily(
    Font(R.font.lexend_variable, weight = FontWeight.Normal),
    Font(R.font.lexend_variable, weight = FontWeight.Medium),
    Font(R.font.lexend_variable, weight = FontWeight.SemiBold),
    Font(R.font.lexend_variable, weight = FontWeight.Bold),
)

private val MaterialTypography = Typography()

// Apply Lexend to every Material text role; individual composables can still override weight/size.
val AppTypography = Typography(
    displayLarge = MaterialTypography.displayLarge.withLexend(),
    displayMedium = MaterialTypography.displayMedium.withLexend(),
    displaySmall = MaterialTypography.displaySmall.withLexend(),
    headlineLarge = MaterialTypography.headlineLarge.withLexend(),
    headlineMedium = MaterialTypography.headlineMedium.withLexend(),
    headlineSmall = MaterialTypography.headlineSmall.withLexend(),
    titleLarge = MaterialTypography.titleLarge.withLexend(),
    titleMedium = MaterialTypography.titleMedium.withLexend(),
    titleSmall = MaterialTypography.titleSmall.withLexend(),
    bodyLarge = MaterialTypography.bodyLarge.withLexend(),
    bodyMedium = MaterialTypography.bodyMedium.withLexend(),
    bodySmall = MaterialTypography.bodySmall.withLexend(),
    labelLarge = MaterialTypography.labelLarge.withLexend(),
    labelMedium = MaterialTypography.labelMedium.withLexend(),
    labelSmall = MaterialTypography.labelSmall.withLexend(),
)

private fun TextStyle.withLexend() = copy(fontFamily = Lexend)
