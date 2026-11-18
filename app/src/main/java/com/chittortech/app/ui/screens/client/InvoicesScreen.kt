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
                    Text("Due: ${formatDateDDMMYYYY(invoice.dueDate)}", fontSize = 11.sp, color = TextSecondary)
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
                            Text(formatDateDDMMYYYY(invoice.dueDate), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
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

                    // Billing Information & Bank Details
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "BILLING INFORMATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CtPrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val displayInvDate = formatDateDDMMYYYY(
                                if (invoice.invoiceDate.isNotBlank()) invoice.invoiceDate
                                else if (invoice.createdAt != null) java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.ENGLISH).format(invoice.createdAt.toDate())
                                else ""
                            )
                            val displayDueDate = formatDateDDMMYYYY(invoice.dueDate)
                            Text("Date: $displayInvDate", fontSize = 11.5.sp, color = TextPrimary)
                            Text("Payment Method: ${invoice.paymentMode.ifBlank { "Bank Transfer" }}", fontSize = 11.5.sp, color = TextPrimary)
                            Text("Due Date: $displayDueDate", fontSize = 11.5.sp, color = TextPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(6.dp))
                            val payInfo = if (invoice.paymentInfo.isNotBlank()) invoice.paymentInfo
                            else "Account Name: ChittorTech\nBank Name: Chittorgarh Urban Co-operative Bank\nUPI ID: Please view the UPI ID in the terms and conditions section"
                            Text(payInfo, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Terms and Conditions
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "TERMS & CONDITIONS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val termsList = if (invoice.termsText.isNotBlank()) {
                                invoice.termsText.split("\n").filter { it.isNotBlank() }
                            } else {
                                listOf(
                                    "Payment is due upon receipt of this milestone invoice.",
                                    "Work milestone deliverables will be released upon clearance.",
                                    "Official UPI ID: chittortech@icici (or scan official ChittorTech QR code).",
                                    "Please make payments payable to ChittorTech official accounts."
                                )
                            }
                            termsList.forEach { term ->
                                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text("• ", fontSize = 11.sp, color = CtPrimaryBlue, fontWeight = FontWeight.Bold)
                                    Text(term, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Digital Generation Notice (Replaces manual signature)
                    Text(
                        "* Bill generated from the digital platform, no manual sign needed",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End,
                        modifier = Modifier.fillMaxWidth().padding(end = 4.dp)
                    )
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

fun formatDateDDMMYYYY(dateStr: String): String {
    return com.chittortech.app.util.formatDateDDMMYYYY(dateStr, fallbackToTodayIfBlank = true)
}

fun generateInvoiceHtml(invoice: Invoice): String {
    val items = if (invoice.lineItems.isNotEmpty()) invoice.lineItems
    else listOf(com.chittortech.app.model.InvoiceLineItem(invoice.title.ifBlank { "Software Development Deliverable" }, invoice.amount))

    val itemsHtml = buildString {
        items.forEachIndexed { idx, item ->
            val bg = if (idx % 2 == 1) "rgba(244,246,248,0.85)" else "rgba(255,255,255,0.85)"
            append("""
                <tr style="background: $bg;">
                    <td style="padding: 7px 10px; font-size: 12px; font-weight: 700; color: #1A1A1A;">${idx + 1}</td>
                    <td style="padding: 7px 10px; font-size: 12px; color: #334155;">${item.description}</td>
                    <td style="padding: 7px 10px; font-size: 12px; text-align: center; color: #4A4A4A;">1</td>
                    <td style="padding: 7px 10px; font-size: 12px; text-align: right; color: #4A4A4A;">₹${item.amount.fmt()}</td>
                    <td style="padding: 7px 10px; font-size: 12px; text-align: right; color: #4A4A4A;">₹${item.amount.fmt()}</td>
                </tr>
            """.trimIndent())
        }
    }

    val isPaid = invoice.status.equals("PAID", ignoreCase = true)
    val totalLabel = if (isPaid) "Total Amount Paid" else "Total Amount Due"
    val clientName = invoice.clientId.ifBlank { "Valued Client" }
    val rawDate = if (invoice.invoiceDate.isNotBlank()) invoice.invoiceDate
        else if (invoice.createdAt != null) java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.ENGLISH).format(invoice.createdAt.toDate())
        else ""
    val invDate = formatDateDDMMYYYY(rawDate)
    val dueDateFormatted = formatDateDDMMYYYY(invoice.dueDate)

    val paymentInfoHtml = if (invoice.paymentInfo.isNotBlank()) {
        invoice.paymentInfo.replace("\n", "<br>")
    } else {
        "Account Name: ChittorTech<br>Bank Name: Chittorgarh Urban Co-operative Bank<br>UPI ID: Please view the UPI ID in the terms and conditions section"
    }

    val termsItemsHtml = if (invoice.termsText.isNotBlank()) {
        invoice.termsText.split("\n").filter { it.isNotBlank() }.joinToString("") { "<li>$it</li>" }
    } else {
        """
            <li>Payment is due upon receipt of this milestone invoice.</li>
            <li>Work milestone deliverables will be released upon clearance.</li>
            <li>Official UPI ID: chittortech@icici (or scan official ChittorTech QR code).</li>
            <li>Please make payments payable to ChittorTech official accounts.</li>
        """.trimIndent()
    }

    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>ChittorTech Invoice ${invoice.invoiceId}</title>
            <style>
                * { -webkit-print-color-adjust: exact !important; print-color-adjust: exact !important; box-sizing: border-box; }
                @page { size: A4 portrait; margin: 0; }
                body {
                    margin: 0;
                    padding: 0;
                    background: #ffffff;
                    color: #1A1A1A;
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Inter", Helvetica, Arial, sans-serif;
                }
                .inv-wrapper {
                    width: 100%;
                    max-width: 210mm;
                    min-height: 297mm;
                    margin: 0 auto;
                    padding: 40px 34px 0 34px;
                    display: flex;
                    flex-direction: column;
                    position: relative;
                    font-size: 12.5px;
                    line-height: 1.55;
                }
                .inv-watermark {
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    width: 55%;
                    opacity: 0.08;
                    pointer-events: none;
                    z-index: 1;
                }
                .inv-body { flex: 1; position: relative; z-index: 2; }
                .inv-title {
                    font-size: 28px;
                    font-weight: 800;
                    color: #0B2545;
                    margin: 0 0 11px 0;
                    letter-spacing: -0.3px;
                    line-height: 1.1;
                }
                .inv-top-banner {
                    display: flex;
                    width: 100%;
                    height: 60px;
                    margin-bottom: 16px;
                }
                .inv-banner-left {
                    background-color: #0B2545;
                    color: #ffffff;
                    padding: 0 18px;
                    flex: 0 0 54%;
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    gap: 4px;
                }
                .inv-banner-left p { margin: 0; font-size: 12px; }
                .inv-banner-left strong { font-weight: 700; margin-right: 5px; }
                .inv-banner-right {
                    background-color: #E8EDF3;
                    flex: 1;
                    display: flex;
                    align-items: center;
                    padding: 0 46px 0 14px;
                    gap: 11px;
                    position: relative;
                    overflow: hidden;
                }
                .inv-banner-right::after {
                    content: '';
                    position: absolute;
                    right: 0; top: 0; bottom: 0;
                    width: 13px;
                    background: #0B2545;
                }
                .inv-banner-right img { height: 48px; width: auto; object-fit: contain; }
                .inv-banner-right h2 { font-size: 24px; font-weight: 800; color: #0B2545; margin: 0; }
                .inv-divider {
                    border: none;
                    border-top: 1.5px dashed #3CBFB4;
                    margin: 0 0 16px 0;
                }
                .inv-billing-row {
                    display: flex;
                    justify-content: space-between;
                    gap: 18px;
                    margin-bottom: 18px;
                }
                .inv-billing-col { flex: 1; }
                .inv-billing-col h3 {
                    font-size: 14px;
                    font-weight: 900;
                    color: #000000;
                    margin: 0 0 7px 0;
                }
                .inv-billing-col p { margin: 0 0 3px 0; font-size: 12px; color: #4A4A4A; }
                .inv-billing-col strong { color: #1A1A1A; font-weight: 600; }
                .inv-section-title {
                    font-size: 14px;
                    font-weight: 900;
                    color: #000000;
                    margin: 0 0 7px 0;
                }
                .inv-table {
                    width: 100%;
                    border-collapse: collapse;
                }
                .inv-table thead tr { background: #3CBFB4; }
                .inv-table th {
                    color: #ffffff;
                    font-weight: 700;
                    font-size: 11.5px;
                    padding: 8px 10px;
                    text-align: left;
                }
                .inv-table th.r { text-align: right; }
                .inv-table th.c { text-align: center; }
                .inv-totals-box {
                    background: #0B2545;
                    color: #ffffff;
                    padding: 9px 14px;
                    display: flex;
                    flex-direction: column;
                    gap: 3px;
                    margin-bottom: 18px;
                }
                .inv-total-row {
                    display: flex;
                    justify-content: space-between;
                    font-size: 12px;
                }
                .inv-total-row.big {
                    font-size: 13.5px;
                    font-weight: 800;
                    border-top: 1px solid rgba(255,255,255,0.25);
                    padding-top: 5px;
                    margin-top: 3px;
                }
                .inv-lower-row {
                    display: flex;
                    gap: 18px;
                    margin-bottom: 18px;
                }
                .inv-card-box {
                    flex: 1;
                    border: 1.5px solid #D8DDE3;
                    border-radius: 4px;
                    padding: 13px 15px;
                    background: rgba(255,255,255,0.8);
                }
                .inv-card-box h3 {
                    font-size: 14px;
                    font-weight: 900;
                    color: #000000;
                    margin: 0 0 9px 0;
                    padding-bottom: 6px;
                    border-bottom: 1.5px solid #D8DDE3;
                }
                .inv-card-box p { margin: 0 0 4px 0; font-size: 12px; color: #4A4A4A; }
                .inv-card-box strong { color: #1A1A1A; font-weight: 600; }
                .inv-card-box ul { margin: 0; padding-left: 16px; font-size: 11.5px; color: #4A4A4A; }
                .inv-card-box ul li { margin-bottom: 4px; }
                .inv-signature-area {
                    display: flex;
                    justify-content: flex-end;
                    padding-right: 4px;
                    margin-bottom: 16px;
                }
                .inv-sig-block { text-align: right; min-width: 260px; }
                .inv-sig-date { font-size: 12px; font-weight: 700; color: #1A1A1A; margin-bottom: 6px; }
                .inv-sig-disclaimer {
                    font-size: 11px;
                    color: #475569;
                    font-weight: 600;
                    font-style: italic;
                    letter-spacing: 0.2px;
                }
                .inv-footer-bar {
                    background: #0B2545;
                    color: #ffffff;
                    padding: 11px 20px;
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    font-size: 12px;
                    margin-top: auto;
                    position: relative;
                    z-index: 2;
                }
            </style>
        </head>
        <body>
            <div class="inv-wrapper">
                <img src="https://chittortech.in/images/chittortech_logo.png" class="inv-watermark" alt="">
                <div class="inv-body">
                    <h1 class="inv-title">Invoice Service</h1>

                    <div class="inv-top-banner">
                        <div class="inv-banner-left">
                            <p><strong>Invoice Number:</strong> ${invoice.invoiceId}</p>
                            <p><strong>Invoice Date:</strong> ${invDate}</p>
                        </div>
                        <div class="inv-banner-right">
                            <img src="https://chittortech.in/images/chittortech_logo.png" alt="ChittorTech Logo">
                            <h2>ChittorTech</h2>
                        </div>
                    </div>

                    <hr class="inv-divider">

                    <div class="inv-billing-row">
                        <div class="inv-billing-col">
                            <h3>Billing Information:</h3>
                            <p><strong>Company Name:</strong> ChittorTech</p>
                            <p><strong>Company Address:</strong> Chittorgarh, Rajasthan – 312001</p>
                            <p><strong>Phone Number:</strong> +91 7597451057</p>
                            <p><strong>Email:</strong> business@chittortech.in</p>
                        </div>
                        <div class="inv-billing-col">
                            <h3>Bill To:</h3>
                            <p><strong>Customer Name:</strong> ${clientName}</p>
                            <p><strong>Billing Address:</strong> Chittorgarh, Rajasthan</p>
                            <p><strong>Phone:</strong> +91 7597451057</p>
                            <p><strong>Email:</strong> ${invoice.clientId}</p>
                        </div>
                    </div>

                    <h2 class="inv-section-title">Service Details:</h2>
                    <table class="inv-table">
                        <thead>
                            <tr>
                                <th style="width: 32px;">No</th>
                                <th>Item Description</th>
                                <th class="c" style="width: 88px;">Hours/Units</th>
                                <th class="r" style="width: 110px;">Rate/Unit (₹)</th>
                                <th class="r" style="width: 100px;">Total (₹)</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${itemsHtml}
                        </tbody>
                    </table>

                    <div class="inv-totals-box">
                        <div class="inv-total-row">
                            <span>Subtotal</span>
                            <span>₹${invoice.amount.fmt()}</span>
                        </div>
                        <div class="inv-total-row big">
                            <span>${totalLabel}</span>
                            <span>₹${invoice.amount.fmt()}</span>
                        </div>
                    </div>

                    <div class="inv-lower-row">
                        <div class="inv-card-box">
                            <h3>Billing Information:</h3>
                            <p><strong>Date: </strong>${invDate}</p>
                            <p><strong>Payment Method: </strong>${invoice.paymentMode.ifBlank { "Bank Transfer" }}</p>
                            <p><strong>Due Date: </strong>${dueDateFormatted}</p>
                            <div style="margin-top: 6px; font-size: 11px; color: #4A4A4A; line-height: 1.4;">
                                ${paymentInfoHtml}
                            </div>
                        </div>
                        <div class="inv-card-box">
                            <h3>Terms and Conditions:</h3>
                            <ul style="margin: 0; padding-left: 16px; font-size: 11.5px; color: #4A4A4A;">
                                ${termsItemsHtml}
                            </ul>
                        </div>
                    </div>

                    <div class="inv-signature-area">
                        <div class="inv-sig-block">
                            <div class="inv-sig-date">Date: ${invDate}</div>
                            <div class="inv-sig-disclaimer">
                                * Bill generated from the digital platform, no manual sign needed
                            </div>
                        </div>
                    </div>
                </div>

                <div class="inv-footer-bar">
                    <span>📞 +91 7597451057</span>
                    <span>✉ business@chittortech.in</span>
                    <span>🌐 chittortech.in</span>
                </div>
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


