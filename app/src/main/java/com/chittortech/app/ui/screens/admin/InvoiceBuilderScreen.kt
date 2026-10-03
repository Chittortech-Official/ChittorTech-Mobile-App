package com.chittortech.app.ui.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.model.*
import com.chittortech.app.theme.*
import com.chittortech.app.ui.components.*

@Composable
fun InvoiceBuilderScreen(
    clients: List<CtUser>,
    projects: List<Project>,
    onCreateInvoice: (Invoice) -> Unit,
    existingInvoices: List<Invoice>,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CtBackground)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF0F172A), CtPrimaryDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column {
                Text("Invoice Builder", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Create & dispatch client invoices", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.align(Alignment.CenterEnd),
                containerColor = CtAmber,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Invoice")
            }
        }

        if (existingInvoices.isEmpty()) {
            EmptyState(icon = Icons.Default.Receipt, message = "No invoices yet. Create the first one!")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(existingInvoices) { invoice ->
                    AdminInvoiceCard(invoice = invoice, clients = clients)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showCreateDialog) {
        CreateInvoiceDialog(
            clients  = clients,
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
private fun AdminInvoiceCard(invoice: Invoice, clients: List<CtUser>) {
    val clientName = clients.find { it.uid == invoice.clientId }?.companyName ?: invoice.clientId

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(invoice.invoiceId.ifBlank { "INV" }, fontSize = 10.sp, color = TextMuted)
                    Text(invoice.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(clientName, fontSize = 12.sp, color = TextSecondary)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    StatusBadge(status = invoice.status)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "₹${invoice.amount.fmtK()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (invoice.status == "PAID") CtGreen else CtRed
                    )
                }
            }

            if (invoice.lineItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = CtBorder)
                Spacer(modifier = Modifier.height(8.dp))
                invoice.lineItems.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• ${item.description}", fontSize = 12.sp, color = TextSecondary)
                        Text("₹${item.amount.fmtK()}", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Due: ${invoice.dueDate.ifBlank { "—" }}", fontSize = 11.sp, color = TextMuted)
        }
    }
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
    var selectedClient by remember { mutableStateOf<CtUser?>(null) }
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var lineItems by remember { mutableStateOf(listOf(Pair("", ""))) }
    var clientExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = CtPrimaryBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Invoice", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Client Picker
                ExposedDropdownMenuBox(
                    expanded = clientExpanded,
                    onExpandedChange = { clientExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedClient?.companyName ?: "Select Client",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Client") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = clientExpanded,
                        onDismissRequest = { clientExpanded = false }
                    ) {
                        clients.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c.companyName) },
                                onClick = { selectedClient = c; clientExpanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Invoice Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = CtPrimaryBlue) }
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text("Line Items", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                lineItems.forEachIndexed { idx, (desc, amt) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { newDesc ->
                                lineItems = lineItems.toMutableList().also { it[idx] = Pair(newDesc, amt) }
                            },
                            label = { Text("Description") },
                            singleLine = true,
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = amt,
                            onValueChange = { newAmt ->
                                lineItems = lineItems.toMutableList().also { it[idx] = Pair(desc, newAmt) }
                            },
                            label = { Text("₹") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        if (lineItems.size > 1) {
                            IconButton(onClick = { lineItems = lineItems.toMutableList().also { list -> list.removeAt(idx) } }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = CtRed)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                TextButton(onClick = { lineItems = lineItems + Pair("", "") }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(16.dp))
                    Text(" Add Line Item", fontSize = 13.sp, color = CtPrimaryBlue)
                }

                val total = lineItems.sumOf { it.second.toLongOrNull() ?: 0L }
                if (total > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CtPrimaryLight)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount", fontWeight = FontWeight.Bold, color = CtPrimaryDark)
                        Text("₹$total", fontWeight = FontWeight.Bold, color = CtPrimaryDark, fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val client = selectedClient ?: return@Button
                    if (title.isBlank()) return@Button
                    val items = lineItems.mapNotNull { (d, a) ->
                        if (d.isBlank()) null else InvoiceLineItem(d, a.toLongOrNull() ?: 0L)
                    }
                    val total = items.sumOf { it.amount }
                    val project = projects.find { it.clientId == client.uid }
                    onConfirm(
                        Invoice(
                            clientId  = client.uid,
                            projectId = project?.projectId ?: "",
                            title     = title,
                            lineItems = items,
                            amount    = total,
                            status    = "UNPAID",
                            dueDate   = dueDate
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Dispatch Invoice") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun Long.fmtK() = if (this >= 1000) "%.1fK".format(this / 1000.0) else this.toString()
