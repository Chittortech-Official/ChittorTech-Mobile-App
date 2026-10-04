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
import com.chittortech.app.model.CtUser
import com.chittortech.app.model.Project
import com.chittortech.app.theme.*
import com.chittortech.app.ui.components.EmptyState
import androidx.compose.material3.HorizontalDivider

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(project.domain, fontSize = 12.sp, color = CtPrimaryBlue)
                    if (client != null) {
                        Text("Client: ${client.displayName}", fontSize = 11.sp, color = TextMuted)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CtGreenLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(project.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CtGreen)
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

                    // ── Domain Section ────────────────────────────────────────
                    DossierSection(title = "🌐 Domain")
                    VaultRow("Registrar", project.domainRegistrar)
                    VaultRow("Registrar Email", project.domainRegistrarEmail)
                    VaultRow("2FA Phone", project.domainRegistrar2FA.ifBlank { "—" })
                    VaultRow("Expiry Date", project.domainExpiryDate)

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Hosting Section ───────────────────────────────────────
                    DossierSection(title = "☁️ Hosting Server")
                    VaultRow("Provider", project.hostingProvider)
                    VaultRow("Server IP", project.hostingServerIp.ifBlank { "—" })
                    VaultRow("Panel Login", project.hostingPanelLogin.ifBlank { "—" })
                    VaultRow("Specs", project.hostingSpecs.ifBlank { "—" })
                    VaultRow("Expiry Date", project.hostingExpiryDate)

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Security ──────────────────────────────────────────────
                    DossierSection(title = "🔒 Security")
                    VaultRow("SSL Status", project.sslStatus)

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Development ───────────────────────────────────────────
                    DossierSection(title = "💻 Development")
                    VaultRow("GitHub Repo", project.githubRepo.ifBlank { "—" })
                    VaultRow("Build Kickoff", project.buildKickoffDate.ifBlank { "—" })
                    VaultRow("Launch Date", project.launchDate.ifBlank { "—" })
                    VaultRow("Current Phase", project.currentPhase.ifBlank { "—" })

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Commercial ────────────────────────────────────────────
                    DossierSection(title = "💰 Commercial")
                    VaultRow("Annual Renewal", "₹${project.annualRenewalFee.toFormattedAmount()}")
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
