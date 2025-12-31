package com.tp.blassa.features.auth

import android.app.DatePickerDialog
import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tp.blassa.R
import com.tp.blassa.core.network.RegisterRequest
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.parseErrorMessage
import com.tp.blassa.ui.theme.Background
import com.tp.blassa.ui.theme.BlassaAmber
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.Border
import com.tp.blassa.ui.theme.Error
import com.tp.blassa.ui.theme.InputBackground
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary
import java.util.Calendar
import kotlinx.coroutines.launch

enum class Gender(val label: String, val apiValue: String) {
        MALE("Homme", "MALE"),
        FEMALE("Femme", "FEMALE")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit, onRegisterSuccess: (String) -> Unit) {
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("+216") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var birthDate by remember { mutableStateOf("") }
        var selectedGender by remember { mutableStateOf<Gender?>(null) }
        var genderExpanded by remember { mutableStateOf(false) }

        var passwordVisible by remember { mutableStateOf(false) }
        var confirmPasswordVisible by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }

        var firstNameError by remember { mutableStateOf<String?>(null) }
        var lastNameError by remember { mutableStateOf<String?>(null) }
        var emailError by remember { mutableStateOf<String?>(null) }
        var phoneError by remember { mutableStateOf<String?>(null) }
        var passwordError by remember { mutableStateOf<String?>(null) }
        var confirmPasswordError by remember { mutableStateOf<String?>(null) }
        var birthDateError by remember { mutableStateOf<String?>(null) }
        var genderError by remember { mutableStateOf<String?>(null) }
        var apiError by remember { mutableStateOf<String?>(null) }

        val focusManager = LocalFocusManager.current
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        val calendar = Calendar.getInstance()
        val datePickerDialog =
                DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                                birthDate =
                                        String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                                birthDateError = null
                        },
                        calendar.get(Calendar.YEAR) - 18,
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                )

        fun validate(): Boolean {
                var isValid = true

                if (firstName.isBlank()) {
                        firstNameError = "Prénom requis"
                        isValid = false
                } else firstNameError = null

                if (lastName.isBlank()) {
                        lastNameError = "Nom requis"
                        isValid = false
                } else lastNameError = null

                if (email.isBlank()) {
                        emailError = "Email requis"
                        isValid = false
                } else if (!EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Email invalide"
                        isValid = false
                } else emailError = null

                if (phoneNumber.isBlank() || phoneNumber == "+216") {
                        phoneError = "Numéro requis"
                        isValid = false
                } else if (!phoneNumber.matches(Regex("^\\+[1-9][0-9]{7,14}$"))) {
                        phoneError = "Format: +21612345678"
                        isValid = false
                } else phoneError = null

                if (password.isBlank()) {
                        passwordError = "Mot de passe requis"
                        isValid = false
                } else if (password.length < 6) {
                        passwordError = "Minimum 6 caractères"
                        isValid = false
                } else passwordError = null

                if (confirmPassword != password) {
                        confirmPasswordError = "Les mots de passe ne correspondent pas"
                        isValid = false
                } else confirmPasswordError = null

                if (birthDate.isBlank()) {
                        birthDateError = "Date de naissance requise"
                        isValid = false
                } else birthDateError = null

                if (selectedGender == null) {
                        genderError = "Genre requis"
                        isValid = false
                } else genderError = null

                return isValid
        }

        fun handleRegister() {
                if (!validate()) return

                isLoading = true
                apiError = null

                scope.launch {
                        try {
                                val response =
                                        RetrofitClient.authApiService.register(
                                                RegisterRequest(
                                                        email = email,
                                                        password = password,
                                                        firstName = firstName,
                                                        lastName = lastName,
                                                        phoneNumber = phoneNumber,
                                                        gender = selectedGender!!.apiValue,
                                                        birthDate = birthDate
                                                )
                                        )

                                when (response.status) {
                                        "REGISTRATION_SUCCESS" -> {
                                                snackbarHostState.showSnackbar(
                                                        message =
                                                                "Inscription réussie! Vérifiez votre email.",
                                                        duration = SnackbarDuration.Long
                                                )
                                                onRegisterSuccess(email)
                                        }
                                        else -> {
                                                apiError =
                                                        response.message
                                                                ?: "Erreur lors de l'inscription"
                                                snackbarHostState.showSnackbar(
                                                        message = response.message
                                                                        ?: "Erreur lors de l'inscription",
                                                        duration = SnackbarDuration.Long
                                                )
                                        }
                                }
                        } catch (e: retrofit2.HttpException) {
                                val errorMessage = e.parseErrorMessage()
                                apiError = errorMessage
                                snackbarHostState.showSnackbar(
                                        message = errorMessage,
                                        duration = SnackbarDuration.Long
                                )
                        } catch (e: java.net.UnknownHostException) {
                                apiError = "Impossible de se connecter au serveur"
                                snackbarHostState.showSnackbar(
                                        message = "Impossible de se connecter au serveur",
                                        duration = SnackbarDuration.Long
                                )
                        } catch (e: java.net.SocketTimeoutException) {
                                apiError = "Le serveur ne répond pas"
                                snackbarHostState.showSnackbar(
                                        message = "Le serveur ne répond pas",
                                        duration = SnackbarDuration.Long
                                )
                        } catch (e: Exception) {
                                apiError = "Erreur réseau. Vérifiez votre connexion."
                                snackbarHostState.showSnackbar(
                                        message = "Erreur réseau. Vérifiez votre connexion.",
                                        duration = SnackbarDuration.Long
                                )
                        } finally {
                                isLoading = false
                        }
                }
        }

        Scaffold(
                snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                                Snackbar(
                                        snackbarData = data,
                                        containerColor =
                                                if (apiError != null) Error else BlassaTeal,
                                        contentColor = Color.White,
                                        shape = RoundedCornerShape(12.dp)
                                )
                        }
                },
                containerColor = Background
        ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                        brush =
                                                Brush.radialGradient(
                                                        colors =
                                                                listOf(
                                                                        BlassaTeal.copy(
                                                                                alpha = 0.08f
                                                                        ),
                                                                        Color.Transparent
                                                                ),
                                                        center =
                                                                Offset(
                                                                        -size.width * 0.2f,
                                                                        -size.height * 0.1f
                                                                ),
                                                        radius = size.width * 0.8f
                                                ),
                                        radius = size.width * 0.8f,
                                        center = Offset(-size.width * 0.2f, -size.height * 0.1f)
                                )
                                drawCircle(
                                        brush =
                                                Brush.radialGradient(
                                                        colors =
                                                                listOf(
                                                                        BlassaAmber.copy(
                                                                                alpha = 0.08f
                                                                        ),
                                                                        Color.Transparent
                                                                ),
                                                        center =
                                                                Offset(
                                                                        size.width * 1.2f,
                                                                        size.height * 1.1f
                                                                ),
                                                        radius = size.width * 0.8f
                                                ),
                                        radius = size.width * 0.8f,
                                        center = Offset(size.width * 1.2f, size.height * 1.1f)
                                )
                        }

                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .verticalScroll(rememberScrollState())
                                                .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                Spacer(modifier = Modifier.height(32.dp))

                                Card(
                                        modifier =
                                                Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color.White
                                                ),
                                        elevation =
                                                CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                        Column(
                                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                                Image(
                                                        painter =
                                                                painterResource(
                                                                        id = R.drawable.logo
                                                                ),
                                                        contentDescription = "Blassa Logo",
                                                        modifier = Modifier.size(64.dp)
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Text(
                                                        text = "Créer un compte",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                        text = "Rejoignez la communauté Blassa",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = TextSecondary,
                                                        textAlign = TextAlign.Center
                                                )

                                                Spacer(modifier = Modifier.height(24.dp))

                                                if (apiError != null) {
                                                        Box(
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .background(
                                                                                        Error.copy(
                                                                                                alpha =
                                                                                                        0.1f
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
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall
                                                                )
                                                        }
                                                        Spacer(modifier = Modifier.height(16.dp))
                                                }

                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                        "Prénom",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        color = TextPrimary
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        4.dp
                                                                                )
                                                                )
                                                                OutlinedTextField(
                                                                        value = firstName,
                                                                        onValueChange = {
                                                                                firstName = it
                                                                                firstNameError =
                                                                                        null
                                                                                apiError = null
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        placeholder = {
                                                                                Text("Prénom")
                                                                        },
                                                                        leadingIcon = {
                                                                                Icon(
                                                                                        Icons.Default
                                                                                                .Person,
                                                                                        null,
                                                                                        tint =
                                                                                                TextSecondary
                                                                                )
                                                                        },
                                                                        isError =
                                                                                firstNameError !=
                                                                                        null,
                                                                        singleLine = true,
                                                                        keyboardOptions =
                                                                                KeyboardOptions(
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
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                ),
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
                                                                if (firstNameError != null) {
                                                                        Text(
                                                                                firstNameError!!,
                                                                                color = Error,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall
                                                                        )
                                                                }
                                                        }
                                                        Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                        "Nom",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        color = TextPrimary
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        4.dp
                                                                                )
                                                                )
                                                                OutlinedTextField(
                                                                        value = lastName,
                                                                        onValueChange = {
                                                                                lastName = it
                                                                                lastNameError = null
                                                                                apiError = null
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        placeholder = {
                                                                                Text("Nom")
                                                                        },
                                                                        isError =
                                                                                lastNameError !=
                                                                                        null,
                                                                        singleLine = true,
                                                                        keyboardOptions =
                                                                                KeyboardOptions(
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
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                ),
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
                                                                if (lastNameError != null) {
                                                                        Text(
                                                                                lastNameError!!,
                                                                                color = Error,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall
                                                                        )
                                                                }
                                                        }
                                                }

                                                Spacer(modifier = Modifier.height(12.dp))

                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                        Text(
                                                                "Email",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelLarge,
                                                                color = TextPrimary
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        OutlinedTextField(
                                                                value = email,
                                                                onValueChange = {
                                                                        email = it
                                                                        emailError = null
                                                                        apiError = null
                                                                },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                placeholder = {
                                                                        Text("votre@email.com")
                                                                },
                                                                leadingIcon = {
                                                                        Icon(
                                                                                Icons.Default.Email,
                                                                                null,
                                                                                tint = TextSecondary
                                                                        )
                                                                },
                                                                isError = emailError != null,
                                                                singleLine = true,
                                                                keyboardOptions =
                                                                        KeyboardOptions(
                                                                                keyboardType =
                                                                                        KeyboardType
                                                                                                .Email,
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
                                                        if (emailError != null) {
                                                                Text(
                                                                        emailError!!,
                                                                        color = Error,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        top = 4.dp
                                                                                )
                                                                )
                                                        }
                                                }

                                                Spacer(modifier = Modifier.height(12.dp))

                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                        Text(
                                                                "Téléphone",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelLarge,
                                                                color = TextPrimary
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        OutlinedTextField(
                                                                value = phoneNumber,
                                                                onValueChange = {
                                                                        phoneNumber = it
                                                                        phoneError = null
                                                                        apiError = null
                                                                },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                placeholder = {
                                                                        Text("+21612345678")
                                                                },
                                                                leadingIcon = {
                                                                        Icon(
                                                                                Icons.Default.Phone,
                                                                                null,
                                                                                tint = TextSecondary
                                                                        )
                                                                },
                                                                isError = phoneError != null,
                                                                singleLine = true,
                                                                keyboardOptions =
                                                                        KeyboardOptions(
                                                                                keyboardType =
                                                                                        KeyboardType
                                                                                                .Phone,
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
                                                        if (phoneError != null) {
                                                                Text(
                                                                        phoneError!!,
                                                                        color = Error,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        top = 4.dp
                                                                                )
                                                                )
                                                        }
                                                }

                                                Spacer(modifier = Modifier.height(12.dp))

                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                        "Genre",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        color = TextPrimary
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        4.dp
                                                                                )
                                                                )
                                                                ExposedDropdownMenuBox(
                                                                        expanded = genderExpanded,
                                                                        onExpandedChange = {
                                                                                genderExpanded =
                                                                                        !genderExpanded
                                                                        }
                                                                ) {
                                                                        OutlinedTextField(
                                                                                value =
                                                                                        selectedGender
                                                                                                ?.label
                                                                                                ?: "",
                                                                                onValueChange = {},
                                                                                readOnly = true,
                                                                                placeholder = {
                                                                                        Text(
                                                                                                "Sélectionner"
                                                                                        )
                                                                                },
                                                                                trailingIcon = {
                                                                                        ExposedDropdownMenuDefaults
                                                                                                .TrailingIcon(
                                                                                                        expanded =
                                                                                                                genderExpanded
                                                                                                )
                                                                                },
                                                                                modifier =
                                                                                        Modifier.menuAnchor()
                                                                                                .fillMaxWidth(),
                                                                                isError =
                                                                                        genderError !=
                                                                                                null,
                                                                                shape =
                                                                                        RoundedCornerShape(
                                                                                                12.dp
                                                                                        ),
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
                                                                        ExposedDropdownMenu(
                                                                                expanded =
                                                                                        genderExpanded,
                                                                                onDismissRequest = {
                                                                                        genderExpanded =
                                                                                                false
                                                                                }
                                                                        ) {
                                                                                Gender.entries
                                                                                        .forEach {
                                                                                                gender
                                                                                                ->
                                                                                                DropdownMenuItem(
                                                                                                        text = {
                                                                                                                Text(
                                                                                                                        gender.label
                                                                                                                )
                                                                                                        },
                                                                                                        onClick = {
                                                                                                                selectedGender =
                                                                                                                        gender
                                                                                                                genderError =
                                                                                                                        null
                                                                                                                genderExpanded =
                                                                                                                        false
                                                                                                        }
                                                                                                )
                                                                                        }
                                                                        }
                                                                }
                                                                if (genderError != null) {
                                                                        Text(
                                                                                genderError!!,
                                                                                color = Error,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall
                                                                        )
                                                                }
                                                        }

                                                        Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                        "Date de naissance",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        color = TextPrimary
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        4.dp
                                                                                )
                                                                )
                                                                OutlinedTextField(
                                                                        value = birthDate,
                                                                        onValueChange = {},
                                                                        readOnly = true,
                                                                        placeholder = {
                                                                                Text("YYYY-MM-DD")
                                                                        },
                                                                        trailingIcon = {
                                                                                IconButton(
                                                                                        onClick = {
                                                                                                datePickerDialog
                                                                                                        .show()
                                                                                        }
                                                                                ) {
                                                                                        Icon(
                                                                                                Icons.Default
                                                                                                        .CalendarMonth,
                                                                                                null,
                                                                                                tint =
                                                                                                        TextSecondary
                                                                                        )
                                                                                }
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                        .clickable {
                                                                                                datePickerDialog
                                                                                                        .show()
                                                                                        },
                                                                        isError =
                                                                                birthDateError !=
                                                                                        null,
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                ),
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
                                                                if (birthDateError != null) {
                                                                        Text(
                                                                                birthDateError!!,
                                                                                color = Error,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall
                                                                        )
                                                                }
                                                        }
                                                }

                                                Spacer(modifier = Modifier.height(12.dp))

                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                        Text(
                                                                "Mot de passe",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelLarge,
                                                                color = TextPrimary
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        OutlinedTextField(
                                                                value = password,
                                                                onValueChange = {
                                                                        password = it
                                                                        passwordError = null
                                                                        apiError = null
                                                                },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                placeholder = { Text("••••••••") },
                                                                leadingIcon = {
                                                                        Icon(
                                                                                Icons.Default.Lock,
                                                                                null,
                                                                                tint = TextSecondary
                                                                        )
                                                                },
                                                                trailingIcon = {
                                                                        IconButton(
                                                                                onClick = {
                                                                                        passwordVisible =
                                                                                                !passwordVisible
                                                                                }
                                                                        ) {
                                                                                Icon(
                                                                                        if (passwordVisible
                                                                                        )
                                                                                                Icons.Default
                                                                                                        .VisibilityOff
                                                                                        else
                                                                                                Icons.Default
                                                                                                        .Visibility,
                                                                                        null,
                                                                                        tint =
                                                                                                TextSecondary
                                                                                )
                                                                        }
                                                                },
                                                                visualTransformation =
                                                                        if (passwordVisible)
                                                                                VisualTransformation
                                                                                        .None
                                                                        else
                                                                                PasswordVisualTransformation(),
                                                                isError = passwordError != null,
                                                                singleLine = true,
                                                                keyboardOptions =
                                                                        KeyboardOptions(
                                                                                keyboardType =
                                                                                        KeyboardType
                                                                                                .Password,
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
                                                        if (passwordError != null) {
                                                                Text(
                                                                        passwordError!!,
                                                                        color = Error,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        top = 4.dp
                                                                                )
                                                                )
                                                        }
                                                }

                                                Spacer(modifier = Modifier.height(12.dp))

                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                        Text(
                                                                "Confirmer le mot de passe",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelLarge,
                                                                color = TextPrimary
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        OutlinedTextField(
                                                                value = confirmPassword,
                                                                onValueChange = {
                                                                        confirmPassword = it
                                                                        confirmPasswordError = null
                                                                        apiError = null
                                                                },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                placeholder = { Text("••••••••") },
                                                                leadingIcon = {
                                                                        Icon(
                                                                                Icons.Default.Lock,
                                                                                null,
                                                                                tint = TextSecondary
                                                                        )
                                                                },
                                                                trailingIcon = {
                                                                        IconButton(
                                                                                onClick = {
                                                                                        confirmPasswordVisible =
                                                                                                !confirmPasswordVisible
                                                                                }
                                                                        ) {
                                                                                Icon(
                                                                                        if (confirmPasswordVisible
                                                                                        )
                                                                                                Icons.Default
                                                                                                        .VisibilityOff
                                                                                        else
                                                                                                Icons.Default
                                                                                                        .Visibility,
                                                                                        null,
                                                                                        tint =
                                                                                                TextSecondary
                                                                                )
                                                                        }
                                                                },
                                                                visualTransformation =
                                                                        if (confirmPasswordVisible)
                                                                                VisualTransformation
                                                                                        .None
                                                                        else
                                                                                PasswordVisualTransformation(),
                                                                isError =
                                                                        confirmPasswordError !=
                                                                                null,
                                                                singleLine = true,
                                                                keyboardOptions =
                                                                        KeyboardOptions(
                                                                                keyboardType =
                                                                                        KeyboardType
                                                                                                .Password,
                                                                                imeAction =
                                                                                        ImeAction
                                                                                                .Done
                                                                        ),
                                                                keyboardActions =
                                                                        KeyboardActions(
                                                                                onDone = {
                                                                                        focusManager
                                                                                                .clearFocus()
                                                                                        handleRegister()
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
                                                        if (confirmPasswordError != null) {
                                                                Text(
                                                                        confirmPasswordError!!,
                                                                        color = Error,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        top = 4.dp
                                                                                )
                                                                )
                                                        }
                                                }

                                                Spacer(modifier = Modifier.height(24.dp))

                                                Button(
                                                        onClick = { handleRegister() },
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(48.dp),
                                                        enabled = !isLoading,
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor = BlassaTeal,
                                                                        contentColor = Color.White
                                                                ),
                                                        shape = RoundedCornerShape(12.dp)
                                                ) {
                                                        if (isLoading) {
                                                                CircularProgressIndicator(
                                                                        modifier =
                                                                                Modifier.size(
                                                                                        20.dp
                                                                                ),
                                                                        color = Color.White,
                                                                        strokeWidth = 2.dp
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.width(8.dp)
                                                                )
                                                                Text("Inscription...")
                                                        } else {
                                                                Text(
                                                                        "S'inscrire",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        fontWeight =
                                                                                FontWeight.Medium,
                                                                        color = Color.White
                                                                )
                                                        }
                                                }
                                        }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row {
                                        Text(
                                                "Vous avez déjà un compte ? ",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                        Text(
                                                "Se connecter",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = BlassaAmber,
                                                fontWeight = FontWeight.Medium,
                                                modifier =
                                                        Modifier.clickable { onNavigateToLogin() }
                                        )
                                }

                                Spacer(modifier = Modifier.height(32.dp))
                        }
                }
        }
}
