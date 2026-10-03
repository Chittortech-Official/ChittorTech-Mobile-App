package com.chittortech.app.ui.vyapar

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

// ── Falling Particle Data ─────────────────────────────────────────────────────
private data class FallingParticle(
    val id: Int,
    val xRatio: Float,
    val size: Float,
    val color: Color,
    val speed: Float,
    val initialDelay: Int
)

@Composable
fun VyaparDesktopScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("chittortech_desktop_prefs", android.content.Context.MODE_PRIVATE) }
    var isRegistered by remember { mutableStateOf(prefs.getBoolean("desktop_early_access_registered", false)) }
    var isRaining by remember { mutableStateOf(false) }
    var showNotifyDialog by remember { mutableStateOf(false) }

    val handleNotifyClick: () -> Unit = {
        if (isRegistered) {
            Toast.makeText(context, "You are already registered! We will notify you immediately on release.", Toast.LENGTH_SHORT).show()
            isRaining = true
        } else {
            prefs.edit().putBoolean("desktop_early_access_registered", true).apply()
            isRegistered = true
            isRaining = true
            showNotifyDialog = true
        }
    }

    // Particle shower animation trigger
    val particles = remember {
        List(40) { index ->
            FallingParticle(
                id = index,
                xRatio = Random.nextFloat(),
                size = Random.nextFloat() * 14f + 6f,
                color = when (index % 5) {
                    0 -> Color(0xFF38BDF8) // Sky blue
                    1 -> Color(0xFF6366F1) // Indigo
                    2 -> Color(0xFF10B981) // Emerald
                    3 -> Color(0xFFF59E0B) // Amber
                    else -> Color(0xFFEC4899) // Pink
                },
                speed = Random.nextFloat() * 0.8f + 0.6f,
                initialDelay = Random.nextInt(0, 500)
            )
        }
    }

    LaunchedEffect(isRaining) {
        if (isRaining) {
            delay(3500)
            isRaining = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VyaparBg)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Main Coming Soon Card ─────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                border = BorderStroke(1.dp, VyaparCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // "COMING SOON" Status Chip (Clickable for Bubble Rain!)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isRegistered) Color(0xFFECFDF5) else Color(0xFFEFF6FF),
                        border = BorderStroke(1.5.dp, if (isRegistered) Color(0xFF10B981) else Color(0xFF38BDF8)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(onClick = handleNotifyClick)
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isRegistered) Color(0xFF10B981) else Color(0xFF0284C7))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRegistered) "✓ LAUNCH VIP ALERT ACTIVE" else "✨ COMING SOON • TAP HERE",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isRegistered) Color(0xFF047857) else Color(0xFF0369A1),
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Windows / Mac Desktop Visual Matrix
                    Row(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFF25022)))
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF00A4EF)))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF7FBA00)))
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFFFB900)))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "ChittorTech Enterprise Desktop",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = VyaparDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Unified workstation for Windows & macOS. Effortlessly manage your live software deployments, support desk, official billing records, and cloud analytics from one powerhouse desktop hub.",
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Coming Soon Button
                    Button(
                        onClick = handleNotifyClick,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRegistered) Color(0xFF0F766E) else Color(0xFF0F172A)
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = if (isRegistered) Icons.Outlined.CheckCircle else Icons.Outlined.NotificationsActive,
                            contentDescription = null,
                            tint = if (isRegistered) Color(0xFF34D399) else Color(0xFFFBBF24),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRegistered) "✓ Registered for Launch Early-Access" else "Notify Me On Launch • Windows & Mac",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // ── Desktop Features Card (Custom ChittorTech Enterprise Suite) ───
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                border = BorderStroke(1.dp, VyaparCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Devices, contentDescription = null, tint = VyaparBlue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Enterprise Desktop Capabilities", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
                    }

                    // 1. Live Real-Time Sync
                    DesktopFeatureRow(
                        icon = Icons.Outlined.Sync,
                        title = "Live Real-Time Sync",
                        subtitle = "Instant bi-directional state synchronization across Mobile, Web, and Desktop with zero latency."
                    )

                    // 2. Products Admin Panel Access
                    DesktopFeatureRow(
                        icon = Icons.Outlined.DashboardCustomize,
                        title = "Products & Portal Admin Panel",
                        subtitle = "Central control over your deployed web apps, custom microservices, and ChittorTech software suites."
                    )

                    // 3. Support Ticket System & Query Desk
                    DesktopFeatureRow(
                        icon = Icons.Outlined.ConfirmationNumber,
                        title = "Support Tickets & Query Desk",
                        subtitle = "Raise engineering tickets, track bug fixes with real-time status updates, and chat with your assigned tech lead."
                    )

                    // 4. Official Invoices & Billing Records
                    DesktopFeatureRow(
                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                        title = "Invoice & Billing Vault",
                        subtitle = "Instant access to all verified GST invoices, transaction statements, payment receipts, and project contracts."
                    )

                    // 5. Request Access & Team Roles
                    DesktopFeatureRow(
                        icon = Icons.Outlined.AdminPanelSettings,
                        title = "Request Access & Role Delegation",
                        subtitle = "Request additional team seats, manage employee access privileges, and configure environment variables safely."
                    )

                    // 6. Enterprise Security
                    DesktopFeatureRow(
                        icon = Icons.Outlined.Security,
                        title = "Enterprise Zero-Knowledge Vault",
                        subtitle = "Hardware-accelerated AES-256 encryption safeguarding sensitive database credentials and API keys."
                    )
                }
            }

            // ── Technical Specs Card ──────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                border = BorderStroke(1.dp, VyaparCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "System Compatibility & Architecture",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = VyaparDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpecChip(modifier = Modifier.weight(1f), title = "Windows", detail = "10 / 11 (64-bit / ARM)")
                        SpecChip(modifier = Modifier.weight(1f), title = "macOS", detail = "Sonoma / Sequoia (M1/M2/M3/M4)")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpecChip(modifier = Modifier.weight(1f), title = "Sync Engine", detail = "Google Firestore Cloud")
                        SpecChip(modifier = Modifier.weight(1f), title = "Engine", detail = "Compose Multiplatform Native")
                    }
                }
            }
        }

        // ── Floating Bubbles / Confetti Rain Overlay ──────────────────────────
        if (isRaining) {
            BubbleShowerCanvas(particles = particles)
        }

        // ── Notification Confirmation Dialog ──────────────────────────────────
        if (showNotifyDialog) {
            AlertDialog(
                onDismissRequest = { showNotifyDialog = false },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsActive,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Launch Alert Registered!",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = VyaparDark
                    )
                },
                text = {
                    Text(
                        text = "We will notify as soon as our ChittorTech Enterprise Desktop will be available for the users.\n\nYou will get direct early-access download links for Windows and Mac!",
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF475569)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showNotifyDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Got it, Thank You!", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

// ── Canvas-based Bubble Rain Component ────────────────────────────────────────
@Composable
private fun BubbleShowerCanvas(particles: List<FallingParticle>) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubbleShower")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainProgress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { particle ->
            // Calculate y offset with speed & looping
            val yOffset = ((progress * particle.speed * 1.5f + (particle.id * 0.05f)) % 1f) * (canvasHeight + 100f) - 50f
            val xOffset = particle.xRatio * canvasWidth

            drawCircle(
                color = particle.color.copy(alpha = 0.85f),
                radius = particle.size,
                center = Offset(xOffset, yOffset)
            )
        }
    }
}

@Composable
private fun DesktopFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFF1F5F9),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = VyaparBlue, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = VyaparDark)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = subtitle, fontSize = 12.sp, lineHeight = 17.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
private fun SpecChip(
    modifier: Modifier = Modifier,
    title: String,
    detail: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier.height(66.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = detail, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VyaparDark, maxLines = 1)
        }
    }
}
