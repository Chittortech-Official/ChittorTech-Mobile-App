package com.chittortech.app.ui.main

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittortech.app.data.ChittorTechRepository
import com.chittortech.app.model.CtUser
import com.chittortech.app.theme.*
import com.chittortech.app.ui.screens.client.*
import com.chittortech.app.util.NotificationHelper
import kotlinx.coroutines.launch

// ─── Client Tab Enum ──────────────────────────────────────────────────────────

enum class ClientTab(val label: String, val icon: ImageVector) {
    DASHBOARD("Home", Icons.Default.Home),
    INVOICES("Invoices", Icons.Default.Receipt),
    HELPDESK("Helpdesk", Icons.Default.SupportAgent),
    PROFILE("Profile", Icons.Default.Person)
}

// ─── Client Main Screen ───────────────────────────────────────────────────────

@Composable
fun ClientMainScreen(
    user: CtUser,
    repository: ChittorTechRepository,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf(ClientTab.DASHBOARD) }
    val snackbarHostState = remember { SnackbarHostState() }

    var activeUser by remember(user) { mutableStateOf(user) }

    // Firebase live data (supports both UID and email as clientId)
    val project       by repository.observeClientProject(activeUser.uid, activeUser.email).collectAsStateWithLifecycle(null)
    val invoices      by repository.observeClientInvoices(activeUser.uid, activeUser.email).collectAsStateWithLifecycle(emptyList())
    val tickets       by repository.observeClientTickets(activeUser.uid, activeUser.email).collectAsStateWithLifecycle(emptyList())
    val notifications by repository.observeNotifications().collectAsStateWithLifecycle(emptyList())

    // ── Welcome Notification on Login / App Open ──────────────────────────────
    var hasShownWelcomeNotification by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(activeUser.email) {
        if (!hasShownWelcomeNotification && activeUser.email.isNotBlank()) {
            hasShownWelcomeNotification = true
            val nameOrCompany = activeUser.companyName.ifBlank { activeUser.displayName.ifBlank { "Client" } }
            NotificationHelper.showNotification(
                context = context,
                title = "👋 Welcome Back, $nameOrCompany!",
                message = "Hello, welcome back $nameOrCompany! Have a nice time with our official app."
            )
        }
    }

    // ── Live Broadcast Notifications from Admin ───────────────────────────────
    var lastNotifId by remember { mutableStateOf<String?>(null) }
    var isNotifFirstEmission by remember { mutableStateOf(true) }

    LaunchedEffect(notifications) {
        if (notifications.isNotEmpty()) {
            val latest = notifications.first()
            if (isNotifFirstEmission) {
                lastNotifId = latest.id
                isNotifFirstEmission = false
            } else if (latest.id != lastNotifId) {
                lastNotifId = latest.id
                NotificationHelper.showNotification(
                    context = context,
                    title = "📢 " + latest.title.ifBlank { "ChittorTech Announcement" },
                    message = latest.message
                )
            }
        }
    }

    // ── Live Ticket Status Updates (when Admin marks In Progress / Resolved) ──
    var prevTicketStatusMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isTicketsFirstEmission by remember { mutableStateOf(true) }

    LaunchedEffect(tickets) {
        if (tickets.isNotEmpty()) {
            if (isTicketsFirstEmission) {
                prevTicketStatusMap = tickets.associate { it.ticketId to it.status }
                isTicketsFirstEmission = false
            } else {
                tickets.forEach { t ->
                    val prev = prevTicketStatusMap[t.ticketId]
                    if (prev != null && prev != t.status) {
                        NotificationHelper.showNotification(
                            context = context,
                            title = "🛠️ Ticket #${t.ticketId} Updated",
                            message = "Status changed to ${t.status}" +
                                    if (t.resolutionNote.isNotBlank()) " • Note: ${t.resolutionNote}" else ""
                        )
                    }
                }
                prevTicketStatusMap = tickets.associate { it.ticketId to it.status }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = CtCardWhite,
                tonalElevation = 8.dp
            ) {
                ClientTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick  = { currentTab = tab },
                        icon = {
                            Icon(tab.icon, contentDescription = tab.label)
                        },
                        label = {
                            Text(tab.label, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = CtPrimaryBlue,
                            selectedTextColor   = CtPrimaryBlue,
                            indicatorColor      = CtPrimaryLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) { padding ->
        AnimatedContent(
            targetState = currentTab,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { tab ->
            when (tab) {
                ClientTab.DASHBOARD -> ClientDashboardScreen(
                    user = activeUser,
                    project = project,
                    invoices = invoices,
                    tickets  = tickets,
                    onViewInvoices    = { currentTab = ClientTab.INVOICES },
                    onRaiseTicket     = { currentTab = ClientTab.HELPDESK },
                    onContactEngineer = {
                        val whatsapp = "https://wa.me/917997888448?text=Hello+ChittorTech+Team"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(whatsapp)))
                    }
                )

                ClientTab.INVOICES -> InvoicesScreen(invoices = invoices)

                ClientTab.HELPDESK -> HelpdeskScreen(
                    clientId    = activeUser.email.trim().lowercase().ifBlank { activeUser.uid },
                    projectId   = project?.projectId ?: "",
                    clientName  = activeUser.displayName,
                    companyName = activeUser.companyName,
                    tickets     = tickets,
                    onCreateTicket = { ticket ->
                        scope.launch {
                            val res = repository.createTicket(ticket, clientEmail = activeUser.email)
                            if (res.isSuccess) {
                                val tktId = res.getOrNull() ?: "TKT"
                                snackbarHostState.showSnackbar("Ticket #$tktId submitted successfully!")
                                NotificationHelper.showNotification(
                                    context = context,
                                    title = "🎫 Ticket Raised Successfully!",
                                    message = "Ticket #$tktId (${ticket.category} - ${ticket.priority}) has been received by our technical team."
                                )
                            } else {
                                snackbarHostState.showSnackbar("Failed to submit ticket: ${res.exceptionOrNull()?.message}")
                            }
                        }
                    }
                )

                ClientTab.PROFILE -> ClientProfileScreen(
                    user = activeUser,
                    onLogout = {
                        repository.signOut()
                        onSignOut()
                    },
                    onUpdateUser = { updated, onDone ->
                        scope.launch {
                            val res = repository.updateUserProfile(updated)
                            if (res.isSuccess) {
                                activeUser = updated
                                onDone(true, null)
                                val targetName = updated.displayName.ifBlank { "Client" }
                                NotificationHelper.showNotification(
                                    context = context,
                                    title = "✅ Account Details Updated",
                                    message = "Profile details for $targetName have been updated successfully."
                                )
                            } else {
                                val err = res.exceptionOrNull()?.message ?: "Permission denied or network issue"
                                onDone(false, err)
                                snackbarHostState.showSnackbar("Failed to update profile: $err")
                            }
                        }
                    },
                    onChangePassword = { oldPassword, newPassword, onDone ->
                        scope.launch {
                            val res = repository.updatePassword(oldPassword, newPassword, activeUser.email)
                            if (res.isSuccess) {
                                onDone(true, null)
                                NotificationHelper.showNotification(
                                    context = context,
                                    title = "🔐 Password Changed Successfully",
                                    message = "Your portal access password has been updated securely."
                                )
                            } else {
                                val err = res.exceptionOrNull()?.message ?: "Failed to update password"
                                onDone(false, err)
                                snackbarHostState.showSnackbar("Password change failed: $err")
                            }
                        }
                    }
                )
            }
        }
    }
}
