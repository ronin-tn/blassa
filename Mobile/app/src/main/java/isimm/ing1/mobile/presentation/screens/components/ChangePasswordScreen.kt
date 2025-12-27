package isimm.ing1.mobile.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import isimm.ing1.mobile.ui.theme.BlassaYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit = {}
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F0E9))
            .padding(16.dp)
    ) {
        // App Bar / Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
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
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Modifier le mot de passe",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E1E)
            )
            Spacer(modifier = Modifier.weight(1f)) // Center the title
            Spacer(modifier = Modifier.size(24.dp)) // Balance the back icon
        }

        Text(
            text = "Votre nouveau mot de passe doit être différent des mots de passe précédemment utilisés.",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Current Password
        Text(
            text = "Mot de passe actuel",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        PasswordTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            placeholder = "Entrez votre mot de passe",
            isVisible = currentPasswordVisible,
            onToggleVisibility = { currentPasswordVisible = !currentPasswordVisible }
        )
        Spacer(modifier = Modifier.height(24.dp))

        // New Password
        Text(
            text = "Nouveau mot de passe",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        PasswordTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            isVisible = newPasswordVisible,
            onToggleVisibility = { newPasswordVisible = !newPasswordVisible }
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Security Strength Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Niveau de sécurité", fontSize = 12.sp, color = Color(0xFF1E1E1E), fontWeight = FontWeight.Bold)
            Text("Moyen", fontSize = 12.sp, color = BlassaYellow, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(BlassaYellow, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(BlassaYellow, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Requirements
        PasswordRequirement(text = "Au moins 8 caractères", isMet = true)
        PasswordRequirement(text = "1 lettre majuscule", isMet = true)
        PasswordRequirement(text = "1 chiffre", isMet = false)
        PasswordRequirement(text = "1 caractère spécial", isMet = false)

        Spacer(modifier = Modifier.height(24.dp))

        // Confirm Password
        Text(
            text = "Confirmer le mot de passe",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        PasswordTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Répétez le mot de passe",
            isVisible = confirmPasswordVisible,
            onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { /* Todo */ },
            colors = ButtonDefaults.buttonColors(containerColor = BlassaYellow),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Mettre à jour",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "•••••••••",
    isVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = BlassaYellow
        ),
        placeholder = { Text(placeholder, color = Color.LightGray) },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                // Using Info as a safe fallback for Visibility icons
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = if (isVisible) "Hide password" else "Show password",
                    tint = Color.Gray
                )
            }
        },
        singleLine = true
    )
}

@Composable
fun PasswordRequirement(text: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
         // Using a simple box/icon for check/circle
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
             if (isMet) {
                 Icon(
                     imageVector = Icons.Filled.CheckCircle,
                     contentDescription = null,
                     tint = BlassaYellow,
                     modifier = Modifier.size(16.dp)
                 )
             } else {
                 Box(
                     modifier = Modifier
                         .size(16.dp)
                         .border(1.dp, Color.LightGray, CircleShape)
                 )
             }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = if (isMet) Color(0xFF1E1E1E) else Color.Gray, fontSize = 14.sp)
    }
}

// Fix import for CheckCircle which I used above speculatively
// Adding import at top: import androidx.compose.material.icons.filled.CheckCircle

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    isimm.ing1.mobile.ui.theme.MobileTheme {
        ChangePasswordScreen()
    }
}
