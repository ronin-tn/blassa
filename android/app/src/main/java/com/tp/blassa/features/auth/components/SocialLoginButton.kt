package com.tp.blassa.features.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tp.blassa.R
import com.tp.blassa.ui.theme.TextPrimary

@Composable
fun GoogleSignInButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
) {
        OutlinedButton(
                onClick = onClick,
                modifier = modifier.fillMaxWidth().height(48.dp),
                enabled = enabled,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
        ) {
                Image(
                        painter = painterResource(id = R.drawable.googleicon),
                        contentDescription = "Google",
                        modifier = Modifier.height(20.dp).width(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                        text = "Continuer avec Google",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                )
        }
}
