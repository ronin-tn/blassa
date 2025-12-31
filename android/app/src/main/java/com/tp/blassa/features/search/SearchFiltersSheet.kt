package com.tp.blassa.features.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tp.blassa.features.search.model.SearchFiltersState
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFiltersSheet(
        filters: SearchFiltersState,
        onFiltersChange: (SearchFiltersState) -> Unit,
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier
) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        var localMaxPrice by
                remember(filters.maxPrice) { mutableStateOf(filters.maxPrice?.toString() ?: "") }

        ModalBottomSheet(
                onDismissRequest = onDismiss,
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                modifier = modifier
        ) {
                Column(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                        .padding(bottom = 32.dp)
                                        .verticalScroll(rememberScrollState())
                ) {
                        FilterSheetHeader(onDismiss = onDismiss)

                        Spacer(modifier = Modifier.height(24.dp))

                        SortBySection(
                                selectedSort = filters.sortBy,
                                onSortChange = { onFiltersChange(filters.copy(sortBy = it)) }
                        )

                        FilterDivider()

                        TimeOfDaySection(
                                selectedTimes = filters.timeOfDay,
                                onTimeToggle = { time ->
                                        val newSet =
                                                if (filters.timeOfDay.contains(time)) {
                                                        filters.timeOfDay - time
                                                } else {
                                                        filters.timeOfDay + time
                                                }
                                        onFiltersChange(filters.copy(timeOfDay = newSet))
                                }
                        )

                        FilterDivider()

                        MaxPriceSection(
                                value = localMaxPrice,
                                onValueChange = { value ->
                                        localMaxPrice = value
                                        val numValue = value.toIntOrNull()
                                        onFiltersChange(filters.copy(maxPrice = numValue))
                                }
                        )

                        FilterDivider()

                        PreferencesSection(
                                ladiesOnly = filters.ladiesOnly,
                                noSmoking = filters.noSmoking,
                                allowsMusic = filters.allowsMusic,
                                allowsPets = filters.allowsPets,
                                luggageSize = filters.luggageSize,
                                onLadiesOnlyChange = {
                                        onFiltersChange(filters.copy(ladiesOnly = it))
                                },
                                onNoSmokingChange = {
                                        onFiltersChange(filters.copy(noSmoking = it))
                                },
                                onAllowsMusicChange = {
                                        onFiltersChange(filters.copy(allowsMusic = it))
                                },
                                onAllowsPetsChange = {
                                        onFiltersChange(filters.copy(allowsPets = it))
                                },
                                onLuggageSizeChange = {
                                        onFiltersChange(filters.copy(luggageSize = it))
                                }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        FilterActions(
                                hasActiveFilters = filters.hasActiveFilters,
                                onReset = {
                                        localMaxPrice = ""
                                        onFiltersChange(SearchFiltersState.DEFAULT)
                                },
                                onApply = onDismiss
                        )
                }
        }
}

@Composable
private fun FilterSheetHeader(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
        Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                Icons.Default.FilterList,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                "Filtres",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                        )
                }
                IconButton(onClick = onDismiss) {
                        Icon(
                                Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = TextSecondary
                        )
                }
        }
}

@Composable
private fun SortBySection(
        selectedSort: String,
        onSortChange: (String) -> Unit,
        modifier: Modifier = Modifier
) {
        val sortOptions =
                listOf(
                        "" to "Par défaut",
                        "price_asc" to "Prix croissant",
                        "price_desc" to "Prix décroissant",
                        "time_asc" to "Départ le plus tôt",
                        "time_desc" to "Départ le plus tard"
                )

        Column(modifier = modifier) {
                SectionTitle(text = "Trier par")
                Spacer(modifier = Modifier.height(8.dp))

                sortOptions.forEach { (value, label) ->
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clickable { onSortChange(value) }
                                                .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                RadioButton(
                                        selected = selectedSort == value,
                                        onClick = { onSortChange(value) },
                                        colors =
                                                RadioButtonDefaults.colors(
                                                        selectedColor = BlassaTeal
                                                )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary
                                )
                        }
                }
        }
}

