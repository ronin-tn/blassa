package com.tp.blassa.features.rides.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tp.blassa.ui.theme.BlassaTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDialog(
        isOpen: Boolean,
        onDismiss: () -> Unit,
        onSubmit: (reason: String, description: String) -> Unit,
        isLoading: Boolean = false
) {
    if (!isOpen) return

    val reasons =
            listOf(
                    "Comportement inapproprié",
                    "Conduite dangereuse",
                    "Absence au rendez-vous",
                    "Harcèlement",
                    "Fraude / Arnaque",
                    "Autre"
            )

    var expanded by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
            onDismissRequest = { if (!isLoading) onDismiss() },
            title = { Text(text = "Signaler", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                            text = "Votre signalement sera traité de manière confidentielle.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                    )

                    // Reason dropdown
                    ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                                value = selectedReason,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Raison") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                        ) {
                            reasons.forEach { reason ->
                                DropdownMenuItem(
                                        text = { Text(reason) },
                                        onClick = {
                                            selectedReason = reason
                                            expanded = false
                                            error = null
                                        }
                                )
                            }
                        }
                    }

                    // Description field
                    OutlinedTextField(
                            value = description,
                            onValueChange = {
                                description = it
                                error = null
                            },
                            label = { Text("Description") },
                            placeholder = { Text("Décrivez le problème en détail...") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 5
                    )

                    if (error != null) {
                        Text(
                                text = error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                        onClick = {
                            if (selectedReason.isBlank() || description.isBlank()) {
                                error = "Veuillez remplir tous les champs"
                            } else {
                                onSubmit(selectedReason, description)
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = BlassaTeal)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                        )
                    } else {
                        Text("Signaler")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Annuler") }
            },
            shape = RoundedCornerShape(16.dp)
    )
}
