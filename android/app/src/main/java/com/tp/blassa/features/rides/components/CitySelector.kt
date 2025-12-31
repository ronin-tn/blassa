package com.tp.blassa.features.rides.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tp.blassa.data.City
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@Composable
fun CitySelector(selectedCity: City?, onClick: ()->Unit, modifier: Modifier = Modifier){
        Box(
                modifier =
                        modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .clickable{ onClick() }
                                .padding(16.dp)
        ){
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                ){
                        Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = BlassaTeal,
                                modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                text = selectedCity?.name ?: "Sélectionner une ville",
                                color = if (selectedCity != null) TextPrimary else TextSecondary,
                                modifier = Modifier.weight(1f)
                        )
                        Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary
                        )
                }
        }
}
