package com.example.attendease.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing
import org.koin.compose.koinInject
import com.example.attendease.data.session.SessionManager
import com.example.attendease.data.repository.AuthRepository
import com.example.attendease.viewModel.AuthViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.MaterialTheme
import androidx.fragment.app.FragmentActivity
import com.example.attendease.utils.BiometricHelper
import android.widget.Toast
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    userRole: UserRole,
    userName: String,
    userEmail: String,
    sessionManager: SessionManager = koinInject(),
    authRepository: AuthRepository = koinInject(),
    authViewModel: AuthViewModel = koinInject()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cachedName = sessionManager.getUserName().takeIf { it != "User" && !it.isNullOrBlank() } ?: userName
    val cachedEmail = sessionManager.getUserEmail().takeIf { !it.isNullOrBlank() } ?: userEmail
    val cachedRole = sessionManager.getUserRole() ?: userRole

    var name by remember { mutableStateOf(cachedName) }
    var email by remember { mutableStateOf(cachedEmail) }
    var role by remember { mutableStateOf(cachedRole) }
    var isRefreshing by remember { mutableStateOf(false) }

    var showPasswordDialog by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    var showProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var isUpdatingProfile by remember { mutableStateOf(false) }
    
    val changePasswordState by authViewModel.changePasswordState.collectAsState()
    val updateProfileState by authViewModel.updateProfileState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(changePasswordState) {
        changePasswordState?.let { result ->
            if (result.isSuccess) {
                showPasswordDialog = false
                oldPassword = ""
                newPassword = ""
                android.widget.Toast.makeText(context, "Password changed successfully!", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(context, result.exceptionOrNull()?.message ?: "Failed to change password", android.widget.Toast.LENGTH_LONG).show()
            }
            authViewModel.resetChangePasswordState()
        }
    }

    LaunchedEffect(updateProfileState) {
        updateProfileState?.let { result ->
            isUpdatingProfile = false
            if (result.isSuccess) {
                val updatedUser = result.getOrNull()
                if (updatedUser != null) {
                    name = updatedUser.name ?: name
                    email = updatedUser.email ?: email
                }
                showProfileDialog = false
                android.widget.Toast.makeText(context, "Profile updated successfully!", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(context, result.exceptionOrNull()?.message ?: "Failed to update profile", android.widget.Toast.LENGTH_LONG).show()
            }
            authViewModel.resetUpdateProfileState()
        }
    }

    LaunchedEffect(Unit) {
        isRefreshing = true
        try {
            val user = authRepository.getMe(forceRefresh = true)
            name = user.name ?: cachedName
            email = user.email ?: cachedEmail
            role = user.role
        } catch (e: Exception) {
            // Silently fall back to cached data since there's already a global offline banner
        } finally {
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(title = "Profile & Settings")
        },
        bottomBar = {
            AttendEaseBottomBar(
                userRole = role,
                currentRoute = "settings",
                navController = navController
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            if (isRefreshing) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.xs),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(Spacing.md))
                // Profile Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.lg),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(Spacing.lg))
                        Column {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                modifier = Modifier.padding(top = 4.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = role.name,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Account Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = Spacing.base)
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.PersonOutline,
                    title = "Edit Profile Info",
                    subtitle = "Change your name, email, etc.",
                    onClick = {
                        editName = name
                        editEmail = email
                        showProfileDialog = true
                    }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.LockOpen,
                    title = "Security & Password",
                    subtitle = "Update your login credentials",
                    onClick = { showPasswordDialog = true }
                )
            }

            item {
                Text(
                    text = "App Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = Spacing.base)
                )
            }

            item {
                val themePreference by sessionManager.themePreferenceFlow.collectAsState()
                var biometricEnabled by remember { mutableStateOf(sessionManager.isBiometricEnabled()) }
                
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.md),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column {
                                Text("App Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text("Choose your preferred look", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.md))
                        
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            val options = listOf("SYSTEM" to "System", "LIGHT" to "Light", "DARK" to "Dark")
                            options.forEachIndexed { index, (value, label) ->
                                SegmentedButton(
                                    selected = themePreference == value,
                                    onClick = { sessionManager.saveThemePreference(value) },
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                SettingsSwitchItem(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric Unlock",
                    subtitle = "Use Face ID / Fingerprint to unlock app & check-in",
                    isChecked = biometricEnabled,
                    onCheckedChange = { isEnabled ->
                        if (isEnabled) {
                            val activity = context as? FragmentActivity
                            if (activity != null && BiometricHelper.isBiometricAvailable(activity)) {
                                coroutineScope.launch {
                                    val success = BiometricHelper.authenticate(
                                        activity = activity,
                                        title = "Enable Biometric Unlock",
                                        subtitle = "Verify your identity to enable biometrics"
                                    )
                                    if (success) {
                                        sessionManager.setBiometricEnabled(true)
                                        biometricEnabled = true
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Biometrics not available on this device", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val activity = context as? FragmentActivity
                            if (activity != null && BiometricHelper.isBiometricAvailable(activity)) {
                                coroutineScope.launch {
                                    val success = BiometricHelper.authenticate(
                                        activity = activity,
                                        title = "Disable Biometric Unlock",
                                        subtitle = "Verify your identity to disable biometrics"
                                    )
                                    if (success) {
                                        sessionManager.setBiometricEnabled(false)
                                        biometricEnabled = false
                                    }
                                }
                            } else {
                                sessionManager.setBiometricEnabled(false)
                                biometricEnabled = false
                            }
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.md))
                Button(
                    onClick = { 
                        authRepository.clearCache()
                        sessionManager.clearSession()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(Spacing.base))
                    Text(text = "Logout", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }

        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                title = { Text("Change Password") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            label = { Text("Current Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (oldPassword.isNotBlank() && newPassword.isNotBlank()) {
                                authViewModel.changePassword(oldPassword, newPassword)
                            }
                        },
                        enabled = oldPassword.isNotBlank() && newPassword.isNotBlank()
                    ) {
                        Text("Update Password")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPasswordDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showProfileDialog) {
            AlertDialog(
                onDismissRequest = { if (!isUpdatingProfile) showProfileDialog = false },
                title = { Text("Edit Profile Info") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !isUpdatingProfile && role == UserRole.ADMIN
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            enabled = !isUpdatingProfile && role == UserRole.ADMIN
                        )
                        if (isUpdatingProfile) {
                            Spacer(modifier = Modifier.height(Spacing.md))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        if (role != UserRole.ADMIN) {
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Text(
                                text = "Only administrators can edit profile information.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editName.isNotBlank() && editEmail.isNotBlank()) {
                                isUpdatingProfile = true
                                authViewModel.updateProfile(editName, editEmail)
                            }
                        },
                        enabled = editName.isNotBlank() && editEmail.isNotBlank() && !isUpdatingProfile && role == UserRole.ADMIN
                    ) {
                        Text("Save Changes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showProfileDialog = false },
                        enabled = !isUpdatingProfile
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.md)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!isChecked) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.md)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
