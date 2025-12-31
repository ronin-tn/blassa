package com.tp.blassa.features.auth.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.tp.blassa.ui.theme.BlassaAmber
import com.tp.blassa.ui.theme.BlassaTeal

@Composable
fun AuthBackground(modifier: Modifier = Modifier) {
        Canvas(modifier = modifier.fillMaxSize()) {
                drawCircle(
                        brush =
                                Brush.radialGradient(
                                        colors =
                                                listOf(
                                                        BlassaTeal.copy(alpha = 0.08f),
                                                        Color.Transparent
                                                ),
                                        center = Offset(-size.width * 0.2f, -size.height * 0.1f),
                                        radius = size.width * 0.8f
                                ),
                        radius = size.width * 0.8f,
                        center = Offset(-size.width * 0.2f, -size.height * 0.1f)
                )

                drawCircle(
                        brush =
                                Brush.radialGradient(
                                        colors =
                                                listOf(
                                                        BlassaAmber.copy(alpha = 0.08f),
                                                        Color.Transparent
                                                ),
                                        center = Offset(size.width * 1.2f, size.height * 1.1f),
                                        radius = size.width * 0.8f
                                ),
                        radius = size.width * 0.8f,
                        center = Offset(size.width * 1.2f, size.height * 1.1f)
                )
        }
}
