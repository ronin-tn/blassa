package com.tp.blassa.features.rides.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary

@Composable
fun GenderPreferenceSelector(
        selectedPreference: String,
        onPreferenceChange: (String) -> Unit,
        modifier: Modifier = Modifier
) {
        val options = listOf("ANY" to "Mixte", "MALE_ONLY" to "Hommes", "FEMALE_ONLY" to "Femmes")

        Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                options.forEach { (value, label) ->
                        Box(
                                modifier =
                                        Modifier.weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                        if (selectedPreference == value)
                                                                BlassaTeal.copy(alpha = 0.1f)
                                                        else Color.Transparent
                                                )
                                                .border(
                                                        width = 1.dp,
                                                        color =
                                                                if (selectedPreference == value)
                                                                        BlassaTeal
                                                                else Color(0xFFE2E8F0),
                                                        shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { onPreferenceChange(value) }
                                                .padding(12.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = label,
                                        fontWeight =
                                                if (selectedPreference == value) FontWeight.Medium
                                                else FontWeight.Normal,
                                        color =
                                                if (selectedPreference == value) BlassaTeal
                                                else TextPrimary
                                )
                        }
                }
        }
}
