package com.chittortech.app.ui.screens.client

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.model.CtUser
import com.chittortech.app.theme.*

@Composable
fun ClientProfileScreen(
    user: CtUser,
    onLogout: () -> Unit,
    onUpdateUser: (CtUser, (Boolean, String?) -> Unit) -> Unit = { _, _ -> },
    onChangePassword: (oldPassword: String, newPassword: String, (Boolean, String?) -> Unit) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentUserState by remember(user) { mutableStateOf(user) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CtBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Gradient Header with Avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(CtPrimaryDark, CtPrimaryMid)))
                .statusBarsPadding()
                .padding(bottom = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUserState.companyName.take(2).uppercase().ifBlank { currentUserState.displayName.take(2).uppercase().ifBlank { "CT" } },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(currentUserState.displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(currentUserState.companyName.ifBlank { "Corporate Client" }, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("CLIENT PORTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Details Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CtCardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Account Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    TextButton(
                        onClick = { showEditDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp), tint = CtPrimaryBlue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Details", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = CtPrimaryBlue)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                ProfileRow(icon = Icons.Default.Person, label = "Client Representative", value = currentUserState.displayName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CtBorder)
                ProfileRow(icon = Icons.Default.Business, label = "Enterprise / Company", value = currentUserState.companyName.ifBlank { "—" })
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CtBorder)
                ProfileRow(icon = Icons.Default.Email, label = "Account Email", value = currentUserState.email)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CtBorder)
                ProfileRow(icon = Icons.Default.Phone, label = "Registered Phone", value = currentUserState.phone.ifBlank { "Not set" })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security & Credentials Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CtCardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Security & Password", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Manage your portal authentication & reset credentials", fontSize = 11.5.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showChangePasswordDialog = true },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue)
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change Account Password", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ChittorTech Contact & Official Channels Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CtCardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ChittorTech Contact & Support", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Business Enquiries
                ClickableContactRow(
                    icon = Icons.Default.BusinessCenter,
                    label = "Business Talks",
                    value = "business@chittortech.in",
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:business@chittortech.in?subject=ChittorTech%20Business%20Inquiry")
                        }
                        context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CtBorder)

                // Support Enquiries
                ClickableContactRow(
                    icon = Icons.Default.MailOutline,
                    label = "Official Support",
                    value = "contact@chittortech.in",
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:contact@chittortech.in?subject=Client%20Support%20Request")
                        }
                        context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CtBorder)

                // Website
                ClickableContactRow(
                    icon = Icons.Default.Language,
                    label = "Official Website",
                    value = "chittortech.in",
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in")))
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Helpdesk Notice Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = CtPrimaryBlue,
                            modifier = Modifier.size(18.dp).padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "For priority technical support and bug fixes, please raise a ticket directly in the Helpdesk tab. Our engineering team resolves active tickets round the clock.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CtRedLight),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = CtRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = CtRed)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "ChittorTech Official App • chittortech.in",
            fontSize = 11.sp,
            color = TextMuted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }

    if (showEditDialog) {
        EditAccountDetailsDialog(
            currentUser = currentUserState,
            onDismiss = { showEditDialog = false },
            onSave = { updated, onComplete ->
                onUpdateUser(updated) { success, err ->
                    if (success) {
                        currentUserState = updated
                        showEditDialog = false
                    }
                    onComplete(success, err)
                }
            }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onUpdatePassword = { oldPassword, newPassword, onComplete ->
                onChangePassword(oldPassword, newPassword) { success, err ->
                    if (success) {
                        showChangePasswordDialog = false
                    }
                    onComplete(success, err)
                }
            }
        )
    }
}

