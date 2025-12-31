package com.tp.blassa.features.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tp.blassa.ui.theme.Error

@Composable
fun AuthErrorBanner(message: String, modifier: Modifier = Modifier) {
        Box(
                modifier =
                        modifier.fillMaxWidth()
                                .background(Error.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .padding(16.dp)
        ) { Text(text = message, color = Error, style = MaterialTheme.typography.bodySmall) }
}
