package isimm.ing1.mobile.presentation.screens.password

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import isimm.ing1.mobile.R
import isimm.ing1.mobile.ui.theme.ActionAmber
import isimm.ing1.mobile.ui.theme.MobileTheme
import isimm.ing1.mobile.ui.theme.PrimaryTeal
import isimm.ing1.mobile.ui.theme.TextFieldBackground
import isimm.ing1.mobile.ui.theme.TextFieldBorder
import isimm.ing1.mobile.ui.theme.TextMuted
import isimm.ing1.mobile.ui.theme.TextPrimary

@Composable
fun ResetPasswordScreen(
    paddingValues: PaddingValues,
    viewModel: ResetPasswordViewModel? = null,
    onResetSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var newPassword by remember { mutableStateOf(viewModel?.uiState?.newPassword ?: "") }
    var confirmPassword by remember { mutableStateOf(viewModel?.uiState?.confirmPassword ?: "") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val passwordStrength = when {
        newPassword.length < 4 -> 1
        newPassword.length < 8 -> 2
        else -> 3
    }
    val strengthColor = when (passwordStrength) {
        1 -> Color(0xFFEF4444)
        2 -> ActionAmber
        else -> Color(0xFF10B981)
    }
    val strengthText = when (passwordStrength) {
        1 -> "Faible"
        2 -> "Moyen"
        else -> "Fort"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFEF3C7).copy(alpha = 0.3f),
                        Color(0xFFCFFAFE).copy(alpha = 0.2f),
                        Color(0xFFF8FAFC)
                    )
                )
            )
            .padding(paddingValues)
    ) {
        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = TextPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp)
        ) {
            // Title
            Text(
                text = "Nouveau mot de passe",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Créez un nouveau mot de passe sécurisé.",
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(32.dp))

            // New Password Field
            Text(
                text = "Nouveau mot de passe",
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = { Text("••••••••", color = TextMuted) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (newPasswordVisible)
                        painterResource(id = R.drawable.visibility_24px)
                    else
                        painterResource(id = R.drawable.visibility_off_24px)

                    Icon(
                        painter = image,
                        contentDescription = if (newPasswordVisible) "Hide password" else "Show password",
                        modifier = Modifier.clickable { newPasswordVisible = !newPasswordVisible },
                        tint = TextMuted
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedBorderColor = TextFieldBorder,
                    focusedBorderColor = PrimaryTeal
                ),
                singleLine = true
            )

            // Password Strength Indicator
            if (newPassword.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                // Strength Label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Force du mot de passe",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "Moyen",
                        fontSize = 12.sp,
                        color = ActionAmber,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 4 level Strength Bar (no logic - just display)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(
                                    if (index < 2) ActionAmber else TextFieldBorder,
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Le mot de passe doit contenir au moins 8 caractères.",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm Password Field
            Text(
                text = "Confirmer le mot de passe",
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("••••••••", color = TextMuted) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible)
                        painterResource(id = R.drawable.visibility_24px)
                    else
                        painterResource(id = R.drawable.visibility_off_24px)

                    Icon(
                        painter = image,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                        modifier = Modifier.clickable { confirmPasswordVisible = !confirmPasswordVisible },
                        tint = TextMuted
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedBorderColor = TextFieldBorder,
                    focusedBorderColor = PrimaryTeal
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            // Reset Button
            Button(
                onClick = { 
                    viewModel?.validateAndReset { onResetSuccess() } ?: onResetSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ActionAmber,
                    contentColor = Color.White
                ),
                enabled = newPassword.isNotBlank() && newPassword == confirmPassword && newPassword.length >= 8
            ) {
                Text(
                    text = "Réinitialiser le mot de passe",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPasswordScreenPreview() {
    MobileTheme {
        ResetPasswordScreen(paddingValues = PaddingValues(0.dp))
    }
}
