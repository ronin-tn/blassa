package isimm.ing1.mobile.presentation.screens.login

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
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
fun LoginScreen(
    paddingValues: PaddingValues,
    viewModel: LoginViewModel? = null,
    onLoginSuccess: (email: String) -> Unit = {},
    onGoogleSignInClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    // Use ViewModel state if provided, otherwise use local state for preview
    var email by remember { mutableStateOf(viewModel?.uiState?.email ?: "") }
    var password by remember { mutableStateOf(viewModel?.uiState?.password ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }
    val emailError = viewModel?.uiState?.emailError ?: ""
    val passwordError = viewModel?.uiState?.passwordError ?: ""

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Logo
            Text(
                text = "Blassa",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Méditerranée en mouvement",
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Email Field
            Text(
                text = "Email",
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    viewModel?.updateEmail(it)
                },
                placeholder = { Text("exemple@email.com", color = TextMuted) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedBorderColor = TextFieldBorder,
                    focusedBorderColor = PrimaryTeal,
                    errorBorderColor = Color(0xFFEF4444)
                ),
                isError = emailError.isNotEmpty(),
                supportingText = if (emailError.isNotEmpty()) {
                    { Text(emailError, color = Color(0xFFEF4444)) }
                } else null,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            Text(
                text = "Mot de passe",
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    viewModel?.updatePassword(it)
                },
                placeholder = { Text("••••••••", color = TextMuted) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.visibility_24px)
                    else
                        painterResource(id = R.drawable.visibility_off_24px)

                    Icon(
                        painter = image,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible },
                        tint = TextMuted
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedBorderColor = TextFieldBorder,
                    focusedBorderColor = PrimaryTeal,
                    errorBorderColor = Color(0xFFEF4444)
                ),
                isError = passwordError.isNotEmpty(),
                supportingText = if (passwordError.isNotEmpty()) {
                    { Text(passwordError, color = Color(0xFFEF4444)) }
                } else null,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password Link
            Text(
                text = "Mot de passe oublié ?",
                color = PrimaryTeal,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onForgotPasswordClick() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button
            Button(
                onClick = {
                    if (viewModel != null) {
                        viewModel.validateAndLogin { successEmail ->
                            onLoginSuccess(successEmail)
                        }
                    } else {
                        // Preview mode: just call success
                        onLoginSuccess(email)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ActionAmber,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Se connecter",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider with "ou"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = TextFieldBorder
                )
                Text(
                    text = "ou",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = TextMuted,
                    fontSize = 14.sp
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = TextFieldBorder
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google Sign-In Button
            OutlinedButton(
                onClick = { onGoogleSignInClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, TextFieldBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Continuer avec Google",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign Up Link
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Pas encore membre ? ",
                    color = TextMuted,
                    fontSize = 14.sp
                )
                Text(
                    text = "Inscrivez-vous!",
                    color = PrimaryTeal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MobileTheme {
        LoginScreen(paddingValues = PaddingValues(0.dp))
    }
}
