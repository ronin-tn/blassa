package com.tp.blassa.features.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tp.blassa.features.profile.viewmodel.ChangeEmailViewModel
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailScreen(onBack: () -> Unit, viewModel: ChangeEmailViewModel = viewModel()) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        var passwordVisible by remember { mutableStateOf(false) }

        // Track whether the last message was an error or success for styling
        var isError by remember { mutableStateOf(false) }

        LaunchedEffect(uiState.error, uiState.successMessage) {
                uiState.error?.let {
                        isError = true
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessages()
                }
                uiState.successMessage?.let {
                        isError = false
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessages()
                }
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Text(
                                                "Changer l'adresse email",
                                                fontWeight = FontWeight.SemiBold
                                        )
                                },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
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
                snackbarHost = {
                        SnackbarHost(snackbarHostState) { data ->
                                Snackbar(
                                        snackbarData = data,
                                        containerColor =
                                                if (isError) Color(0xFFDC2626)
                                                else Color(0xFF16A34A),
                                        contentColor = Color.White,
                                        shape = MaterialTheme.shapes.medium
                                )
                        }
                },
                containerColor = Color(0xFFF8FAFC)
        ) { padding ->
                if (uiState.isLoading) {
                        Box(
                                modifier = Modifier.fillMaxSize().padding(padding),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = BlassaTeal) }
                } else {
                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .padding(padding)
                                                .padding(16.dp)
                                                .verticalScroll(rememberScrollState())
                        ) {
                                Card(
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color.White
                                                ),
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                        text = "Modifier votre email",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                        text =
                                                                "Un email de vérification sera envoyé à votre nouvelle adresse.",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = TextSecondary
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Card(
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color.White
                                                ),
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                        text = "Email actuel",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = TextSecondary,
                                                        modifier = Modifier.padding(bottom = 8.dp)
                                                )
                                                OutlinedTextField(
                                                        value = uiState.currentEmail,
                                                        onValueChange = {},
                                                        modifier = Modifier.fillMaxWidth(),
                                                        enabled = false,
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.Email,
                                                                        contentDescription = null,
                                                                        tint = TextSecondary
                                                                )
                                                        },
                                                        singleLine = true,
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        disabledBorderColor =
                                                                                Color.LightGray,
                                                                        disabledTextColor =
                                                                                TextPrimary,
                                                                        disabledLeadingIconColor =
                                                                                TextSecondary
                                                                )
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Card(
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color.White
                                                ),
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                        text = "Nouvel email",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = TextSecondary,
                                                        modifier = Modifier.padding(bottom = 8.dp)
                                                )
                                                OutlinedTextField(
                                                        value = uiState.newEmail,
                                                        onValueChange = viewModel::updateNewEmail,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        placeholder = {
                                                                Text("Entrez votre nouvel email")
                                                        },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.Email,
                                                                        contentDescription = null,
                                                                        tint = TextSecondary
                                                                )
                                                        },
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Email
                                                                ),
                                                        singleLine = true,
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                BlassaTeal,
                                                                        unfocusedBorderColor =
                                                                                Color.LightGray
                                                                )
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Card(
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color.White
                                                ),
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                        text = "Mot de passe actuel",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = TextSecondary,
                                                        modifier = Modifier.padding(bottom = 8.dp)
                                                )
                                                OutlinedTextField(
                                                        value = uiState.password,
                                                        onValueChange = viewModel::updatePassword,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        placeholder = {
                                                                Text("Confirmez votre identité")
                                                        },
                                                        visualTransformation =
                                                                if (passwordVisible)
                                                                        VisualTransformation.None
                                                                else PasswordVisualTransformation(),
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType
                                                                                        .Password
                                                                ),
                                                        trailingIcon = {
                                                                IconButton(
                                                                        onClick = {
                                                                                passwordVisible =
                                                                                        !passwordVisible
                                                                        }
                                                                ) {
                                                                        Icon(
                                                                                if (passwordVisible)
                                                                                        Icons.Default
                                                                                                .VisibilityOff
                                                                                else
                                                                                        Icons.Default
                                                                                                .Visibility,
                                                                                contentDescription =
                                                                                        if (passwordVisible
                                                                                        )
                                                                                                "Masquer"
                                                                                        else
                                                                                                "Afficher"
                                                                        )
                                                                }
                                                        },
                                                        singleLine = true,
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                BlassaTeal,
                                                                        unfocusedBorderColor =
                                                                                Color.LightGray
                                                                )
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                        text =
                                                                "Requis pour confirmer votre identité",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = TextSecondary
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Button(
                                        onClick = { viewModel.changeEmail() },
                                        modifier = Modifier.fillMaxWidth().height(50.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = BlassaTeal
                                                ),
                                        enabled = !uiState.isSubmitting
                                ) {
                                        if (uiState.isSubmitting) {
                                                CircularProgressIndicator(
                                                        modifier = Modifier.size(24.dp),
                                                        color = Color.White,
                                                        strokeWidth = 2.dp
                                                )
                                        } else {
                                                Text(
                                                        text = "Changer l'email",
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.White
                                                )
                                        }
                                }
                        }
                }
        }
}
