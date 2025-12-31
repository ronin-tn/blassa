package com.tp.blassa.features.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tp.blassa.core.network.RetrofitClient
import com.tp.blassa.core.network.parseErrorMessage
import com.tp.blassa.features.profile.viewmodel.ProfileMenuViewModel
import com.tp.blassa.ui.theme.BlassaRed
import com.tp.blassa.ui.theme.BlassaTeal
import com.tp.blassa.ui.theme.TextPrimary
import com.tp.blassa.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMenuScreen(
        onBack: () -> Unit,
        onNavigateToEditProfile: () -> Unit,
        onNavigateToChangePassword: () -> Unit,
        onNavigateToMyVehicles: () -> Unit,
        onNavigateToReviews: () -> Unit,
        onNavigateToNotifications: () -> Unit,
        onLogout: () -> Unit,
        viewModel: ProfileMenuViewModel = viewModel()
) {
        val uiState by viewModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        var showDeleteDialog by remember { mutableStateOf(false) }
        var isDeleting by remember { mutableStateOf(false) }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Menu", fontWeight = FontWeight.SemiBold) },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
                                                Icon(
                                                        Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Retour",
                                                        tint = Color.Black
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color.White,
                                                titleContentColor = TextPrimary
                                        )
                        )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = Color(0xFFF8FAFC)
        ) { padding ->
                Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                        if (uiState.isGuest) {
                                Column(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                modifier =
                                                        Modifier.size(64.dp).padding(bottom = 8.dp),
                                                tint = BlassaTeal
                                        )
                                        Text(
                                                text = "Mode Invité",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                        )
                                        Text(
                                                text = "Connectez-vous pour accéder à votre profil",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                                onClick = onLogout,
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = BlassaTeal
                                                        ),
                                                modifier = Modifier.fillMaxWidth()
                                        ) { Text("Se connecter / S'inscrire") }
                                }
                        } else if (uiState.isLoading) {
                                Box(
                                        modifier = Modifier.fillMaxWidth().height(100.dp),
                                        contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator(color = BlassaTeal) }
                        } else if (uiState.userProfile != null) {
                                val user = uiState.userProfile!!
                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        if (user.profilePictureUrl != null) {
                                                AsyncImage(
                                                        model = user.profilePictureUrl,
                                                        contentDescription = "Profile Picture",
                                                        modifier =
                                                                Modifier.size(64.dp)
                                                                        .clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                )
                                        } else {
                                                Surface(
                                                        modifier = Modifier.size(64.dp),
                                                        shape = CircleShape,
                                                        color = BlassaTeal.copy(alpha = 0.1f)
                                                ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                                Icon(
                                                                        Icons.Default.Person,
                                                                        contentDescription = null,
                                                                        tint = BlassaTeal,
                                                                        modifier =
                                                                                Modifier.size(32.dp)
                                                                )
                                                        }
                                                }
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                                Text(
                                                        text = "${user.firstName} ${user.lastName}",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                )
                                                Text(
                                                        text = user.email,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = TextSecondary
                                                )
                                        }
                                }
                        } else {
                                Text("Impossible de charger le profil", color = Color.Red)
                        }

                        if (!uiState.isGuest) {
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(16.dp))

                                MenuItem(
                                        icon = Icons.Default.Edit,
                                        label = "Modifier le profil",
                                        onClick = onNavigateToEditProfile
                                )
                                MenuItem(
                                        icon = Icons.Default.Lock,
                                        label = "Changer le mot de passe",
                                        onClick = onNavigateToChangePassword
                                )
                                MenuItem(
                                        icon = Icons.Default.DirectionsCar,
                                        label = "Mes Véhicules",
                                        onClick = onNavigateToMyVehicles
                                )
                                MenuItem(
                                        icon = Icons.Default.Star,
                                        label = "Avis Reçus",
                                        onClick = onNavigateToReviews
                                )
                                MenuItem(
                                        icon = Icons.Default.Notifications,
                                        label = "Notifications",
                                        onClick = onNavigateToNotifications
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                Surface(
                                        onClick = { showDeleteDialog = true },
                                        color = Color.White,
                                        shape = MaterialTheme.shapes.medium,
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                ) {
                                        Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = null,
                                                        tint = BlassaRed.copy(alpha = 0.7f)
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                        text = "Supprimer mon compte",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = BlassaRed.copy(alpha = 0.7f)
                                                )
                                        }
                                }

                                Surface(
                                        onClick = onLogout,
                                        color = Color.White,
                                        shape = MaterialTheme.shapes.medium,
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Icon(
                                                        Icons.Default.ExitToApp,
                                                        contentDescription = null,
                                                        tint = BlassaRed
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                        text = "Se déconnecter",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = BlassaRed
                                                )
                                        }
                                }
                        }
                }
        }

        if (showDeleteDialog) {
                AlertDialog(
                        onDismissRequest = { if (!isDeleting) showDeleteDialog = false },
                        title = {
                                Text(
                                        text = "Supprimer le compte",
                                        fontWeight = FontWeight.Bold,
                                        color = BlassaRed
                                )
                        },
                        text = {
                                Column {
                                        Text(
                                                text =
                                                        "Êtes-vous sûr de vouloir supprimer votre compte ?",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text =
                                                        "Cette action est irréversible. Toutes vos données, trajets et avis seront définitivement supprimés.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                        )
                                }
                        },
                        confirmButton = {
                                Button(
                                        onClick = {
                                                isDeleting = true
                                                scope.launch {
                                                        try {
                                                                val response =
                                                                        RetrofitClient
                                                                                .dashboardApiService
                                                                                .deleteAccount()
                                                                if (response.isSuccessful) {
                                                                        showDeleteDialog = false
                                                                        isDeleting = false
                                                                        onLogout()
                                                                } else {
                                                                        isDeleting = false
                                                                        showDeleteDialog = false
                                                                        snackbarHostState
                                                                                .showSnackbar(
                                                                                        message =
                                                                                                "Erreur lors de la suppression du compte"
                                                                                )
                                                                }
                                                        } catch (e: retrofit2.HttpException) {
                                                                isDeleting = false
                                                                showDeleteDialog = false
                                                                snackbarHostState.showSnackbar(
                                                                        message =
                                                                                e.parseErrorMessage()
                                                                )
                                                        } catch (e: Exception) {
                                                                isDeleting = false
                                                                showDeleteDialog = false
                                                                snackbarHostState.showSnackbar(
                                                                        message =
                                                                                "Erreur de connexion"
                                                                )
                                                        }
                                                }
                                        },
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = BlassaRed
                                                ),
                                        enabled = !isDeleting
                                ) {
                                        if (isDeleting) {
                                                CircularProgressIndicator(
                                                        modifier = Modifier.size(20.dp),
                                                        color = Color.White,
                                                        strokeWidth = 2.dp
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        Text("Supprimer", color = Color.White)
                                }
                        },
                        dismissButton = {
                                TextButton(
                                        onClick = { showDeleteDialog = false },
                                        enabled = !isDeleting
                                ) { Text("Annuler", color = TextSecondary) }
                        },
                        containerColor = Color.White
                )
        }
}

@Composable
fun MenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
        Surface(
                onClick = onClick,
                color = Color.White,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(icon, contentDescription = null, tint = TextSecondary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                                text = label,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                        )
                        Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary.copy(alpha = 0.5f)
                        )
                }
        }
}
