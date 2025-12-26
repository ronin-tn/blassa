package com.example.blassa.onboarding

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.blassa.ui.theme.TextGrey
import com.example.blassa.R
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border

@Composable
fun OnboardingScreenCore(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image Container
        Box(
            modifier = Modifier
                .size(320.dp) // Approximate size from screenshot
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF1E2D33)) // Slightly lighter than bg
        ) {
             Image(
                painter = painterResource(id = page.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Overlays for specific pages
            if (page.title == R.string.onboarding_title_3) { // Screen 3 Check
                // 1. Verified Badge (Top Left)
                Box(
                    modifier = Modifier
                        .padding(top = 24.dp, start = 24.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f)) // Glassmorphism-ish
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Icon(
                             imageVector = Icons.Filled.CheckCircle,
                             contentDescription = null,
                             tint = Color(0xFF10B981), // Success Green
                             modifier = Modifier.size(16.dp)
                         )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Vérifié",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 2. Women Option Badge (Center Right / Bottom Right)
                Box(
                    modifier = Modifier
                        .padding(bottom = 60.dp, end = 0.dp) // Adjusted visual placement
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        .background(Color(0xFF1E293B)) // Dark Navy
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Female Symbol Circle
                        Box(
                             modifier = Modifier
                                 .size(32.dp)
                                 .clip(CircleShape)
                                 .background(Color(0xFF502E4E)), // Muted Purple/Pink bg
                             contentAlignment = Alignment.Center
                        ) {
                            Text(text = "♀", color = Color(0xFFF472B6), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        
                        Spacer(modifier = Modifier.size(12.dp))
                        
                        Column {
                            Text(
                                text = "OPTION",
                                color = TextGrey,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Femmes",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = stringResource(id = page.title),
            style = MaterialTheme.typography.displayMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = stringResource(id = page.description),
            style = MaterialTheme.typography.bodyLarge,
            color = TextGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Spacer to lift the content up, clearing space for bottom indicators
        Spacer(modifier = Modifier.height(100.dp))
    }
}
