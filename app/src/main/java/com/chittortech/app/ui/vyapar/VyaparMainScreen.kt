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
import androidx.compose.foundation.lazy.items
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
import com.chittortech.app.model.AppNotification
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
    val notifications by repository.observeNotifications().collectAsStateWithLifecycle(initialValue = emptyList())

    // Dialog & Notification Read States
    var showNotificationsDialog by remember { mutableStateOf(false) }
    val notifPrefs = remember { context.getSharedPreferences("ct_notifs_prefs", android.content.Context.MODE_PRIVATE) }
    var readNotifIds by remember {
        mutableStateOf(notifPrefs.getStringSet("read_ids", emptySet())?.toSet() ?: emptySet())
    }

    val unreadCount = remember(notifications, readNotifIds) {
        notifications.count { it.id !in readNotifIds }
    }

    // Keyboard (IME) detection to hide bottom bar when typing in Chatbot / fields
    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
    val isImeOpen = imeBottom > 0

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            VyaparTopBar(
                businessName = if (user.companyName.isNotBlank() && user.companyName != "ChittorTech Solutions" && user.companyName != "Public Visitor") user.companyName else "ChittorTech",
                notificationCount = unreadCount,
                onNotificationClick = { showNotificationsDialog = true }
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
                        onOpenAiChat = { currentTab = VyaparTab.SERVICES }
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
                        onHelpdeskClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://wa.me/917597451057?text=Namaste%20Lav%20Sir!%20I%20need%20assistance%20regarding%20ChittorTech%20services.")
                            }
                            context.startActivity(intent)
                        },
                        onSignOut = {
                            repository.signOut()
                            onSignOut()
                        }
                    )
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
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = {
                                val allIds = notifications.map { it.id }.toSet()
                                readNotifIds = readNotifIds + allIds
                                notifPrefs.edit().putStringSet("read_ids", readNotifIds).apply()
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
                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.NotificationsNone,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Notifications",
                                fontWeight = FontWeight.Bold,
                                color = VyaparDark,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "You're all caught up! New updates from admin will appear here.",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(notifications) { notif ->
                            val isUnread = notif.id !in readNotifIds
                            NotificationCardItem(
                                title = notif.title,
                                time = notif.timestamp?.toDate()?.let { d ->
                                    java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault()).format(d)
                                } ?: "Just now",
                                detail = notif.message,
                                isUnread = isUnread,
                                onClick = {
                                    if (isUnread) {
                                        readNotifIds = readNotifIds + notif.id
                                        notifPrefs.edit().putStringSet("read_ids", readNotifIds).apply()
                                    }
                                }
                            )
                        }
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
}

// ── Notification Card Item ────────────────────────────────────────────────────
@Composable
private fun NotificationCardItem(
    title: String,
    time: String,
    detail: String,
    isUnread: Boolean,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isUnread) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isUnread) Color(0xFFBFDBFE) else Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
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
