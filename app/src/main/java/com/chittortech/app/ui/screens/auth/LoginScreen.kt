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
import com.chittortech.app.R
import com.chittortech.app.model.CtUser
import com.chittortech.app.theme.*

enum class LoginRoleTab {
    GUEST,      // Open to all public users / visitors (1st & Default)
    CORPORATE,  // Verified Clients / Enterprises (2nd)
    ADMIN       // Founders (Kush Sharma & Lav Sharma) (3rd)
}

@Composable
fun LoginScreen(
    onLoginSuccess: (email: String, password: String, role: String) -> Unit,
    onDirectRoleAccess: (role: String, user: CtUser) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LoginRoleTab.GUEST) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
                            Color(0xFF0F172A), // Slate 900
                            Color(0xFF0369A1), // Ocean Dark
                            CtPrimaryBlue      // Ocean Blue
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
                        .background(Color(0xFF0F172A))
                        .border(2.dp, Color(0xFF38BDF8), CircleShape),
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
                        onClick = { selectedTab = LoginRoleTab.GUEST },
                        modifier = Modifier.weight(1f)
                    )
                    RoleTabPill(
                        label = "Corporate",
                        icon = Icons.Default.Business,
                        isSelected = selectedTab == LoginRoleTab.CORPORATE,
                        onClick = { selectedTab = LoginRoleTab.CORPORATE },
                        modifier = Modifier.weight(1f)
                    )
                    RoleTabPill(
                        label = "Admin",
                        icon = Icons.Default.AdminPanelSettings,
                        isSelected = selectedTab == LoginRoleTab.ADMIN,
                        onClick = { selectedTab = LoginRoleTab.ADMIN },
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
                                    GuestFeatureRow("📊 Test Quick Launch Vyapar Billing & Sales features")
                                    GuestFeatureRow("🤖 AI Chatbot with Kaira & Groq LPU sub-500ms answers")
                                    GuestFeatureRow("🚀 1-Tap WhatsApp connect with Founder & Tech Leads")
                                    GuestFeatureRow("💼 Request Free 15-Minute Project & Tech Consultation")
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
                            AnimatedVisibility(visible = errorMessage != null) {
                                errorMessage?.let { msg ->
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(msg, fontSize = 12.sp, color = CtRed)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    if (email.isNotBlank() && password.isNotBlank()) {
                                        onLoginSuccess(email.trim(), password, "client")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign In to Client Portal", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Instant Client Demo Access Button
                            OutlinedButton(
                                onClick = {
                                    onDirectRoleAccess(
                                        "client",
                                        CtUser(
                                            uid = "demo_client_amplr",
                                            email = "client@amplrhealth.com",
                                            displayName = "AMPLR Health Enterprise",
                                            companyName = "AMPLR Health Services",
                                            role = "client",
                                            phone = "+91 79978 88448"
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, CtPrimaryBlue)
                            ) {
                                Text("Instant Client Preview (AMPLR Health)", color = CtPrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // ── 2. ADMIN / FOUNDER LOGIN ──────────────────────────
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
                                    Text("Founder Command Center", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Kush Sharma & Lav Sharma (Founder & Tech Lead)", fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Founder Email (kush@chittortech.in)") },
                                placeholder = { Text("kush@chittortech.in") },
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

                            AnimatedVisibility(visible = errorMessage != null) {
                                errorMessage?.let { msg ->
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(msg, fontSize = 12.sp, color = CtRed)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    if (email.isNotBlank() && password.isNotBlank()) {
                                        onLoginSuccess(email.trim(), password, "admin")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                } else {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign In as Founder / Admin", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Instant Founder 1-Tap Access Button
                            OutlinedButton(
                                onClick = {
                                    onDirectRoleAccess(
                                        "admin",
                                        CtUser(
                                            uid = "founder_kush",
                                            email = "kush@chittortech.in",
                                            displayName = "Kush Sharma",
                                            companyName = "ChittorTech Solutions",
                                            role = "admin",
                                            phone = "+91 75974 51057"
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFD97706))
                            ) {
                                Text("Instant Founder Mode (Kush Sharma)", color = Color(0xFFB45309), fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
