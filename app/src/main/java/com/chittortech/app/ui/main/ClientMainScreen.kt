package com.chittortech.app.ui.main

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    // Firebase live data
    val project  by repository.observeClientProject(user.uid).collectAsStateWithLifecycle(null)
    val invoices by repository.observeClientInvoices(user.uid).collectAsStateWithLifecycle(emptyList())
    val tickets  by repository.observeClientTickets(user.uid).collectAsStateWithLifecycle(emptyList())

    Scaffold(
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
                    user = user,
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
                    clientId    = user.uid,
                    projectId   = project?.projectId ?: "",
                    clientName  = user.displayName,
                    companyName = user.companyName,
                    tickets     = tickets,
                    onCreateTicket = { ticket ->
                        scope.launch { repository.createTicket(ticket) }
                    }
                )

                ClientTab.PROFILE -> ClientProfileScreen(
                    user     = user,
                    onLogout = {
                        repository.signOut()
                        onSignOut()
                    }
                )
            }
        }
    }
}
