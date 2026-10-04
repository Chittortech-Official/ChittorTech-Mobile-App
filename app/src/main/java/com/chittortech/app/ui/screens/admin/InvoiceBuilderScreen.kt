package com.chittortech.app.ui.screens.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.model.*
import com.chittortech.app.theme.*
import com.chittortech.app.ui.components.*
import com.chittortech.app.util.*
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceBuilderScreen(
    clients: List<CtUser>,
    projects: List<Project>,
    existingInvoices: List<Invoice>,
    onCreateInvoice: (Invoice) -> Unit,
    onUpdateInvoiceStatus: (invoiceId: String, status: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("ALL", "UNPAID", "PAID")

    val filteredInvoices = remember(existingInvoices, selectedFilter, searchQuery) {
        existingInvoices.filter { inv ->
            val matchesFilter = when (selectedFilter) {
                "UNPAID" -> inv.status.equals("UNPAID", ignoreCase = true) || inv.status.equals("OVERDUE", ignoreCase = true)
                "PAID" -> inv.status.equals("PAID", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                inv.title.contains(searchQuery, ignoreCase = true) ||
                inv.invoiceId.contains(searchQuery, ignoreCase = true) ||
                inv.clientId.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesSearch
        }
    }

    val totalUnpaid = existingInvoices.filter { it.status != "PAID" }.sumOf { it.amount }
    val totalPaid = existingInvoices.filter { it.status == "PAID" }.sumOf { it.amount }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CtBackground)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF0F172A), CtPrimaryDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = CtAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "BILLING & INVOICING",
                        fontSize = 11.sp,
                        color = CtAmber,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Invoice Builder", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Create, dispatch & track client payments", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                    FloatingActionButton(
                        onClick = { showCreateDialog = true },
                        containerColor = CtAmber,
                        contentColor = Color.White,
                        modifier = Modifier.size(44.dp),
                        elevation = FloatingActionButtonDefaults.elevation(4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Invoice", modifier = Modifier.size(22.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CtGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Paid: ₹${totalPaid.fmtK()}", fontSize = 11.5.sp, color = CtGreen, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CtRed.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Due: ₹${totalUnpaid.fmtK()}", fontSize = 11.5.sp, color = CtRed, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Total: ${existingInvoices.size}", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ── Search & Filter ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search invoices by title, client, or #INV...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CtCardWhite,
                    unfocusedContainerColor = CtCardWhite,
                    focusedBorderColor = CtPrimaryBlue,
                    unfocusedBorderColor = CtBorder
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { f ->
                    val isSelected = selectedFilter == f
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = f },
                        label = { Text(f, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CtPrimaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = CtCardWhite,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // ── Invoices List ─────────────────────────────────────────────────────
        if (filteredInvoices.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Receipt,
                message = if (searchQuery.isNotBlank()) "No invoices match \"$searchQuery\"" else "No invoices yet. Tap + to create one!"
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredInvoices, key = { it.invoiceId.ifBlank { it.hashCode().toString() } }) { invoice ->
                    AdminInvoiceCard(
                        invoice = invoice,
                        clients = clients,
                        onUpdateStatus = { newStatus ->
                            onUpdateInvoiceStatus(invoice.invoiceId, newStatus)
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showCreateDialog) {
        CreateInvoiceDialog(
            clients = clients,
            projects = projects,
            onDismiss = { showCreateDialog = false },
            onConfirm = { invoice ->
                onCreateInvoice(invoice)
                showCreateDialog = false
            }
        )
    }
}

// ─── Admin Invoice Card ───────────────────────────────────────────────────────

@Composable
private fun AdminInvoiceCard(
    invoice: Invoice,
    clients: List<CtUser>,
    onUpdateStatus: (String) -> Unit
) {
    val context = LocalContext.current
    val client = clients.find { it.uid == invoice.clientId || it.email.equals(invoice.clientId, ignoreCase = true) }
    val clientName = client?.companyName?.ifBlank { client.displayName.ifBlank { client.email } } ?: invoice.clientId

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = invoice.invoiceId.ifBlank { "INV" },
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = invoice.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = clientName,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    StatusBadge(status = invoice.status)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "₹${invoice.amount.fmtAmount()}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (invoice.status == "PAID") CtGreen else CtRed,
                        maxLines = 1
                    )
                }
            }

            if (invoice.lineItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CtBackground)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    invoice.lineItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• ${item.description}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Text(
                                text = "₹${item.amount.fmtAmount()}",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Due: ${invoice.dueDate.ifBlank { "Immediate" }}",
                    fontSize = 11.5.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CtBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Send via WhatsApp + Toggle Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // WhatsApp / Share Bill Button
                OutlinedButton(
                    onClick = {
                        shareInvoiceToClient(context, invoice, client, clientName)
                    },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF16A34A)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF16A34A))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send Bill", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Toggle Paid/Unpaid Status Button
                val isPaid = invoice.status.equals("PAID", ignoreCase = true)
                Button(
                    onClick = {
                        val nextStatus = if (isPaid) "UNPAID" else "PAID"
                        onUpdateStatus(nextStatus)
                    },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPaid) CtAmberLight else CtGreenLight,
                        contentColor = if (isPaid) CtAmber else CtGreen
                    )
                ) {
                    Icon(
                        if (isPaid) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isPaid) "Mark Unpaid" else "Mark Paid", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun shareInvoiceToClient(
    context: Context,
    invoice: Invoice,
    client: CtUser?,
    clientName: String
) {
    val itemsSummary = invoice.lineItems.joinToString("\n") { "• ${it.description}: ₹${it.amount.fmtAmount()}" }
    val message = """
        *CHITTORTECH OFFICIAL INVOICE*
        ----------------------------------
        📄 Invoice ID: #${invoice.invoiceId}
        🏢 Client: $clientName
        📌 Title: ${invoice.title}
        💰 Total Amount: ₹${invoice.amount.fmtAmount()}
        📅 Due Date: ${invoice.dueDate.ifBlank { "Immediate" }}
        Status: ${invoice.status}
        
        *Line Items:*
        ${if (itemsSummary.isNotBlank()) itemsSummary else "• IT Services"}
        
        *Payment Options:*
        Bank / UPI Transfer
        
        Thank you for choosing ChittorTech IT Solutions!
    """.trimIndent()

    try {
        val phone = client?.phone?.replace(Regex("[^0-9]"), "") ?: ""
        if (phone.isNotBlank()) {
            val finalNum = if (phone.length == 10) "91$phone" else phone
            val url = "https://wa.me/$finalNum?text=${URLEncoder.encode(message, "UTF-8")}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            return
        }
    } catch (_: Exception) {}

    // Fallback to standard chooser
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "ChittorTech Invoice #${invoice.invoiceId}")
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Send Invoice via"))
}

// ─── Create Invoice Dialog ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateInvoiceDialog(
    clients: List<CtUser>,
    projects: List<Project>,
    onDismiss: () -> Unit,
    onConfirm: (Invoice) -> Unit
) {
    var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }
    var selectedProject by remember { mutableStateOf(projects.firstOrNull()) }
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var lineItemDesc by remember { mutableStateOf("") }
    var lineItemAmount by remember { mutableStateOf("") }
    val lineItems = remember { mutableStateListOf<InvoiceLineItem>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Client Invoice", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Client Selector
                item {
                    Text("Select Client:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    var clientMenuExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { clientMenuExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                selectedClient?.let { it.companyName.ifBlank { it.displayName.ifBlank { it.email } } } ?: "Select Client",
                                fontSize = 12.sp
                            )
                        }
                        DropdownMenu(
                            expanded = clientMenuExpanded,
                            onDismissRequest = { clientMenuExpanded = false }
                        ) {
                            clients.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text("${c.displayName} (${c.companyName.ifBlank { c.email }})", fontSize = 12.sp) },
                                    onClick = {
                                        selectedClient = c
                                        clientMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Project Selector
                item {
                    Text("Select Project:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    var projectMenuExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { projectMenuExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(selectedProject?.name ?: "General / No Project", fontSize = 12.sp)
                        }
                        DropdownMenu(
                            expanded = projectMenuExpanded,
                            onDismissRequest = { projectMenuExpanded = false }
                        ) {
                            projects.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.name, fontSize = 12.sp) },
                                    onClick = {
                                        selectedProject = p
                                        projectMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Invoice Title (e.g. Milestone 1 Payment)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date (e.g. 15 Oct 2026)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Add Line Item section
                item {
                    Text("Line Items:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = lineItemDesc,
                            onValueChange = { lineItemDesc = it },
                            label = { Text("Item", fontSize = 11.sp) },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lineItemAmount,
                            onValueChange = { lineItemAmount = it },
                            label = { Text("₹ Amount", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(
                            onClick = {
                                val amt = lineItemAmount.toLongOrNull() ?: 0L
                                if (lineItemDesc.isNotBlank() && amt > 0) {
                                    lineItems.add(InvoiceLineItem(lineItemDesc, amt))
                                    lineItemDesc = ""
                                    lineItemAmount = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add Item", tint = CtPrimaryBlue)
                        }
                    }
                }

                // List of added line items
                items(lineItems) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("• ${item.description}", fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("₹${item.amount.fmtAmount()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { lineItems.remove(item) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = CtRed, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                item {
                    val total = lineItems.sumOf { it.amount }
                    Text(
                        "Total Amount: ₹${total.fmtAmount()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CtPrimaryBlue
                    )
                }
            }
        },
        confirmButton = {
            val total = lineItems.sumOf { it.amount }
            Button(
                onClick = {
                    if (title.isNotBlank() && selectedClient != null && total > 0) {
                        val client = selectedClient!!
                        val inv = Invoice(
                            invoiceId = "INV-" + System.currentTimeMillis().toString().takeLast(6),
                            clientId = client.email.ifBlank { client.uid },
                            projectId = selectedProject?.projectId ?: "",
                            title = title,
                            lineItems = lineItems.toList(),
                            amount = total,
                            status = "UNPAID",
                            dueDate = dueDate.ifBlank { "Within 7 days" }
                        )
                        onConfirm(inv)
                    }
                },
                enabled = title.isNotBlank() && selectedClient != null && total > 0,
                colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue)
            ) {
                Text("Generate Invoice")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
