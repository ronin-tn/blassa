package com.tp.blassa.features.rides

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tp.blassa.features.rides.components.CityPickerBottomSheet
import com.tp.blassa.features.rides.components.CitySelector
import com.tp.blassa.features.rides.components.GenderPreferenceSelector
import com.tp.blassa.features.rides.components.SeatsCounter
import com.tp.blassa.features.rides.components.SectionCard
import com.tp.blassa.features.rides.components.SuccessScreen
import com.tp.blassa.features.rides.viewmodel.PublishRideViewModel
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishRideScreen(
        onBack: ()->Unit,
        onSuccess: (String)->Unit,
        onNavigateToAddVehicle: ()->Unit ={},
        viewModel: PublishRideViewModel = viewModel()
){
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current
        val snackbarHostState = remember{ SnackbarHostState() }

        var showOriginPicker by remember{ mutableStateOf(false) }
        var showDestinationPicker by remember{ mutableStateOf(false) }
        val originSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val destinationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        var vehicleExpanded by remember{ mutableStateOf(false) }

        val calendar = Calendar.getInstance()
        val datePickerDialog =
                DatePickerDialog(
                                context,
                               { _, year, month, dayOfMonth ->
                                        viewModel.setDepartureDate(
                                                String.format(
                                                        "%04d-%02d-%02d",
                                                        year,
                                                        month + 1,
                                                        dayOfMonth
                                                )
                                        )
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        )
                        .apply{ datePicker.minDate = System.currentTimeMillis() }

        val timePickerDialog =
                TimePickerDialog(
                        context,
                       { _, hourOfDay, minute ->
                                viewModel.setDepartureTime(
                                        String.format("%02d:%02d", hourOfDay, minute)
                                )
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                )

        LaunchedEffect(uiState.snackbarMessage){
                uiState.snackbarMessage?.let{ message ->
                        snackbarHostState.showSnackbar(
                                message = message,
                                duration = SnackbarDuration.Long
                        )
                        viewModel.clearSnackbarMessage()
                }
        }

        if (uiState.showSuccess){
                SuccessScreen(
                        onViewRide ={ uiState.createdRideId?.let{ onSuccess(it) } },
                        onBack = onBack
                )
                return
        }

        Scaffold(
                topBar ={
                        TopAppBar(
                                title ={ Text("Publier un trajet", fontWeight = FontWeight.Bold) },
                                navigationIcon ={
                                        IconButton(onClick = onBack){
                                                Icon(
                                                        Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Retour",
                                                        tint = Color.Black
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color.White,
                                                titleContentColor = TextPrimary
                                        )
                        )
                },
                snackbarHost ={ SnackbarHost(snackbarHostState) },
                containerColor = Color(0xFFF8FAFC)
        ){ padding ->
                LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                        item(key = "route"){
                                SectionCard(title = "Itinéraire", icon = Icons.Default.LocationOn){
                                        Text(
                                                "Ville de départ",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        CitySelector(
                                                selectedCity = uiState.originCity,
                                                onClick ={ showOriginPicker = true }
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                                "Ville d'arrivée",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        CitySelector(
                                                selectedCity = uiState.destinationCity,
                                                onClick ={ showDestinationPicker = true }
                                        )
                                }
                        }

                        item(key = "datetime"){
                                SectionCard(
                                        title = "Date et heure",
                                        icon = Icons.Default.CalendarMonth
                                ){
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ){
                                                Column(modifier = Modifier.weight(1f)){
                                                        Text(
                                                                "Date",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color = TextSecondary
                                                        )
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        DateTimeSelector(
                                                                value =
                                                                        uiState.departureDate
                                                                                .ifEmpty{ null }
                                                                                ?.let{
                                                                                        formatDisplayDate(
                                                                                                it
                                                                                        )
                                                                                }
                                                                                ?: "Sélectionner",
                                                                icon = Icons.Default.CalendarMonth,
                                                                hasValue =
                                                                        uiState.departureDate
                                                                                .isNotEmpty(),
                                                                onClick ={
                                                                        datePickerDialog.show()
                                                                }
                                                        )
                                                }

                                                Column(modifier = Modifier.weight(1f)){
                                                        Text(
                                                                "Heure",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color = TextSecondary
                                                        )
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        DateTimeSelector(
                                                                value =
                                                                        uiState.departureTime
                                                                                .ifEmpty{
                                                                                        "Sélectionner"
                                                                                },
                                                                icon = Icons.Default.AccessTime,
                                                                hasValue =
                                                                        uiState.departureTime
                                                                                .isNotEmpty(),
                                                                onClick ={
                                                                        timePickerDialog.show()
                                                                }
                                                        )
                                                }
                                        }
                                }
                        }

                        item(key = "vehicle"){
                                SectionCard(
                                        title = "Véhicule",
                                        icon = Icons.Default.DirectionsCar
                                ){
                                        when{
                                                uiState.isLoadingVehicles->{
                                                        Row(
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .padding(16.dp),
                                                                horizontalArrangement =
                                                                        Arrangement.Center
                                                        ){
                                                                CircularProgressIndicator(
                                                                        color = BlassaTeal,
                                                                        modifier =
                                                                                Modifier.size(24.dp)
                                                                )
                                                        }
                                                }
                                                uiState.vehicles.isEmpty()->{
                                                        VehicleEmptyState(
                                                                onAddVehicle =
                                                                        onNavigateToAddVehicle
                                                        )
                                                }
                                                else->{
                                                        VehicleDropdown(
                                                                selectedVehicle =
                                                                        uiState.selectedVehicle,
                                                                vehicles = uiState.vehicles,
                                                                expanded = vehicleExpanded,
                                                                onExpandedChange ={
                                                                        vehicleExpanded = it
                                                                },
                                                                onVehicleSelected ={
                                                                        viewModel
                                                                                .setSelectedVehicle(
                                                                                        it
                                                                                )
                                                                }
                                                        )
                                                }
                                        }
                                }
                        }

                        item(key = "seats_price"){
                                SectionCard(title = "Places et prix", icon = Icons.Default.Person){
                                        Text(
                                                "Nombre de places",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SeatsCounter(
                                                seats = uiState.totalSeats,
                                                onSeatsChange ={ viewModel.setTotalSeats(it) }
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                                "Prix par place (TND)",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                                value = uiState.pricePerSeat,
                                                onValueChange ={ viewModel.setPricePerSeat(it) },
                                                placeholder ={ Text("15.00") },
                                                suffix ={ Text("TND", color = TextSecondary) },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Decimal
                                                        ),
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = BlassaTeal,
                                                                unfocusedBorderColor =
                                                                        Color(0xFFE2E8F0)
                                                        ),
                                                modifier = Modifier.fillMaxWidth()
                                        )
                                }
                        }

                        item(key = "preferences"){
                                SectionCard(title = "Préférences", icon = Icons.Default.Person){
                                        PreferenceToggle(
                                                title = "Fumeur autorisé",
                                                subtitle = "Autoriser les fumeurs",
                                                icon = Icons.Default.SmokingRooms,
                                                checked = uiState.allowsSmoking,
                                                onToggle ={ viewModel.setAllowsSmoking(it) }
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        PreferenceToggle(
                                                title = "Musique autorisée",
                                                subtitle = "Autoriser la musique",
                                                icon = Icons.Default.MusicNote,
                                                checked = uiState.allowsMusic,
                                                onToggle ={ viewModel.setAllowsMusic(it) }
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        PreferenceToggle(
                                                title = "Animaux autorisés",
                                                subtitle = "Autoriser les animaux de compagnie",
                                                icon = Icons.Default.Pets,
                                                checked = uiState.allowsPets,
                                                onToggle ={ viewModel.setAllowsPets(it) }
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                                "Taille des bagages",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LuggageSelector(
                                                selectedSize = uiState.luggageSize,
                                                onSizeSelected ={ viewModel.setLuggageSize(it) }
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                                "Préférence de genre",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        GenderPreferenceSelector(
                                                selectedPreference = uiState.genderPreference,
                                                onPreferenceChange ={
                                                        viewModel.setGenderPreference(it)
                                                }
                                        )
                                }
                        }

                        item(key = "submit"){
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                        onClick ={ viewModel.validateAndSubmit() },
                                        enabled = !uiState.isSubmitting,
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = BlassaTeal
                                                )
                                ){
                                        if (uiState.isSubmitting){
                                                CircularProgressIndicator(
                                                        color = Color.White,
                                                        modifier = Modifier.size(24.dp)
                                                )
                                        } else{
                                                Text(
                                                        "Publier le trajet",
                                                        fontWeight = FontWeight.SemiBold
                                                )
                                        }
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                        }
                }
        }

        if (showOriginPicker){
                CityPickerBottomSheet(
                        title = "Ville de départ",
                        selectedCity = uiState.originCity,
                        sheetState = originSheetState,
                        onCitySelected ={
                                viewModel.setOriginCity(it)
                                showOriginPicker = false
                        },
                        onDismiss ={ showOriginPicker = false }
                )
        }
        if (showDestinationPicker){
                CityPickerBottomSheet(
                        title = "Ville d'arrivée",
                        selectedCity = uiState.destinationCity,
                        sheetState = destinationSheetState,
                        onCitySelected ={
                                viewModel.setDestinationCity(it)
                                showDestinationPicker = false
                        },
                        onDismiss ={ showDestinationPicker = false }
                )
        }
}

@Composable
private fun DateTimeSelector(
        value: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        hasValue: Boolean,
        onClick: ()->Unit
){
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .clickable{ onClick() }
                                .padding(16.dp)
        ){
                Row(verticalAlignment = Alignment.CenterVertically){
                        Icon(
                                icon,
                                contentDescription = null,
                                tint = BlassaTeal,
                                modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = value, color = if (hasValue) TextPrimary else TextSecondary)
                }
        }
}

@Composable
private fun VehicleEmptyState(onAddVehicle: ()->Unit){
        Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
        ){
                Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                        "Aucun véhicule enregistré",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                )
                Text(
                        "Ajoutez un véhicule pour publier un trajet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                        onClick = onAddVehicle,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BlassaTeal)
                ){
                        Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter un véhicule", fontWeight = FontWeight.Medium)
                }
        }
}

@Composable
private fun VehicleDropdown(
        selectedVehicle: com.tp.blassa.core.network.Vehicle?,
        vehicles: List<com.tp.blassa.core.network.Vehicle>,
        expanded: Boolean,
        onExpandedChange: (Boolean)->Unit,
        onVehicleSelected: (com.tp.blassa.core.network.Vehicle)->Unit
){
        Box{
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                        .clickable{ onExpandedChange(true) }
                                        .padding(16.dp)
                ){
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ){
                                Text(
                                        text =
                                                selectedVehicle?.let{
                                                        "${it.make} ${it.model} - ${it.licensePlate}"
                                                }
                                                        ?: "Sélectionner un véhicule",
                                        color =
                                                if (selectedVehicle != null) TextPrimary
                                                else TextSecondary
                                )
                                Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = TextSecondary
                                )
                        }
                }
                DropdownMenu(expanded = expanded, onDismissRequest ={ onExpandedChange(false) }){
                        vehicles.forEach{ vehicle ->
                                DropdownMenuItem(
                                        text ={
                                                Column{
                                                        Text(
                                                                "${vehicle.make} ${vehicle.model}",
                                                                fontWeight = FontWeight.Medium
                                                        )
                                                        Text(
                                                                vehicle.licensePlate,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color = TextSecondary
                                                        )
                                                }
                                        },
                                        onClick ={
                                                onVehicleSelected(vehicle)
                                                onExpandedChange(false)
                                        }
                                )
                        }
                }
        }
}

