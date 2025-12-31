package com.tp.blassa.features.auth

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tp.blassa.R
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.parseErrorMessage
import com.tp.blassa.ui.theme.Background
import com.tp.blassa.ui.theme.BlassaAmber
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.Error
import com.tp.blassa.ui.theme.InputBackground
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EmailVerificationScreen(
        email: String,
        onNavigateToLogin: () -> Unit,
        onVerificationComplete: () -> Unit
) {
        var isLoading by remember { mutableStateOf(false) }
        var countdown by remember { mutableIntStateOf(60) }
        var apiError by remember { mutableStateOf<String?>(null) }

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(countdown) {
                if (countdown > 0) {
                        delay(1000L)
                        countdown--
                }
        }

        fun handleResend() {
                if (countdown > 0 || isLoading) return

                isLoading = true
                apiError = null

                scope.launch {
                        try {
                                val response =
                                        RetrofitClient.authApiService.resendVerification(
                                                mapOf("email" to email)
                                        )

                                when (response.status) {
                                        "SUCCESS" -> {
                                                countdown = 60
                                                snackbarHostState.showSnackbar(
                                                        message = "Email de vérification envoyé !",
                                                        duration = SnackbarDuration.Short
                                                )
                                        }
                                        else -> {
                                                apiError = response.message ?: "Échec de l'envoi"
                                                snackbarHostState.showSnackbar(
                                                        message = response.message
                                                                        ?: "Échec de l'envoi",
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
                                apiError = "Erreur: ${e.message}"
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
                                                                        size.width * 0.1f,
                                                                        size.height * 0.1f
                                                                ),
                                                        radius = size.width * 0.6f
                                                ),
                                        center = Offset(size.width * 0.1f, size.height * 0.1f),
                                        radius = size.width * 0.5f
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
                                                                        size.width * 0.9f,
                                                                        size.height * 0.9f
                                                                ),
                                                        radius = size.width * 0.6f
                                                ),
                                        center = Offset(size.width * 0.9f, size.height * 0.9f),
                                        radius = size.width * 0.5f
                                )
                        }

                        Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                        ) {
                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color.White
                                                ),
                                        elevation =
                                                CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                        Column(
                                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                                Image(
                                                        painter =
                                                                painterResource(
                                                                        id = R.drawable.logo
                                                                ),
                                                        contentDescription = "Blassa Logo",
                                                        modifier = Modifier.size(72.dp)
                                                )

                                                Spacer(modifier = Modifier.height(24.dp))

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
                                                                                shape = CircleShape
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Icon(
                                                                imageVector = Icons.Default.Email,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(40.dp),
                                                                tint = BlassaTeal
                                                        )
                                                }

                                                Spacer(modifier = Modifier.height(24.dp))

                                                Text(
                                                        text = "Vérifiez votre email",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                )

                                                Spacer(modifier = Modifier.height(12.dp))

                                                Text(
                                                        text =
                                                                "Nous avons envoyé un lien de vérification à :",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = TextSecondary,
                                                        textAlign = TextAlign.Center
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text(
                                                        text = email,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = TextPrimary
                                                )

                                                Spacer(modifier = Modifier.height(32.dp))

                                                Button(
                                                        onClick = { handleResend() },
                                                        enabled = countdown == 0 && !isLoading,
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(48.dp),
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor = BlassaTeal,
                                                                        disabledContainerColor =
                                                                                BlassaTeal.copy(
                                                                                        alpha = 0.5f
                                                                                )
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
                                                                Text("Envoi en cours...")
                                                        } else if (countdown > 0) {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default
                                                                                        .Refresh,
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
                                                                                "Renvoyer dans ${countdown}s",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        fontWeight =
                                                                                FontWeight.Medium,
                                                                        color = Color.White
                                                                )
                                                        } else {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default
                                                                                        .Refresh,
                                                                        contentDescription = null,
                                                                        modifier =
                                                                                Modifier.size(20.dp)
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.width(8.dp)
                                                                )
                                                                Text(
                                                                        text = "Renvoyer l'email",
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

                                                Spacer(modifier = Modifier.height(16.dp))

                                                OutlinedButton(
                                                        onClick = onNavigateToLogin,
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(48.dp),
                                                        shape = RoundedCornerShape(12.dp),
                                                        colors =
                                                                ButtonDefaults.outlinedButtonColors(
                                                                        contentColor = TextSecondary
                                                                )
                                                ) {
                                                        Icon(
                                                                imageVector =
                                                                        Icons.AutoMirrored.Filled
                                                                                .ArrowBack,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                                text = "Retour à la connexion",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelLarge,
                                                                fontWeight = FontWeight.Medium
                                                        )
                                                }

                                                Spacer(modifier = Modifier.height(24.dp))

                                                Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(12.dp),
                                                        colors =
                                                                CardDefaults.cardColors(
                                                                        containerColor =
                                                                                InputBackground
                                                                )
                                                ) {
                                                        Text(
                                                                text =
                                                                        "Vous n'avez pas reçu l'email ? Vérifiez votre dossier spam ou cliquez sur le bouton ci-dessus pour renvoyer.",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color = TextSecondary,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier.padding(16.dp)
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}
