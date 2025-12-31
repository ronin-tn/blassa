package com.tp.blassa.features.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tp.blassa.features.profile.viewmodel.ChangePasswordViewModel
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(onBack: () -> Unit, viewModel: ChangePasswordViewModel = viewModel()) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        var currentPasswordVisible by remember { mutableStateOf(false) }
        var newPasswordVisible by remember { mutableStateOf(false) }
        var confirmPasswordVisible by remember { mutableStateOf(false) }

        LaunchedEffect(uiState.error, uiState.successMessage) {
                uiState.error?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessages()
                }
                uiState.successMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessages()
                }
        }

        LaunchedEffect(uiState.isPasswordChanged) {
                if (uiState.isPasswordChanged) {
                        onBack()
                }
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Text(
                                                "Changer le mot de passe",
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
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = Color(0xFFF8FAFC)
        ) { padding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(padding)
                                        .padding(16.dp)
                                        .verticalScroll(rememberScrollState())
                ) {
                        Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                                text = "Sécurisez votre compte",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text =
                                                        "Votre nouveau mot de passe doit contenir au moins 8 caractères.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                                value = uiState.currentPassword,
                                                onValueChange = viewModel::updateCurrentPassword,
                                                modifier = Modifier.fillMaxWidth(),
                                                placeholder = {
                                                        Text("Entrez votre mot de passe actuel")
                                                },
                                                visualTransformation =
                                                        if (currentPasswordVisible)
                                                                VisualTransformation.None
                                                        else PasswordVisualTransformation(),
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Password
                                                        ),
                                                trailingIcon = {
                                                        IconButton(
                                                                onClick = {
                                                                        currentPasswordVisible =
                                                                                !currentPasswordVisible
                                                                }
                                                        ) {
                                                                Icon(
                                                                        if (currentPasswordVisible)
                                                                                Icons.Default
                                                                                        .VisibilityOff
                                                                        else
                                                                                Icons.Default
                                                                                        .Visibility,
                                                                        contentDescription =
                                                                                if (currentPasswordVisible
                                                                                )
                                                                                        "Masquer"
                                                                                else "Afficher"
                                                                )
                                                        }
                                                },
                                                singleLine = true,
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = BlassaTeal,
                                                                unfocusedBorderColor =
                                                                        Color.LightGray
                                                        )
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                                text = "Nouveau mot de passe",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = TextSecondary,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        OutlinedTextField(
                                                value = uiState.newPassword,
                                                onValueChange = viewModel::updateNewPassword,
                                                modifier = Modifier.fillMaxWidth(),
                                                placeholder = {
                                                        Text("Entrez votre nouveau mot de passe")
                                                },
                                                visualTransformation =
                                                        if (newPasswordVisible)
                                                                VisualTransformation.None
                                                        else PasswordVisualTransformation(),
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Password
                                                        ),
                                                trailingIcon = {
                                                        IconButton(
                                                                onClick = {
                                                                        newPasswordVisible =
                                                                                !newPasswordVisible
                                                                }
                                                        ) {
                                                                Icon(
                                                                        if (newPasswordVisible)
                                                                                Icons.Default
                                                                                        .VisibilityOff
                                                                        else
                                                                                Icons.Default
                                                                                        .Visibility,
                                                                        contentDescription =
                                                                                if (newPasswordVisible
                                                                                )
                                                                                        "Masquer"
                                                                                else "Afficher"
                                                                )
                                                        }
                                                },
                                                singleLine = true,
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = BlassaTeal,
                                                                unfocusedBorderColor =
                                                                        Color.LightGray
                                                        )
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                                text = "Confirmer le mot de passe",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = TextSecondary,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        OutlinedTextField(
                                                value = uiState.confirmPassword,
                                                onValueChange = viewModel::updateConfirmPassword,
                                                modifier = Modifier.fillMaxWidth(),
                                                placeholder = {
                                                        Text("Confirmez votre nouveau mot de passe")
                                                },
                                                visualTransformation =
                                                        if (confirmPasswordVisible)
                                                                VisualTransformation.None
                                                        else PasswordVisualTransformation(),
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Password
                                                        ),
                                                trailingIcon = {
                                                        IconButton(
                                                                onClick = {
                                                                        confirmPasswordVisible =
                                                                                !confirmPasswordVisible
                                                                }
                                                        ) {
                                                                Icon(
                                                                        if (confirmPasswordVisible)
                                                                                Icons.Default
                                                                                        .VisibilityOff
                                                                        else
                                                                                Icons.Default
                                                                                        .Visibility,
                                                                        contentDescription =
                                                                                if (confirmPasswordVisible
                                                                                )
                                                                                        "Masquer"
                                                                                else "Afficher"
                                                                )
                                                        }
                                                },
                                                singleLine = true,
                                                isError =
                                                        uiState.confirmPassword.isNotEmpty() &&
                                                                uiState.newPassword !=
                                                                        uiState.confirmPassword,
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = BlassaTeal,
                                                                unfocusedBorderColor =
                                                                        Color.LightGray
                                                        )
                                        )

                                        if (uiState.confirmPassword.isNotEmpty() &&
                                                        uiState.newPassword !=
                                                                uiState.confirmPassword
                                        ) {
                                                Text(
                                                        text =
                                                                "Les mots de passe ne correspondent pas",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.padding(top = 4.dp)
                                                )
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                                onClick = { viewModel.changePassword() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BlassaTeal),
                                enabled = !uiState.isLoading
                        ) {
                                if (uiState.isLoading) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                        )
                                } else {
                                        Text(
                                                text = "Changer le mot de passe",
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                        )
                                }
                        }
                }
        }
}
