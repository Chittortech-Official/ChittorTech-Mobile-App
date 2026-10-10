package com.chittortech.app.ui.screens.client

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
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
    var showReferralPolicyDialog by remember { mutableStateOf(false) }

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
        // ─── Premium FinTech / VIP Card Header with Curved Bottom Wave ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Background Canvas: Royal Sapphire Gradient + Ambient Light + Organic Curved Bottom Wave
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // 1. Organic Curved Bottom Wave Path
                val wavePath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(canvasWidth, 0f)
                    lineTo(canvasWidth, canvasHeight - 24.dp.toPx())
                    quadraticTo(
                        canvasWidth * 0.5f, canvasHeight + 14.dp.toPx(),
                        0f, canvasHeight - 24.dp.toPx()
                    )
                    close()
                }

                // 2. Royal Sapphire Deep Corporate Gradient
                drawPath(
                    path = wavePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF031633), // Deep Navy Midnight
                            Color(0xFF07295E), // Deep Sapphire Blue
                            Color(0xFF0A479D), // Royal ChittorTech Blue
                            Color(0xFF0284C7)  // Electric Cyan Accent
                        )
                    )
                )

                // 3. Ambient Glowing Light Orbs
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.35f * pulseGlow), Color.Transparent),
                        center = Offset(canvasWidth * 0.85f, canvasHeight * 0.22f),
                        radius = canvasWidth * 0.50f
                    ),
                    center = Offset(canvasWidth * 0.85f, canvasHeight * 0.22f),
                    radius = canvasWidth * 0.50f
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF6366F1).copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(canvasWidth * 0.15f, canvasHeight * 0.70f),
                        radius = canvasWidth * 0.45f
                    ),
                    center = Offset(canvasWidth * 0.15f, canvasHeight * 0.70f),
                    radius = canvasWidth * 0.45f
                )

                // 4. Subtle Ambient Tech Pinstripes
                val step = 36.dp.toPx()
                var gx = step
                while (gx < canvasWidth) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.035f),
                        start = Offset(gx, 0f),
                        end = Offset(gx, canvasHeight - 32.dp.toPx()),
                        strokeWidth = 0.8.dp.toPx()
                    )
                    gx += step
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 26.dp)
            ) {
                // Top Branding Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "CHITTORTECH ENTERPRISE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Official Client Network",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Live Client Portal Chip
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981).copy(alpha = pulseGlow))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CLIENT PORTAL",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFE0F2FE),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ─── 3D Glowing Glass VIP Card ───
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.Transparent,
                    border = BorderStroke(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.55f),
                                Color(0xFF38BDF8).copy(alpha = 0.45f),
                                Color(0xFF818CF8).copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF0C254F).copy(alpha = 0.92f),
                                        Color(0xFF061733).copy(alpha = 0.96f)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            // Card Top Row: EMV Chip & Authenticated Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 30.dp, height = 22.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        Color(0xFFFDE68A),
                                                        Color(0xFFD97706)
                                                    )
                                                )
                                            )
                                            .border(0.8.dp, Color(0xFFF59E0B), RoundedCornerShape(5.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Nfc,
                                            contentDescription = null,
                                            tint = Color(0xFF78350F),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "VIP ACCESS PASS",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFDE68A),
                                        letterSpacing = 1.2.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0284C7).copy(alpha = 0.25f),
                                    border = BorderStroke(0.8.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.VerifiedUser,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "AUTHENTICATED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE0F2FE),
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Card Middle: Avatar + Client Name & Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar with Dual Glow Ring
                                Box(
                                    modifier = Modifier.size(68.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(66.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.sweepGradient(
                                                    listOf(
                                                        Color(0xFF38BDF8),
                                                        Color(0xFF6366F1),
                                                        Color(0xFFEC4899),
                                                        Color(0xFF38BDF8)
                                                    )
                                                )
                                            )
                                            .padding(2.5.dp)
                                    ) {
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
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    // Verified Badge Checkmark
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF0284C7))
                                            .border(1.5.dp, Color(0xFF0F172A), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentUserState.displayName.ifBlank { "Client Representative" },
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Business,
                                            contentDescription = null,
                                            tint = Color(0xFF93C5FD),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = currentUserState.companyName.ifBlank { "Enterprise Client" },
                                            fontSize = 12.5.sp,
                                            color = Color(0xFF93C5FD),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentUserState.email,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.65f),
                                        maxLines = 1
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Card Footer: Security & Session Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "256-BIT ENCRYPTED SESSION",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFBAE6FD),
                                        letterSpacing = 0.6.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "PRIORITY ACTIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF86EFAC),
                                        letterSpacing = 0.6.sp
                                    )
                                }
                            }
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

        // Client Referral & Partner Rewards Card
        ReferralPartnerCard(
            user = currentUserState,
            onShowPolicy = { showReferralPolicyDialog = true }
        )

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

                // WhatsApp Support
                ClickableContactRow(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    label = "WhatsApp Tech Support",
                    value = "+91 7597451057",
                    onClick = {
                        val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/917597451057?text=Hello+ChittorTech+Team,+I+need+assistance+regarding+my+project."))
                        context.startActivity(whatsappIntent)
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

                // Security & Session Expiry Policy Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp).padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Corporate Session Policy: Your login remains securely active for up to 7 days. If no activity is detected for 5 consecutive days, your session will automatically log out for enterprise security. Re-authentication via 2FA OTP will be required upon expiry.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF475569),
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
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = CtRed)
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

    if (showReferralPolicyDialog) {
        val cleanName = (currentUserState.companyName.ifBlank { currentUserState.displayName })
            .replace(Regex("[^A-Za-z0-9]"), "")
            .uppercase()
            .take(12)
            .ifBlank { "CLIENT" }
        val refCode = "CT-REF-$cleanName"
        ReferralPolicyDialog(
            referralCode = refCode,
            onDismiss = { showReferralPolicyDialog = false }
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
        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
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

@Composable
private fun ReferralPartnerCard(
    user: CtUser,
    onShowPolicy: () -> Unit
) {
    val context = LocalContext.current
    val cleanName = remember(user) {
        (user.companyName.ifBlank { user.displayName })
            .replace(Regex("[^A-Za-z0-9]"), "")
            .uppercase()
            .take(12)
            .ifBlank { "CLIENT" }
    }
    val referralCode = "CT-REF-$cleanName"
    val shareMessage = "Hey! I recommend ChittorTech for world-class mobile apps, enterprise web portals, and custom software. Use my partner referral code *$referralCode* when consulting them to get priority onboarding and enterprise perks: https://chittortech.in or WhatsApp +91 7597451057"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row (Standard style matching other cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CtPrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = CtPrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Partner & Client Referral",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Earn 5%–10% payout on client conversions",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Referral Code Display Container
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, CtBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "YOUR UNIQUE REFERRAL ID",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = referralCode,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CtPrimaryBlue,
                            letterSpacing = 0.8.sp
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("ChittorTech Referral Code", referralCode)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Referral Code $referralCode copied!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = CtPrimaryLight,
                            contentColor = CtPrimaryBlue
                        )
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Key Perks List (Unified with ProfileRow style)
            ProfileRow(
                icon = Icons.Default.MonetizationOn,
                label = "Commission Incentive",
                value = "5% to 10% Direct Payout per Converted Client"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CtBorder)
            ProfileRow(
                icon = Icons.Default.AccountBalanceWallet,
                label = "Settlement Method",
                value = "Direct Transfer to Registered UPI / Bank Account"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Primary 1-Tap WhatsApp Share Button
            Button(
                onClick = {
                    try {
                        val waIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareMessage)
                            setPackage("com.whatsapp")
                        }
                        context.startActivity(waIntent)
                    } catch (e: Exception) {
                        val generalShare = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareMessage)
                        }
                        context.startActivity(Intent.createChooser(generalShare, "Share Referral Code"))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share on WhatsApp",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Share Row (Email & Share Sheet)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:?subject=" + Uri.encode("Enterprise Tech Referral - ChittorTech") + "&body=" + Uri.encode(shareMessage))
                        }
                        try {
                            context.startActivity(Intent.createChooser(emailIntent, "Send Email Invite"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "No email client found", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CtBorder)
                ) {
                    Icon(Icons.Default.MailOutline, contentDescription = null, modifier = Modifier.size(16.dp), tint = CtPrimaryBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Email Invite", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }

                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareMessage)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Referral Details"))
                    },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CtBorder)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = CtPrimaryBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("More Options", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // View Program Policy & Payout Details Surface (Clean & clickable)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onShowPolicy),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = CtPrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View Program Policy & Payout Rules",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CtPrimaryBlue
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = CtPrimaryBlue,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReferralPolicyDialog(
    referralCode: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = CtCardWhite,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CtPrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = CtPrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Referral & Partner Policy",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        "Program rules & payout workflow",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Referral Code Highlight
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, CtBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "YOUR REFERRAL CODE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                referralCode,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CtPrimaryBlue
                            )
                        }
                        FilledTonalButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("ChittorTech Referral Code", referralCode)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Referral Code $referralCode copied!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = CtPrimaryLight,
                                contentColor = CtPrimaryBlue
                            )
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CtPrimaryBlue, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Program Overview
                Text(
                    text = "ChittorTech values strategic client partnerships. When you refer businesses or startups that build their digital platforms with us, you receive a direct monetary reward upon project execution.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )

                // 4-Step Process Container
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, CtBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "HOW IT WORKS (4 STEPS)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        PolicyStepItem(
                            stepNumber = "1",
                            title = "Share Your Referral Code",
                            description = "Share code $referralCode with businesses seeking web, mobile app, or software engineering."
                        )

                        PolicyStepItem(
                            stepNumber = "2",
                            title = "Discovery & Scoping",
                            description = "The client connects with ChittorTech quoting your code. Our tech architects scope their requirements."
                        )

                        PolicyStepItem(
                            stepNumber = "3",
                            title = "SLA & Project Sign-Off",
                            description = "Once the client executes the agreement and signs the Service Level Agreement (SLA) with ChittorTech."
                        )

                        PolicyStepItem(
                            stepNumber = "4",
                            title = "Direct 5%–10% UPI Payout",
                            description = "Instant cash commission is credited directly to your registered UPI ID upon milestone kickoff."
                        )
                    }
                }

                // Terms Notice
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
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = CtPrimaryBlue,
                            modifier = Modifier.size(16.dp).padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Commission rates (5%–10%) are calculated on the total project valuation. All payouts are disbursed strictly to your verified registered account.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 16.sp
                        )
                    }
                }

                // Dedicated Partnership WhatsApp Support Box (Full Width, perfectly styled)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            val waIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://wa.me/917597451057?text=Hello+ChittorTech+Team,+I+have+a+question+regarding+the+Client+Referral+Program+($referralCode)")
                            )
                            context.startActivity(waIntent)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF25D366)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Partnership Support Desk",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "WhatsApp: +91 7597451057",
                                fontSize = 11.5.sp,
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF166534),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("Got It", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
            }
        }
    )
}

@Composable
private fun PolicyStepItem(
    stepNumber: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(CtPrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.5.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}
