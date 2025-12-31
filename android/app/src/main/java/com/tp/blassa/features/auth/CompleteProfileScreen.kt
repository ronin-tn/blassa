package com.tp.blassa.features.auth

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tp.blassa.features.auth.viewmodel.CompleteProfileViewModel
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
        onProfileCompleted: () -> Unit,
        viewModel: CompleteProfileViewModel = viewModel()
) {
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current

        val calendar = Calendar.getInstance()
        val datePickerDialog =
                DatePickerDialog(
                        context,
                        { _, year, month, day ->
                                val formattedDate =
                                        String.format("%04d-%02d-%02d", year, month + 1, day)
                                viewModel.updateDateOfBirth(formattedDate)
                        },
                        calendar.get(Calendar.YEAR) - 18,
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) {
                        onProfileCompleted()
                }
        }

        Scaffold(containerColor = Color.White) { padding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(padding)
                                        .padding(24.dp)
                                        .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        Surface(
                                shape = RoundedCornerShape(50),
                                color = BlassaTeal.copy(alpha = 0.1f),
                                modifier = Modifier.size(80.dp)
                        ) {
                                Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = BlassaTeal,
                                                modifier = Modifier.size(40.dp)
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                                text = "Complétez votre profil",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                        )

                        Text(
                                text =
                                        "Ces informations sont nécessaires pour utiliser Blassa et assurer la confiance.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                        )

                        OutlinedTextField(
                                value = uiState.phoneNumber,
                                onValueChange = { viewModel.updatePhoneNumber(it) },
                                label = { Text("Numéro de téléphone") },
                                leadingIcon = {
                                        Icon(Icons.Default.Phone, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isError = uiState.fieldErrors.containsKey("phoneNumber"),
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = BlassaTeal,
                                                cursorColor = BlassaTeal,
                                                focusedLabelColor = BlassaTeal
                                        )
                        )
                        if (uiState.fieldErrors.containsKey("phoneNumber")) {
                                Text(
                                        text = uiState.fieldErrors["phoneNumber"]!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier =
                                                Modifier.align(Alignment.Start)
                                                        .padding(start = 8.dp, top = 4.dp)
                                )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                                value = uiState.dateOfBirth,
                                onValueChange = {},
                                label = { Text("Date de naissance") },
                                leadingIcon = {
                                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                                },
                                modifier =
                                        Modifier.fillMaxWidth().clickable {
                                                datePickerDialog.show()
                                        },
                                enabled = false,
                                isError = uiState.fieldErrors.containsKey("dateOfBirth"),
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                disabledTextColor = Color.Black,
                                                disabledBorderColor =
                                                        if (uiState.fieldErrors.containsKey(
                                                                        "dateOfBirth"
                                                                )
                                                        )
                                                                MaterialTheme.colorScheme.error
                                                        else Color.Gray,
                                                disabledLabelColor = Color.Gray,
                                                disabledLeadingIconColor = Color.Gray
                                        ),
                                shape = RoundedCornerShape(12.dp)
                        )
                        if (uiState.fieldErrors.containsKey("dateOfBirth")) {
                                Text(
                                        text = uiState.fieldErrors["dateOfBirth"]!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier =
                                                Modifier.align(Alignment.Start)
                                                        .padding(start = 8.dp, top = 4.dp)
                                )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                text = "Genre",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
                        )
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                FilterChip(
                                        selected = uiState.gender == "MALE",
                                        onClick = { viewModel.updateGender("MALE") },
                                        label = { Text("Homme") },
                                        colors =
                                                FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor =
                                                                BlassaTeal.copy(alpha = 0.2f),
                                                        selectedLabelColor = BlassaTeal
                                                ),
                                        modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                        selected = uiState.gender == "FEMALE",
                                        onClick = { viewModel.updateGender("FEMALE") },
                                        label = { Text("Femme") },
                                        colors =
                                                FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor =
                                                                BlassaTeal.copy(alpha = 0.2f),
                                                        selectedLabelColor = BlassaTeal
                                                ),
                                        modifier = Modifier.weight(1f)
                                )
                        }
                        if (uiState.fieldErrors.containsKey("gender")) {
                                Text(
                                        text = uiState.fieldErrors["gender"]!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier =
                                                Modifier.align(Alignment.Start)
                                                        .padding(start = 8.dp, top = 4.dp)
                                )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                                onClick = { viewModel.submitProfile() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !uiState.isLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = BlassaTeal)
                        ) {
                                if (uiState.isLoading) {
                                        CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(24.dp)
                                        )
                                } else {
                                        Text(
                                                "Continuer",
                                                fontSize =
                                                        androidx.compose.ui.unit.TextUnit
                                                                .Unspecified
                                        )
                                }
                        }

                        if (uiState.error != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                        text = uiState.error!!,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                )
                        }
                }
        }
}
