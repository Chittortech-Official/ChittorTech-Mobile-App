package com.chittortech.app.ui.screens.client

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.geometry.Offset
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

    val infiniteTransition = rememberInfiniteTransition(label = "portalPulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CtBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // ─── Dynamic Enterprise Tech Header with Animated Canvas ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Background Canvas: Deep space gradient + glowing orbs + cyber dot matrix + accent circuits
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // 1. Futuristic Base Gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF030712), // Deep cosmic black
                            Color(0xFF0A1628), // Sapphire night
                            Color(0xFF0F1E36), // High-tech navy
                            Color(0xFF132238)  // Deep slate blue
                        )
                    )
                )

                // 2. Ambient Glowing Orbs
                // Top-left Electric Cyan Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF06B6D4).copy(alpha = 0.30f * pulseGlow), Color.Transparent),
                        center = Offset(canvasWidth * 0.15f, canvasHeight * 0.25f),
                        radius = canvasWidth * 0.55f
                    ),
                    center = Offset(canvasWidth * 0.15f, canvasHeight * 0.25f),
                    radius = canvasWidth * 0.55f
                )

                // Center-behind-avatar Royal Sapphire Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF3B82F6).copy(alpha = 0.35f * pulseGlow), Color.Transparent),
                        center = Offset(canvasWidth * 0.5f, canvasHeight * 0.42f),
                        radius = canvasWidth * 0.45f
                    ),
                    center = Offset(canvasWidth * 0.5f, canvasHeight * 0.42f),
                    radius = canvasWidth * 0.45f
                )

                // Bottom-right Violet Tech Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF8B5CF6).copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(canvasWidth * 0.88f, canvasHeight * 0.70f),
                        radius = canvasWidth * 0.50f
                    ),
                    center = Offset(canvasWidth * 0.88f, canvasHeight * 0.70f),
                    radius = canvasWidth * 0.50f
                )

                // 3. Cyber Dot Matrix Grid Pattern
                val dotSpacing = 28.dp.toPx()
                val dotRadius = 1.1.dp.toPx()
                var x = 14.dp.toPx()
                while (x < canvasWidth) {
                    var y = 14.dp.toPx()
                    while (y < canvasHeight) {
                        drawCircle(
                            color = Color(0xFF38BDF8).copy(alpha = 0.08f),
                            radius = dotRadius,
                            center = Offset(x, y)
                        )
                        y += dotSpacing
                    }
                    x += dotSpacing
                }

                // 4. Subtle Decorative Tech Circuit Lines
                drawLine(
                    color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                    start = Offset(0f, canvasHeight * 0.88f),
                    end = Offset(canvasWidth * 0.35f, canvasHeight * 0.88f),
                    strokeWidth = 1.2.dp.toPx()
                )
                drawLine(
                    color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                    start = Offset(canvasWidth * 0.35f, canvasHeight * 0.88f),
                    end = Offset(canvasWidth * 0.42f, canvasHeight * 0.96f),
                    strokeWidth = 1.2.dp.toPx()
                )
                drawLine(
                    color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                    start = Offset(canvasWidth * 0.42f, canvasHeight * 0.96f),
                    end = Offset(canvasWidth, canvasHeight * 0.96f),
                    strokeWidth = 1.2.dp.toPx()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Live Enterprise Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF0F172A).copy(alpha = 0.65f),
                    border = BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF38BDF8).copy(alpha = 0.7f),
                                Color(0xFF818CF8).copy(alpha = 0.7f),
                                Color(0xFFC084FC).copy(alpha = 0.4f)
                            )
                        )
                    ),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Pulsing Green Live Node Dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981).copy(alpha = pulseGlow))
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "CLIENT PORTAL • ENTERPRISE NODE",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE0F2FE),
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Avatar with Dual-Layered Gradient Ring & Verified Badge
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Glowing Ring (Gradient border)
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFF38BDF8),
                                        Color(0xFF6366F1),
                                        Color(0xFFA855F7),
                                        Color(0xFF38BDF8)
                                    )
                                )
                            )
                            .padding(3.dp)
                    ) {
                        // Inner Dark Glassmorphic Avatar
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF1E293B),
                                            Color(0xFF0F172A)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUserState.companyName.take(2).uppercase().ifBlank {
                                    currentUserState.displayName.take(2).uppercase().ifBlank { "CT" }
                                },
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Verified Badge Icon at bottom-right
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0284C7))
                            .border(2.dp, Color(0xFF0F172A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verified Client",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Client Representative Name
                Text(
                    text = currentUserState.displayName.ifBlank { "Client Representative" },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.3.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Enterprise / Company Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null,
                            tint = Color(0xFF93C5FD),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentUserState.companyName.ifBlank { "Corporate Enterprise Client" },
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Security & Trust Badges Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0369A1).copy(alpha = 0.35f),
                        border = BorderStroke(0.8.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("256-Bit Encrypted", fontSize = 10.sp, color = Color(0xFFE0F2FE), fontWeight = FontWeight.Medium)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF15803D).copy(alpha = 0.35f),
                        border = BorderStroke(0.8.dp, Color(0xFF4ADE80).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Active Session", fontSize = 10.sp, color = Color(0xFFDCFCE7), fontWeight = FontWeight.Medium)
                        }
                    }
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
