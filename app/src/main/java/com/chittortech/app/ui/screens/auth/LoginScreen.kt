package com.chittortech.app.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import com.chittortech.app.R
import com.chittortech.app.data.OtpSession
import com.chittortech.app.model.CtUser
import com.chittortech.app.theme.*
import kotlinx.coroutines.delay

enum class LoginRoleTab {
    GUEST,      // Open to all public users / visitors (1st & Default)
    CORPORATE,  // Verified Clients / Enterprises (2nd)
    ADMIN       // Founders (Kush Sharma & Lav Sharma) (3rd)
}

@Composable
fun LoginScreen(
    onLoginSuccess: (email: String, password: String, role: String) -> Unit,
    onDirectRoleAccess: (role: String, user: CtUser) -> Unit,
    onRequestOtp: (email: String, password: String, role: String, onSessionReady: (OtpSession) -> Unit, onError: (String) -> Unit) -> Unit = { _, _, _, _, _ -> },
    onVerifyOtp: (email: String, otp: String, token: String, expiresAt: Long, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit = { _, _, _, _, _, _ -> },
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LoginRoleTab.GUEST) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ── 2FA OTP State ────────────────────────────────────────────────────────
    var otpSession by remember { mutableStateOf<OtpSession?>(null) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    var otpError by remember { mutableStateOf<String?>(null) }
    var pendingRole by remember { mutableStateOf("client") }

    val activeError = localError ?: errorMessage

    // Helper to trigger OTP request after validating credentials
    val handleSignInClick = { targetRole: String ->
        if (email.isNotBlank() && password.isNotBlank()) {
            localError = null
            isSendingOtp = true
            pendingRole = targetRole
            onRequestOtp(
                email.trim(),
                password.trim(),
                targetRole,
                { session ->
                    isSendingOtp = false
                    otpSession = session
                    otpError = null
                },
                { err ->
                    isSendingOtp = false
                    localError = err
                }
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CtBackground)
    ) {
        // Gradient Header Arc
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0284C7), // ChittorTech Brand Blue
                            Color(0xFF0EA5E9), // Light Sky Blue
                            Color(0xFF38BDF8)  // Radiant Sky
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ── Top Logo & 3D Avatar Header ──────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // ChittorTech Logo
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.chittortech_logo),
                        contentDescription = "ChittorTech Logo",
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 3D AI Mascot Peek
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.chittortech_ai_mascot),
                        contentDescription = "ChittorTech AI Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "ChittorTech",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Enterprise AI · Cloud Architecture · Custom Software",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 30.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── 3-Way Login Mode Segmented Controller (Guest -> Corporate -> Admin) ──
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.25f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RoleTabPill(
                        label = "Guest",
                        icon = Icons.Default.Explore,
                        isSelected = selectedTab == LoginRoleTab.GUEST,
                        onClick = {
                            selectedTab = LoginRoleTab.GUEST
                            localError = null
                            otpSession = null
                            otpError = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                    RoleTabPill(
                        label = "Corporate",
                        icon = Icons.Default.Business,
                        isSelected = selectedTab == LoginRoleTab.CORPORATE,
                        onClick = {
                            selectedTab = LoginRoleTab.CORPORATE
                            localError = null
                            otpSession = null
                            otpError = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                    RoleTabPill(
                        label = "Admin",
                        icon = Icons.Default.AdminPanelSettings,
                        isSelected = selectedTab == LoginRoleTab.ADMIN,
                        onClick = {
                            selectedTab = LoginRoleTab.ADMIN
                            localError = null
                            otpSession = null
                            otpError = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Dynamic Card based on Selected Tab ───────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CtCardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    when (selectedTab) {
                        // ── 1. GUEST EXPLORER MODE (DEFAULT & FIRST) ──────────
                        LoginRoleTab.GUEST -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF0FDF4),
                                    modifier = Modifier.padding(end = 10.dp)
                                ) {
                                    Icon(Icons.Default.Explore, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.padding(6.dp).size(20.dp))
                                }
                                Column {
                                    Text("Guest Explorer Mode", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("No password required! Explore app & all ChittorTech services", fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    GuestFeatureRow("✨ Browse full ChittorTech Services Catalog")
                                    GuestFeatureRow("🤖 AI Chatbot with ChittorTech GPT")
                                    GuestFeatureRow("⚡ One-Tap Instant Access to all Engineering Capabilities")
                                    GuestFeatureRow("🚀 1-Tap Direct WhatsApp Connect with Tech Leads")
                                    GuestFeatureRow("💼 Request Free Project & Architecture Consultation")
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    onDirectRoleAccess(
                                        "guest",
                                        CtUser(
                                            uid = "guest_visitor_${System.currentTimeMillis()}",
                                            email = "guest@chittortech.in",
                                            displayName = "Guest Explorer",
                                            companyName = "Public Visitor",
                                            role = "guest",
                                            phone = ""
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)), // Deep Emerald
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Explore ChittorTech as Guest", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // ── 2. CORPORATE / CLIENT LOGIN ────────────────────────
                        LoginRoleTab.CORPORATE -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEFF6FF),
                                    modifier = Modifier.padding(end = 10.dp)
                                ) {
                                    Icon(Icons.Default.Business, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.padding(6.dp).size(20.dp))
                                }
                                Column {
                                    Text("Corporate Client Portal", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Access invoices, signed SOWs & live project milestones", fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Corporate Registered Email") },
                                placeholder = { Text("client@company.com") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CtPrimaryBlue) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Client Portal Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CtPrimaryBlue) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Error display
                            AnimatedVisibility(visible = activeError != null) {
                                activeError?.let { msg ->
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(msg, fontSize = 12.sp, color = CtRed)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = { handleSignInClick("client") },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading && !isSendingOtp
                            ) {
                                if (isLoading || isSendingOtp) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sending OTP via Titan Mail...", fontSize = 13.sp)
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign In with 2FA OTP", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                        }

                        // ── 3. ADMIN PORTAL LOGIN ─────────────────────────────
                        LoginRoleTab.ADMIN -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEF3C7),
                                    modifier = Modifier.padding(end = 10.dp)
                                ) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.padding(6.dp).size(20.dp))
                                }
                                Column {
                                    Text("Administrator Portal", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Authorized Administrative Access", fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Admin Email Address") },
                                placeholder = { Text("admin@yourdomain.com") },
                                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFD97706)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Master Access Key") },
                                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFFD97706)) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            AnimatedVisibility(visible = activeError != null) {
                                activeError?.let { msg ->
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(msg, fontSize = 12.sp, color = CtRed)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = { handleSignInClick("admin") },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading && !isSendingOtp
                            ) {
                                if (isLoading || isSendingOtp) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sending OTP via Titan Mail...", fontSize = 13.sp)
                                } else {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign In with 2FA OTP", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Trust & Compliance Badges ─────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrustBadge(icon = Icons.Default.Security, label = "Firebase Auth")
                TrustBadge(icon = Icons.Default.Verified, label = "DGFT & DPIIT")
                TrustBadge(icon = Icons.Default.Shield, label = "256-bit SSL")
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "ChittorTech © 2026 • chittortech.in",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))
        }

        // ── 2FA OTP Verification Dialog ──────────────────────────────────────
        otpSession?.let { session ->
            OtpVerificationDialog(
                session = session,
                role = pendingRole,
                isVerifying = isVerifyingOtp,
                errorMessage = otpError,
                onVerify = { enteredOtp ->
                    otpError = null
                    isVerifyingOtp = true
                    onVerifyOtp(
                        session.email,
                        enteredOtp,
                        session.token,
                        session.expiresAt,
                        {
                            isVerifyingOtp = false
                            otpSession = null
                            onLoginSuccess(session.email, password.trim(), pendingRole)
                        },
                        { err ->
                            isVerifyingOtp = false
                            otpError = err
                        }
                    )
                },
                onResend = {
                    otpError = null
                    isSendingOtp = true
                    onRequestOtp(
                        email.trim(),
                        password.trim(),
                        pendingRole,
                        { newSession ->
                            isSendingOtp = false
                            otpSession = newSession
                        },
                        { err ->
                            isSendingOtp = false
                            otpError = err
                        }
                    )
                },
                onDismiss = {
                    if (!isVerifyingOtp) {
                        otpSession = null
                        otpError = null
                    }
                }
            )
        }
    }
}

