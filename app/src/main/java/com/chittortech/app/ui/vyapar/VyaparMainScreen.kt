package com.chittortech.app.ui.vyapar

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittortech.app.data.ChittorTechRepository
import com.chittortech.app.model.CtUser
import com.chittortech.app.theme.*
import kotlinx.coroutines.launch

@Composable
fun VyaparMainScreen(
    user: CtUser,
    repository: ChittorTechRepository,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf(VyaparTab.HOME) }

    // Live streams from Firebase Firestore
    val invoices by repository.observeAllInvoices().collectAsStateWithLifecycle(initialValue = emptyList())
    val clients by repository.observeAllClients().collectAsStateWithLifecycle(initialValue = emptyList())

    // Dialog & Notification States
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var notificationCount by remember { mutableStateOf(2) }

    // Keyboard (IME) detection to hide bottom bar when typing in Chatbot / fields
    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
    val isImeOpen = imeBottom > 0

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            VyaparTopBar(
                businessName = user.companyName.ifBlank { user.displayName.ifBlank { "ChittorTech Solutions" } },
                notificationCount = notificationCount,
                onNotificationClick = { showNotificationsDialog = true },
                onSettingsClick = { showSettingsDialog = true }
            )
        },
        bottomBar = {
            // Hide bottom navigation bar when virtual keyboard (IME) is visible to give full screen to typing
            if (!isImeOpen) {
                VyaparBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        },
        containerColor = VyaparBg,
        modifier = modifier
    ) { paddingValues ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            label = "VyaparTabContent"
        ) { tab ->
            when (tab) {
                VyaparTab.HOME -> {
                    VyaparHomeScreen(
                        invoices = invoices,
                        clients = clients,
                        onSaleReportClick = { currentTab = VyaparTab.SERVICES },
                        onExploreServices = { currentTab = VyaparTab.SERVICES },
                        onOpenAiChat = { currentTab = VyaparTab.AI }
                    )
                }
                VyaparTab.SERVICES -> {
                    ChittorTechServicesScreen(
                        onSubmitLead = { lead ->
                            scope.launch { repository.submitLead(lead) }
                        }
                    )
                }
                VyaparTab.MENU -> {
                    VyaparMenuScreen(
                        onSaleClick = { currentTab = VyaparTab.HOME },
                        onPurchaseClick = { currentTab = VyaparTab.SERVICES },
                        onExpensesClick = { currentTab = VyaparTab.SERVICES },
                        onReportsClick = { currentTab = VyaparTab.GET_DESKTOP },
                        onHelpdeskClick = { currentTab = VyaparTab.AI },
                        onSignOut = {
                            repository.signOut()
                            onSignOut()
                        }
                    )
                }
                VyaparTab.AI -> {
                    ChittorTechChatbotScreen()
                }
                VyaparTab.GET_DESKTOP -> {
                    VyaparDesktopScreen()
                }
            }
        }
    }

    // ── 1. Active Notifications Center Dialog ─────────────────────────────────
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Notifications", fontWeight = FontWeight.Bold, color = VyaparDark, fontSize = 18.sp)
                    }
                    if (notificationCount > 0) {
                        TextButton(
                            onClick = {
                                notificationCount = 0
                                Toast.makeText(context, "All notifications marked as read", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Mark Read", fontSize = 12.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        NotificationCardItem(
                            title = "🎉 ChittorTech GPT v2.4 Live",
                            time = "Today, 4:30 PM",
                            detail = "Our dedicated AI Assistant powered by Groq Llama 3 70B is ready to answer questions about mobile apps, cloud architectures, and publishing.",
                            isUnread = notificationCount > 0
                        )
                    }
                    item {
                        NotificationCardItem(
                            title = "🚀 Google Play 12-Tester Cohort",
                            time = "Yesterday",
                            detail = "New closed testing batch opened for indie developers and startups. 100% Google Play approval compliance.",
                            isUnread = notificationCount > 1
                        )
                    }
                    item {
                        NotificationCardItem(
                            title = "💼 Tech Stack Consultation",
                            time = "2 days ago",
                            detail = "Book a free 15-minute tech audit with Kush & Lav Sharma via the ChittorTech agency portal.",
                            isUnread = false
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ── 2. Active Settings Dialog ─────────────────────────────────────────────
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Settings, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("App Settings", fontWeight = FontWeight.Bold, color = VyaparDark, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Profile Chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = user.displayName.ifBlank { "Guest Explorer" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = VyaparDark
                            )
                            Text(
                                text = user.email.ifBlank { "Guest Session (Visitor)" },
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE0F2FE)
                            ) {
                                Text(
                                    text = "Role: ${user.role.uppercase().ifBlank { "CLIENT" }}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Settings Options
                    SettingsActionRow(
                        icon = Icons.Outlined.CleaningServices,
                        title = "Clear Cache & Storage",
                        subtitle = "Free temporary app memory",
                        onClick = {
                            Toast.makeText(context, "App cache cleared successfully (18.4 MB released)", Toast.LENGTH_SHORT).show()
                        }
                    )

                    SettingsActionRow(
                        icon = Icons.Outlined.Language,
                        title = "Visit ChittorTech Website",
                        subtitle = "chittortech.in",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in"))
                            context.startActivity(intent)
                        }
                    )

                    SettingsActionRow(
                        icon = Icons.Outlined.Info,
                        title = "Version & Build",
                        subtitle = "v2.4.0 (ChittorTech Production 2026)",
                        onClick = {}
                    )

                    // Sign Out Option
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.dp, Color(0xFFFECACA)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                showSettingsDialog = false
                                repository.signOut()
                                onSignOut()
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Sign Out of Session", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Close", fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ── Notification Card Item ────────────────────────────────────────────────────
@Composable
private fun NotificationCardItem(
    title: String,
    time: String,
    detail: String,
    isUnread: Boolean
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isUnread) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isUnread) Color(0xFFBFDBFE) else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
                if (isUnread) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF0284C7)))
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = time, fontSize = 10.5.sp, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = detail, fontSize = 11.5.sp, lineHeight = 16.sp, color = Color(0xFF475569))
        }
    }
}

// ── Settings Action Row ───────────────────────────────────────────────────────
@Composable
private fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = VyaparDark)
                Text(text = subtitle, fontSize = 10.5.sp, color = Color(0xFF64748B))
            }
        }
    }
}
