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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun AdminDashboardScreen(
    kpi: AdminKpi,
    projects: List<Project>,
    invoices: List<Invoice>,
    notifications: List<AppNotification> = emptyList(),
    onSendNotification: (title: String, message: String) -> Unit = { _, _ -> },
    onDeleteNotification: (id: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCreateNotifDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CtBackground),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        // ── Admin Header ──────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF0F172A), CtPrimaryDark)))
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CtAmber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ADMIN COMMAND CENTER", fontSize = 11.sp, color = CtAmber, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("ChittorTech HQ", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Agency Operations Dashboard", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White)
                            .border(1.5.dp, Color.White.copy(alpha = 0.85f), androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.chittortech.app.R.drawable.chittortech_logo),
                            contentDescription = "ChittorTech Logo",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        // ── Financial KPI Grid ────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)) {
                SectionHeader(title = "📊 Financial Overview")
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        title = "Monthly Invoiced",
                        value = "₹${kpi.totalMonthlyInvoiced.fmtAmount()}",
                        subtitle = "Collected (PAID)",
                        icon = Icons.Default.CurrencyRupee,
                        iconBg = CtGreenLight,
                        iconTint = CtGreen,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Outstanding",
                        value = "₹${kpi.totalOutstandingDues.fmtAmount()}",
                        subtitle = "Pending dues",
                        icon = Icons.Default.Warning,
                        iconBg = CtRedLight,
                        iconTint = CtRed,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        title = "Active Deployments",
                        value = kpi.activeDeployments.toString(),
                        subtitle = "Live client projects",
                        icon = Icons.Default.CloudDone,
                        iconBg = CtPrimaryLight,
                        iconTint = CtPrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Open Tickets",
                        value = kpi.openTickets.toString(),
                        subtitle = "Awaiting resolution",
                        icon = Icons.Default.SupportAgent,
                        iconBg = CtAmberLight,
                        iconTint = CtAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ── Renewal Alerts ────────────────────────────────────────────────────
        val renewalAlerts = projects.mapNotNull { p ->
            val days = daysUntil(p.domainExpiryDate)
            if (days != null && days <= 30) Pair(p, days) else null
        }.sortedBy { it.second }

        if (renewalAlerts.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(title = "🚨 Renewal Alerts")
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            items(renewalAlerts) { (project, days) ->
                RenewalAlertBanner(
                    label = "${project.name} — Domain Expires",
                    daysLeft = days,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                )
            }
        }

        // ── Broadcast Notifications Management ──────────────────────────────
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(title = "📢 Broadcast Notifications")
                    Button(
                        onClick = { showCreateNotifDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.AddAlert, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("New Alert", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (notifications.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CtCardWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No active broadcast notifications",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Tap 'New Alert' to broadcast an announcement to all app users.",
                            fontSize = 11.5.sp,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(notifications) { notif ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CtCardWhite),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CtPrimaryLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                notif.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                notif.message,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                notif.timestamp?.toDate()?.let { d ->
                                    java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(d)
                                } ?: "Just now",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                        IconButton(
                            onClick = { onDeleteNotification(notif.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Delete notification",
                                tint = CtRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── Recent Invoices ───────────────────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(title = "🧾 Recent Invoices", actionLabel = "All Invoices")
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        items(invoices.take(5)) { invoice ->
            AdminInvoiceRow(invoice = invoice, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    // ── Broadcast Notification Dialog ─────────────────────────────────────────
    if (showCreateNotifDialog) {
        var notifTitle by remember { mutableStateOf("") }
        var notifMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateNotifDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = CtPrimaryBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Broadcast Notification", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This will send a live in-app notification to all mobile app users.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = notifTitle,
                        onValueChange = { notifTitle = it },
                        label = { Text("Title") },
                        placeholder = { Text("e.g. 🎉 New Update Available") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = notifMessage,
                        onValueChange = { notifMessage = it },
                        label = { Text("Message") },
                        placeholder = { Text("Enter notification description...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (notifTitle.isNotBlank() && notifMessage.isNotBlank()) {
                            onSendNotification(notifTitle.trim(), notifMessage.trim())
                            showCreateNotifDialog = false
                        }
                    },
                    enabled = notifTitle.isNotBlank() && notifMessage.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Broadcast Now", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateNotifDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

// ─── Admin Invoice Row ────────────────────────────────────────────────────────

@Composable
private fun AdminInvoiceRow(invoice: Invoice, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CtPrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(invoice.invoiceId.ifBlank { "Invoice" }, fontSize = 11.sp, color = TextMuted)
                Text(invoice.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(status = invoice.status)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "₹${invoice.amount.fmtAmount()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (invoice.status == "PAID") CtGreen else CtRed
                )
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun daysUntil(dateStr: String): Int? {
    return try {
        val target = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        ChronoUnit.DAYS.between(LocalDate.now(), target).toInt()
    } catch (e: Exception) { null }
}

private fun Long.fmtAmount(): String {
    return when {
        this >= 100000 -> "%.1fL".format(this / 100000.0)
        this >= 1000   -> "%.1fK".format(this / 1000.0)
        else           -> this.toString()
    }
}