@Composable
private fun TimeOfDaySection(
        selectedTimes: Set<String>,
        onTimeToggle: (String) -> Unit,
        modifier: Modifier = Modifier
) {
        val timeOptions =
                listOf(
                        "morning" to "Matin (6h - 12h)",
                        "afternoon" to "Après-midi (12h - 18h)",
                        "evening" to "Soir (18h - 00h)"
                )

        Column(modifier = modifier) {
                SectionTitle(text = "Heure de départ")
                Spacer(modifier = Modifier.height(12.dp))

                timeOptions.forEach { (value, label) ->
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clickable { onTimeToggle(value) }
                                                .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Checkbox(
                                        checked = selectedTimes.contains(value),
                                        onCheckedChange = { onTimeToggle(value) },
                                        colors = CheckboxDefaults.colors(checkedColor = BlassaTeal)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary
                                )
                        }
                }
        }
}

@Composable
private fun MaxPriceSection(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier
) {
        Column(modifier = modifier) {
                SectionTitle(text = "Prix maximum")
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: 50 TND") },
                        suffix = { Text("TND") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BlassaTeal,
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                        singleLine = true
                )
        }
}

@Composable
private fun PreferencesSection(
        ladiesOnly: Boolean,
        noSmoking: Boolean,
        allowsMusic: Boolean,
        allowsPets: Boolean,
        luggageSize: String,
        onLadiesOnlyChange: (Boolean) -> Unit,
        onNoSmokingChange: (Boolean) -> Unit,
        onAllowsMusicChange: (Boolean) -> Unit,
        onAllowsPetsChange: (Boolean) -> Unit,
        onLuggageSizeChange: (String) -> Unit,
        modifier: Modifier = Modifier
) {
        Column(modifier = modifier) {
                SectionTitle(text = "Préférences")
                Spacer(modifier = Modifier.height(8.dp))

                PreferenceCheckbox(
                        label = "Ladies Only",
                        checked = ladiesOnly,
                        onCheckedChange = onLadiesOnlyChange,
                        checkedColor = Color(0xFFEC4899)
                )

                PreferenceCheckbox(
                        label = "Non-fumeur",
                        checked = noSmoking,
                        onCheckedChange = onNoSmokingChange,
                        checkedColor = BlassaTeal
                )

                PreferenceCheckbox(
                        label = "Musique autorisée",
                        checked = allowsMusic,
                        onCheckedChange = onAllowsMusicChange,
                        checkedColor = Color(0xFF10B981)
                )

                PreferenceCheckbox(
                        label = "Animaux autorisés",
                        checked = allowsPets,
                        onCheckedChange = onAllowsPetsChange,
                        checkedColor = Color(0xFF9333EA)
                )
        }
}

@Composable
private fun PreferenceCheckbox(
        label: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        checkedColor: Color,
        modifier: Modifier = Modifier
) {
        Row(
                modifier =
                        modifier.fillMaxWidth()
                                .clickable { onCheckedChange(!checked) }
                                .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Checkbox(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors = CheckboxDefaults.colors(checkedColor = checkedColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
}

@Composable
private fun FilterActions(
        hasActiveFilters: Boolean,
        onReset: () -> Unit,
        onApply: () -> Unit,
        modifier: Modifier = Modifier
) {
        Column(modifier = modifier) {
                if (hasActiveFilters) {
                        OutlinedButton(
                                onClick = onReset,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                contentColor = TextSecondary
                                        )
                        ) {
                                Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Réinitialiser les filtres")
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                        onClick = onApply,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlassaTeal)
                ) {
                        Text(
                                "Appliquer",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                        )
                }
        }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
        Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = modifier
        )
}

@Composable
private fun FilterDivider(modifier: Modifier = Modifier) {
        HorizontalDivider(modifier = modifier.padding(vertical = 16.dp), color = Color(0xFFE2E8F0))
}
