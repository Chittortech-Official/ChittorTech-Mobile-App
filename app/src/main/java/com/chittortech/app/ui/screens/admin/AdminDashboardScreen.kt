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
    modifier: Modifier = Modifier
) {
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
