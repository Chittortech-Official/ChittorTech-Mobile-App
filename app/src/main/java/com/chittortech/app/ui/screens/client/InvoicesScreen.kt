package com.chittortech.app.ui.screens.client

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.model.Invoice
import com.chittortech.app.theme.*
import com.chittortech.app.ui.components.*

@Composable
fun InvoicesScreen(
    invoices: List<Invoice>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var filterStatus by remember { mutableStateOf("ALL") }

    val filters = listOf("ALL", "PAID", "UNPAID", "OVERDUE")
    val filtered = if (filterStatus == "ALL") invoices
    else invoices.filter { it.status.equals(filterStatus, ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CtBackground)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(CtPrimaryDark, GradEnd)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column {
                Text("Billing & Invoices", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("All your ChittorTech invoices", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }

        // Summary Row
        val totalPaid = invoices.filter { it.status == "PAID" }.sumOf { it.amount }
        val totalDue  = invoices.filter { it.status != "PAID" }.sumOf { it.amount }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryChip("₹${totalPaid.fmt()}", "Paid", CtGreen, Modifier.weight(1f))
            SummaryChip("₹${totalDue.fmt()}", "Outstanding", CtRed, Modifier.weight(1f))
        }

        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { f ->
                FilterChip(
                    selected = filterStatus == f,
                    onClick = { filterStatus = f },
                    label = { Text(f, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CtPrimaryBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            EmptyState(icon = Icons.Default.Receipt, message = "No invoices found")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { invoice ->
                    InvoiceCard(
                        invoice = invoice,
                        onDownloadPdf = {
                            if (invoice.invoicePdfUrl.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(invoice.invoicePdfUrl))
                                context.startActivity(intent)
                            }
                        },
                        onPayNow = {
                            // Payment gateway placeholder
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun InvoiceCard(
    invoice: Invoice,
    onDownloadPdf: () -> Unit,
    onPayNow: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                    Text(invoice.invoiceId, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(invoice.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Due: ${invoice.dueDate.ifBlank { "—" }}", fontSize = 11.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    StatusBadge(status = invoice.status)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "₹${invoice.amount.fmt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (invoice.status == "PAID") CtGreen else CtRed
                    )
                }
            }

            // Line Items (expandable)
            if (invoice.lineItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = CtPrimaryBlue
                    )
                    Text(
                        if (expanded) "Hide line items" else "View ${invoice.lineItems.size} line items",
                        fontSize = 12.sp,
                        color = CtPrimaryBlue
                    )
                }
                AnimatedVisibility(visible = expanded) {
                    Column {
                        HorizontalDivider(color = CtBorder)
                        invoice.lineItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.description, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                                Text("₹${item.amount.fmt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CtBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (invoice.invoicePdfUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = onDownloadPdf,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CtPrimaryBlue)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = CtPrimaryBlue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download PDF", fontSize = 12.sp, color = CtPrimaryBlue)
                    }
                }
                if (invoice.status != "PAID") {
                    Button(
                        onClick = onPayNow,
                        modifier = Modifier.weight(1f).height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pay Now", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryChip(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

private fun Long.fmt(): String {
    return if (this >= 1000) {
        val thousands = this / 1000
        val remainder = this % 1000
        if (remainder == 0L) "${thousands}k" else "${thousands},${String.format("%03d", remainder)}"
    } else this.toString()
}
