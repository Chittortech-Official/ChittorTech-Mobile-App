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
import com.chittortech.app.util.*

@Composable
fun ClientDashboardScreen(
    user: CtUser,
    project: Project? = null,
    projects: List<Project> = listOfNotNull(project),
    invoices: List<Invoice>,
    tickets: List<SupportTicket>,
    onViewInvoices: () -> Unit,
    onRaiseTicket: () -> Unit,
    onContactEngineer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeProjects = if (projects.isNotEmpty()) projects else listOfNotNull(project)
    val pendingCount = invoices.count { it.status != "PAID" }
    val openTickets  = tickets.count { it.status == "OPEN" }
    val recentTickets = remember(tickets) {
        tickets.sortedByDescending { it.createdAt?.seconds ?: 0L }.take(5)
    }

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

        // ── Active Projects Section ───────────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                val headerTitle = if (activeProjects.size > 1) "🚀 Active Projects (${activeProjects.size})" else "🚀 Active Project"
                SectionHeader(title = headerTitle)
                Spacer(modifier = Modifier.height(10.dp))

                if (activeProjects.isNotEmpty()) {
                    activeProjects.forEachIndexed { index, proj ->
                        if (index > 0) Spacer(modifier = Modifier.height(12.dp))
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
                                            proj.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        if (proj.domain.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                proj.domain,
                                                fontSize = 12.sp,
                                                color = CtPrimaryBlue
                                            )
                                        }
                                    }
                                    StatusBadge(status = proj.status)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Current Phase
                                if (proj.currentPhase.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CtGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Current Phase: ${proj.currentPhase}",
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
                                    progress = { proj.milestoneProgress / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = CtPrimaryBlue,
                                    trackColor = CtPrimaryLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${proj.milestoneProgress}% Complete", fontSize = 11.sp, color = TextMuted)
                            }
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

        // ── Active Client Deliverables & Infrastructure ──────────────────────
        if (activeProjects.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(title = "🚀 Active Project Deliverables")
                    Spacer(modifier = Modifier.height(10.dp))
                    activeProjects.forEachIndexed { idx, p ->
                        if (idx > 0) Spacer(modifier = Modifier.height(14.dp))
                        when (p.getCategory()) {
                            ProjectCategory.SEO_MARKETING -> SeoCampaignCard(project = p)
                            ProjectCategory.MOBILE_APP -> MobileAppReleaseCard(project = p)
                            ProjectCategory.AI_SOLUTIONS -> AiArchitectureCard(project = p)
                            ProjectCategory.AI_CHATBOT -> ChatbotStatusCard(project = p)
                            ProjectCategory.ENTERPRISE_ERP -> EnterpriseErpCard(project = p)
                            ProjectCategory.WEBSITE_SAAS -> WebSaasInfraCard(project = p)
                        }
                    }
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
            items(recentTickets) { ticket ->
                TicketRowCard(ticket = ticket, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (tickets.size > 5) {
                item {
                    TextButton(
                        onClick = onRaiseTicket,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "View all ${tickets.size} tickets in Helpdesk →",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CtPrimaryBlue
                        )
                    }
                }
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

// ─── Dedicated Category-Specific Deliverable Cards ────────────────────────────

@Composable
fun SeoCampaignCard(project: Project, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("SEO & Ranking Campaign", fontSize = 11.sp, color = TextSecondary)
                        Text(project.domain.ifBlank { project.name }, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFEF3C7)) {
                    Text("📈 SEO Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            if (project.targetKeywords.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFFBEB),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("TARGET KEYWORDS FOCUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                        Text(project.targetKeywords, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CtBorder)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Target Region", fontSize = 11.sp, color = TextSecondary)
                    Text(project.platform.ifBlank { "Pan-India / Local GMB" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Campaign Kickoff", fontSize = 11.sp, color = TextSecondary)
                    val kickoffDate = formatDateDDMMYYYY(project.buildKickoffDate)
                    Text(kickoffDate.ifBlank { "Active" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun MobileAppReleaseCard(project: Project, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFECFDF5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(project.platform.ifBlank { "Android (Google Play)" }, fontSize = 11.sp, color = TextSecondary)
                        Text(project.domain.ifBlank { "App Package In Progress" }, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFECFDF5)) {
                    Text(project.hostingProvider.ifBlank { "12-Tester Track" }, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CtBorder)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Build Kickoff", fontSize = 11.sp, color = TextSecondary)
                    val kickoffDate = formatDateDDMMYYYY(project.buildKickoffDate)
                    Text(kickoffDate.ifBlank { "Recent" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Target Launch Date", fontSize = 11.sp, color = TextSecondary)
                    val expiryOrLaunch = formatDateDDMMYYYY(project.domainExpiryDate.ifBlank { project.launchDate })
                    Text(expiryOrLaunch.ifBlank { "TBD" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF059669))
                }
            }
        }
    }
}

@Composable
fun WebSaasInfraCard(project: Project, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF0F9FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Production Domain", fontSize = 11.sp, color = TextSecondary)
                        Text(project.domain.ifBlank { "Domain In Setup" }, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                val isSsl = project.sslStatus.contains("Active", ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSsl) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = if (isSsl) Color(0xFF059669) else Color(0xFFEF4444), modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isSsl) "SSL Active" else "SSL Issue", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSsl) Color(0xFF059669) else Color(0xFFEF4444))
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CtBorder)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Cloud Server", fontSize = 11.sp, color = TextSecondary)
                    Text(project.hostingProvider.ifBlank { "Vercel / AWS" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Domain Expiry", fontSize = 11.sp, color = TextSecondary)
                    val expiryDate = formatDateDDMMYYYY(project.domainExpiryDate)
                    Text(expiryDate.ifBlank { "Auto-Renew" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0284C7))
                }
            }
        }
    }
}

@Composable
fun AiArchitectureCard(project: Project, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF5F3FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Enterprise AI Architecture", fontSize = 11.sp, color = TextSecondary)
                        Text(project.platform.ifBlank { "RAG Knowledge Pipeline" }, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF5F3FF)) {
                    Text("🧠 Groq LPU", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CtBorder)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Foundation Model", fontSize = 11.sp, color = TextSecondary)
                    Text(project.hostingProvider.ifBlank { "Llama 3.3 (Groq)" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Vector DB Store", fontSize = 11.sp, color = TextSecondary)
                    Text(project.domain.ifBlank { "Pinecone / pgvector" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF7C3AED))
                }
            }
        }
    }
}

@Composable
fun ChatbotStatusCard(project: Project, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFECFEFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFF0891B2), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Conversational AI Agent", fontSize = 11.sp, color = TextSecondary)
                        Text(project.platform.ifBlank { "WhatsApp Business API" }, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFECFEFF)) {
                    Text("💬 24/7 Live", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0891B2), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CtBorder)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Bot Persona", fontSize = 11.sp, color = TextSecondary)
                    Text(project.hostingProvider.ifBlank { "Customer Support & Booking" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Languages", fontSize = 11.sp, color = TextSecondary)
                    Text(project.domain.ifBlank { "Hindi, English" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0891B2))
                }
            }
        }
    }
}

