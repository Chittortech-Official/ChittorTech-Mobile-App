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
import com.chittortech.app.model.SupportTicket
import com.chittortech.app.theme.*
import com.chittortech.app.ui.components.*

@Composable
fun AdminTicketResolverScreen(
    tickets: List<SupportTicket>,
    onUpdateTicket: (ticketId: String, status: String, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterStatus by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "OPEN", "IN_PROGRESS", "RESOLVED")
    val filtered = if (filterStatus == "ALL") tickets
    else tickets.filter { it.status.equals(filterStatus, ignoreCase = true) }

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
                Text("Central Ticket Resolver", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("All client support requests", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }

        // Filter Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { f ->
                FilterChip(
                    selected = filterStatus == f,
                    onClick = { filterStatus = f },
                    label = { Text(f.replace("_", " "), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CtPrimaryBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        if (filtered.isEmpty()) {
            EmptyState(icon = Icons.Default.Done, message = "No tickets in this category.")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { ticket ->
                    AdminTicketCard(ticket = ticket, onUpdate = onUpdateTicket)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun AdminTicketCard(
    ticket: SupportTicket,
    onUpdate: (ticketId: String, status: String, note: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showResolveDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(ticket.ticketId.ifBlank { "#" }, fontSize = 10.sp, color = TextMuted)
                    Text(ticket.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(ticket.companyName.ifBlank { ticket.clientName }, fontSize = 12.sp, color = TextSecondary)
                    }
                    Text(ticket.category, fontSize = 11.sp, color = TextMuted)
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
                    Text("Issue Description", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(ticket.description.ifBlank { "No description provided." }, fontSize = 13.sp, color = TextPrimary)

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

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons
                    if (ticket.status != "RESOLVED") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (ticket.status == "OPEN") {
                                OutlinedButton(
                                    onClick = { onUpdate(ticket.ticketId, "IN_PROGRESS", ticket.resolutionNote) },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, CtAmber)
                                ) {
                                    Text("Mark In Progress", fontSize = 12.sp, color = CtAmber)
                                }
                            }
                            Button(
                                onClick = { showResolveDialog = true },
                                modifier = Modifier.weight(1f).height(38.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CtGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Resolve", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showResolveDialog) {
        ResolveTicketDialog(
            onDismiss = { showResolveDialog = false },
            onConfirm = { note ->
                onUpdate(ticket.ticketId, "RESOLVED", note)
                showResolveDialog = false
                expanded = false
            }
        )
    }
}

@Composable
private fun ResolveTicketDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var note by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CtGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Resolve Ticket", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text("Add a resolution note for the client:", fontSize = 13.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Resolution Note") },
                    placeholder = { Text("Describe the fix/solution...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (note.isNotBlank()) onConfirm(note) },
                colors = ButtonDefaults.buttonColors(containerColor = CtGreen),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Resolve & Notify") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
