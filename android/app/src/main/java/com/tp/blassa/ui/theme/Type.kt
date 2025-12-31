package com.tp.blassa.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Poppins = FontFamily.SansSerif
val Inter = FontFamily.SansSerif

val Typography =
        Typography(
                displayLarge =
                        TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 57.sp,
                                lineHeight = 64.sp,
                                letterSpacing = (-0.25).sp,
                                color = TextPrimary
                        ),
                displayMedium =
                        TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 45.sp,
                                lineHeight = 52.sp,
                                letterSpacing = 0.sp,
                                color = TextPrimary
                        ),
                displaySmall =
                        TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp,
                                lineHeight = 44.sp,
                                letterSpacing = 0.sp,
                                color = TextPrimary
                        ),
                headlineLarge =
                        TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 32.sp,
                                lineHeight = 40.sp,
                                letterSpacing = 0.sp,
                                color = TextPrimary
                        ),
                headlineMedium =
                        TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 28.sp,
                                lineHeight = 36.sp,
                                letterSpacing = 0.sp,
                                color = TextPrimary
                        ),
                headlineSmall =
                        TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp,
                                lineHeight = 32.sp,
                                letterSpacing = 0.sp,
                                color = TextPrimary
                        ),
                titleLarge =
                        TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 22.sp,
                                lineHeight = 28.sp,
                                letterSpacing = 0.sp,
                                color = TextPrimary
                        ),
                titleMedium =
                        TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                letterSpacing = 0.15.sp,
                                color = TextPrimary
                        ),
                titleSmall =
                        TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                letterSpacing = 0.1.sp,
                                color = TextPrimary
                        ),
                bodyLarge =
                        TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                letterSpacing = 0.5.sp,
                                color = TextPrimary
                        ),
                bodyMedium =
                        TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                letterSpacing = 0.25.sp,
                                color = TextPrimary
                        ),
                bodySmall =
                        TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                letterSpacing = 0.4.sp,
                                color = TextSecondary
                        ),
                labelLarge =
                        TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                letterSpacing = 0.1.sp,
                                color = TextPrimary
                        ),
                labelMedium =
                        TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                letterSpacing = 0.5.sp,
                                color = TextPrimary
                        ),
                labelSmall =
                        TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                letterSpacing = 0.5.sp,
                                color = TextSecondary
                        )
        )
