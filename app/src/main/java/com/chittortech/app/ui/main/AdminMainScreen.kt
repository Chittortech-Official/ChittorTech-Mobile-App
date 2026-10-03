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
    INVOICES("Invoices", Icons.Default.Receipt),
    TICKETS("Tickets", Icons.Default.SupportAgent),
    VAULT("Vault", Icons.Default.Security)
}

// ─── Admin Main Screen ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    user: CtUser,
    repository: ChittorTechRepository,
    onSignOut: () -> Unit = {},
    onOpenVyapar: () -> Unit = {},
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
                    IconButton(onClick = onOpenVyapar) {
                        Icon(Icons.Default.Storefront, contentDescription = "Quick Launch / Services", tint = CtPrimaryBlue)
                    }
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
                            selectedIconColor   = CtAmber,
                            selectedTextColor   = CtAmber,
                            indicatorColor      = CtAmberLight,
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
                    kpi      = kpi,
                    projects = projects,
                    invoices = invoices
                )

                AdminTab.INVOICES -> InvoiceBuilderScreen(
                    clients          = clients,
                    projects         = projects,
                    existingInvoices = invoices,
                    onCreateInvoice  = { invoice ->
                        scope.launch { repository.createInvoice(invoice) }
                    }
                )

                AdminTab.TICKETS -> AdminTicketResolverScreen(
                    tickets = tickets,
                    onUpdateTicket = { ticketId, status, note ->
                        scope.launch { repository.updateTicketStatus(ticketId, status, note) }
                    }
                )

                AdminTab.VAULT -> ClientVaultScreen(
                    clients  = clients,
                    projects = projects
                )
            }
        }
    }
}
