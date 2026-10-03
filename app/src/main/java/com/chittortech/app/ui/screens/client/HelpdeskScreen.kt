package com.chittortech.app.ui.screens.client

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.model.SupportTicket
import com.chittortech.app.theme.*
import com.chittortech.app.ui.components.*

@Composable
fun HelpdeskScreen(
    clientId: String,
    projectId: String,
    clientName: String,
    companyName: String,
    tickets: List<SupportTicket>,
    onCreateTicket: (SupportTicket) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRaiseDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
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
                    Text("Helpdesk", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Track & manage your support requests", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }

            // Status summary
            val open     = tickets.count { it.status == "OPEN" }
            val progress = tickets.count { it.status == "IN_PROGRESS" }
            val resolved = tickets.count { it.status == "RESOLVED" }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TicketStatChip("Open", open, CtRed, Modifier.weight(1f))
                TicketStatChip("In Progress", progress, CtAmber, Modifier.weight(1f))
                TicketStatChip("Resolved", resolved, CtGreen, Modifier.weight(1f))
            }

            if (tickets.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.SupportAgent,
                    message = "No tickets yet. Raise one below!"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tickets) { ticket ->
                        TicketDetailCard(ticket = ticket)
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // FAB
        ExtendedFloatingActionButton(
            onClick = { showRaiseDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .navigationBarsPadding(),
            containerColor = CtPrimaryBlue,
            contentColor = Color.White,
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Raise Ticket", fontWeight = FontWeight.SemiBold) }
        )
    }

    // Raise Ticket Dialog
    if (showRaiseDialog) {
        RaiseTicketDialog(
            onDismiss = { showRaiseDialog = false },
            onSubmit = { ticket ->
                onCreateTicket(
                    SupportTicket(
                        clientId = clientId,
                        projectId = projectId,
                        clientName = clientName,
                        companyName = companyName,
                        title = ticket.title,
                        category = ticket.category,
                        priority = ticket.priority,
                        description = ticket.description
                    )
                )
                showRaiseDialog = false
            }
        )
    }
}

// ─── Ticket Detail Card ────────────────────────────────────────────────────────

@Composable
private fun TicketDetailCard(ticket: SupportTicket) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
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
                    Text(ticket.ticketId.ifBlank { "#" }, fontSize = 10.sp, color = TextMuted)
                    Text(ticket.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(ticket.category, fontSize = 12.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    StatusBadge(status = ticket.status)
                    Spacer(modifier = Modifier.height(4.dp))
                    PriorityBadge(priority = ticket.priority)
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = CtBorder)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Description", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(ticket.description.ifBlank { "—" }, fontSize = 13.sp, color = TextPrimary)
                    if (ticket.resolutionNote.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = CtGreenLight)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Resolution Note", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CtGreen)
                                Text(ticket.resolutionNote, fontSize = 12.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Stat Chip ────────────────────────────────────────────────────────────────

@Composable
private fun TicketStatChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

// ─── Raise Ticket Dialog ──────────────────────────────────────────────────────

data class NewTicketForm(
    val title: String = "",
    val category: String = "",
    val priority: String = "MEDIUM",
    val description: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseTicketDialog(
    onDismiss: () -> Unit,
    onSubmit: (NewTicketForm) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Bug") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var description by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Bug", "New Feature Request", "Server Downtime", "Email Deliverability", "SEO / GMB", "Other")
    val priorities = listOf("LOW", "MEDIUM", "HIGH", "URGENT")

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BugReport, contentDescription = null, tint = CtPrimaryBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Raise New Ticket", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Issue Title") },
                    placeholder = { Text("Brief summary of your issue") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Issue Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = { category = cat; categoryExpanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Priority
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = it }
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        priorities.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = { priority = p; priorityExpanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detailed Description") },
                    placeholder = { Text("Describe the issue in detail...") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        onSubmit(NewTicketForm(title, category, priority, description))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CtPrimaryBlue),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Submit Ticket") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
