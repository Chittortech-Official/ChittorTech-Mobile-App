package com.chittortech.app.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.R
import com.chittortech.app.data.OtpSession
import com.chittortech.app.model.CtUser
import com.chittortech.app.theme.*
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// Role enum
// ─────────────────────────────────────────────────────────────────────────────
enum class LoginRoleTab {
    GUEST,      // Public visitors (default)
    CORPORATE   // Verified clients
}

// ─────────────────────────────────────────────────────────────────────────────
// Role colour system — Rich, premium corporate palette
// ─────────────────────────────────────────────────────────────────────────────
private val GuestGrad    = listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF10B981))
private val ClientGrad   = listOf(Color(0xFF0A2540), Color(0xFF0369A1), Color(0xFF0284C7))

private val GuestTint    = Color(0xFF059669)
private val GuestSurface = Color(0xFFECFDF5)
private val ClientTint   = Color(0xFF0284C7)
private val ClientSurface= Color(0xFFEFF6FF)

// ─────────────────────────────────────────────────────────────────────────────
// Main LoginScreen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLoginSuccess: (email: String, password: String, role: String) -> Unit,
    onDirectRoleAccess: (role: String, user: CtUser) -> Unit,
    onRequestOtp: (email: String, password: String, role: String, onSessionReady: (OtpSession) -> Unit, onError: (String) -> Unit) -> Unit = { _, _, _, _, _ -> },
    onVerifyOtp: (email: String, otp: String, token: String, expiresAt: Long, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit = { _, _, _, _, _, _ -> },
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onShowOnboarding: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LoginRoleTab.GUEST) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var otpSession by remember { mutableStateOf<OtpSession?>(null) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    var otpError by remember { mutableStateOf<String?>(null) }
    var pendingRole by remember { mutableStateOf("client") }

    val activeError = localError ?: errorMessage
    val scrollState = rememberScrollState()

    // Reset fields on tab change
    LaunchedEffect(selectedTab) {
        email = ""; password = ""; localError = null; otpSession = null; otpError = null
    }

    val handleSignInClick = { targetRole: String ->
        if (email.isNotBlank() && password.isNotBlank()) {
            localError = null; isSendingOtp = true; pendingRole = targetRole
            onRequestOtp(email.trim(), password.trim(), targetRole,
                { session -> isSendingOtp = false; otpSession = session; otpError = null },
                { err  -> isSendingOtp = false; localError = err }
            )
        }
    }

    // Active role theming
    val activeGrad   = when (selectedTab) { LoginRoleTab.GUEST -> GuestGrad; LoginRoleTab.CORPORATE -> ClientGrad }
    val activeTint   = when (selectedTab) { LoginRoleTab.GUEST -> GuestTint; LoginRoleTab.CORPORATE -> ClientTint }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9)) // Premium light slate base
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Hero Header ───────────────────────────────────────────────────
            HeroHeader(
                activeGrad = activeGrad,
                activeTint = activeTint,
                selectedTab = selectedTab,
                onShowOnboarding = onShowOnboarding
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Role Selector ─────────────────────────────────────────────────
            PremiumRoleSelector(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Login Card ────────────────────────────────────────────────────
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 8 } togetherWith
                    fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 8 }
                },
                label = "LoginCardTransition"
            ) { tab ->
                when (tab) {
                    LoginRoleTab.GUEST -> GuestCard(onDirectRoleAccess = onDirectRoleAccess)
                    LoginRoleTab.CORPORATE -> ClientCard(
                        email = email, onEmailChange = { email = it },
                        password = password, onPasswordChange = { password = it },
                        passwordVisible = passwordVisible, onToggleVisibility = { passwordVisible = !passwordVisible },
                        activeError = activeError, isLoading = isLoading, isSendingOtp = isSendingOtp,
                        onSignIn = { handleSignInClick("client") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Trust strip ───────────────────────────────────────────────────
            TrustStrip()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ChittorTech © 2026 • chittortech.in",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
        }

        // ── OTP Dialog ────────────────────────────────────────────────────────
        otpSession?.let { session ->
            OtpVerificationDialog(
                session = session, role = pendingRole,
                isVerifying = isVerifyingOtp, errorMessage = otpError,
                onVerify = { enteredOtp ->
                    otpError = null; isVerifyingOtp = true
                    onVerifyOtp(session.email, enteredOtp, session.token, session.expiresAt,
                        { isVerifyingOtp = false; otpSession = null; onLoginSuccess(session.email, password.trim(), pendingRole) },
                        { err -> isVerifyingOtp = false; otpError = err }
                    )
                },
                onResend = {
                    otpError = null; isSendingOtp = true
                    onRequestOtp(email.trim(), password.trim(), pendingRole,
                        { newSession -> isSendingOtp = false; otpSession = newSession },
                        { err -> isSendingOtp = false; otpError = err }
                    )
                },
                onDismiss = { if (!isVerifyingOtp) { otpSession = null; otpError = null } }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero Header
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun HeroHeader(
    activeGrad: List<Color>,
    activeTint: Color,
    selectedTab: LoginRoleTab,
    onShowOnboarding: () -> Unit
) {
    val inf = rememberInfiniteTransition(label = "header")
    val pulse by inf.animateFloat(
        0.94f, 1.04f,
        InfiniteRepeatableSpec(tween(2200, easing = EaseInOut), RepeatMode.Reverse),
        label = "pulse"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(activeGrad))
                .padding(top = 28.dp, bottom = 28.dp, start = 20.dp, end = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo + Mascot duo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Logo ring
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.5.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                            .shadow(8.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.chittortech_logo),
                            contentDescription = "ChittorTech Logo",
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    // Connector line
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(2.dp)
                            .background(Color.White.copy(alpha = 0.6f))
                    )

                    // Mascot ring with pulse
                    Box(
                        modifier = Modifier
                            .size((68 * pulse).dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.5.dp, Color.White, CircleShape)
                            .shadow(10.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.chittortech_ai_mascot),
                            contentDescription = "AI Mascot",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ChittorTech",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                val tagline = when (selectedTab) {
                    LoginRoleTab.GUEST     -> "Explore AI · Cloud · Custom Software"
                    LoginRoleTab.CORPORATE -> "Enterprise Client Portal"
                }
                AnimatedContent(targetState = tagline, label = "tagline") { tl ->
                    Text(
                        text = tl,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App tour button — completely inside the header with solid contrast and no clipping
                Surface(
                    onClick = onShowOnboarding,
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.22f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "App Tour & Feature Guide",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Premium Role Selector
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PremiumRoleSelector(
    selectedTab: LoginRoleTab,
    onTabSelected: (LoginRoleTab) -> Unit
) {
    val tabs = listOf(
        Triple(LoginRoleTab.GUEST, "Guest Explorer", Icons.Default.Explore),
        Triple(LoginRoleTab.CORPORATE, "Client Portal", Icons.Default.Business)
    )

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            tabs.forEach { (tab, label, icon) ->
                val isSelected = selectedTab == tab
                val tint = when (tab) {
                    LoginRoleTab.GUEST     -> GuestTint
                    LoginRoleTab.CORPORATE -> ClientTint
                }
                val gradColors = when (tab) {
                    LoginRoleTab.GUEST     -> GuestGrad
                    LoginRoleTab.CORPORATE -> ClientGrad
                }

                val scale by animateFloatAsState(
                    if (isSelected) 1f else 0.95f, tween(200), label = "scale"
                )
                val elevation by animateDpAsState(
                    if (isSelected) 4.dp else 0.dp, tween(200), label = "elev"
                )

                Surface(
                    onClick = { onTabSelected(tab) },
                    shape = RoundedCornerShape(13.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (isSelected) Modifier.background(
                                    Brush.linearGradient(gradColors),
                                    RoundedCornerShape(13.dp)
                                ) else Modifier.background(Color.Transparent, RoundedCornerShape(13.dp))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                icon, null,
                                tint = if (isSelected) Color.White else Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Guest Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun GuestCard(onDirectRoleAccess: (role: String, user: CtUser) -> Unit) {
    PremiumCard {
        // Role header
        RoleCardHeader(
            icon = Icons.Default.Explore,
            iconTint = GuestTint,
            iconBg = GuestSurface,
            title = "Guest Explorer Mode",
            subtitle = "No password needed — explore everything freely"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Feature grid
        val features = listOf(
            Pair(Icons.Default.Category, "Full Services Catalog"),
            Pair(Icons.Default.SmartToy, "ChittorTech AI Chatbot"),
            Pair(Icons.Default.Forum, "1-Tap WhatsApp Connect"),
            Pair(Icons.Default.RequestQuote, "Free Project Consultation"),
            Pair(Icons.Default.Speed, "Instant Access · No OTP"),
            Pair(Icons.Default.EmojiEvents, "Portfolio & Case Studies")
        )

        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            features.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { (icon, label) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GuestSurface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                            ) {
                                Icon(icon, null, tint = GuestTint, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(7.dp))
                                Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF134E4A), lineHeight = 14.sp)
                            }
                        }
                    }
                    // Fill empty slot if odd
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // CTA Button
        GradientButton(
            label = "Explore ChittorTech as Guest",
            icon = Icons.Default.RocketLaunch,
            gradient = Brush.linearGradient(GuestGrad),
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
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Your session is private. No account or signup required.",
            fontSize = 10.5.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Client / Corporate Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ClientCard(
    email: String, onEmailChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean, onToggleVisibility: () -> Unit,
    activeError: String?, isLoading: Boolean, isSendingOtp: Boolean,
    onSignIn: () -> Unit
) {
    PremiumCard {
        RoleCardHeader(
            icon = Icons.Default.Business,
            iconTint = ClientTint,
            iconBg = ClientSurface,
            title = "Corporate Client Portal",
            subtitle = "Access invoices, SOWs & live project milestones"
        )

        Spacer(modifier = Modifier.height(22.dp))

        PremiumTextField(
            value = email, onValueChange = onEmailChange,
            label = "Corporate Email", placeholder = "client@yourcompany.com",
            leadingIcon = Icons.Default.Email, accentColor = ClientTint,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(14.dp))

        PremiumTextField(
            value = password, onValueChange = onPasswordChange,
            label = "Client Portal Password", placeholder = "••••••••",
            leadingIcon = Icons.Default.Lock, accentColor = ClientTint,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { onSignIn() }),
            isPassword = true,
            passwordVisible = passwordVisible,
            onToggleVisibility = onToggleVisibility
        )

        AnimatedVisibility(visible = activeError != null) {
            activeError?.let { msg ->
                Spacer(modifier = Modifier.height(10.dp))
                ErrorBanner(msg)
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        GradientButton(
            label = if (isLoading || isSendingOtp) "Sending OTP via Titan Mail…" else "Sign In with 2FA OTP",
            icon = Icons.AutoMirrored.Filled.Login,
            gradient = Brush.linearGradient(ClientGrad),
            enabled = !isLoading && !isSendingOtp && email.isNotBlank() && password.isNotBlank(),
            isLoading = isLoading || isSendingOtp,
            onClick = onSignIn
        )

        Spacer(modifier = Modifier.height(12.dp))
        SecurityNote("2FA · Firebase Auth · 256-bit SSL · OTP via Titan Mail")
    }
}



// ─────────────────────────────────────────────────────────────────────────────
// Reusable UI components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PremiumCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            content = content
        )
    }
}

@Composable
private fun RoleCardHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF64748B), lineHeight = 15.sp)
        }
    }
}

@Composable
private fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onToggleVisibility: () -> Unit = {}
) {
    val isFocused = value.isNotEmpty()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = { Text(placeholder, color = Color(0xFFCBD5E1), fontSize = 13.sp) },
        leadingIcon = {
            Icon(
                leadingIcon, null,
                tint = if (isFocused) accentColor else Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        null, tint = Color(0xFF94A3B8), modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = keyboardActions,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accentColor,
            unfocusedBorderColor = Color(0xFFCBD5E1),
            focusedLabelColor = accentColor,
            unfocusedLabelColor = Color(0xFF64748B),
            cursorColor = accentColor,
            focusedContainerColor = Color(0xFFFAFCFF),
            unfocusedContainerColor = Color(0xFFF8FAFC)
        )
    )
}

@Composable
private fun GradientButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(if (enabled) 1f else 0.55f, label = "btnAlpha")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .alpha(alpha)
            .background(gradient)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
            } else {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun ErrorBanner(message: String, isAdmin: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isAdmin) Color(0xFFFFF1F2) else Color(0xFFFEF2F2),
        border = BorderStroke(1.dp, if (isAdmin) Color(0xFFFCA5A5) else Color(0xFFFECACA))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFEF4444), modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(message, fontSize = 12.sp, color = Color(0xFF991B1B), lineHeight = 16.sp)
        }
    }
}

@Composable
private fun SecurityNote(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.Lock, null, tint = Color(0xFF94A3B8), modifier = Modifier.size(11.dp))
        Spacer(modifier = Modifier.width(5.dp))
        Text(text, fontSize = 10.sp, color = Color(0xFF94A3B8), textAlign = TextAlign.Center)
    }
}

