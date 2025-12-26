package com.example.blassa.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blassa.ui.theme.BlassaYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var promoCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) } // Default to true based on screenshot

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F0E9)) // Beige/Cream background
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onBack),
                tint = Color(0xFF1E1E1E)
            )
            Text(
                text = "Checkout",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E1E)
            )
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Options",
                tint = Color(0xFF1E1E1E)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // TRIP SUMMARY
            SectionTitle("TRIP SUMMARY")
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF3E0), // Light Orange
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.DirectionsBoat, // Replaced with valid core icon if available or generic
                                    contentDescription = null,
                                    tint = Color(0xFFEF6C00)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Tunis to Marseille",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1E1E),
                                fontSize = 16.sp
                            )
                            Text(
                                "Wed, 12 Oct • 10:00 AM",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Divider(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF5F5F5),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF1E1E1E),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "1 Passenger",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E1E),
                                    fontSize = 14.sp
                                )
                                Text(
                                    "Economy Class",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Text(
                            "$450.00",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1E1E),
                            fontSize = 16.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // PAYMENT METHOD
            SectionTitle("PAYMENT METHOD")
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Fake Visa Badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF1A237E), // Dark Blue
                            modifier = Modifier.size(width = 40.dp, height = 24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "VISA",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "•••• 4242",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1E1E),
                                fontSize = 14.sp
                            )
                            Text(
                                "Expires 12/25",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Text(
                        "Change",
                        color = BlassaYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { /* Todo */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PROMO CODE
            SectionTitle("PROMO CODE")
            OutlinedTextField(
                value = promoCode,
                onValueChange = { promoCode = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Enter code", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = BlassaYellow
                ),
                trailingIcon = {
                    Text(
                        "Apply",
                        color = BlassaYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable { /* Todo */ }
                            .padding(end = 16.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            // Totals
            PricingRow("Subtotal", "$450.00")
            PricingRow("Taxes & Fees", "$12.50")
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E1E1E)
                )
                Text(
                    "$462.50",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E1E1E)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom Button
        Button(
            onClick = { isLoading = !isLoading },
            colors = ButtonDefaults.buttonColors(containerColor = BlassaYellow),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Confirm Payment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Please wait while we secure your booking...",
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.Gray, // A bit generic "Goldish/Brown" in screenshot looks like #8E8B82 or just Gray
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun PricingRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = Color.Gray, fontSize = 14.sp)
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    com.example.blassa.ui.theme.BlassaTheme {
        CheckoutScreen()
    }
}
