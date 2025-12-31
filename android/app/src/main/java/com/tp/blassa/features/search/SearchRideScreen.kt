package com.tp.blassa.features.search

import android.app.DatePickerDialog
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tp.blassa.data.TUNISIA_CITIES
import com.tp.blassa.ui.theme.Background
import com.tp.blassa.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRideScreen(
        onBack: () -> Unit,
        onSearch:
                (
                        from: String,
                        to: String,
                        oLat: Double,
                        oLon: Double,
                        dLat: Double,
                        dLon: Double,
                        date: String?,
                        passengers: Int,
                        gender: String?) -> Unit
) {
        var origin by remember { mutableStateOf("") }
        var destination by remember { mutableStateOf("") }

        var selectedDate by remember { mutableStateOf<Calendar?>(null) }

        var passengers by remember { mutableIntStateOf(1) }

        val formattedDate =
                remember(selectedDate) {
                        selectedDate?.let {
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.time)
                        }
                }

        val displayDate =
                remember(selectedDate) {
                        selectedDate?.let {
                                val now = Calendar.getInstance()
                                if (now.get(Calendar.YEAR) == it.get(Calendar.YEAR) &&
                                                now.get(Calendar.DAY_OF_YEAR) ==
                                                        it.get(Calendar.DAY_OF_YEAR)
                                ) {
                                        "Aujourd'hui"
                                } else {
                                        SimpleDateFormat("EEE d MMM", Locale.FRANCE).format(it.time)
                                }
                        }
                                ?: "Date (Optionnel)"
                }

        val context = LocalContext.current

        fun showDatePicker() {
                val current = selectedDate ?: Calendar.getInstance()
                DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                        val newDate = Calendar.getInstance()
                                        newDate.set(year, month, day)
                                        selectedDate = newDate
                                },
                                current.get(Calendar.YEAR),
                                current.get(Calendar.MONTH),
                                current.get(Calendar.DAY_OF_MONTH)
                        )
                        .apply { datePicker.minDate = System.currentTimeMillis() - 1000 }
                        .show()
        }

        var gender by remember { mutableStateOf("Tous") }
        var smoking by remember { mutableStateOf(false) }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Text("Rechercher un trajet", fontWeight = FontWeight.Bold)
                                },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
                                                Icon(
                                                        Icons.Default.ArrowBack,
                                                        contentDescription = "Retour",
                                                        tint = Color.Black
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Background,
                                                titleContentColor = TextPrimary
                                        )
                        )
                },
        ) { padding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(padding)
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                        RouteInputs(
                                origin = origin,
                                destination = destination,
                                onOriginChange = { origin = it },
                                onDestinationChange = { destination = it }
                        )

                        DatePassengerInputs(
                                date = displayDate,
                                onDateClick = { showDatePicker() },
                                passengers = passengers,
                                onPassengersChange = { passengers = it }
                        )

                        FilterOptions(
                                gender = gender,
                                smoking = smoking,
                                onGenderChange = { gender = it },
                                onSmokingChange = { smoking = it }
                        )

                        Button(
                                onClick = {
                                        val originCity =
                                                TUNISIA_CITIES.find {
                                                        it.name.equals(origin, ignoreCase = true)
                                                }
                                        val destCity =
                                                TUNISIA_CITIES.find {
                                                        it.name.equals(
                                                                destination,
                                                                ignoreCase = true
                                                        )
                                                }

                                        if (originCity != null && destCity != null) {
                                                val genderParam =
                                                        if (gender == "Tous") null
                                                        else gender.uppercase()
                                                onSearch(
                                                        origin,
                                                        destination,
                                                        originCity.lat,
                                                        originCity.lon,
                                                        destCity.lat,
                                                        destCity.lon,
                                                        formattedDate,
                                                        passengers,
                                                        genderParam
                                                )
                                        }
                                },
                                enabled = origin.isNotBlank() && destination.isNotBlank(),
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = com.tp.blassa.ui.theme.BlassaAmber,
                                                disabledContainerColor = Color(0xFFFED7AA)
                                        ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                                Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        "Rechercher",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                )
                        }

                        Column {
                                Text(
                                        "Trajets populaires",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                                        PopularRouteChip("Tunis", "Sousse") {
                                                origin = "Tunis"
                                                destination = "Sousse"
                                        }
                                        PopularRouteChip("Sfax", "Tunis") {
                                                origin = "Sfax"
                                                destination = "Tunis"
                                        }
                                        PopularRouteChip("Bizerte", "Tunis") {
                                                origin = "Bizerte"
                                                destination = "Tunis"
                                        }
                                        PopularRouteChip("Nabeul", "Tunis") {
                                                origin = "Nabeul"
                                                destination = "Tunis"
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                }
        }
}