@Composable
private fun TrustStrip() {
    val badges = listOf(
        Triple(Icons.Default.Security, "Firebase Auth", Color(0xFF0284C7)),
        Triple(Icons.Default.Verified, "DGFT & DPIIT", Color(0xFF10B981)),
        Triple(Icons.Default.Shield, "256-bit SSL", Color(0xFF8B5CF6))
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            badges.forEachIndexed { i, (icon, label, color) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                }
                if (i < badges.size - 1) {
                    Box(Modifier.width(1.dp).height(32.dp).background(Color(0xFFE2E8F0)))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// OTP Verification Dialog
// ─────────────────────────────────────────────────────────────────────────────
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
    var timeLeftSeconds by remember { mutableStateOf(300) }

    LaunchedEffect(session) {
        timeLeftSeconds = 300
        while (timeLeftSeconds > 0) { delay(1000); timeLeftSeconds-- }
    }

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val timerText = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
    val canResend = timeLeftSeconds <= 270

    val dialogTint = when (role) {
        "client" -> ClientTint
        else -> GuestTint
    }
    val dialogGrad = when (role) {
        "client" -> ClientGrad
        else -> GuestGrad
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
    ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 24.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header icon with gradient ring
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(dialogGrad)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, null, tint = Color.White, modifier = Modifier.size(30.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("2-Step Security Verification", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Enter the 6-digit code sent to:",
                        fontSize = 13.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(shape = RoundedCornerShape(8.dp), color = dialogTint.copy(alpha = 0.08f)) {
                        Text(
                            session.email, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                            color = dialogTint, modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MarkEmailRead, null, tint = Color(0xFF10B981), modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Via business@chittortech.in (Titan Mail)", fontSize = 10.5.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 6-digit OTP boxes
                    BasicTextField(
                        value = otpDigits,
                        onValueChange = {
                            if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                otpDigits = it
                                if (it.length == 6) onVerify(it)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        decorationBox = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                repeat(6) { index ->
                                    val digit = otpDigits.getOrNull(index)?.toString() ?: ""
                                    val isCurrent = otpDigits.length == index
                                    val isFilled = index < otpDigits.length
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                when {
                                                    isFilled -> dialogTint.copy(alpha = 0.08f)
                                                    isCurrent -> dialogTint.copy(alpha = 0.04f)
                                                    else -> Color(0xFFF8FAFC)
                                                }
                                            )
                                            .border(
                                                width = if (isCurrent || isFilled) 2.dp else 1.dp,
                                                color = if (isCurrent || isFilled) dialogTint else Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            digit, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                                            color = dialogTint, textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    )

                    AnimatedVisibility(visible = errorMessage != null) {
                        errorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(10.dp))
                            ErrorBanner(err)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Timer + Resend row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(8.dp), color = if (timeLeftSeconds > 60) Color(0xFFF1F5F9) else Color(0xFFFEF2F2)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(Icons.Default.Timer, null, tint = if (timeLeftSeconds > 60) Color(0xFF64748B) else Color(0xFFEF4444), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(timerText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (timeLeftSeconds > 60) Color(0xFF374151) else Color(0xFFEF4444))
                            }
                        }
                        TextButton(onClick = onResend, enabled = canResend && !isVerifying) {
                            Text(
                                if (canResend) "↺ Resend Code" else "Wait (${300 - timeLeftSeconds}s)",
                                fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                color = if (canResend) dialogTint else Color(0xFF94A3B8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    GradientButton(
                        label = if (isVerifying) "Verifying…" else "Verify & Continue",
                        icon = Icons.Default.CheckCircle,
                        gradient = Brush.linearGradient(dialogGrad),
                        enabled = otpDigits.length == 6 && !isVerifying && timeLeftSeconds > 0,
                        isLoading = isVerifying,
                        onClick = { onVerify(otpDigits) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = onDismiss, enabled = !isVerifying) {
                        Text("Cancel / Change Email", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    }
                }
            }
        }
}
