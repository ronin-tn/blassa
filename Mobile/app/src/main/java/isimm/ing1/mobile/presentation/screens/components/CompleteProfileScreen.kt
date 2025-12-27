package isimm.ing1.mobile.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import isimm.ing1.mobile.ui.theme.BlassaYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen() {
    // Mimicking a Modal Bottom Sheet look with a darkened background and a white surface
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3E50)) // Dark background from screenshot
            .padding(top = 100.dp), // Push down to simulate bottom sheet
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Handle/Grabber
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.LightGray, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Complétez votre profil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pour continuer, veuillez ajouter les informations suivantes.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Phone Number
                Text(
                    text = "Numéro de téléphone",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Country Code
                    Box(
                        modifier = Modifier
                            .weight(0.3f)
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, Color.LightGray.copy(alpha=0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                         Row(verticalAlignment = Alignment.CenterVertically) {
                             // Flag placeholder (using emoji or simple box)
                             Text("🇫🇷", fontSize = 18.sp)
                             Spacer(modifier = Modifier.width(4.dp))
                             Text("+33", fontWeight = FontWeight.Bold, color = Color(0xFF1E1E1E))
                         }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    // Number Input
                    OutlinedTextField(
                        value = "6 12 34 56 78",
                        onValueChange = {},
                        modifier = Modifier.weight(0.7f),
                        shape = RoundedCornerShape(12.dp),
                         colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.LightGray.copy(alpha=0.5f),
                            focusedBorderColor = BlassaYellow
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Birth Date
                Text(
                    text = "Date de naissance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("JJ/MM/AAAA", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = BlassaYellow)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.LightGray.copy(alpha=0.5f),
                        focusedBorderColor = BlassaYellow
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Gender
                Text(
                    text = "Genre",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(25.dp)) // Light grey background
                        .padding(4.dp)
                ) {
                    // Selected: Homme (Orange)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(BlassaYellow, RoundedCornerShape(25.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Homme", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    // Unselected: Femme
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Femme", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Submit Button
                 Button(
                    onClick = { /* Todo */ },
                    colors = ButtonDefaults.buttonColors(containerColor = BlassaYellow),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Enregistrer",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                     Spacer(modifier = Modifier.width(8.dp))
                     // Arrow icon if needed
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun CompleteProfileScreenPreview() {
    isimm.ing1.mobile.ui.theme.MobileTheme {
        CompleteProfileScreen()
    }
}
