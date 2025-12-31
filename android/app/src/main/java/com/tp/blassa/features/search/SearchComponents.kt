package com.tp.blassa.features.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.SmokeFree
import androidx.compose.material.icons.outlined.SmokingRooms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tp.blassa.data.TUNISIA_CITIES
import com.tp.blassa.ui.theme.BlassaAmber
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@Composable
fun RouteInputs(
        origin: String,
        destination: String,
        onOriginChange: (String) -> Unit,
        onDestinationChange: (String) -> Unit
) {
        Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
        ) {
                Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        Icons.Default.MyLocation,
                                        contentDescription = "Départ",
                                        tint = BlassaAmber,
                                        modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                "Départ",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary
                                        )
                                        SearchCityInput(
                                                value = origin,
                                                onValueChange = onOriginChange,
                                                placeholder = "Tunis"
                                        )
                                }
                        }

                        Row {
                                Column(
                                        modifier = Modifier.width(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Canvas(modifier = Modifier.height(24.dp).width(1.dp)) {
                                                drawLine(
                                                        color = Color.LightGray,
                                                        start = Offset(0f, 0f),
                                                        end = Offset(0f, size.height),
                                                        pathEffect =
                                                                PathEffect.dashPathEffect(
                                                                        floatArrayOf(10f, 10f),
                                                                        0f
                                                                ),
                                                        strokeWidth = 2f
                                                )
                                        }
                                }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = "Destination",
                                        tint = BlassaTeal,
                                        modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                "Destination",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary
                                        )
                                        SearchCityInput(
                                                value = destination,
                                                onValueChange = onDestinationChange,
                                                placeholder = "Où allez-vous ?"
                                        )
                                }
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCityInput(value: String, onValueChange: (String) -> Unit, placeholder: String) {
        var expanded by remember { mutableStateOf(false) }
        val cities = TUNISIA_CITIES.map { it.name }

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .menuAnchor()
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                        if (value.isEmpty()) {
                                Text(
                                        text = placeholder,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary.copy(alpha = 0.7f)
                                )
                        }
                        BasicTextField(
                                value = value,
                                onValueChange = {
                                        onValueChange(it)
                                        expanded = true
                                },
                                textStyle =
                                        MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.SemiBold
                                        ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                        )
                }

                if (expanded) {
                        ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color.White)
                        ) {
                                val filteredCities =
                                        cities
                                                .filter { it.contains(value, ignoreCase = true) }
                                                .take(5)
                                filteredCities.forEach { city ->
                                        DropdownMenuItem(
                                                text = { Text(city) },
                                                onClick = {
                                                        onValueChange(city)
                                                        expanded = false
                                                }
                                        )
                                }
                        }
                }
        }
}

@Composable
fun BasicTextField(
        value: String,
        onValueChange: (String) -> Unit,
        textStyle: androidx.compose.ui.text.TextStyle,
        singleLine: Boolean,
        modifier: Modifier
) {
        androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = textStyle,
                singleLine = singleLine,
                modifier = modifier
        )
}

@Composable
fun DatePassengerInputs(
        date: String,
        onDateClick: () -> Unit,
        passengers: Int,
        onPassengersChange: (Int) -> Unit
) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                "Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(30.dp))
                                                .background(Color(0xFFF1F5F9))
                                                .clickable { onDateClick() }
                                                .padding(vertical = 12.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = date,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                )
                        }
                }

                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                "Passagers",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(30.dp))
                                                .background(Color(0xFFF1F5F9))
                                                .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                IconButton(
                                        onClick = {
                                                if (passengers > 1)
                                                        onPassengersChange(passengers - 1)
                                        },
                                        modifier =
                                                Modifier.size(36.dp)
                                                        .background(Color.White, CircleShape)
                                ) {
                                        Icon(
                                                Icons.Default.Remove,
                                                null,
                                                modifier = Modifier.size(16.dp),
                                                tint = TextSecondary
                                        )
                                }

                                Text(
                                        text = "$passengers",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                )

                                IconButton(
                                        onClick = {
                                                if (passengers < 8)
                                                        onPassengersChange(passengers + 1)
                                        },
                                        modifier =
                                                Modifier.size(36.dp)
                                                        .background(Color.White, CircleShape)
                                ) {
                                        Icon(
                                                Icons.Default.Add,
                                                null,
                                                modifier = Modifier.size(16.dp),
                                                tint = BlassaTeal
                                        )
                                }
                        }
                }
        }
}

@Composable
fun FilterOptions(
        gender: String,
        smoking: Boolean,
        onGenderChange: (String) -> Unit,
        onSmokingChange: (Boolean) -> Unit
) {
        Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
        ) {
                Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        text = "Options & Filtres",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                "Préférence genre",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(
                                                        Color(0xFFF1F5F9),
                                                        RoundedCornerShape(30.dp)
                                                )
                                                .padding(4.dp)
                        ) {
                                listOf("Tous", "Hommes", "Femmes").forEach { option ->
                                        val isSelected = gender == option
                                        Box(
                                                modifier =
                                                        Modifier.weight(1f)
                                                                .clip(RoundedCornerShape(30.dp))
                                                                .background(
                                                                        if (isSelected) Color.White
                                                                        else Color.Transparent
                                                                )
                                                                .clickable {
                                                                        onGenderChange(option)
                                                                }
                                                                .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Text(
                                                        text = option,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .labelMedium,
                                                        fontWeight =
                                                                if (isSelected) FontWeight.Bold
                                                                else FontWeight.Normal,
                                                        color =
                                                                if (isSelected) TextPrimary
                                                                else TextSecondary
                                                )
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Icon(
                                        if (smoking) Icons.Outlined.SmokingRooms
                                        else Icons.Outlined.SmokeFree,
                                        null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        "Fumeur autorisé",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Switch(
                                        checked = smoking,
                                        onCheckedChange = onSmokingChange,
                                        colors =
                                                SwitchDefaults.colors(
                                                        checkedThumbColor = Color.White,
                                                        checkedTrackColor = BlassaTeal,
                                                        uncheckedThumbColor = Color.White,
                                                        uncheckedTrackColor = Color(0xFFE2E8F0),
                                                        uncheckedBorderColor = Color.Transparent
                                                )
                                )
                        }
                }
        }
}

@Composable
fun PopularRouteChip(from: String, to: String, onClick: () -> Unit) {
        Surface(
                onClick = onClick,
                shape = RoundedCornerShape(30.dp),
                color = Color.White,
                modifier = Modifier.padding(end = 8.dp)
        ) {
                Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = "$from → $to",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                        )
                }
        }
}
