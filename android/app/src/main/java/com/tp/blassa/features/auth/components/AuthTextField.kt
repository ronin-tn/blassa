package com.tp.blassa.features.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.Border
import com.tp.blassa.ui.theme.Error
import com.tp.blassa.ui.theme.InputBackground
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@Composable
fun AuthTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        placeholder: String,
        modifier: Modifier = Modifier,
        leadingIcon: ImageVector? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        error: String? = null,
        keyboardType: KeyboardType = KeyboardType.Text,
        imeAction: ImeAction = ImeAction.Next,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        onImeAction: () -> Unit = {},
        singleLine: Boolean = true,
        readOnly: Boolean = false
) {
        Column(modifier = modifier.fillMaxWidth()) {
                Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(placeholder) },
                        leadingIcon =
                                leadingIcon?.let {
                                        {
                                                Icon(
                                                        imageVector = it,
                                                        contentDescription = null,
                                                        tint = TextSecondary
                                                )
                                        }
                                },
                        trailingIcon = trailingIcon,
                        isError = error != null,
                        singleLine = singleLine,
                        readOnly = readOnly,
                        visualTransformation = visualTransformation,
                        keyboardOptions =
                                KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                        keyboardActions =
                                KeyboardActions(
                                        onNext = { onImeAction() },
                                        onDone = { onImeAction() }
                                ),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BlassaTeal,
                                        unfocusedBorderColor = Border,
                                        errorBorderColor = Error,
                                        focusedContainerColor = InputBackground,
                                        unfocusedContainerColor = InputBackground
                                )
                )
                if (error != null) {
                        Text(
                                text = error,
                                color = Error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                        )
                }
        }
}

@Composable
fun PasswordVisibilityToggle(
        visible: Boolean,
        onToggle: () -> Unit,
        visibleIcon: ImageVector,
        hiddenIcon: ImageVector
) {
        IconButton(onClick = onToggle) {
                Icon(
                        imageVector = if (visible) visibleIcon else hiddenIcon,
                        contentDescription = if (visible) "Hide password" else "Show password",
                        tint = TextSecondary
                )
        }
}
