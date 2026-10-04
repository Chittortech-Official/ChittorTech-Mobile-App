package com.chittortech.app.ui.screens.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.model.CtUser
import com.chittortech.app.model.SupportTicket
import com.chittortech.app.theme.*
import com.chittortech.app.ui.components.*
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTicketResolverScreen(
    tickets: List<SupportTicket>,
    clients: List<CtUser> = emptyList(),
    onUpdateTicket: (ticketId: String, status: String, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterStatus by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("ALL", "OPEN", "IN_PROGRESS", "RESOLVED")

    val filtered = remember(tickets, filterStatus, searchQuery) {
        tickets.filter { tkt ->
            val matchesFilter = if (filterStatus == "ALL") true
            else tkt.status.equals(filterStatus, ignoreCase = true)

            val matchesSearch = searchQuery.isBlank() ||
                tkt.title.contains(searchQuery, ignoreCase = true) ||
                tkt.ticketId.contains(searchQuery, ignoreCase = true) ||
                tkt.clientName.contains(searchQuery, ignoreCase = true) ||
                tkt.companyName.contains(searchQuery, ignoreCase = true) ||
                tkt.category.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesSearch
        }
    }

    val openCount = tickets.count { it.status.equals("OPEN", ignoreCase = true) }
    val inProgressCount = tickets.count { it.status.equals("IN_PROGRESS", ignoreCase = true) }
    val resolvedCount = tickets.count { it.status.equals("RESOLVED", ignoreCase = true) }

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
                        Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = CtAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "TICKET CENTER & SLA",
                        fontSize = 11.sp,
                        color = CtAmber,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Support Ticket Center", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Real-time client tickets & SLA resolution", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))

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
                        Text("Total: ${tickets.size}", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CtRed.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Open: $openCount", fontSize = 11.5.sp, color = CtRed, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CtAmber.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Prog: $inProgressCount", fontSize = 11.5.sp, color = CtAmber, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CtGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Done: $resolvedCount", fontSize = 11.5.sp, color = CtGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ── Search & Filter Row ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by ticket title, client, or #ID...", fontSize = 13.sp) },
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

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { f ->
                    val isSelected = filterStatus == f
                    FilterChip(
                        selected = isSelected,
                        onClick = { filterStatus = f },
                        label = { Text(f.replace("_", " "), fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CtPrimaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = CtCardWhite,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Done,
                message = if (searchQuery.isNotBlank()) "No tickets match \"$searchQuery\"" else "No tickets in this category."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.ticketId.ifBlank { it.hashCode().toString() } }) { ticket ->
                    val client = clients.find {
                        it.uid == ticket.clientId || it.email.equals(ticket.clientId, ignoreCase = true)
                    }
                    AdminTicketCard(
                        ticket = ticket,
                        client = client,
                        onUpdate = onUpdateTicket
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun AdminTicketCard(
    ticket: SupportTicket,
    client: CtUser?,
    onUpdate: (ticketId: String, status: String, note: String) -> Unit
) {
    val context = LocalContext.current
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = ticket.ticketId.ifBlank { "#TKT" },
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = ticket.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = ticket.companyName.ifBlank { ticket.clientName.ifBlank { client?.companyName ?: "Client" } },
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    if (ticket.category.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(ticket.category, fontSize = 11.sp, color = TextMuted)
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    StatusBadge(status = ticket.status)
                    Spacer(modifier = Modifier.height(6.dp))
                    PriorityBadge(priority = ticket.priority)
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = CtBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Issue Description", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CtBackground)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = ticket.description.ifBlank { "No description provided." },
                            fontSize = 12.5.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }

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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Client Direct Actions (Call, WhatsApp)
                    val phone = client?.phone?.replace(Regex("[^0-9]"), "") ?: ""
                    if (phone.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call Client", fontSize = 11.5.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    try {
                                        val finalNum = if (phone.length == 10) "91$phone" else phone
                                        val msg = "Hello, this is ChittorTech Support regarding your ticket #${ticket.ticketId}: \"${ticket.title}\"."
                                        val url = "https://wa.me/$finalNum?text=${URLEncoder.encode(msg, "UTF-8")}"
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    } catch (_: Exception) {
                                        Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF16A34A))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", fontSize = 11.5.sp, color = Color(0xFF16A34A))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Status Action Buttons
                    if (ticket.status != "RESOLVED") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (ticket.status == "OPEN") {
                                OutlinedButton(
                                    onClick = { onUpdate(ticket.ticketId, "IN_PROGRESS", ticket.resolutionNote) },
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, CtAmber),
                                    contentPadding = PaddingValues(horizontal = 6.dp)
                                ) {
                                    Text("In Progress", fontSize = 11.5.sp, color = CtAmber, fontWeight = FontWeight.Bold)
                                }
                            }
                            Button(
                                onClick = { showResolveDialog = true },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CtGreen),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Resolve Ticket", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showResolveDialog) {
        var note by remember { mutableStateOf(ticket.resolutionNote) }
        AlertDialog(
            onDismissRequest = { showResolveDialog = false },
            title = { Text("Resolve Ticket #${ticket.ticketId}") },
            text = {
                Column {
                    Text("Add resolution details for the client:", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Resolution Note") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdate(ticket.ticketId, "RESOLVED", note)
                        showResolveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CtGreen)
                ) {
                    Text("Confirm Resolved")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResolveDialog = false }) { Text("Cancel") }
            }
        )
    }
}
