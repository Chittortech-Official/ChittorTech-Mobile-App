package com.chittortech.app.ui.screens.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.model.CtUser
import com.chittortech.app.model.Project
import com.chittortech.app.theme.*
import com.chittortech.app.ui.components.EmptyState
import com.chittortech.app.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectControlRoomScreen(
    projects: List<Project>,
    clients: List<CtUser>,
    onUpdateMilestone: (projectId: String, clientId: String, progress: Int, phase: String, status: String) -> Unit,
    onSaveProject: (Project) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddProjectDialog by remember { mutableStateOf(false) }
    var selectedProjectForEdit by remember { mutableStateOf<Project?>(null) }

    val filteredProjects = remember(projects, searchQuery) {
        if (searchQuery.isBlank()) projects
        else {
            projects.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.domain.contains(searchQuery, ignoreCase = true) ||
                it.clientId.contains(searchQuery, ignoreCase = true) ||
                it.currentPhase.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val activeCount = projects.count { it.status.contains("Live", ignoreCase = true) || it.status.contains("Active", ignoreCase = true) }
    val inDevCount = projects.count { it.status.contains("Dev", ignoreCase = true) || it.status.contains("Progress", ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CtBackground)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF0F172A), CtPrimaryDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Terminal,
                        contentDescription = null,
                        tint = CtAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "PROJECT CONTROL ROOM",
                        fontSize = 11.sp,
                        color = CtAmber,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Project Manager",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Active Milestones & Infrastructure Dossier",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    FloatingActionButton(
                        onClick = { showAddProjectDialog = true },
                        containerColor = CtAmber,
                        contentColor = Color.White,
                        modifier = Modifier.size(44.dp),
                        elevation = FloatingActionButtonDefaults.elevation(4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Project", modifier = Modifier.size(22.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Total: ${projects.size}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CtGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Live: $activeCount", fontSize = 12.sp, color = CtGreen, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CtPrimaryBlue.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("In Dev: $inDevCount", fontSize = 12.sp, color = CtPrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ── Search Bar ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by project name, domain, client...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CtCardWhite,
                    unfocusedContainerColor = CtCardWhite,
                    focusedBorderColor = CtPrimaryBlue,
                    unfocusedBorderColor = CtBorder
                ),
                singleLine = true
            )
        }

        // ── Projects List ─────────────────────────────────────────────────────
        if (filteredProjects.isEmpty()) {
            EmptyState(
                icon = Icons.Default.AccountTree,
                message = if (searchQuery.isNotBlank()) "No projects match \"$searchQuery\"" else "No projects in control room yet."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredProjects, key = { it.projectId.ifBlank { it.name } }) { project ->
                    val client = clients.find {
                        it.uid == project.clientId || it.email.equals(project.clientId, ignoreCase = true)
                    }
                    ProjectControlCard(
                        project = project,
                        client = client,
                        onEditMilestone = { selectedProjectForEdit = project }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // ── Edit Milestone Dialog ─────────────────────────────────────────────────
    selectedProjectForEdit?.let { proj ->
        val client = clients.find { it.uid == proj.clientId || it.email.equals(proj.clientId, ignoreCase = true) }
        EditMilestoneDialog(
            project = proj,
            clientName = client?.companyName?.ifBlank { client.displayName } ?: proj.clientId,
            onDismiss = { selectedProjectForEdit = null },
            onSave = { progress, phase, status ->
                onUpdateMilestone(proj.projectId, proj.clientId, progress, phase, status)
                selectedProjectForEdit = null
            }
        )
    }

    // ── Add New Project Dialog ────────────────────────────────────────────────
    if (showAddProjectDialog) {
        AddNewProjectDialog(
            clients = clients,
            onDismiss = { showAddProjectDialog = false },
            onConfirm = { newProject ->
                onSaveProject(newProject)
                showAddProjectDialog = false
            }
        )
    }
}

// ─── Project Control Card ─────────────────────────────────────────────────────

@Composable
private fun ProjectControlCard(
    project: Project,
    client: CtUser?,
    onEditMilestone: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showVaultDetails by remember { mutableStateOf(false) }

    val clientTitle = client?.companyName?.ifBlank { client.displayName }
        ?: project.clientId.substringBefore("@").replaceFirstChar { it.uppercase() }

    val statusColor = when {
        project.status.contains("Live", ignoreCase = true) -> CtGreen
        project.status.contains("Dev", ignoreCase = true) -> CtPrimaryBlue
        project.status.contains("Testing", ignoreCase = true) -> Color(0xFF8B5CF6)
        else -> CtAmber
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                ) {
                    Text(
                        text = project.name.ifBlank { "Untitled Project" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = clientTitle,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    if (project.domain.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                try {
                                    val url = if (project.domain.startsWith("http")) project.domain else "https://${project.domain}"
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                } catch (_: Exception) {}
                            }
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = project.domain,
                                fontSize = 12.sp,
                                color = CtPrimaryBlue,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Status Badge
                StatusBadge(status = project.status)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Milestone Progress
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CtBackground)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp)
                    ) {
                        Text(
                            "CURRENT PHASE",
                            fontSize = 10.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = project.currentPhase.ifBlank { "Development" },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CtPrimaryBlue.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${project.milestoneProgress}%",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CtPrimaryBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { (project.milestoneProgress.coerceIn(0, 100)) / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CtPrimaryBlue,
                    trackColor = CtBorder
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action: Update Milestone Button
                Button(
                    onClick = onEditMilestone,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Update Milestone & Phase", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Accordion toggle for Infrastructure Vault
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showVaultDetails = !showVaultDetails }
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = CtAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("7-Tier Infrastructure Dossier", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Icon(
                    if (showVaultDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted
                )
            }

            AnimatedVisibility(visible = showVaultDetails) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    HorizontalDivider(color = CtBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    VaultInfoRow(label = "Server IP", value = project.hostingServerIp) {
                        clipboardManager.setText(AnnotatedString(project.hostingServerIp))
                        Toast.makeText(context, "Copied IP", Toast.LENGTH_SHORT).show()
                    }
                    VaultInfoRow(label = "Hosting Provider", value = project.hostingProvider)
                    VaultInfoRow(label = "Panel Login", value = project.hostingPanelLogin) {
                        clipboardManager.setText(AnnotatedString(project.hostingPanelLogin))
                        Toast.makeText(context, "Copied Login", Toast.LENGTH_SHORT).show()
                    }
                    VaultInfoRow(label = "Hosting Specs", value = project.hostingSpecs)
                    VaultInfoRow(label = "Domain Registrar", value = project.domainRegistrar)
                    VaultInfoRow(label = "Domain Expiry", value = project.domainExpiryDate)
                    VaultInfoRow(label = "Hosting Expiry", value = project.hostingExpiryDate)
                    VaultInfoRow(label = "SSL Status", value = project.sslStatus)
                    VaultInfoRow(label = "GitHub Repo", value = project.githubRepo) {
                        if (project.githubRepo.isNotBlank()) {
                            try {
                                val url = if (project.githubRepo.startsWith("http")) project.githubRepo else "https://${project.githubRepo}"
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            } catch (_: Exception) {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VaultInfoRow(
    label: String,
    value: String,
    onCopyOrClick: (() -> Unit)? = null
) {
    if (value.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 11.sp, color = TextMuted)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = if (onCopyOrClick != null) Modifier.clickable { onCopyOrClick() } else Modifier
            ) {
                Text(
                    value,
                    fontSize = 12.sp,
                    color = if (onCopyOrClick != null) CtPrimaryBlue else TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                if (onCopyOrClick != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CtPrimaryBlue, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

// ─── Edit Milestone & Phase Dialog ────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditMilestoneDialog(
    project: Project,
    clientName: String,
    onDismiss: () -> Unit,
    onSave: (progress: Int, phase: String, status: String) -> Unit
) {
    var progressVal by remember { mutableFloatStateOf(project.milestoneProgress.toFloat()) }
    var selectedPhase by remember { mutableStateOf(project.currentPhase.ifBlank { "Backend & Database Development" }) }
    var selectedStatus by remember { mutableStateOf(project.status.ifBlank { "In Development" }) }

    val phases = listOf(
        "Requirement Analysis & SOW",
        "UI/UX Design & Wireframing",
        "Frontend & UI Development",
        "Backend & Database Architecture",
        "Integration & QA Testing",
        "Live Deployment & Handover",
        "Post-Launch Maintenance"
    )

    val statuses = listOf(
        "In Development",
        "Live & Active",
        "QA / Staging Review",
        "On Hold"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Project Control: ${project.name}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Client: $clientName", fontSize = 12.sp, color = TextSecondary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // Slider for milestone
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Milestone Completion:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("${progressVal.toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CtPrimaryBlue)
                }

                Slider(
                    value = progressVal,
                    onValueChange = { progressVal = it },
                    valueRange = 0f..100f,
                    steps = 19, // 5% increments
                    colors = SliderDefaults.colors(
                        thumbColor = CtPrimaryBlue,
                        activeTrackColor = CtPrimaryBlue
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Phase Selector
                Text("Current Phase:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                var showPhaseDropdown by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showPhaseDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedPhase, fontSize = 12.sp, maxLines = 1)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = showPhaseDropdown,
                        onDismissRequest = { showPhaseDropdown = false }
                    ) {
                        phases.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p, fontSize = 12.sp) },
                                onClick = {
                                    selectedPhase = p
                                    showPhaseDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status Selector
                Text("Project Status:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                var showStatusDropdown by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showStatusDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedStatus, fontSize = 12.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false }
                    ) {
                        statuses.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s, fontSize = 12.sp) },
                                onClick = {
                                    selectedStatus = s
                                    showStatusDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(progressVal.toInt(), selectedPhase, selectedStatus) },
                colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue)
            ) {
                Text("Save Live Update", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ─── Add New Project Dialog ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddNewProjectDialog(
    clients: List<CtUser>,
    onDismiss: () -> Unit,
    onConfirm: (Project) -> Unit
) {
    var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }
    var projectName by remember { mutableStateOf("") }
    var domain by remember { mutableStateOf("") }
    var hostingProvider by remember { mutableStateOf("DigitalOcean / Hostinger") }
    var serverIp by remember { mutableStateOf("") }
    var currentPhase by remember { mutableStateOf("UI/UX Design & Wireframing") }
    var milestone by remember { mutableFloatStateOf(10f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Client Project", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Select Client:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    var clientMenuExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { clientMenuExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                selectedClient?.let { it.companyName.ifBlank { it.displayName.ifBlank { it.email } } } ?: "Select Client",
                                fontSize = 12.sp
                            )
                        }
                        DropdownMenu(
                            expanded = clientMenuExpanded,
                            onDismissRequest = { clientMenuExpanded = false }
                        ) {
                            clients.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text("${c.displayName} (${c.companyName.ifBlank { c.email }})", fontSize = 12.sp) },
                                    onClick = {
                                        selectedClient = c
                                        clientMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = projectName,
                        onValueChange = { projectName = it },
                        label = { Text("Project Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = domain,
                        onValueChange = { domain = it },
                        label = { Text("Domain (e.g. clientapp.com)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = serverIp,
                        onValueChange = { serverIp = it },
                        label = { Text("Server IP Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = hostingProvider,
                        onValueChange = { hostingProvider = it },
                        label = { Text("Hosting Provider") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (projectName.isNotBlank() && selectedClient != null) {
                        val client = selectedClient!!
                        val newProj = Project(
                            projectId = "PROJ-" + System.currentTimeMillis().toString().takeLast(6),
                            clientId = client.email.ifBlank { client.uid },
                            name = projectName,
                            domain = domain,
                            hostingProvider = hostingProvider,
                            hostingServerIp = serverIp,
                            status = "In Development",
                            currentPhase = currentPhase,
                            milestoneProgress = milestone.toInt()
                        )
                        onConfirm(newProj)
                    }
                },
                enabled = projectName.isNotBlank() && selectedClient != null,
                colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue)
            ) {
                Text("Create Project")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
