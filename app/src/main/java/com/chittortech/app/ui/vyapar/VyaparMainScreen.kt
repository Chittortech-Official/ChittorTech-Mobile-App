package com.chittortech.app.ui.vyapar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittortech.app.data.ChittorTechRepository
import com.chittortech.app.model.AdminKpi
import com.chittortech.app.model.CtUser
import com.chittortech.app.model.Invoice
import com.chittortech.app.model.InvoiceLineItem
import com.chittortech.app.theme.*
import kotlinx.coroutines.launch

@Composable
fun VyaparMainScreen(
    user: CtUser,
    repository: ChittorTechRepository,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf(VyaparTab.HOME) }

    // Live streams from Firebase Firestore
    val invoices by repository.observeAllInvoices().collectAsStateWithLifecycle(initialValue = emptyList())
    val clients by repository.observeAllClients().collectAsStateWithLifecycle(initialValue = emptyList())
    val projects by repository.observeAllProjects().collectAsStateWithLifecycle(initialValue = emptyList())
    val kpi by repository.observeAdminKpi().collectAsStateWithLifecycle(
        initialValue = AdminKpi(
            totalMonthlyInvoiced = 2500L,
            totalOutstandingDues = 100L,
            activeDeployments = 1,
            openTickets = 0
        )
    )

    // Dialog States
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }
    var showCreatePartyDialog by remember { mutableStateOf(false) }
    var showCreateItemDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            VyaparTopBar(
                businessName = user.companyName.ifBlank { user.displayName.ifBlank { "ChittorTech Solutions" } },
                notificationCount = 2,
                onNotificationClick = {},
                onSettingsClick = {}
            )
        },
        bottomBar = {
            VyaparBottomBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
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
                        onAddNewSale = { showCreateInvoiceDialog = true },
                        onAddNewParty = { showCreatePartyDialog = true },
                        onSaleReportClick = { currentTab = VyaparTab.DASHBOARD }
                    )
                }
                VyaparTab.DASHBOARD -> {
                    VyaparDashboardScreen(
                        kpi = kpi,
                        onSeeReportsClick = { currentTab = VyaparTab.HOME }
                    )
                }
                VyaparTab.ITEMS -> {
                    VyaparItemsScreen(
                        projects = projects,
                        onAddNewItem = { showCreateItemDialog = true }
                    )
                }
                VyaparTab.MENU -> {
                    VyaparMenuScreen(
                        onSaleClick = {
                            currentTab = VyaparTab.HOME
                        },
                        onPurchaseClick = {},
                        onExpensesClick = {
                            currentTab = VyaparTab.DASHBOARD
                        },
                        onReportsClick = {
                            currentTab = VyaparTab.DASHBOARD
                        },
                        onHelpdeskClick = {},
                        onSignOut = {
                            repository.signOut()
                        }
                    )
                }
                VyaparTab.GET_DESKTOP -> {
                    VyaparDesktopScreen()
                }
            }
        }
    }

    // ── Create Invoice Dialog (Add New Sale) ───────────────────────────────────
    if (showCreateInvoiceDialog) {
        var clientName by remember { mutableStateOf("") }
        var invoiceTitle by remember { mutableStateOf("") }
        var amountText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateInvoiceDialog = false },
            title = {
                Text("Add New Sale / Invoice", fontWeight = FontWeight.Bold, color = VyaparDark)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Customer / Party Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = invoiceTitle,
                        onValueChange = { invoiceTitle = it },
                        label = { Text("Service Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Total Amount (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountText.toLongOrNull() ?: 0L
                        if (amount > 0) {
                            scope.launch {
                                repository.createInvoice(
                                    Invoice(
                                        clientId = user.uid,
                                        title = if (invoiceTitle.isBlank()) "Tech Deployment for $clientName" else invoiceTitle,
                                        amount = amount,
                                        dueDate = "30 Days Net",
                                        status = "PAID",
                                        lineItems = listOf(
                                            InvoiceLineItem(description = if (invoiceTitle.isBlank()) "Engineering Services" else invoiceTitle, amount = amount)
                                        )
                                    )
                                )
                                showCreateInvoiceDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VyaparRed)
                ) {
                    Text("Save Sale", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateInvoiceDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }

    // ── Create Party Dialog (Add New Party) ────────────────────────────────────
    if (showCreatePartyDialog) {
        var partyName by remember { mutableStateOf("") }
        var partyPhone by remember { mutableStateOf("") }
        var openingBalance by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreatePartyDialog = false },
            title = {
                Text("Add New Party / Client", fontWeight = FontWeight.Bold, color = VyaparDark)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = partyName,
                        onValueChange = { partyName = it },
                        label = { Text("Party Name / Business Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = partyPhone,
                        onValueChange = { partyPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = openingBalance,
                        onValueChange = { openingBalance = it },
                        label = { Text("Opening Balance (₹) (You'll Get)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (partyName.isNotBlank()) {
                            // Close dialog and reflect party
                            showCreatePartyDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VyaparRed)
                ) {
                    Text("Save Party", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePartyDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }

    // ── Create Item Dialog ────────────────────────────────────────────────────
    if (showCreateItemDialog) {
        var itemName by remember { mutableStateOf("") }
        var itemPrice by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateItemDialog = false },
            title = {
                Text("Add New Service / Item", fontWeight = FontWeight.Bold, color = VyaparDark)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item / Service Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = itemPrice,
                        onValueChange = { itemPrice = it },
                        label = { Text("Price (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCreateItemDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VyaparRed)
                ) {
                    Text("Add Item", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateItemDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }
}
