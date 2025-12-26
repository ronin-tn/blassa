package com.example.blassa.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blassa.R
import com.example.blassa.ui.theme.BlassaYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    onBack: () -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    var rating by remember { mutableStateOf(4) }
    var reviewText by remember { mutableStateOf("") }
    
    // Tag selection state could be implemented here
    val tags = listOf("Conduite agréable", "Véhicule propre", "Bonne conversation")
    // Simple set for demonstration
    val selectedTags = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F0E9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable(onClick = onBack),
                tint = Color(0xFF1E1E1E)
            )
            Text(
                text = "Évaluer votre trajet",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1E1E1E)
            )
            Text(
                text = "Passer",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Gray, // Or a secondary color
                modifier = Modifier.clickable { /* Todo */ }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Driver Profile
        Box(contentAlignment = Alignment.BottomEnd) {
             Image(
                painter = painterResource(id = R.drawable.avatar_driver_review),
                contentDescription = "Driver",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.White, CircleShape)
            )
            // Verified Badge
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Verified",
                tint = BlassaYellow,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Karim Benali",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E)
        )
        Text(
            text = "CHAUFFEUR",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Car Info Pill
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF3F4F6), // Light grey
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                 // Simple text for now, could actally allow icon
                 Icon(painterResource(id = android.R.drawable.ic_menu_directions), contentDescription=null, modifier=Modifier.size(16.dp), tint=Color.Gray)
                 Spacer(modifier = Modifier.width(8.dp))
                 Text("Hyundai Accent • 123 TU 4567", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Comment s'est passé votre trajet ?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Votre avis aide à améliorer la communauté Blassa.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Stars
        Row {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < rating) BlassaYellow else Color.LightGray,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .clickable { rating = index + 1 }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Comment Input
        OutlinedTextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFF9F9F9),
                unfocusedBorderColor = Color.LightGray.copy(alpha=0.3f),
                focusedBorderColor = BlassaYellow
            ),
            placeholder = { 
                Text(
                    "Partagez votre expérience... Était-ce une conduite fluide ? Le véhicule était-il propre ?",
                    color = Color.Gray,
                    fontSize = 14.sp
                ) 
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tags
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
             // Just displaying tags horizontally or wrapped flow row, using simple row for mockup
             tags.forEach { tag ->
                 SuggestionChip(
                     onClick = { 
                         if(selectedTags.contains(tag)) selectedTags.remove(tag) else selectedTags.add(tag)
                     },
                     label = { Text(tag, fontSize = 10.sp) },
                     shape = RoundedCornerShape(16.dp),
                     colors = SuggestionChipDefaults.suggestionChipColors(
                         containerColor = if(selectedTags.contains(tag)) Color.White else Color.White,
                         labelColor = Color(0xFF1E1E1E)
                     ),
                     border = SuggestionChipDefaults.suggestionChipBorder(
                         borderColor = if(selectedTags.contains(tag)) BlassaYellow else Color.LightGray.copy(alpha=0.3f)
                     ),
                     modifier = Modifier.padding(horizontal = 4.dp).height(28.dp)
                 )
             }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onSubmit,
            colors = ButtonDefaults.buttonColors(containerColor = BlassaYellow),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
             Text("Envoyer l'avis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
             Spacer(modifier = Modifier.width(8.dp))
             Icon(Icons.Filled.Send, contentDescription = null, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ReviewScreenPreview() {
    com.example.blassa.ui.theme.BlassaTheme {
        ReviewScreen()
    }
}
