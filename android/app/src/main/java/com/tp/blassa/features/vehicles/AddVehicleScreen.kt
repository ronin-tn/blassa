package com.tp.blassa.features.vehicles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.VehicleRequest
import com.tp.blassa.core.network.parseErrorMessage
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.Border
import com.tp.blassa.ui.theme.Error
import com.tp.blassa.ui.theme.InputBackground
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(onBack: () -> Unit, onSuccess: () -> Unit) {
        var make by remember { mutableStateOf("") }
        var model by remember { mutableStateOf("") }
        var color by remember { mutableStateOf("") }
        var licensePlate by remember { mutableStateOf("") }
        var productionYear by remember { mutableStateOf("") }

        var makeError by remember { mutableStateOf<String?>(null) }
        var modelError by remember { mutableStateOf<String?>(null) }
        var colorError by remember { mutableStateOf<String?>(null) }
        var licensePlateError by remember { mutableStateOf<String?>(null) }
        var productionYearError by remember { mutableStateOf<String?>(null) }

        var isLoading by remember { mutableStateOf(false) }
        var showSuccess by remember { mutableStateOf(false) }
        var apiError by remember { mutableStateOf<String?>(null) }

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val focusManager = LocalFocusManager.current

        fun validate(): Boolean {
                var isValid = true

                if (make.isBlank()) {
                        makeError = "Marque requise"
                        isValid = false
                } else makeError = null

                if (model.isBlank()) {
                        modelError = "Modèle requis"
                        isValid = false
                } else modelError = null

                if (color.isBlank()) {
                        colorError = "Couleur requise"
                        isValid = false
                } else colorError = null

                if (licensePlate.isBlank()) {
                        licensePlateError = "Immatriculation requise"
                        isValid = false
                } else if (licensePlate.length < 4) {
                        licensePlateError = "Immatriculation invalide"
                        isValid = false
                } else licensePlateError = null

                if (productionYear.isNotBlank()) {
                        val year = productionYear.toIntOrNull()
                        if (year == null || year < 1950 || year > 2025) {
                                productionYearError = "Année invalide (1950-2025)"
                                isValid = false
                        } else productionYearError = null
                } else productionYearError = null

                return isValid
        }

        fun handleSubmit() {
                if (!validate()) return

                isLoading = true
                apiError = null

                scope.launch {
                        try {
                                val request =
                                        VehicleRequest(
                                                make = make.trim(),
                                                model = model.trim(),
                                                color = color.trim(),
                                                licensePlate = licensePlate.trim().uppercase(),
                                                productionYear = productionYear.toIntOrNull()
                                        )

                                RetrofitClient.dashboardApiService.createVehicle(request)
                                showSuccess = true
                        } catch (e: retrofit2.HttpException) {
                                val errorMessage = e.parseErrorMessage()
                                apiError = errorMessage
                                snackbarHostState.showSnackbar(
                                        message = errorMessage,
                                        duration = SnackbarDuration.Long
                                )
                        } catch (e: java.net.UnknownHostException) {
                                apiError = "Pas de connexion internet"
                                snackbarHostState.showSnackbar(
                                        message = "Pas de connexion internet",
                                        duration = SnackbarDuration.Long
                                )
                        } catch (e: java.net.SocketTimeoutException) {
                                apiError = "Le serveur ne répond pas"
                                snackbarHostState.showSnackbar(
                                        message = "Le serveur ne répond pas",
                                        duration = SnackbarDuration.Long
                                )
                        } catch (e: Exception) {
                                apiError = "Erreur inattendue"
                                snackbarHostState.showSnackbar(
                                        message = "Erreur inattendue",
                                        duration = SnackbarDuration.Long
                                )
                        } finally {
                                isLoading = false
                        }
                }
        }

        // Success screen
        if (showSuccess) {
                SuccessScreen(onBack = onSuccess)
                return
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Text("Ajouter un véhicule", fontWeight = FontWeight.Bold)
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
                                                containerColor = Color.White,
                                                titleContentColor = TextPrimary
                                        )
                        )
                },
                snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                                Snackbar(
                                        snackbarData = data,
                                        containerColor = Error,
                                        contentColor = Color.White,
                                        shape = RoundedCornerShape(12.dp)
                                )
                        }
                },
                containerColor = Color(0xFFF8FAFC)
        ) { padding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(padding)
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        // Header Card
                        Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                        modifier =
                                                                Modifier.size(48.dp)
                                                                        .background(
                                                                                BlassaTeal.copy(
                                                                                        alpha = 0.1f
                                                                                ),
                                                                                CircleShape
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Icon(
                                                                Icons.Default.DirectionsCar,
                                                                contentDescription = null,
                                                                tint = BlassaTeal,
                                                                modifier = Modifier.size(24.dp)
                                                        )
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Column {
                                                        Text(
                                                                text = "Nouveau véhicule",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium,
                                                                fontWeight = FontWeight.Bold,
                                                                color = TextPrimary
                                                        )
                                                        Text(
                                                                text =
                                                                        "Ajoutez les informations de votre véhicule",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color = TextSecondary
                                                        )
                                                }
                                        }
                                }
                        }

                        // Form Card
                        Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                                Column(
                                        modifier = Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                        // API Error Banner
                                        if (apiError != null) {
                                                Box(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .background(
                                                                                Error.copy(
                                                                                        alpha = 0.1f
                                                                                ),
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                )
                                                                        )
                                                                        .padding(16.dp)
                                                ) {
                                                        Text(
                                                                text = apiError!!,
                                                                color = Error,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall
                                                        )
                                                }
                                        }

                                        // Make & Model Row
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                                // Make
                                                Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                                "Marque",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelLarge,
                                                                color = TextPrimary
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        OutlinedTextField(
                                                                value = make,
                                                                onValueChange = {
                                                                        make = it
                                                                        makeError = null
                                                                        apiError = null
                                                                },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                placeholder = { Text("Toyota") },
                                                                leadingIcon = {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .DirectionsCar,
                                                                                null,
                                                                                tint = TextSecondary
                                                                        )
                                                                },
                                                                isError = makeError != null,
                                                                singleLine = true,
                                                                keyboardOptions =
                                                                        KeyboardOptions(
                                                                                capitalization =
                                                                                        KeyboardCapitalization
                                                                                                .Words,
                                                                                imeAction =
                                                                                        ImeAction
                                                                                                .Next
                                                                        ),
                                                                keyboardActions =
                                                                        KeyboardActions(
                                                                                onNext = {
                                                                                        focusManager
                                                                                                .moveFocus(
                                                                                                        FocusDirection
                                                                                                                .Right
                                                                                                )
                                                                                }
                                                                        ),
                                                                shape = RoundedCornerShape(12.dp),
                                                                colors =
                                                                        OutlinedTextFieldDefaults
                                                                                .colors(
                                                                                        focusedBorderColor =
                                                                                                BlassaTeal,
                                                                                        unfocusedBorderColor =
                                                                                                Border,
                                                                                        errorBorderColor =
                                                                                                Error,
                                                                                        focusedContainerColor =
                                                                                                InputBackground,
                                                                                        unfocusedContainerColor =
                                                                                                InputBackground
                                                                                )
                                                        )
                                                        if (makeError != null) {
                                                                Text(
                                                                        makeError!!,
                                                                        color = Error,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall
                                                                )
                                                        }
                                                }

                                                // Model
                                                Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                                "Modèle",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelLarge,
                                                                color = TextPrimary
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        OutlinedTextField(
                                                                value = model,
                                                                onValueChange = {
                                                                        model = it
                                                                        modelError = null
                                                                        apiError = null
                                                                },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                placeholder = { Text("Corolla") },
                                                                isError = modelError != null,
                                                                singleLine = true,
                                                                keyboardOptions =
                                                                        KeyboardOptions(
                                                                                capitalization =
                                                                                        KeyboardCapitalization
                                                                                                .Words,
                                                                                imeAction =
                                                                                        ImeAction
                                                                                                .Next
                                                                        ),
                                                                keyboardActions =
                                                                        KeyboardActions(
                                                                                onNext = {
                                                                                        focusManager
                                                                                                .moveFocus(
                                                                                                        FocusDirection
                                                                                                                .Down
                                                                                                )
                                                                                }
                                                                        ),
                                                                shape = RoundedCornerShape(12.dp),
                                                                colors =
                                                                        OutlinedTextFieldDefaults
                                                                                .colors(
                                                                                        focusedBorderColor =
                                                                                                BlassaTeal,
                                                                                        unfocusedBorderColor =
                                                                                                Border,
                                                                                        errorBorderColor =
                                                                                                Error,
                                                                                        focusedContainerColor =
                                                                                                InputBackground,
                                                                                        unfocusedContainerColor =
                                                                                                InputBackground
                                                                                )
                                                        )
                                                        if (modelError != null) {
                                                                Text(
                                                                        modelError!!,
                                                                        color = Error,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall
                                                                )
                                                        }
                                                }
                                        }

                                        // Color
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                        "Couleur",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = TextPrimary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                OutlinedTextField(
                                                        value = color,
                                                        onValueChange = {
                                                                color = it
                                                                colorError = null
                                                                apiError = null
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        placeholder = { Text("Blanc") },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.ColorLens,
                                                                        null,
                                                                        tint = TextSecondary
                                                                )
                                                        },
                                                        isError = colorError != null,
                                                        singleLine = true,
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        capitalization =
                                                                                KeyboardCapitalization
                                                                                        .Words,
                                                                        imeAction = ImeAction.Next
                                                                ),
                                                        keyboardActions =
                                                                KeyboardActions(
                                                                        onNext = {
                                                                                focusManager
                                                                                        .moveFocus(
                                                                                                FocusDirection
                                                                                                        .Down
                                                                                        )
                                                                        }
                                                                ),
                                                        shape = RoundedCornerShape(12.dp),
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                BlassaTeal,
                                                                        unfocusedBorderColor =
                                                                                Border,
                                                                        errorBorderColor = Error,
                                                                        focusedContainerColor =
                                                                                InputBackground,
                                                                        unfocusedContainerColor =
                                                                                InputBackground
                                                                )
                                                )
                                                if (colorError != null) {
                                                        Text(
                                                                colorError!!,
                                                                color = Error,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                modifier =
                                                                        Modifier.padding(top = 4.dp)
                                                        )
                                                }
                                        }

                                        // License Plate
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                        "Immatriculation",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = TextPrimary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                OutlinedTextField(
                                                        value = licensePlate,
                                                        onValueChange = {
                                                                licensePlate = it.uppercase()
                                                                licensePlateError = null
                                                                apiError = null
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        placeholder = { Text("123 TUN 4567") },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.Numbers,
                                                                        null,
                                                                        tint = TextSecondary
                                                                )
                                                        },
                                                        isError = licensePlateError != null,
                                                        singleLine = true,
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        capitalization =
                                                                                KeyboardCapitalization
                                                                                        .Characters,
                                                                        imeAction = ImeAction.Next
                                                                ),
                                                        keyboardActions =
                                                                KeyboardActions(
                                                                        onNext = {
                                                                                focusManager
                                                                                        .moveFocus(
                                                                                                FocusDirection
                                                                                                        .Down
                                                                                        )
                                                                        }
                                                                ),
                                                        shape = RoundedCornerShape(12.dp),
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                BlassaTeal,
                                                                        unfocusedBorderColor =
                                                                                Border,
                                                                        errorBorderColor = Error,
                                                                        focusedContainerColor =
                                                                                InputBackground,
                                                                        unfocusedContainerColor =
                                                                                InputBackground
                                                                )
                                                )
                                                if (licensePlateError != null) {
                                                        Text(
                                                                licensePlateError!!,
                                                                color = Error,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                modifier =
                                                                        Modifier.padding(top = 4.dp)
                                                        )
                                                }
                                        }

                                        // Production Year (optional)
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                        "Année de production (optionnel)",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = TextPrimary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                OutlinedTextField(
                                                        value = productionYear,
                                                        onValueChange = {
                                                                if (it.length <= 4 &&
                                                                                it.all { c ->
                                                                                        c.isDigit()
                                                                                }
                                                                ) {
                                                                        productionYear = it
                                                                        productionYearError = null
                                                                        apiError = null
                                                                }
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        placeholder = { Text("2020") },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.CalendarMonth,
                                                                        null,
                                                                        tint = TextSecondary
                                                                )
                                                        },
                                                        isError = productionYearError != null,
                                                        singleLine = true,
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Number,
                                                                        imeAction = ImeAction.Done
                                                                ),
                                                        keyboardActions =
                                                                KeyboardActions(
                                                                        onDone = {
                                                                                focusManager
                                                                                        .clearFocus()
                                                                                handleSubmit()
                                                                        }
                                                                ),
                                                        shape = RoundedCornerShape(12.dp),
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                BlassaTeal,
                                                                        unfocusedBorderColor =
                                                                                Border,
                                                                        errorBorderColor = Error,
                                                                        focusedContainerColor =
                                                                                InputBackground,
                                                                        unfocusedContainerColor =
                                                                                InputBackground
                                                                )
                                                )
                                                if (productionYearError != null) {
                                                        Text(
                                                                productionYearError!!,
                                                                color = Error,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                modifier =
                                                                        Modifier.padding(top = 4.dp)
                                                        )
                                                }
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Submit Button
                        Button(
                                onClick = { handleSubmit() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                enabled = !isLoading,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BlassaTeal)
                        ) {
                                if (isLoading) {
                                        CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(24.dp)
                                        )
                                } else {
                                        Text(
                                                "Ajouter le véhicule",
                                                fontWeight = FontWeight.SemiBold
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                }
        }
}

@Composable
private fun SuccessScreen(onBack: () -> Unit) {
        Column(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                Box(
                        modifier =
                                Modifier.size(80.dp)
                                        .background(
                                                Color(0xFF10B981).copy(alpha = 0.1f),
                                                CircleShape
                                        ),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(48.dp)
                        )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                        text = "Véhicule ajouté !",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = "Votre véhicule a été ajouté avec succès.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlassaTeal)
                ) { Text("Continuer", fontWeight = FontWeight.SemiBold) }
        }
}
