package com.tp.blassa.features.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tp.blassa.features.profile.viewmodel.EditProfileViewModel
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onBack: () -> Unit, viewModel: EditProfileViewModel = viewModel()) {
        val context = LocalContext.current
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }

        val imagePickerLauncher =
                rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? -> uri?.let { viewModel.uploadProfilePicture(context, it) } }

        val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

        LaunchedEffect(uiState.successMessage, uiState.error) {
                uiState.successMessage?.let {
                        focusManager.clearFocus() // Close keyboard and clear focus
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessages()
                }
                uiState.error?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessages()
                }
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Text("Modifier le profil", fontWeight = FontWeight.SemiBold)
                                },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
                                                Icon(
                                                        Icons.AutoMirrored.Filled.ArrowBack,
                                                        "Retour",
                                                        tint = Color.Black
                                                )
                                        }
                                },
                                actions = {
                                        if (uiState.isSaving) {
                                                CircularProgressIndicator(
                                                        modifier =
                                                                Modifier.size(24.dp)
                                                                        .padding(end = 16.dp),
                                                        strokeWidth = 2.dp,
                                                        color = BlassaTeal
                                                )
                                        } else {
                                                TextButton(onClick = { viewModel.saveProfile() }) {
                                                        Text(
                                                                "Enregistrer",
                                                                color = BlassaTeal,
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                }
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color.White
                                        )
                        )
                },
                snackbarHost = {
                        SnackbarHost(snackbarHostState) { data ->
                                Snackbar(
                                        snackbarData = data,
                                        containerColor = BlassaTeal,
                                        contentColor = Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                )
                        }
                },
                containerColor = Color(0xFFF8FAFC)
        ) { padding ->
                if (uiState.isLoading) {
                        Box(
                                modifier = Modifier.fillMaxSize().padding(padding),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = BlassaTeal) }
                } else {
                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .padding(padding)
                                                .verticalScroll(rememberScrollState())
                                                .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                Box(
                                        modifier = Modifier.size(120.dp),
                                        contentAlignment = Alignment.BottomEnd
                                ) {
                                        if (uiState.profile?.profilePictureUrl != null) {
                                                AsyncImage(
                                                        model = uiState.profile?.profilePictureUrl,
                                                        contentDescription = "Photo de profil",
                                                        modifier =
                                                                Modifier.fillMaxSize()
                                                                        .clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                )
                                        } else {
                                                Surface(
                                                        modifier = Modifier.fillMaxSize(),
                                                        shape = CircleShape,
                                                        color = BlassaTeal.copy(alpha = 0.1f)
                                                ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                                Icon(
                                                                        Icons.Default.Person,
                                                                        contentDescription = null,
                                                                        tint = BlassaTeal,
                                                                        modifier =
                                                                                Modifier.size(48.dp)
                                                                )
                                                        }
                                                }
                                        }

                                        if (uiState.isUploadingPicture) {
                                                Surface(
                                                        shape = CircleShape,
                                                        color = BlassaTeal,
                                                        modifier = Modifier.size(36.dp)
                                                ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                                CircularProgressIndicator(
                                                                        modifier =
                                                                                Modifier.size(
                                                                                        20.dp
                                                                                ),
                                                                        strokeWidth = 2.dp,
                                                                        color = Color.White
                                                                )
                                                        }
                                                }
                                        } else {
                                                Surface(
                                                        shape = CircleShape,
                                                        color = BlassaTeal,
                                                        modifier =
                                                                Modifier.size(36.dp).clickable {
                                                                        imagePickerLauncher.launch(
                                                                                "image/*"
                                                                        )
                                                                }
                                                ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                                Icon(
                                                                        Icons.Default.CameraAlt,
                                                                        contentDescription =
                                                                                "Changer photo",
                                                                        tint = Color.White,
                                                                        modifier =
                                                                                Modifier.size(20.dp)
                                                                )
                                                        }
                                                }
                                        }
                                }

                                if (uiState.profile?.profilePictureUrl != null &&
                                                !uiState.isUploadingPicture
                                ) {
                                        TextButton(onClick = { viewModel.deleteProfilePicture() }) {
                                                Text("Supprimer la photo", color = Color.Red)
                                        }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color.White
                                                )
                                ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                        "Informations personnelles",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                OutlinedTextField(
                                                        value = uiState.firstName,
                                                        onValueChange = {
                                                                viewModel.updateFirstName(it)
                                                        },
                                                        label = { Text("Prénom") },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.Person,
                                                                        null,
                                                                        tint = TextSecondary
                                                                )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        singleLine = true,
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                BlassaTeal,
                                                                        focusedLabelColor =
                                                                                BlassaTeal
                                                                )
                                                )

                                                Spacer(modifier = Modifier.height(12.dp))

                                                OutlinedTextField(
                                                        value = uiState.lastName,
                                                        onValueChange = {
                                                                viewModel.updateLastName(it)
                                                        },
                                                        label = { Text("Nom") },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.Person,
                                                                        null,
                                                                        tint = TextSecondary
                                                                )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        singleLine = true,
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                BlassaTeal,
                                                                        focusedLabelColor =
                                                                                BlassaTeal
                                                                )
                                                )

                                                Spacer(modifier = Modifier.height(12.dp))

                                                OutlinedTextField(
                                                        value = uiState.phoneNumber,
                                                        onValueChange = {
                                                                viewModel.updatePhoneNumber(it)
                                                        },
                                                        label = { Text("Téléphone") },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.Phone,
                                                                        null,
                                                                        tint = TextSecondary
                                                                )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        singleLine = true,
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Phone
                                                                ),
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                BlassaTeal,
                                                                        focusedLabelColor =
                                                                                BlassaTeal
                                                                )
                                                )

                                                Spacer(modifier = Modifier.height(12.dp))

                                                OutlinedTextField(
                                                        value = uiState.bio,
                                                        onValueChange = { viewModel.updateBio(it) },
                                                        label = { Text("Bio") },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.Edit,
                                                                        null,
                                                                        tint = TextSecondary
                                                                )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        minLines = 3,
                                                        maxLines = 5,
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                BlassaTeal,
                                                                        focusedLabelColor =
                                                                                BlassaTeal
                                                                )
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color.White
                                                )
                                ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                        "Réseaux sociaux",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                OutlinedTextField(
                                                        value = uiState.facebookUrl,
                                                        onValueChange = {
                                                                viewModel.updateFacebookUrl(it)
                                                        },
                                                        label = { Text("Facebook (URL)") },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.Facebook,
                                                                        null,
                                                                        tint = Color(0xFF1877F2)
                                                                )
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        singleLine = true,
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                BlassaTeal,
                                                                        focusedLabelColor =
                                                                                BlassaTeal
                                                                )
                                                )

                                                Spacer(modifier = Modifier.height(12.dp))

                                                OutlinedTextField(
                                                        value = uiState.instagramUrl,
                                                        onValueChange = {
                                                                viewModel.updateInstagramUrl(it)
                                                        },
                                                        label = { Text("Instagram (URL)") },
                                                        leadingIcon = {
                                                                Box(
                                                                        modifier =
                                                                                Modifier.size(24.dp)
                                                                                        .background(
                                                                                                brush =
                                                                                                        androidx.compose
                                                                                                                .ui
                                                                                                                .graphics
                                                                                                                .Brush
                                                                                                                .linearGradient(
                                                                                                                        colors =
                                                                                                                                listOf(
                                                                                                                                        Color(
                                                                                                                                                0xFFF58529
                                                                                                                                        ),
                                                                                                                                        Color(
                                                                                                                                                0xFFDD2A7B
                                                                                                                                        ),
                                                                                                                                        Color(
                                                                                                                                                0xFF8134AF
                                                                                                                                        )
                                                                                                                                )
                                                                                                                ),
                                                                                                shape =
                                                                                                        RoundedCornerShape(
                                                                                                                6.dp
                                                                                                        )
                                                                                        ),
                                                                        contentAlignment =
                                                                                Alignment.Center
                                                                ) {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .CameraAlt,
                                                                                null,
                                                                                tint = Color.White,
                                                                                modifier =
                                                                                        Modifier.size(
                                                                                                16.dp
                                                                                        )
                                                                        )
                                                                }
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        singleLine = true,
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                BlassaTeal,
                                                                        focusedLabelColor =
                                                                                BlassaTeal
                                                                )
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(32.dp))
                        }
                }
        }
}
