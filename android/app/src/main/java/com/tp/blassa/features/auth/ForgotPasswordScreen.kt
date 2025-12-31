package com.tp.blassa.features.auth

import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tp.blassa.R
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
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(onNavigateToLogin: () -> Unit) {
        var email by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        var isEmailSent by remember { mutableStateOf(false) }
        var apiError by remember { mutableStateOf<String?>(null) }

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        fun validateEmail(): Boolean {
                emailError =
                        when {
                                email.isBlank() -> "Email requis"
                                !EMAIL_ADDRESS.matcher(email).matches() -> "Email invalide"
                                else -> null
                        }
                return emailError == null
        }

        fun handleSubmit() {
                if (!validateEmail()) return

                isLoading = true
                apiError = null

                scope.launch {
                        try {
                                val response =
                                        RetrofitClient.authApiService.forgotPassword(
                                                mapOf("email" to email)
                                        )

                                when (response.status) {
                                        "SUCCESS" -> {
                                                isEmailSent = true
                                                snackbarHostState.showSnackbar(
                                                        message = "Email envoyé !",
                                                        duration = SnackbarDuration.Short
                                                )
                                        }
                                        else -> {
                                                apiError =
                                                        response.message ?: "Erreur lors de l'envoi"
                                                snackbarHostState.showSnackbar(
                                                        message = response.message
                                                                        ?: "Erreur lors de l'envoi",
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
        ) { padding ->
                Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                ) {
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
                                                Box(modifier = Modifier.fillMaxWidth()) {
                                                        IconButton(
                                                                onClick = onNavigateToLogin,
                                                                modifier =
                                                                        Modifier.align(
                                                                                Alignment
                                                                                        .CenterStart
                                                                        )
                                                        ) {
                                                                Icon(
                                                                        Icons.AutoMirrored.Filled
                                                                                .ArrowBack,
                                                                        contentDescription =
                                                                                "Retour",
                                                                        tint = TextSecondary
                                                                )
                                                        }
                                                }

                                                Image(
                                                        painter =
                                                                painterResource(
                                                                        id = R.drawable.logo
                                                                ),
                                                        contentDescription = "Blassa Logo",
                                                        modifier = Modifier.size(72.dp)
                                                )

                                                Spacer(modifier = Modifier.height(24.dp))

                                                if (isEmailSent) {

                                                        Box(
                                                                modifier =
                                                                        Modifier.size(80.dp)
                                                                                .background(
                                                                                        brush =
                                                                                                Brush.linearGradient(
                                                                                                        colors =
                                                                                                                listOf(
                                                                                                                        BlassaTeal
                                                                                                                                .copy(
                                                                                                                                        alpha =
                                                                                                                                                0.2f
                                                                                                                                ),
                                                                                                                        BlassaTeal
                                                                                                                                .copy(
                                                                                                                                        alpha =
                                                                                                                                                0.05f
                                                                                                                                )
                                                                                                                )
                                                                                                ),
                                                                                        shape =
                                                                                                CircleShape
                                                                                ),
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default
                                                                                        .MarkEmailRead,
                                                                        contentDescription = null,
                                                                        modifier =
                                                                                Modifier.size(
                                                                                        40.dp
                                                                                ),
                                                                        tint = BlassaTeal
                                                                )
                                                        }

                                                        Spacer(modifier = Modifier.height(24.dp))

                                                        Text(
                                                                text = "Email envoyé !",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .headlineSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = TextPrimary
                                                        )

                                                        Spacer(modifier = Modifier.height(12.dp))

                                                        Text(
                                                                text =
                                                                        "Si un compte existe avec cet email, vous recevrez un lien pour réinitialiser votre mot de passe.",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color = TextSecondary,
                                                                textAlign = TextAlign.Center
                                                        )

                                                        Spacer(modifier = Modifier.height(8.dp))

                                                        Text(
                                                                text = email,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyLarge,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = TextPrimary
                                                        )

                                                        Spacer(modifier = Modifier.height(32.dp))

                                                        Button(
                                                                onClick = onNavigateToLogin,
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .height(48.dp),
                                                                colors =
                                                                        ButtonDefaults.buttonColors(
                                                                                containerColor =
                                                                                        BlassaTeal
                                                                        ),
                                                                shape = RoundedCornerShape(12.dp)
                                                        ) {
                                                                Text(
                                                                        text =
                                                                                "Retour à la connexion",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        fontWeight =
                                                                                FontWeight.Medium,
                                                                        color = Color.White
                                                                )
                                                        }

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        OutlinedButton(
                                                                onClick = {
                                                                        isEmailSent = false
                                                                        email = ""
                                                                },
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .height(48.dp),
                                                                shape = RoundedCornerShape(12.dp),
                                                                colors =
                                                                        ButtonDefaults
                                                                                .outlinedButtonColors(
                                                                                        contentColor =
                                                                                                TextSecondary
                                                                                )
                                                        ) {
                                                                Text(
                                                                        text =
                                                                                "Essayer avec un autre email",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        fontWeight =
                                                                                FontWeight.Medium
                                                                )
                                                        }
                                                } else {

                                                        Text(
                                                                text = "Mot de passe oublié ?",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .headlineSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = TextPrimary
                                                        )

                                                        Spacer(modifier = Modifier.height(8.dp))

                                                        Text(
                                                                text =
                                                                        "Entrez votre adresse email et nous vous enverrons un lien pour réinitialiser votre mot de passe.",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color = TextSecondary,
                                                                textAlign = TextAlign.Center
                                                        )

                                                        Spacer(modifier = Modifier.height(32.dp))

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
                                                                                        .padding(
                                                                                                16.dp
                                                                                        )
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
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        16.dp
                                                                                )
                                                                )
                                                        }

                                                        Column(modifier = Modifier.fillMaxWidth()) {
                                                                Text(
                                                                        text = "Email",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        color = TextPrimary,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        bottom =
                                                                                                8.dp
                                                                                )
                                                                )
                                                                OutlinedTextField(
                                                                        value = email,
                                                                        onValueChange = {
                                                                                email = it
                                                                                emailError = null
                                                                                apiError = null
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        placeholder = {
                                                                                Text(
                                                                                        "votre@email.com"
                                                                                )
                                                                        },
                                                                        leadingIcon = {
                                                                                Icon(
                                                                                        imageVector =
                                                                                                Icons.Default
                                                                                                        .Email,
                                                                                        contentDescription =
                                                                                                null,
                                                                                        tint =
                                                                                                TextSecondary
                                                                                )
                                                                        },
                                                                        isError =
                                                                                emailError != null,
                                                                        singleLine = true,
                                                                        keyboardOptions =
                                                                                KeyboardOptions(
                                                                                        keyboardType =
                                                                                                KeyboardType
                                                                                                        .Email,
                                                                                        imeAction =
                                                                                                ImeAction
                                                                                                        .Done
                                                                                ),
                                                                        keyboardActions =
                                                                                KeyboardActions(
                                                                                        onDone = {
                                                                                                handleSubmit()
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
                                                                if (emailError != null) {
                                                                        Text(
                                                                                text = emailError!!,
                                                                                color = Error,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall,
                                                                                modifier =
                                                                                        Modifier.padding(
                                                                                                top =
                                                                                                        4.dp
                                                                                        )
                                                                        )
                                                                }
                                                        }

                                                        Spacer(modifier = Modifier.height(24.dp))

                                                        Button(
                                                                onClick = { handleSubmit() },
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .height(48.dp),
                                                                enabled = !isLoading,
                                                                colors =
                                                                        ButtonDefaults.buttonColors(
                                                                                containerColor =
                                                                                        BlassaTeal,
                                                                                contentColor =
                                                                                        Color.White
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
                                                                                        Modifier.width(
                                                                                                8.dp
                                                                                        )
                                                                        )
                                                                        Text("Envoi en cours...")
                                                                } else {
                                                                        Text(
                                                                                text =
                                                                                        "Envoyer le lien",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .labelLarge,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Medium,
                                                                                color = Color.White
                                                                        )
                                                                }
                                                        }

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        OutlinedButton(
                                                                onClick = onNavigateToLogin,
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .height(48.dp),
                                                                shape = RoundedCornerShape(12.dp),
                                                                colors =
                                                                        ButtonDefaults
                                                                                .outlinedButtonColors(
                                                                                        contentColor =
                                                                                                TextSecondary
                                                                                )
                                                        ) {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.AutoMirrored
                                                                                        .Filled
                                                                                        .ArrowBack,
                                                                        contentDescription = null,
                                                                        modifier =
                                                                                Modifier.size(20.dp)
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.width(8.dp)
                                                                )
                                                                Text(
                                                                        text =
                                                                                "Retour à la connexion",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        fontWeight =
                                                                                FontWeight.Medium
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}
