package com.chittortech.app.ui.screens.client

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.chittortech.app.util.NotificationHelper
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

    var selectedInvoiceForView by remember { mutableStateOf<Invoice?>(null) }

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
                Text("All your ChittorTech invoices & receipts", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
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
                        onViewInvoice = {
                            selectedInvoiceForView = invoice
                        },
                        onPayNow = {
                            // Payment gateway / UPI trigger
                            val upiUri = Uri.parse("upi://pay?pa=business@chittortech&pn=ChittorTech&am=${invoice.amount}&cu=INR")
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, upiUri))
                            } catch (_: Exception) {
                                selectedInvoiceForView = invoice
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }

    // Interactive In-App Rendered Invoice Bill Sheet
    selectedInvoiceForView?.let { inv ->
        RenderedInvoiceDialog(
            invoice = inv,
            onDismiss = { selectedInvoiceForView = null },
            onPrintPdf = {
                printInvoiceDocument(context, inv)
            },
            onPayNow = {
                val upiUri = Uri.parse("upi://pay?pa=business@chittortech&pn=ChittorTech&am=${inv.amount}&cu=INR")
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, upiUri))
                } catch (_: Exception) {
                    Toast.makeText(context, "UPI App not found. Please pay to business@chittortech", Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}

@Composable
private fun InvoiceCard(
    invoice: Invoice,
    onViewInvoice: () -> Unit,
    onPayNow: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isPaid = invoice.status.equals("PAID", ignoreCase = true)

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
                    if (invoice.projectName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(invoice.projectName, fontSize = 11.5.sp, color = CtPrimaryBlue, fontWeight = FontWeight.SemiBold)
                    }
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
                        color = if (isPaid) CtGreen else CtRed
                    )
                }
            }

            // Line Items preview
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
                        if (expanded) "Hide breakdown" else "View ${invoice.lineItems.size} line items",
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

            // Action Buttons: View Invoice, Pay Now
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // View Invoice Bill Button
                OutlinedButton(
                    onClick = onViewInvoice,
                    modifier = if (isPaid) Modifier.fillMaxWidth().height(42.dp) else Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CtPrimaryBlue)
                ) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = CtPrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPaid) "View Invoice Bill" else "View Invoice",
                        fontSize = 12.5.sp,
                        color = CtPrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Pay Now Button (if unpaid)
                if (!isPaid) {
                    Button(
                        onClick = onPayNow,
                        modifier = Modifier.weight(1f).height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pay Now", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─── High-Fidelity In-App Rendered Invoice Bill Modal ─────────────────────────

@Composable
private fun RenderedInvoiceDialog(
    invoice: Invoice,
    onDismiss: () -> Unit,
    onPrintPdf: () -> Unit,
    onPayNow: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(CtPrimaryDark, GradEnd)))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "ChittorTech Official Bill",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Scrollable Bill Sheet
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // ChittorTech Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "CHITTORTECH INNOVATIONS",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CtPrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Enterprise Software & Cloud Engineering", fontSize = 11.sp, color = TextSecondary)
                            Text("contact@chittortech.in • chittortech.in", fontSize = 10.5.sp, color = TextMuted)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            StatusBadge(status = invoice.status)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(invoice.invoiceId, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = CtBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Bill Meta: Purpose & Due Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PURPOSE / SERVICE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(invoice.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("DUE DATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(invoice.dueDate.ifBlank { "On Receipt" }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Itemized Table
                    Text("ITEMIZED DELIVERABLES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("# Deliverable", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1f))
                                Text("Amount", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            }
                            HorizontalDivider(color = Color(0xFFE2E8F0))

                            val displayItems = if (invoice.lineItems.isNotEmpty()) invoice.lineItems
                            else listOf(com.chittortech.app.model.InvoiceLineItem(invoice.title, invoice.amount))

                            displayItems.forEachIndexed { idx, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${idx + 1}. ${item.description}",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                                    )
                                    Text(
                                        "₹${item.amount.fmt()}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                if (idx < displayItems.size - 1) {
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Calculation Summary Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (invoice.status == "PAID") CtGreenLight else CtPrimaryLight,
                        border = BorderStroke(1.dp, if (invoice.status == "PAID") CtGreen.copy(alpha = 0.3f) else CtPrimaryBlue.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal", fontSize = 12.sp, color = TextSecondary)
                                Text("₹${invoice.amount.fmt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Taxes (GST 18%)", fontSize = 12.sp, color = TextSecondary)
                                Text("Included", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CtGreen)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.Black.copy(alpha = 0.08f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Payable", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(
                                    "₹${invoice.amount.fmt()}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (invoice.status == "PAID") CtGreen else CtPrimaryBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Official Notice
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verified digital receipt issued by ChittorTech Innovations. Payment accepted via UPI (business@chittortech) and verified bank transfer.",
                                fontSize = 10.5.sp,
                                color = TextSecondary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // Sticky Bottom Action Buttons
                Surface(
                    color = Color.White,
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Pay Now Button (if unpaid)
                        if (invoice.status != "PAID") {
                            Button(
                                onClick = onPayNow,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(17.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pay Now • ₹${invoice.amount.fmt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Print / Save as PDF Button (Full width - never clipped)
                        OutlinedButton(
                            onClick = onPrintPdf,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CtPrimaryBlue)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Print / Save as PDF",
                                fontSize = 13.sp,
                                color = CtPrimaryBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── PDF Printing & Document Generation ──────────────────────────────────────

fun printInvoiceDocument(context: Context, invoice: Invoice) {
    try {
        NotificationHelper.showNotification(
            context = context,
            title = "📄 Invoice #${invoice.invoiceId} Ready",
            message = "Your invoice for ₹${invoice.amount.fmt()} has been prepared for download and printing."
        )
        val webView = WebView(context)
        val htmlContent = generateInvoiceHtml(invoice)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                if (printManager != null) {
                    val printAdapter = webView.createPrintDocumentAdapter("Invoice_${invoice.invoiceId}")
                    val builder = PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .build()
                    printManager.print("ChittorTech_${invoice.invoiceId}", printAdapter, builder)
                } else {
                    Toast.makeText(context, "Printing service unavailable on this device", Toast.LENGTH_SHORT).show()
                }
            }
        }
        webView.loadDataWithBaseURL("https://chittortech.in", htmlContent, "text/html", "UTF-8", null)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not launch PDF print: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun generateInvoiceHtml(invoice: Invoice): String {
    val itemsHtml = buildString {
        if (invoice.lineItems.isEmpty()) {
            append("""
                <tr>
                    <td style="padding: 10px 12px; border-bottom: 1px solid #E2E8F0; font-size: 13px; color: #475569;">1</td>
                    <td style="padding: 10px 12px; border-bottom: 1px solid #E2E8F0; font-size: 13px; font-weight: 600; color: #1E293B;">${invoice.title}</td>
                    <td style="padding: 10px 12px; border-bottom: 1px solid #E2E8F0; font-size: 13px; font-weight: bold; text-align: right; color: #1E293B;">₹${invoice.amount.fmt()}</td>
                </tr>
            """.trimIndent())
        } else {
            invoice.lineItems.forEachIndexed { idx, item ->
                append("""
                    <tr>
                        <td style="padding: 10px 12px; border-bottom: 1px solid #E2E8F0; font-size: 13px; color: #475569;">${idx + 1}</td>
                        <td style="padding: 10px 12px; border-bottom: 1px solid #E2E8F0; font-size: 13px; font-weight: 600; color: #1E293B;">${item.description}</td>
                        <td style="padding: 10px 12px; border-bottom: 1px solid #E2E8F0; font-size: 13px; font-weight: bold; text-align: right; color: #1E293B;">₹${item.amount.fmt()}</td>
                    </tr>
                """.trimIndent())
            }
        }
    }

    val isPaid = invoice.status.equals("PAID", ignoreCase = true)
    val statusColor = if (isPaid) "#16A34A" else "#DC2626"
    val statusBg = if (isPaid) "#DCFCE7" else "#FEE2E2"

    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>ChittorTech Invoice ${invoice.invoiceId}</title>
            <style>
                body {
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                    margin: 0;
                    padding: 28px;
                    color: #0F172A;
                    background-color: #FFFFFF;
                }
                .header-table { width: 100%; border-collapse: collapse; margin-bottom: 24px; }
                .brand-title { font-size: 22px; font-weight: 800; color: #1E3A8A; letter-spacing: 0.5px; margin: 0; }
                .brand-subtitle { font-size: 11px; color: #64748B; margin: 3px 0 0 0; }
                .inv-title { font-size: 24px; font-weight: 800; color: #0F172A; text-align: right; margin: 0; }
                .inv-meta { font-size: 12px; color: #64748B; text-align: right; margin: 3px 0 0 0; }
                .badge {
                    display: inline-block;
                    padding: 4px 12px;
                    border-radius: 6px;
                    font-size: 12px;
                    font-weight: 700;
                    color: $statusColor;
                    background-color: $statusBg;
                    margin-top: 6px;
                }
                .info-box {
                    background: #F8FAFC;
                    border: 1px solid #E2E8F0;
                    border-radius: 8px;
                    padding: 14px 18px;
                    margin-bottom: 24px;
                }
                .items-table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-bottom: 20px;
                }
                .items-table th {
                    background: #F1F5F9;
                    padding: 10px 12px;
                    font-size: 11px;
                    font-weight: 700;
                    text-transform: uppercase;
                    color: #475569;
                    text-align: left;
                    border-bottom: 2px solid #CBD5E1;
                }
                .footer {
                    margin-top: 48px;
                    padding-top: 16px;
                    border-top: 1px solid #E2E8F0;
                    font-size: 11px;
                    color: #94A3B8;
                    text-align: center;
                }
            </style>
        </head>
        <body>
            <table class="header-table">
                <tr>
                    <td style="vertical-align: top;">
                        <h1 class="brand-title">CHITTORTECH INNOVATIONS</h1>
                        <p class="brand-subtitle">Enterprise Software, Mobile & Cloud Solutions</p>
                        <p class="brand-subtitle">business@chittortech.in &bull; contact@chittortech.in &bull; chittortech.in</p>
                    </td>
                    <td style="vertical-align: top; text-align: right;">
                        <h2 class="inv-title">TAX INVOICE</h2>
                        <p class="inv-meta">Invoice #: <strong>${invoice.invoiceId}</strong></p>
                        <p class="inv-meta">Due Date: <strong>${invoice.dueDate.ifBlank { "On Receipt" }}</strong></p>
                        <div><span class="badge">${invoice.status}</span></div>
                    </td>
                </tr>
            </table>

            <div class="info-box">
                <div style="font-size: 11px; font-weight: 700; color: #64748B; text-transform: uppercase; margin-bottom: 4px;">Invoice Scope & Description</div>
                <div style="font-size: 14px; font-weight: 700; color: #1E293B;">${invoice.title}</div>
            </div>

            <table class="items-table">
                <thead>
                    <tr>
                        <th style="width: 40px;">#</th>
                        <th>Service / Deliverable Description</th>
                        <th style="width: 130px; text-align: right;">Amount (INR)</th>
                    </tr>
                </thead>
                <tbody>
                    $itemsHtml
                </tbody>
            </table>

            <table style="width: 100%; margin-top: 20px;">
                <tr>
                    <td style="vertical-align: top; width: 55%; font-size: 11.5px; color: #64748B; line-height: 18px;">
                        <strong>ChittorTech Payment Support:</strong><br>
                        &bull; UPI ID: <code>business@chittortech</code><br>
                        &bull; Bank Transfer / NEFT / IMPS<br>
                        &bull; For billing queries: contact@chittortech.in
                    </td>
                    <td style="vertical-align: top; width: 45%;">
                        <table style="width: 100%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 6px 0; font-size: 13px; color: #64748B;">Total Amount:</td>
                                <td style="padding: 6px 0; font-size: 13px; font-weight: 600; text-align: right;">₹${invoice.amount.fmt()}</td>
                            </tr>
                            <tr style="border-top: 2px solid #0F172A;">
                                <td style="padding: 10px 0; font-size: 15px; font-weight: 800; color: #0F172A;">Total Payable:</td>
                                <td style="padding: 10px 0; font-size: 18px; font-weight: 800; text-align: right; color: #1E3A8A;">₹${invoice.amount.fmt()}</td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>

            <div class="footer">
                Thank you for partnering with ChittorTech Innovations &bull; www.chittortech.in &bull; Official Digital Invoice
            </div>
        </body>
        </html>
    """.trimIndent()
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