@Composable
fun EnterpriseErpCard(project: Project, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEEF2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = Color(0xFF4338CA), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Enterprise ERP Suite", fontSize = 11.sp, color = TextSecondary)
                        Text(project.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFEEF2FF)) {
                    Text("🏢 RBAC Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4338CA), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CtBorder)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Modules", fontSize = 11.sp, color = TextSecondary)
                    Text(project.platform.ifBlank { "GST Billing & CRM" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Deployment", fontSize = 11.sp, color = TextSecondary)
                    Text(project.hostingProvider.ifBlank { "Dedicated Cloud VPS" }, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4338CA))
                }
            }
        }
    }
}

@Composable
fun InfraStatusCard(project: Project, modifier: Modifier = Modifier) {
    when (project.getCategory()) {
        ProjectCategory.SEO_MARKETING -> SeoCampaignCard(project = project, modifier = modifier)
        ProjectCategory.MOBILE_APP -> MobileAppReleaseCard(project = project, modifier = modifier)
        ProjectCategory.AI_SOLUTIONS -> AiArchitectureCard(project = project, modifier = modifier)
        ProjectCategory.AI_CHATBOT -> ChatbotStatusCard(project = project, modifier = modifier)
        ProjectCategory.ENTERPRISE_ERP -> EnterpriseErpCard(project = project, modifier = modifier)
        ProjectCategory.WEBSITE_SAAS -> WebSaasInfraCard(project = project, modifier = modifier)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(ticket.category, fontSize = 11.sp, color = TextSecondary)
                    val dateFormatted = formatTimestampDDMMYYYY(ticket.createdAt)
                    if (dateFormatted.isNotBlank()) {
                        Text(" • $dateFormatted", fontSize = 11.sp, color = TextMuted)
                    }
                }
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
