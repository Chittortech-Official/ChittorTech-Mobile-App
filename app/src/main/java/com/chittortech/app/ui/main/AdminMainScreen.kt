package com.chittortech.app.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittortech.app.data.ChittorTechRepository
import com.chittortech.app.model.CtUser
import com.chittortech.app.theme.*
import com.chittortech.app.ui.screens.admin.*
import kotlinx.coroutines.launch

// ─── Admin Tab Enum ───────────────────────────────────────────────────────────

enum class AdminTab(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    PROJECTS("Projects", Icons.Default.AccountTree),
    INVOICES("Invoices", Icons.Default.ReceiptLong),
    TICKETS("Tickets", Icons.Default.SupportAgent),
    LEADS("Leads", Icons.Default.ContactMail)
}

// ─── Admin Main Screen ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    user: CtUser,
    repository: ChittorTechRepository,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf(AdminTab.DASHBOARD) }

    // Firebase live streams
    val kpi by repository.observeAdminKpi().collectAsStateWithLifecycle(initialValue = com.chittortech.app.model.AdminKpi())
    val projects by repository.observeAllProjects().collectAsStateWithLifecycle(emptyList())
    val invoices by repository.observeAllInvoices().collectAsStateWithLifecycle(emptyList())
    val tickets by repository.observeAllTickets().collectAsStateWithLifecycle(emptyList())
    val clients by repository.observeAllClients().collectAsStateWithLifecycle(emptyList())
    val leads by repository.observeIncomingLeads().collectAsStateWithLifecycle(emptyList())
    val notifications by repository.observeNotifications().collectAsStateWithLifecycle(emptyList())

    // Badge counts
    val openTicketsCount = remember(tickets) { tickets.count { it.status.equals("OPEN", ignoreCase = true) } }
    val newLeadsCount = remember(leads) { leads.count { it.status.equals("new", ignoreCase = true) } }
    val unpaidInvoicesCount = remember(invoices) { invoices.count { it.status.equals("UNPAID", ignoreCase = true) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Command Center",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${user.displayName.ifBlank { "Founder" }} • ${user.email}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        repository.signOut()
                        onSignOut()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign Out", tint = CtRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CtCardWhite)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CtCardWhite,
                tonalElevation = 8.dp
            ) {
                AdminTab.entries.forEach { tab ->
                    val badgeCount = when (tab) {
                        AdminTab.TICKETS -> openTicketsCount
                        AdminTab.LEADS -> newLeadsCount
                        AdminTab.INVOICES -> unpaidInvoicesCount
                        else -> 0
                    }

                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (badgeCount > 0) {
                                        Badge(
                                            containerColor = if (tab == AdminTab.TICKETS) CtRed else CtAmber
                                        ) {
                                            Text(badgeCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(tab.icon, contentDescription = tab.label)
                            }
                        },
                        label = {
                            Text(tab.label, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CtPrimaryBlue,
                            selectedTextColor = CtPrimaryBlue,
                            indicatorColor = CtPrimaryLight,
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
                .padding(padding),
            label = "AdminTabContent"
        ) { tab ->
            when (tab) {
                AdminTab.DASHBOARD -> AdminDashboardScreen(
                    kpi = kpi,
                    projects = projects,
                    invoices = invoices,
                    leads = leads,
                    tickets = tickets,
                    notifications = notifications,
                    onSendNotification = { title, message ->
                        scope.launch { repository.sendNotification(title, message) }
                    },
                    onDeleteNotification = { id ->
                        scope.launch { repository.deleteNotification(id) }
                    }
                )

                AdminTab.PROJECTS -> ProjectControlRoomScreen(
                    projects = projects,
                    clients = clients,
                    onUpdateMilestone = { projectId, clientId, progress, phase, status ->
                        scope.launch {
                            repository.updateProjectMilestone(projectId, clientId, progress, phase, status)
                        }
                    },
                    onSaveProject = { project ->
                        scope.launch { repository.saveProject(project) }
                    }
                )

                AdminTab.INVOICES -> InvoiceBuilderScreen(
                    clients = clients,
                    projects = projects,
                    existingInvoices = invoices,
                    onCreateInvoice = { invoice ->
                        scope.launch { repository.createInvoice(invoice) }
                    },
                    onUpdateInvoiceStatus = { invoiceId, status ->
                        scope.launch { repository.updateInvoiceStatus(invoiceId, status) }
                    }
                )

                AdminTab.TICKETS -> AdminTicketResolverScreen(
                    tickets = tickets,
                    clients = clients,
                    onUpdateTicket = { ticketId, status, note ->
                        scope.launch { repository.updateTicketStatus(ticketId, status, note) }
                    }
                )

                AdminTab.LEADS -> AdminLeadsScreen(
                    leads = leads,
                    onUpdateLeadStatus = { leadId, status ->
                        scope.launch { repository.updateLeadStatus(leadId, status) }
                    }
                )
            }
        }
    }
}
