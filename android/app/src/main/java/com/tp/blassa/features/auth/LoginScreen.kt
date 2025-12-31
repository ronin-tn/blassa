package com.tp.blassa.features.auth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.tp.blassa.BuildConfig
import com.tp.blassa.R
import com.tp.blassa.features.auth.components.AuthBackground
import com.tp.blassa.features.auth.components.AuthErrorBanner
import com.tp.blassa.features.auth.components.AuthTextField
import com.tp.blassa.features.auth.components.GoogleSignInButton
import com.tp.blassa.features.auth.components.PasswordVisibilityToggle
import com.tp.blassa.features.auth.viewmodel.LoginViewModel
import com.tp.blassa.ui.theme.Background
import com.tp.blassa.ui.theme.BlassaAmber
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.Border
import com.tp.blassa.ui.theme.Error
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
        onNavigateToRegister: () -> Unit,
        onNavigateToForgotPassword: () -> Unit,
        onNavigateToCompleteProfile: () -> Unit,
        onLoginSuccess: () -> Unit,
        viewModel: LoginViewModel = viewModel()
) {
        val uiState by viewModel.uiState.collectAsState()
        val focusManager = LocalFocusManager.current
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(uiState.loginSuccess) {
                if (uiState.loginSuccess) {
                        if (uiState.isProfileIncomplete) {
                                onNavigateToCompleteProfile()
                        } else {
                                onLoginSuccess()
                        }
                }
        }

        LaunchedEffect(uiState.snackbarMessage) {
                uiState.snackbarMessage?.let { message ->
                        snackbarHostState.showSnackbar(
                                message = message,
                                duration =
                                        if (uiState.apiError != null) SnackbarDuration.Long
                                        else SnackbarDuration.Short
                        )
                        viewModel.clearSnackbarMessage()
                }
        }

        Scaffold(
                snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                                Snackbar(
                                        snackbarData = data,
                                        containerColor =
                                                if (uiState.apiError != null) Error else BlassaTeal,
                                        contentColor = Color.White,
                                        shape = RoundedCornerShape(12.dp)
                                )
                        }
                },
                containerColor = Background
        ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        AuthBackground()

                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .verticalScroll(rememberScrollState())
                                                .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                        ) {
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
                                                        modifier = Modifier.size(80.dp)
                                                )

                                                Spacer(modifier = Modifier.height(24.dp))

                                                Text(
                                                        text = "Connexion",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                        text =
                                                                "Connectez-vous à votre compte Blassa",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = TextSecondary,
                                                        textAlign = TextAlign.Center
                                                )

                                                Row(
                                                        modifier = Modifier.padding(top = 12.dp),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Icon(
                                                                imageVector = Icons.Default.Lock,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(14.dp),
                                                                tint = TextSecondary
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                                text = "Sécurisé par Blassa",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color = TextSecondary
                                                        )
                                                }

                                                Spacer(modifier = Modifier.height(32.dp))

                                                if (uiState.apiError != null) {
                                                        AuthErrorBanner(
                                                                message = uiState.apiError!!
                                                        )
                                                        Spacer(modifier = Modifier.height(16.dp))
                                                }

                                                AuthTextField(
                                                        value = uiState.email,
                                                        onValueChange = viewModel::updateEmail,
                                                        label = "Email",
                                                        placeholder = "votre@email.com",
                                                        leadingIcon = Icons.Default.Email,
                                                        error = uiState.emailError,
                                                        keyboardType = KeyboardType.Email,
                                                        imeAction = ImeAction.Next,
                                                        onImeAction = {
                                                                focusManager.moveFocus(
                                                                        FocusDirection.Down
                                                                )
                                                        }
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                        Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement =
                                                                        Arrangement.SpaceBetween,
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically
                                                        ) {
                                                                Text(
                                                                        text = "Mot de passe",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        color = TextPrimary
                                                                )
                                                                Text(
                                                                        text =
                                                                                "Mot de passe oublié ?",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelMedium,
                                                                        color = BlassaAmber,
                                                                        fontWeight =
                                                                                FontWeight.Medium,
                                                                        modifier =
                                                                                Modifier.clickable {
                                                                                        onNavigateToForgotPassword()
                                                                                }
                                                                )
                                                        }
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        AuthTextField(
                                                                value = uiState.password,
                                                                onValueChange =
                                                                        viewModel::updatePassword,
                                                                label = "",
                                                                placeholder = "••••••••",
                                                                leadingIcon = Icons.Default.Lock,
                                                                trailingIcon = {
                                                                        PasswordVisibilityToggle(
                                                                                visible =
                                                                                        uiState.passwordVisible,
                                                                                onToggle =
                                                                                        viewModel::togglePasswordVisibility,
                                                                                visibleIcon =
                                                                                        Icons.Default
                                                                                                .VisibilityOff,
                                                                                hiddenIcon =
                                                                                        Icons.Default
                                                                                                .Visibility
                                                                        )
                                                                },
                                                                error = uiState.passwordError,
                                                                keyboardType =
                                                                        KeyboardType.Password,
                                                                imeAction = ImeAction.Done,
                                                                visualTransformation =
                                                                        if (uiState.passwordVisible
                                                                        ) {
                                                                                VisualTransformation
                                                                                        .None
                                                                        } else {
                                                                                PasswordVisualTransformation()
                                                                        },
                                                                onImeAction = {
                                                                        focusManager.clearFocus()
                                                                        viewModel.login()
                                                                }
                                                        )
                                                }

                                                Spacer(modifier = Modifier.height(24.dp))

                                                Button(
                                                        onClick = { viewModel.login() },
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(48.dp),
                                                        enabled = !uiState.isLoading,
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor = BlassaTeal,
                                                                        contentColor = Color.White
                                                                ),
                                                        shape = RoundedCornerShape(12.dp)
                                                ) {
                                                        if (uiState.isLoading) {
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
                                                                Text("Connexion...")
                                                        } else {
                                                                Text(
                                                                        text = "Se connecter",
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

                                                Spacer(modifier = Modifier.height(24.dp))

                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        HorizontalDivider(
                                                                modifier = Modifier.weight(1f),
                                                                color = Border
                                                        )
                                                        Text(
                                                                text = "ou",
                                                                modifier =
                                                                        Modifier.padding(
                                                                                horizontal = 16.dp
                                                                        ),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                color = TextSecondary
                                                        )
                                                        HorizontalDivider(
                                                                modifier = Modifier.weight(1f),
                                                                color = Border
                                                        )
                                                }

                                                Spacer(modifier = Modifier.height(24.dp))

                                                GoogleSignInButton(
                                                        onClick = {
                                                                if (uiState.isGoogleLoading)
                                                                        return@GoogleSignInButton

                                                                scope.launch {
                                                                        try {
                                                                                val credentialManager =
                                                                                        CredentialManager
                                                                                                .create(
                                                                                                        context
                                                                                                )
                                                                                val googleIdOption =
                                                                                        GetGoogleIdOption
                                                                                                .Builder()
                                                                                                .setServerClientId(
                                                                                                        BuildConfig
                                                                                                                .GOOGLE_CLIENT_ID
                                                                                                )
                                                                                                .setFilterByAuthorizedAccounts(
                                                                                                        false
                                                                                                )
                                                                                                .setAutoSelectEnabled(
                                                                                                        false
                                                                                                )
                                                                                                .build()

                                                                                val request =
                                                                                        GetCredentialRequest
                                                                                                .Builder()
                                                                                                .addCredentialOption(
                                                                                                        googleIdOption
                                                                                                )
                                                                                                .build()

                                                                                val result =
                                                                                        credentialManager
                                                                                                .getCredential(
                                                                                                        context =
                                                                                                                context,
                                                                                                        request =
                                                                                                                request
                                                                                                )

                                                                                val googleIdTokenCredential =
                                                                                        GoogleIdTokenCredential
                                                                                                .createFrom(
                                                                                                        result.credential
                                                                                                                .data
                                                                                                )
                                                                                viewModel
                                                                                        .loginWithGoogle(
                                                                                                googleIdTokenCredential
                                                                                                        .idToken
                                                                                        )
                                                                        } catch (
                                                                                e:
                                                                                        GetCredentialCancellationException) {
                                                                                viewModel
                                                                                        .onGoogleSignInCancelled()
                                                                        } catch (e: Exception) {
                                                                                viewModel
                                                                                        .onGoogleSignInCancelled()
                                                                        }
                                                                }
                                                        },
                                                        enabled = !uiState.isGoogleLoading
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row {
                                        Text(
                                                text = "Pas encore de compte ? ",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                        Text(
                                                text = "S'inscrire",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = BlassaAmber,
                                                fontWeight = FontWeight.Medium,
                                                modifier =
                                                        Modifier.clickable {
                                                                onNavigateToRegister()
                                                        }
                                        )
                                }
                        }
                }
        }
}
