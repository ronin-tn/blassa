package com.tp.blassa.features.rides.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tp.blassa.data.City
import com.tp.blassa.data.TUNISIA_CITIES
import com.tp.blassa.ui.theme.BlassaTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPickerBottomSheet(
        title: String,
        selectedCity: City?,
        sheetState: SheetState,
        onCitySelected: (City) -> Unit,
        onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCities = TUNISIA_CITIES.filter { it.name.contains(searchQuery, ignoreCase = true) }

    ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = null
    ) {
        Column(
                modifier =
                        Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp)
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
                Text(text = title, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            }

            OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Rechercher une ville") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlassaTeal,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                    singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                items(filteredCities, key = { it.name }) { city ->
                    CityItem(
                            city = city,
                            isSelected = city == selectedCity,
                            onClick = { onCitySelected(city) }
                    )
                    if (city != filteredCities.last()) {
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CityItem(city: City, isSelected: Boolean, onClick: () -> Unit) {
    Row(
            modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
                text = city.name,
                color = if (isSelected) BlassaTeal else Color.Unspecified,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = BlassaTeal)
        }
    }
}