@Composable
private fun ProfileRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CtPrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun ClickableContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CtPrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = CtPrimaryBlue
            )
        }
        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun EditAccountDetailsDialog(
    currentUser: CtUser,
    onDismiss: () -> Unit,
    onSave: (CtUser, (Boolean, String?) -> Unit) -> Unit
) {
    var representativeName by remember { mutableStateOf(currentUser.displayName) }
    var phone by remember { mutableStateOf(currentUser.phone) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dialogFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF0F172A),
        unfocusedBorderColor = Color(0xFF0F172A),
        focusedLabelColor = Color(0xFF0F172A),
        unfocusedLabelColor = Color(0xFF334155),
        focusedTextColor = Color(0xFF0F172A),
        unfocusedTextColor = Color(0xFF0F172A),
        disabledTextColor = Color(0xFF1E293B),
        disabledBorderColor = Color(0xFF475569),
        disabledLabelColor = Color(0xFF334155),
        disabledContainerColor = Color(0xFFF1F5F9)
    )

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Account Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "You can update your Representative Name and Registered Phone Number. Company Name and Email are permanent enterprise identifiers registered with ChittorTech.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEE2E2),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFF991B1B),
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Client Representative Name (Editable)
                OutlinedTextField(
                    value = representativeName,
                    onValueChange = { 
                        representativeName = it
                        errorMessage = null
                    },
                    label = { Text("Client Representative Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CtPrimaryBlue) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogFieldColors
                )

                // Registered Phone Number (Editable)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { 
                        phone = it
                        errorMessage = null
                    },
                    label = { Text("Registered Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CtPrimaryBlue) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogFieldColors
                )

                // Enterprise / Company Name (Locked)
                OutlinedTextField(
                    value = currentUser.companyName.ifBlank { "ChittorTech Client" },
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Enterprise / Company (Registered)") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = TextMuted) },
                    trailingIcon = { Icon(Icons.Default.Lock, contentDescription = "Locked", tint = TextMuted, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    supportingText = { Text("Company name is verified and cannot be changed", fontSize = 10.sp, color = TextMuted) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogFieldColors
                )

                // Account / Company Email (Locked)
                OutlinedTextField(
                    value = currentUser.email,
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Account Email (Registered)") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextMuted) },
                    trailingIcon = { Icon(Icons.Default.Lock, contentDescription = "Locked", tint = TextMuted, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    supportingText = { Text("Official login email ID cannot be changed", fontSize = 10.sp, color = TextMuted) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogFieldColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (representativeName.isBlank()) {
                        errorMessage = "Representative name cannot be empty."
                        return@Button
                    }
                    isSaving = true
                    errorMessage = null
                    val updated = currentUser.copy(
                        displayName = representativeName.trim(),
                        phone = phone.trim()
                    )
                    onSave(updated) { success, err ->
                        isSaving = false
                        if (!success) {
                            errorMessage = err ?: "Failed to update profile. Please verify database permissions."
                        }
                    }
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Saving...", fontWeight = FontWeight.Bold)
                } else {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onUpdatePassword: (oldPassword: String, newPassword: String, (Boolean, String?) -> Unit) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dialogFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF0F172A),
        unfocusedBorderColor = Color(0xFF0F172A),
        focusedLabelColor = Color(0xFF0F172A),
        unfocusedLabelColor = Color(0xFF334155),
        focusedTextColor = Color(0xFF0F172A),
        unfocusedTextColor = Color(0xFF0F172A)
    )

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Key, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Password", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Verify your current password to set a new password. New password must be at least 6 characters.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEE2E2),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFF991B1B),
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Current / Old Password
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = {
                        oldPassword = it
                        errorMessage = null
                    },
                    label = { Text("Current Password") },
                    leadingIcon = { Icon(Icons.Default.Password, contentDescription = null, tint = CtPrimaryBlue) },
                    trailingIcon = {
                        IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                            Icon(
                                if (oldPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    },
                    visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogFieldColors
                )

                // New Password
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = null
                    },
                    label = { Text("New Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CtPrimaryBlue) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogFieldColors
                )

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    label = { Text("Confirm New Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CtPrimaryBlue) },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogFieldColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (oldPassword.isBlank()) {
                        errorMessage = "Please enter your current password."
                        return@Button
                    }
                    if (newPassword.length < 6) {
                        errorMessage = "New password must be at least 6 characters long."
                        return@Button
                    }
                    if (newPassword == oldPassword) {
                        errorMessage = "New password must be different from current password."
                        return@Button
                    }
                    if (newPassword != confirmPassword) {
                        errorMessage = "New passwords do not match. Please re-enter."
                        return@Button
                    }
                    isSaving = true
                    errorMessage = null
                    onUpdatePassword(oldPassword.trim(), newPassword.trim()) { success, err ->
                        isSaving = false
                        if (!success) {
                            errorMessage = err ?: "Failed to update password. Please check your current password."
                        }
                    }
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Updating...", fontWeight = FontWeight.Bold)
                } else {
                    Text("Update Password", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
