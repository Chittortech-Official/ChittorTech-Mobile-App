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
import com.chittortech.app.data.SessionManager
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

    // Update active timestamp whenever client navigates or interacts with the portal
    LaunchedEffect(currentTab) {
        SessionManager.updateLastActive(context)
    }

    // Firebase live data (supports both UID and email as clientId)
    val projects      by repository.observeClientProjects(activeUser.uid, activeUser.email).collectAsStateWithLifecycle(emptyList())
    val project       = projects.firstOrNull()
    val invoices      by repository.observeClientInvoices(activeUser.uid, activeUser.email).collectAsStateWithLifecycle(emptyList())
    val tickets       by repository.observeClientTickets(activeUser.uid, activeUser.email).collectAsStateWithLifecycle(emptyList())
    val rawNotifications by repository.observeNotifications().collectAsStateWithLifecycle(emptyList())
    val notifications = remember(rawNotifications, activeUser.email) {
        val clientEmail = activeUser.email.trim().lowercase()
        rawNotifications.filter { n ->
            val targetEmail = n.targetEmail.trim().lowercase()
            val targetClientId = n.targetClientId.trim().lowercase()
            val targetRole = n.targetRole.trim().lowercase()
            when {
                targetEmail.isNotBlank() -> targetEmail == clientEmail
                targetClientId.isNotBlank() -> targetClientId == clientEmail
                else -> targetRole.isBlank() || targetRole == "all" || targetRole == "client"
            }
        }
    }

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

    // ── Persistent Notification Deduplication Store (Per-Client) ─────────────
    val notifPrefs = remember(activeUser.email) {
        val safeKey = activeUser.email.trim().lowercase().replace(Regex("[^a-zA-Z0-9_]"), "_")
        context.getSharedPreferences("ct_notif_store_$safeKey", android.content.Context.MODE_PRIVATE)
    }

    // ── Live Broadcast Notifications from Admin ───────────────────────────────
    LaunchedEffect(notifications) {
        if (notifications.isEmpty()) return@LaunchedEffect
        val latest = notifications.first()
        val hasInit = notifPrefs.getBoolean("broadcast_seeded", false)
        if (!hasInit) {
            notifPrefs.edit()
                .putString("last_broadcast_id", latest.id)
                .putBoolean("broadcast_seeded", true)
                .apply()
        } else {
            val lastId = notifPrefs.getString("last_broadcast_id", null)
            if (latest.id.isNotBlank() && latest.id != lastId) {
                notifPrefs.edit().putString("last_broadcast_id", latest.id).apply()
                val displayTitle = if (latest.title.startsWith("📄") || latest.title.startsWith("🚀") || latest.title.startsWith("✅") || latest.title.startsWith("⏳") || latest.title.startsWith("📢")) {
                    latest.title
                } else {
                    "📢 " + latest.title.ifBlank { "ChittorTech Announcement" }
                }
                NotificationHelper.showNotification(
                    context = context,
                    title = displayTitle,
                    message = latest.message
                )
            }
        }
    }

    // ── Live Ticket Status Updates (when Admin marks In Progress / Resolved or New Ticket) ──
    LaunchedEffect(tickets) {
        if (tickets.isEmpty()) return@LaunchedEffect
        val hasInit = notifPrefs.getBoolean("tickets_seeded", false)
        val notifiedTickets = notifPrefs.getStringSet("notified_ticket_ids", emptySet())?.toMutableSet() ?: mutableSetOf()

        if (!hasInit) {
            tickets.forEach { t ->
                if (t.ticketId.isNotBlank()) {
                    notifiedTickets.add(t.ticketId)
                    notifPrefs.edit().putString("ticket_status_${t.ticketId}", t.status).apply()
                }
            }
            notifPrefs.edit()
                .putStringSet("notified_ticket_ids", notifiedTickets)
                .putBoolean("tickets_seeded", true)
                .apply()
        } else {
            var setChanged = false
            tickets.forEach { t ->
                val tid = t.ticketId
                if (tid.isNotBlank()) {
                    if (tid !in notifiedTickets) {
                        notifiedTickets.add(tid)
                        setChanged = true
                        notifPrefs.edit().putString("ticket_status_$tid", t.status).apply()
                        NotificationHelper.showNotification(
                            context = context,
                            title = "🎫 New Support Ticket",
                            message = "#$tid: ${t.title.ifBlank { "Ticket registered" }} (${t.status})"
                        )
                    } else {
                        val prev = notifPrefs.getString("ticket_status_$tid", null)
                        if (prev != null && prev != t.status) {
                            notifPrefs.edit().putString("ticket_status_$tid", t.status).apply()
                            NotificationHelper.showNotification(
                                context = context,
                                title = "🛠️ Ticket #$tid Updated",
                                message = "Status changed to ${t.status}" +
                                        if (t.resolutionNote.isNotBlank()) " • Note: ${t.resolutionNote}" else ""
                            )
                        }
                    }
                }
            }
            if (setChanged) {
                notifPrefs.edit().putStringSet("notified_ticket_ids", notifiedTickets).apply()
            }
        }
    }

    // ── Live Invoice Notifications (Fires Exactly ONCE per new invoice or status change) ──
    LaunchedEffect(invoices) {
        if (invoices.isEmpty()) return@LaunchedEffect

        val hasInit = notifPrefs.getBoolean("invoices_seeded", false)
        val notifiedSet = notifPrefs.getStringSet("notified_inv_ids", emptySet())?.toMutableSet() ?: mutableSetOf()

        if (!hasInit) {
            // First run: seed existing invoices silently so existing records don't trigger false alerts
            invoices.forEach { inv ->
                val id = inv.invoiceId
                if (id.isNotBlank()) {
                    notifiedSet.add(id)
                    notifPrefs.edit().putString("inv_status_$id", inv.status).apply()
                }
            }
            notifPrefs.edit()
                .putStringSet("notified_inv_ids", notifiedSet)
                .putBoolean("invoices_seeded", true)
                .apply()
        } else {
            var setChanged = false
            invoices.forEach { inv ->
                val id = inv.invoiceId
                if (id.isNotBlank()) {
                    if (id !in notifiedSet) {
                        // Brand new invoice issued by Admin!
                        notifiedSet.add(id)
                        setChanged = true
                        notifPrefs.edit().putString("inv_status_$id", inv.status).apply()

                        val amountStr = try { String.format(java.util.Locale.US, "%,d", inv.amount) } catch (_: Exception) { "${inv.amount}" }
                        val displayTitle = inv.title.ifBlank { inv.projectName.ifBlank { "Project Milestone Deliverable" } }
                        NotificationHelper.showNotification(
                            context = context,
                            title = "📄 New Invoice Issued: $id",
                            message = "$displayTitle • ₹$amountStr (${inv.status})"
                        )
                    } else {
                        // Existing invoice: check if payment status changed (e.g. UNPAID -> PAID)
                        val prevStatus = notifPrefs.getString("inv_status_$id", null)
                        if (prevStatus != null && prevStatus != inv.status) {
                            notifPrefs.edit().putString("inv_status_$id", inv.status).apply()
                            val amountStr = try { String.format(java.util.Locale.US, "%,d", inv.amount) } catch (_: Exception) { "${inv.amount}" }
                            val statusEmoji = if (inv.status.equals("PAID", ignoreCase = true)) "✅" else "⚠️"
                            NotificationHelper.showNotification(
                                context = context,
                                title = "$statusEmoji Invoice $id Updated",
                                message = "Status changed to ${inv.status} • ₹$amountStr"
                            )
                        }
                    }
                }
            }
            if (setChanged) {
                notifPrefs.edit().putStringSet("notified_inv_ids", notifiedSet).apply()
            }
        }
    }

    // ── Live Project Notifications (Fires Exactly ONCE per new project or milestone change) ──
    LaunchedEffect(projects) {
        if (projects.isEmpty()) return@LaunchedEffect

        val hasInit = notifPrefs.getBoolean("projects_seeded", false)
        val notifiedProjects = notifPrefs.getStringSet("notified_proj_ids", emptySet())?.toMutableSet() ?: mutableSetOf()

        if (!hasInit) {
            // First run: seed existing projects silently
            projects.forEach { proj ->
                val projId = proj.projectId.ifBlank { proj.name }
                if (projId.isNotBlank()) {
                    notifiedProjects.add(projId)
                    notifPrefs.edit()
                        .putInt("proj_progress_$projId", proj.milestoneProgress)
                        .putString("proj_phase_$projId", proj.currentPhase)
                        .apply()
                }
            }
            notifPrefs.edit()
                .putStringSet("notified_proj_ids", notifiedProjects)
                .putBoolean("projects_seeded", true)
                .apply()
        } else {
            var setChanged = false
            projects.forEach { proj ->
                val projId = proj.projectId.ifBlank { proj.name }
                if (projId.isNotBlank()) {
                    if (projId !in notifiedProjects) {
                        // Brand new project configured!
                        notifiedProjects.add(projId)
                        setChanged = true
                        notifPrefs.edit()
                            .putInt("proj_progress_$projId", proj.milestoneProgress)
                            .putString("proj_phase_$projId", proj.currentPhase)
                            .apply()

                        NotificationHelper.showNotification(
                            context = context,
                            title = "🚀 New Project Configured!",
                            message = "Your project \"${proj.name}\" has been set up in your client portal."
                        )
                    } else {
                        // Check if progress percentage or phase changed
                        val prevProgress = notifPrefs.getInt("proj_progress_$projId", -1)
                        val prevPhase = notifPrefs.getString("proj_phase_$projId", null)

                        if ((prevProgress != -1 && prevProgress != proj.milestoneProgress) || (prevPhase != null && prevPhase != proj.currentPhase)) {
                            notifPrefs.edit()
                                .putInt("proj_progress_$projId", proj.milestoneProgress)
                                .putString("proj_phase_$projId", proj.currentPhase)
                                .apply()

                            NotificationHelper.showNotification(
                                context = context,
                                title = "🚀 Project Milestone Update",
                                message = "${proj.name}: Progress is now ${proj.milestoneProgress}% (${proj.currentPhase})"
                            )
                        }
                    }
                }
            }
            if (setChanged) {
                notifPrefs.edit().putStringSet("notified_proj_ids", notifiedProjects).apply()
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
                    projects = projects,
                    invoices = invoices,
                    tickets  = tickets,
                    onViewInvoices    = { currentTab = ClientTab.INVOICES },
                    onRaiseTicket     = { currentTab = ClientTab.HELPDESK },
                    onContactEngineer = {
                        val whatsapp = "https://wa.me/917597451057?text=Hello+ChittorTech+Team,+I+need+assistance+regarding+my+project."
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
                        SessionManager.clearSession(context)
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
