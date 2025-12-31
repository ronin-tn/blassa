package com.tp.blassa.features.rides.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@Composable
fun SuccessScreen(onViewRide: () -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier) {
        Box(
                modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
        ) {
                Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Box(
                                modifier =
                                        Modifier.size(96.dp)
                                                .background(
                                                        BlassaTeal.copy(alpha = 0.1f),
                                                        CircleShape
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = BlassaTeal,
                                        modifier = Modifier.size(56.dp)
                                )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                                text = "Trajet publié !",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text =
                                        "Votre trajet a été publié avec succès.\nLes passagers peuvent maintenant le réserver.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                                onClick = onViewRide,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BlassaTeal)
                        ) { Text("Voir le trajet", fontWeight = FontWeight.Medium) }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                contentColor = BlassaTeal
                                        )
                        ) { Text("Retour à l'accueil", fontWeight = FontWeight.Medium) }
                }
        }
}
