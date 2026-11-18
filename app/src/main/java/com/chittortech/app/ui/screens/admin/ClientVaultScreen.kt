package com.chittortech.app.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
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
import com.chittortech.app.ui.components.EmptyState
import androidx.compose.material3.HorizontalDivider
import com.chittortech.app.util.formatDateDDMMYYYY

@Composable
fun ClientVaultScreen(
    clients: List<CtUser>,
    projects: List<Project>,
    modifier: Modifier = Modifier
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = CtAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CREDENTIAL VAULT", fontSize = 10.sp, color = CtAmber, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Infrastructure Dossier", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("7-tier project data for all clients", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }

        if (projects.isEmpty()) {
            EmptyState(icon = Icons.Default.Storage, message = "No client projects yet.")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(projects) { project ->
                    val client = clients.find { it.uid == project.clientId || it.email.equals(project.clientId, ignoreCase = true) }
                    ProjectDossierCard(project = project, client = client)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun ProjectDossierCard(project: Project, client: CtUser?) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            val category = project.getCategory()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        if (category == ProjectCategory.MOBILE_APP && !project.domain.contains(".")) "Play Store App"
                        else project.domain.ifBlank { category.label },
                        fontSize = 12.sp,
                        color = CtPrimaryBlue
                    )
                    if (client != null) {
                        Text("Client: ${client.displayName}", fontSize = 11.sp, color = TextMuted)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(category.badgeColorHex).copy(alpha = 0.12f)
                        ) {
                            Text(
                                category.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(category.badgeColorHex),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CtGreenLight)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(project.status.ifBlank { "Active" }, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CtGreen)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextMuted
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = CtBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    when (category) {
                        ProjectCategory.WEBSITE_SAAS -> {
                            DossierSection(title = "🌐 Domain Infrastructure")
                            VaultRow("Registrar", project.domainRegistrar.ifBlank { "—" })
                            VaultRow("Registrar Email", project.domainRegistrarEmail.ifBlank { "—" })
                            VaultRow("2FA Phone", project.domainRegistrar2FA.ifBlank { "—" })
                            VaultRow("Expiry Date", formatDateDDMMYYYY(project.domainExpiryDate).ifBlank { "—" })

                            Spacer(modifier = Modifier.height(10.dp))
                            DossierSection(title = "☁️ Hosting Server")
                            VaultRow("Provider", project.hostingProvider.ifBlank { "—" })
                            VaultRow("Server IP", project.hostingServerIp.ifBlank { "—" })
                            VaultRow("Panel Login", project.hostingPanelLogin.ifBlank { "—" })
                            VaultRow("Specs", project.hostingSpecs.ifBlank { "—" })
                            VaultRow("Expiry Date", formatDateDDMMYYYY(project.hostingExpiryDate).ifBlank { "—" })

                            Spacer(modifier = Modifier.height(10.dp))
                            DossierSection(title = "🔒 Security")
                            VaultRow("SSL Status", project.sslStatus.ifBlank { "Active" })

                            Spacer(modifier = Modifier.height(10.dp))
                            DossierSection(title = "💻 Development & Roadmap")
                            VaultRow("GitHub Repo", project.githubRepo.ifBlank { "—" })
                            VaultRow("Build Kickoff", formatDateDDMMYYYY(project.buildKickoffDate).ifBlank { "—" })
                            VaultRow("Launch Date", formatDateDDMMYYYY(project.launchDate).ifBlank { "—" })
                            VaultRow("Current Phase", project.currentPhase.ifBlank { "—" })
                        }
                        ProjectCategory.MOBILE_APP -> {
                            DossierSection(title = "📱 Distribution & Play Store")
                            VaultRow("Target Platform", project.platform.ifBlank { "Android & iOS" })
                            VaultRow("Package Name / App ID", project.domain.ifBlank { "com.chittortech.app" })
                            VaultRow("Release Track", project.currentPhase.ifBlank { "Production Track" })
                            VaultRow("Developer Console", "ChittorTech Managed Developer Account")

                            Spacer(modifier = Modifier.height(10.dp))
                            DossierSection(title = "💻 Development & Source")
                            VaultRow("GitHub Repo", project.githubRepo.ifBlank { "—" })
                            VaultRow("Build Kickoff", formatDateDDMMYYYY(project.buildKickoffDate).ifBlank { "—" })
                            VaultRow("Launch Date", formatDateDDMMYYYY(project.launchDate).ifBlank { "—" })
                            VaultRow("Build Pipeline", "Gradle / GitHub Actions CI/CD")
                        }
                        ProjectCategory.SEO_MARKETING -> {
                            DossierSection(title = "📈 SEO & Visibility Campaign")
                            VaultRow("Target Website", project.domain.ifBlank { "Client Portal" })
                            VaultRow("Campaign Focus", project.currentPhase.ifBlank { "Keyword Rankings & Technical SEO" })
                            VaultRow("Audit Frequency", "Weekly Search Console & Speed Audit")
                            VaultRow("Reporting Deliverable", "Monthly Backlink & Organic Traffic Dossier")

                            Spacer(modifier = Modifier.height(10.dp))
                            DossierSection(title = "💻 Campaign Roadmap")
                            VaultRow("Campaign Kickoff", formatDateDDMMYYYY(project.buildKickoffDate).ifBlank { "—" })
                            VaultRow("Target Review Date", formatDateDDMMYYYY(project.launchDate).ifBlank { "—" })
                            VaultRow("Execution Sprint", "${project.milestoneProgress}% Sprint Completed")
                        }
                        ProjectCategory.AI_CHATBOT -> {
                            DossierSection(title = "🤖 Conversational AI Architecture")
                            VaultRow("Deployment Channels", "WhatsApp Business Cloud API / Web")
                            VaultRow("LLM Engine", "Groq LPU / Meta Llama 3 Fine-Tuned")
                            VaultRow("Knowledge Scope", project.currentPhase.ifBlank { "Trained on Client FAQs & Docs" })
                            VaultRow("Inference SLA", "Sub-500ms Instant Response")

                            Spacer(modifier = Modifier.height(10.dp))
                            DossierSection(title = "💻 Pipeline & Integration")
                            VaultRow("Kickoff Date", formatDateDDMMYYYY(project.buildKickoffDate).ifBlank { "—" })
                            VaultRow("Knowledge Vectorization", "${project.milestoneProgress}% Complete")
                            VaultRow("Engine Architecture", "ChittorTech AI Engine v2")
                        }
                        ProjectCategory.AI_SOLUTIONS -> {
                            DossierSection(title = "🧠 Enterprise AI & RAG Pipeline")
                            VaultRow("Model Architecture", "Custom RAG & Embedding Pipeline")
                            VaultRow("Vector DB", "Private Encrypted Vector Store")
                            VaultRow("Ingestion Status", project.currentPhase.ifBlank { "Data Vectorization Complete" })
                            VaultRow("Privacy Protocol", "Air-Gapped / Zero Data Leakage")

                            Spacer(modifier = Modifier.height(10.dp))
                            DossierSection(title = "💻 Development & Compute")
                            VaultRow("Kickoff Date", formatDateDDMMYYYY(project.buildKickoffDate).ifBlank { "—" })
                            VaultRow("Pipeline Progress", "${project.milestoneProgress}% Integration")
                            VaultRow("Source Repository", project.githubRepo.ifBlank { "—" })
                        }
                        ProjectCategory.ENTERPRISE_ERP -> {
                            DossierSection(title = "💼 Enterprise Architecture")
                            VaultRow("Core Modules", project.currentPhase.ifBlank { "GST Billing, Inventory, Multi-User RBAC" })
                            VaultRow("Hosting Database", project.hostingProvider.ifBlank { "Encrypted Cloud Database" })
                            VaultRow("Server Specs", project.hostingSpecs.ifBlank { "Dedicated Cloud Node" })
                            VaultRow("Backup Frequency", "Daily Automated Snapshot & Recovery")

                            Spacer(modifier = Modifier.height(10.dp))
                            DossierSection(title = "💻 Deployment & Roadmap")
                            VaultRow("Implementation Kickoff", formatDateDDMMYYYY(project.buildKickoffDate).ifBlank { "—" })
                            VaultRow("Go-Live Launch", formatDateDDMMYYYY(project.launchDate).ifBlank { "—" })
                            VaultRow("Rollout Progress", "${project.milestoneProgress}% Completed")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    DossierSection(title = "💰 Commercial & SLA")
                    VaultRow("Annual Retainer / AMC", if (project.annualRenewalFee > 0) "₹${project.annualRenewalFee.toFormattedAmount()}/yr" else "Included in Project Contract")
                }
            }
        }
    }
}

@Composable
private fun DossierSection(title: String) {
    Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun VaultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.weight(0.45f)
        )
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.weight(0.55f)
        )
    }
}

private fun Long.toFormattedAmount(): String {
    return if (this >= 1000) "${this / 1000},${String.format("%03d", this % 1000)}" else this.toString()
}
