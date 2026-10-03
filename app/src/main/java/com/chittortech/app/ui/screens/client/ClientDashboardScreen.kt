package com.chittortech.app.ui.screens.client

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
fun ClientDashboardScreen(
    user: CtUser,
    project: Project?,
    invoices: List<Invoice>,
    tickets: List<SupportTicket>,
    onViewInvoices: () -> Unit,
    onRaiseTicket: () -> Unit,
    onContactEngineer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingCount = invoices.count { it.status != "PAID" }
    val openTickets  = tickets.count { it.status == "OPEN" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CtBackground),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {

        // ── Gradient Welcome Header ───────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(CtPrimaryDark, GradEnd)))
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Welcome back,",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = user.companyName.ifBlank { user.displayName },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your ChittorTech Client Portal",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
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

        // ── Quick Stats Row ───────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-12).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "Pending Bills",
                    value = if (pendingCount > 0) "₹${invoices.filter { it.status != "PAID" }.sumOf { it.amount }.toFormatted()}" else "All Clear",
                    subtitle = "$pendingCount invoice(s)",
                    icon = Icons.Default.Receipt,
                    iconBg = if (pendingCount > 0) CtAmberLight else CtGreenLight,
                    iconTint = if (pendingCount > 0) CtAmber else CtGreen,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Open Tickets",
                    value = openTickets.toString(),
                    subtitle = "${tickets.size} total raised",
                    icon = Icons.Default.Support,
                    iconBg = if (openTickets > 0) CtRedLight else CtGreenLight,
                    iconTint = if (openTickets > 0) CtRed else CtGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Active Project Card ───────────────────────────────────────────────
        item {
            if (project != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(title = "🚀 Active Project")
                    Spacer(modifier = Modifier.height(10.dp))
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
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        project.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        project.domain,
                                        fontSize = 12.sp,
                                        color = CtPrimaryBlue
                                    )
                                }
                                StatusBadge(status = project.status)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Current Phase
                            if (project.currentPhase.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CtGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Current Phase: ${project.currentPhase}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            // Milestone Progress Bar
                            Text("Milestone Progress", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { project.milestoneProgress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = CtPrimaryBlue,
                                trackColor = CtPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${project.milestoneProgress}% Complete", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            }
        }

        // ── Quick Actions ─────────────────────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(title = "⚡ Quick Actions")
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.Receipt,
                        label = "View Invoices",
                        color = CtPrimaryBlue,
                        onClick = onViewInvoices,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        icon = Icons.Default.BugReport,
                        label = "Raise Ticket",
                        color = CtAmber,
                        onClick = onRaiseTicket,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        icon = Icons.Default.Chat,
                        label = "Contact Lead",
                        color = CtGreen,
                        onClick = onContactEngineer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ── Renewal Info ──────────────────────────────────────────────────────
        if (project != null && project.domainExpiryDate.isNotBlank()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(title = "🔒 Infrastructure Status")
                    Spacer(modifier = Modifier.height(10.dp))
                    InfraStatusCard(project = project)
                }
            }
        }

        // ── Recent Tickets ────────────────────────────────────────────────────
        if (tickets.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(title = "🎫 Recent Tickets", actionLabel = "View All", onAction = {})
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            items(tickets.take(3)) { ticket ->
                TicketRowCard(ticket = ticket, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ─── Quick Action Card ────────────────────────────────────────────────────────

@Composable
fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ─── Infra Status Card ────────────────────────────────────────────────────────

@Composable
fun InfraStatusCard(project: Project, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfraRow(label = "Domain", value = project.domain, icon = Icons.Default.Language)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CtBorder)
            InfraRow(label = "Hosting", value = project.hostingProvider, icon = Icons.Default.Cloud)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CtBorder)
            InfraRow(
                label = "SSL Certificate",
                value = project.sslStatus,
                icon = Icons.Default.Lock,
                valueColor = if (project.sslStatus == "Active") CtGreen else CtRed
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CtBorder)
            InfraRow(label = "Domain Expiry", value = project.domainExpiryDate, icon = Icons.Default.CalendarToday)
            if (project.annualRenewalFee > 0) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CtBorder)
                InfraRow(
                    label = "Annual Renewal",
                    value = "₹${project.annualRenewalFee.toFormatted()}",
                    icon = Icons.Default.CurrencyRupee
                )
            }
        }
    }
}

@Composable
private fun InfraRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    valueColor: Color = TextPrimary
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.weight(1f))
        Text(value.ifBlank { "—" }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

// ─── Ticket Row Card ──────────────────────────────────────────────────────────

@Composable
fun TicketRowCard(ticket: SupportTicket, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CtPrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(ticket.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(ticket.category, fontSize = 11.sp, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(status = ticket.status)
                Spacer(modifier = Modifier.height(4.dp))
                com.chittortech.app.ui.components.PriorityBadge(priority = ticket.priority)
            }
        }
    }
}

// ─── Extension ────────────────────────────────────────────────────────────────

private fun Long.toFormatted(): String {
    return if (this >= 100000) "${this / 100000}.${(this % 100000) / 1000}L"
    else if (this >= 1000) "${this / 1000},${String.format("%03d", this % 1000)}"
    else this.toString()
}