@Composable
private fun RoleTabPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color.White else Color.Transparent,
        modifier = modifier.height(38.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) CtPrimaryBlue else Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) CtPrimaryBlue else Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun GuestFeatureRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text, fontSize = 12.sp, color = Color(0xFF334155), lineHeight = 16.sp)
    }
}

@Composable
private fun TrustBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(CtPrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(17.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OtpVerificationDialog(
    session: OtpSession,
    role: String,
    isVerifying: Boolean,
    errorMessage: String?,
    onVerify: (otp: String) -> Unit,
    onResend: () -> Unit,
    onDismiss: () -> Unit
) {
    var otpDigits by remember { mutableStateOf("") }
    var timeLeftSeconds by remember { mutableStateOf(300) } // 5 minutes

    LaunchedEffect(session) {
        timeLeftSeconds = 300
        while (timeLeftSeconds > 0) {
            delay(1000)
            timeLeftSeconds--
        }
    }

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val timerText = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
    val canResend = timeLeftSeconds <= 270 // 30s cooldown

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        content = {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CtCardWhite,
                tonalElevation = 6.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Icon
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = CtPrimaryBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "2-Step Security Verification",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter the 6-digit code sent to your registered email:",
                        fontSize = 12.5.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = session.email,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CtPrimaryBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.MarkEmailRead,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sent via business@chittortech.in (Titan Mail)",
                            fontSize = 10.5.sp,
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 6-Digit OTP Box Row
                    BasicTextField(
                        value = otpDigits,
                        onValueChange = {
                            if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                otpDigits = it
                                if (it.length == 6) {
                                    onVerify(it)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        decorationBox = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                repeat(6) { index ->
                                    val digit = otpDigits.getOrNull(index)?.toString() ?: ""
                                    val isFocused = otpDigits.length == index
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isFocused) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                                            .border(
                                                width = if (isFocused) 2.dp else 1.dp,
                                                color = if (isFocused) CtPrimaryBlue else Color(0xFFCBD5E1),
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = digit,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CtPrimaryBlue,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    )

                    // Error text
                    AnimatedVisibility(visible = errorMessage != null) {
                        errorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = err,
                                fontSize = 12.sp,
                                color = CtRed,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timer & Resend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (timeLeftSeconds > 60) TextSecondary else CtRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timerText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (timeLeftSeconds > 60) TextSecondary else CtRed
                            )
                        }

                        TextButton(
                            onClick = onResend,
                            enabled = canResend && !isVerifying
                        ) {
                            Text(
                                text = if (canResend) "Resend Code" else "Wait (${300 - timeLeftSeconds}s)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (canResend) CtPrimaryBlue else TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Confirm Verification Button
                    Button(
                        onClick = { onVerify(otpDigits) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                        enabled = otpDigits.length == 6 && !isVerifying && timeLeftSeconds > 0
                    ) {
                        if (isVerifying) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verifying Security Code...", fontSize = 13.sp)
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verify & Continue", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = onDismiss,
                        enabled = !isVerifying
                    ) {
                        Text("Cancel / Change Email", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    )
}
