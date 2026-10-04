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
            Spacer(modifier = Modifier.height(4.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(title = "🚀 Active Project")
                Spacer(modifier = Modifier.height(10.dp))
                if (project != null) {
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
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CtPrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.RocketLaunch,
                                    contentDescription = null,
                                    tint = CtPrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "No Active Project Linked Yet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Once your ChittorTech workspace is linked to your account, live progress, domain, and server stats will appear here.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = onContactEngineer,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, CtPrimaryBlue),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CtPrimaryBlue)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Contact Lead to Link Project", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
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
                        label = "Contact Support",
                        color = CtGreen,
                        onClick = onContactEngineer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ── Infrastructure & Renewal Info ────────────────────────────────────
        if (project != null && (project.domain.isNotBlank() || project.domainExpiryDate.isNotBlank())) {
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
                    SectionHeader(title = "🎫 Recent Tickets", actionLabel = "View All", onAction = onRaiseTicket)
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
        modifier = modifier
            .height(104.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}

// ─── Infra Status Card (Client-Facing: Domain, SSL, Dates & Maintenance) ──────

@Composable
fun InfraStatusCard(project: Project, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Domain & SSL Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CtPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Production Domain", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            project.domain.ifBlank { "—" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                // SSL Badge
                val isSslActive = project.sslStatus.contains("Active", ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSslActive) CtGreenLight else CtRedLight,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isSslActive) CtGreen else CtRed,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSslActive) "SSL Active" else "SSL Issue",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSslActive) CtGreen else CtRed
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CtBorder)

            // Start Date, Expiry Date & Annual Maintenance Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Project Kickoff Date
                Column(modifier = Modifier.weight(1f)) {
                    Text("Kickoff Date", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = CtGreen, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            project.buildKickoffDate.ifBlank { "—" },
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                // Domain Expiry Date
                Column(modifier = Modifier.weight(1f)) {
                    Text("Domain Expiry", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            project.domainExpiryDate.ifBlank { "—" },
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                // Annual Maintenance (AMC)
                if (project.annualRenewalFee > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Annual Maintenance", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "₹${project.annualRenewalFee.toFormatted()}/yr",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CtPrimaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ChittorTech Admin Note for Deeper Server Configs
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
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = CtPrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "For custom cloud scaling, dedicated IPs, or server changes, please contact ChittorTech Admin.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
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
