package com.tp.blassa.features.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tp.blassa.core.auth.TokenManager
import com.tp.blassa.ui.theme.BlassaAmber
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.BlassaTealDark
import kotlinx.coroutines.launch

data class OnboardingPage(
        val title: String,
        val description: String,
        val icon: ImageVector,
        val backgroundColor: Color = Color(0xFF0F172A)
)

val onboardingPages =
        listOf(
                OnboardingPage(
                        title = "Trouvez votre trajet",
                        description =
                                "Recherchez parmi des milliers de trajets vers votre destination.",
                        icon = Icons.Default.LocationOn
                ),
                OnboardingPage(
                        title = "Proposez un trajet",
                        description = "Partagez vos frais et réduisez votre empreinte carbone.",
                        icon = Icons.Default.DirectionsCar
                ),
                OnboardingPage(
                        title = "Voyagez en toute sécurité",
                        description = "Profils vérifiés et options pour les femmes.",
                        icon = Icons.Default.VerifiedUser
                )
        )

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
        val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
        val scope = rememberCoroutineScope()
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) {
                if (isLandscape) {
                        // Landscape Layout: Side-by-side content and controls
                        Row(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .padding(horizontal = 24.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                // Left Side: Pager Content
                                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                        HorizontalPager(
                                                state = pagerState,
                                                modifier = Modifier.fillMaxSize()
                                        ) { pageIndex ->
                                                OnboardingPageContentLandscape(
                                                        page = onboardingPages[pageIndex]
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.width(24.dp))

                                // Right Side: Controls
                                Column(
                                        modifier = Modifier.weight(0.6f).fillMaxHeight(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                ) {
                                        // Skip Button
                                        if (pagerState.currentPage < onboardingPages.size - 1) {
                                                TextButton(onClick = onFinish) {
                                                        Text(
                                                                text = "Passer",
                                                                color =
                                                                        Color.White.copy(
                                                                                alpha = 0.7f
                                                                        ),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelLarge
                                                        )
                                                }
                                                Spacer(modifier = Modifier.height(16.dp))
                                        }

                                        // Pager Indicators
                                        Row(
                                                horizontalArrangement = Arrangement.Center,
                                                modifier = Modifier.padding(bottom = 24.dp)
                                        ) {
                                                repeat(onboardingPages.size) { iteration ->
                                                        val color =
                                                                if (pagerState.currentPage ==
                                                                                iteration
                                                                )
                                                                        BlassaAmber
                                                                else Color.Gray
                                                        Box(
                                                                modifier =
                                                                        Modifier.padding(4.dp)
                                                                                .clip(CircleShape)
                                                                                .background(color)
                                                                                .size(
                                                                                        if (pagerState
                                                                                                        .currentPage ==
                                                                                                        iteration
                                                                                        )
                                                                                                12.dp
                                                                                        else 8.dp
                                                                                )
                                                        )
                                                }
                                        }

                                        // Action Button
                                        if (pagerState.currentPage == onboardingPages.size - 1) {
                                                Button(
                                                        onClick = onFinish,
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(56.dp),
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor =
                                                                                BlassaAmber,
                                                                        contentColor = Color.White
                                                                ),
                                                        shape = RoundedCornerShape(16.dp)
                                                ) {
                                                        Text(
                                                                text = "Commencer",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium
                                                        )
                                                }
                                        } else {
                                                Button(
                                                        onClick = {
                                                                scope.launch {
                                                                        pagerState
                                                                                .animateScrollToPage(
                                                                                        pagerState
                                                                                                .currentPage +
                                                                                                1
                                                                                )
                                                                }
                                                        },
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(56.dp),
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor = BlassaTeal,
                                                                        contentColor = Color.White
                                                                ),
                                                        shape = RoundedCornerShape(16.dp)
                                                ) {
                                                        Row(
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically
                                                        ) {
                                                                Text(
                                                                        text = "Suivant",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleMedium
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.size(8.dp)
                                                                )
                                                                Icon(
                                                                        Icons.Default.ArrowForward,
                                                                        contentDescription = null
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                } else {
                        // Portrait Layout (Original)
                        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) {
                                pageIndex ->
                                OnboardingPageContentPortrait(page = onboardingPages[pageIndex])
                        }

                        // Bottom Navigation Section
                        Column(
                                modifier =
                                        Modifier.align(Alignment.BottomCenter)
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                // Pager Indicators
                                Row(
                                        Modifier.height(50.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                ) {
                                        repeat(onboardingPages.size) { iteration ->
                                                val color =
                                                        if (pagerState.currentPage == iteration)
                                                                BlassaAmber
                                                        else Color.Gray
                                                Box(
                                                        modifier =
                                                                Modifier.padding(4.dp)
                                                                        .clip(CircleShape)
                                                                        .background(color)
                                                                        .size(
                                                                                if (pagerState
                                                                                                .currentPage ==
                                                                                                iteration
                                                                                )
                                                                                        12.dp
                                                                                else 8.dp
                                                                        )
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Buttons
                                if (pagerState.currentPage == onboardingPages.size - 1) {
                                        Button(
                                                onClick = {
                                                        TokenManager.setOnboardingCompleted(true)
                                                        onFinish()
                                                },
                                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = BlassaAmber,
                                                                contentColor = Color.White
                                                        ),
                                                shape = RoundedCornerShape(16.dp)
                                        ) {
                                                Text(
                                                        text = "Commencer",
                                                        style = MaterialTheme.typography.titleMedium
                                                )
                                        }
                                } else {
                                        Button(
                                                onClick = {
                                                        scope.launch {
                                                                pagerState.animateScrollToPage(
                                                                        pagerState.currentPage + 1
                                                                )
                                                        }
                                                },
                                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = BlassaTeal,
                                                                contentColor = Color.White
                                                        ),
                                                shape = RoundedCornerShape(16.dp)
                                        ) {
                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Text(
                                                                text = "Suivant",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium
                                                        )
                                                        Spacer(modifier = Modifier.size(8.dp))
                                                        Icon(
                                                                Icons.Default.ArrowForward,
                                                                contentDescription = null
                                                        )
                                                }
                                        }
                                }
                        }

                        // "Passer" Button (Top Right)
                        if (pagerState.currentPage < onboardingPages.size - 1) {
                                TextButton(
                                        onClick = onFinish,
                                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                                ) {
                                        Text(
                                                text = "Passer",
                                                color = Color.White.copy(alpha = 0.7f),
                                                style = MaterialTheme.typography.labelLarge
                                        )
                                }
                        }
                }
        }
}

@Composable
fun OnboardingPageContentPortrait(page: OnboardingPage) {
        Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                // Illustration Background
                Box(
                        modifier = Modifier.size(300.dp).padding(bottom = 32.dp),
                        contentAlignment = Alignment.Center
                ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                        color = BlassaTealDark.copy(alpha = 0.2f),
                                        radius = size.minDimension / 2,
                                        center = center
                                )
                        }

                        Box(
                                modifier =
                                        Modifier.size(200.dp)
                                                .clip(RoundedCornerShape(32.dp))
                                                .background(BlassaTeal.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = page.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = BlassaAmber
                                )
                        }
                }

                Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(120.dp))
        }
}

@Composable
fun OnboardingPageContentLandscape(page: OnboardingPage) {
        Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
        ) {
                // Icon/Illustration
                Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                        color = BlassaTealDark.copy(alpha = 0.2f),
                                        radius = size.minDimension / 2,
                                        center = center
                                )
                        }

                        Box(
                                modifier =
                                        Modifier.size(100.dp)
                                                .clip(RoundedCornerShape(24.dp))
                                                .background(BlassaTeal.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = page.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp),
                                        tint = BlassaAmber
                                )
                        }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Text Content
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                        Text(
                                text = page.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = page.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Start
                        )
                }
        }
}
