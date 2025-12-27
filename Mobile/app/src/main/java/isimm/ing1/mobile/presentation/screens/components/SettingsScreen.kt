package isimm.ing1.mobile.presentation.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import isimm.ing1.mobile.R
import isimm.ing1.mobile.ui.theme.BlassaYellow

@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F0E9)) // Beige/Cream background
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Paramètres",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E1E)
            )
            
            Image(
                painter = painterResource(id = R.drawable.avatar_user_settings),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        }

        // Section: COMPTE
        SettingsSectionTitle("COMPTE")
        SettingsCard {
            SettingsItem(
                icon = Icons.Filled.Lock,
                title = "Modifier le mot de passe",
                onClick = { /* Todo */ }
            )
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            SettingsToggleItem(
                icon = Icons.Filled.Notifications,
                title = "Notifications / Email",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }

        // Section: APPARENCE
        SettingsSectionTitle("APPARENCE")
        SettingsCard {
            SettingsToggleItem(
                icon = Icons.Filled.Settings,
                title = "Mode Sombre",
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it },
                checkedTrackColor = Color.LightGray.copy(alpha=0.5f), 
                checkedThumbColor = Color.White
            )
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            SettingsItem(
                icon = Icons.Filled.Info,
                title = "Langue",
                value = "Français",
                onClick = { /* Todo */ }
            )
        }

        // Section: SUPPORT
        SettingsSectionTitle("SUPPORT")
        SettingsCard {
            SettingsItem(
                icon = Icons.Filled.Info,
                title = "FAQ / Aide",
                onClick = { /* Todo */ }
            )
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            SettingsItem(
                icon = Icons.Filled.Email,
                title = "Nous contacter",
                onClick = { /* Todo */ }
            )
        }

        // Section: LÉGAL
        SettingsSectionTitle("LÉGAL")
        SettingsCard {
            SettingsItem(
                icon = Icons.Filled.Info,
                title = "Conditions d'utilisation",
                trailingIcon = Icons.Filled.ArrowForward,
                onClick = { /* Todo */ }
            )
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            SettingsItem(
                icon = Icons.Filled.Lock,
                title = "Politique de confidentialité",
                trailingIcon = Icons.Filled.ArrowForward,
                onClick = { /* Todo */ }
            )
        }

        // Section: ZONE DE DANGER
        SettingsSectionTitle("ZONE DE DANGER")
        Button(
            onClick = { /* Todo */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp), 
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).height(50.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
             Text(
                 text = "Déconnexion",
                 color = Color(0xFFEF4444),
                 fontWeight = FontWeight.Bold
             )
        }
        
        Button(
            onClick = { /* Todo */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F0E9)), // Transparent-ish
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
             Text(
                 text = "Supprimer mon compte",
                 color = Color(0xFFEF4444),
                 fontWeight = FontWeight.Bold
             )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Blassa v2.4.1 (Build 2023)",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 12.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- Helper Composables ---

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        color = Color.Gray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    value: String? = null,
    trailingIcon: ImageVector = Icons.Filled.ArrowForward,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            //.clickable(onClick = onClick) // Removed clickable for visual only focus to avoid ripple issues in preview
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            color = Color(0xFF1E1E1E),
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkedTrackColor: Color = BlassaYellow,
    checkedThumbColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            color = Color(0xFF1E1E1E),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = checkedThumbColor,
                checkedTrackColor = checkedTrackColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.3f),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    isimm.ing1.mobile.ui.theme.MobileTheme {
        SettingsScreen()
    }
}
