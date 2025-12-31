package com.tp.blassa.features.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextSecondary

@Composable
fun FilterChipRow(
        selectedFilter: String,
        onFilterChange: (String) -> Unit,
        modifier: Modifier = Modifier
) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RideFilterChip(
                        selected = selectedFilter == "ALL",
                        label = "Tous",
                        onClick = { onFilterChange("ALL") }
                )
                RideFilterChip(
                        selected = selectedFilter == "DRIVER",
                        label = "Conducteur",
                        onClick = { onFilterChange("DRIVER") }
                )
                RideFilterChip(
                        selected = selectedFilter == "PASSENGER",
                        label = "Passager",
                        onClick = { onFilterChange("PASSENGER") }
                )
        }
}

@Composable
private fun RideFilterChip(selected: Boolean, label: String, onClick: () -> Unit) {
        FilterChip(
                selected = selected,
                onClick = onClick,
                label = { Text(label) },
                colors =
                        FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BlassaTeal.copy(alpha = 0.1f),
                                selectedLabelColor = BlassaTeal,
                                containerColor = Color.Transparent,
                                labelColor = TextSecondary
                        ),
                border =
                        FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = Color(0xFFE2E8F0),
                                selectedBorderColor = BlassaTeal
                        )
        )
}