@Composable
private fun PreferenceToggle(
        title: String,
        subtitle: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        checked: Boolean,
        onToggle: (Boolean)->Unit
){
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ){
                Row(verticalAlignment = Alignment.CenterVertically){
                        Icon(
                                icon,
                                contentDescription = null,
                                tint = if (checked) BlassaTeal else TextSecondary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column{
                                Text(title, fontWeight = FontWeight.Medium, color = TextPrimary)
                                Text(
                                        subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                )
                        }
                }
                Switch(
                        checked = checked,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(checkedTrackColor = BlassaTeal)
                )
        }
}

@Composable
private fun LuggageSelector(selectedSize: String, onSizeSelected: (String)->Unit){
        val options = listOf("SMALL" to "Petit", "MEDIUM" to "Moyen", "LARGE" to "Grand")

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
                options.forEach{ (key, label) ->
                        val isSelected = selectedSize==key
                        OutlinedButton(
                                onClick ={ onSizeSelected(key) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                containerColor =
                                                        if (isSelected)
                                                                BlassaTeal.copy(alpha = 0.1f)
                                                        else Color.Transparent,
                                                contentColor =
                                                        if (isSelected) BlassaTeal
                                                        else TextSecondary
                                        ),
                                border =
                                        androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isSelected) BlassaTeal else Color(0xFFE2E8F0)
                                        )
                        ){
                                Text(
                                        label,
                                        fontWeight =
                                                if (isSelected) FontWeight.SemiBold
                                                else FontWeight.Normal
                                )
                        }
                }
        }
}

private fun formatDisplayDate(isoDate: String): String{
        return try{
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("EEE d MMM", Locale.FRENCH)
                inputFormat.parse(isoDate)?.let{ outputFormat.format(it) } ?: isoDate
        } catch (e: Exception){
                isoDate
        }
}
