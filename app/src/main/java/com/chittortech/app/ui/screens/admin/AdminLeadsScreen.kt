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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.chittortech.app.model.LeadInquiry
import com.chittortech.app.theme.*
import com.chittortech.app.ui.components.EmptyState
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLeadsScreen(
    leads: List<LeadInquiry>,
    onUpdateLeadStatus: (leadId: String, status: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("ALL", "NEW", "CONTACTED", "PROPOSAL", "CONVERTED")

    // Filter incoming leads (Web & App only, outbound excluded by repository)
    val filteredLeads = remember(leads, selectedFilter, searchQuery) {
        leads.filter { lead ->
            val matchesFilter = when (selectedFilter) {
                "ALL" -> true
                "NEW" -> lead.status.equals("new", ignoreCase = true)
                "CONTACTED" -> lead.status.equals("contacted", ignoreCase = true)
                "PROPOSAL" -> lead.status.equals("proposal", ignoreCase = true) || lead.status.equals("proposal_sent", ignoreCase = true)
                "CONVERTED" -> lead.status.equals("converted", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                lead.name.contains(searchQuery, ignoreCase = true) ||
                lead.company.contains(searchQuery, ignoreCase = true) ||
                lead.email.contains(searchQuery, ignoreCase = true) ||
                lead.contact.contains(searchQuery, ignoreCase = true) ||
                lead.service.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesSearch
        }
    }

    val newCount = leads.count { it.status.equals("new", ignoreCase = true) }
    val contactedCount = leads.count { it.status.equals("contacted", ignoreCase = true) }
    val convertedCount = leads.count { it.status.equals("converted", ignoreCase = true) }

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
                        Icons.Default.ContactMail,
                        contentDescription = null,
                        tint = CtAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "INCOMING LEADS PIPELINE",
                        fontSize = 11.sp,
                        color = CtAmber,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Website & App Inquiries",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Direct client enquiries from online portals",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LeadMetricBadge(title = "Total", count = leads.size, color = Color.White, bgColor = Color.White.copy(alpha = 0.15f))
                    LeadMetricBadge(title = "New", count = newCount, color = CtAmber, bgColor = CtAmber.copy(alpha = 0.2f))
                    LeadMetricBadge(title = "Contacted", count = contactedCount, color = CtPrimaryBlue, bgColor = CtPrimaryBlue.copy(alpha = 0.2f))
                    LeadMetricBadge(title = "Won", count = convertedCount, color = CtGreen, bgColor = CtGreen.copy(alpha = 0.2f))
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
                placeholder = { Text("Search by name, company, or service...", fontSize = 13.sp) },
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

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { f ->
                    val isSelected = selectedFilter == f
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = f },
                        label = { Text(f, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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

        // ── Leads List ────────────────────────────────────────────────────────
        if (filteredLeads.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Inbox,
                message = if (searchQuery.isNotBlank()) "No leads match \"$searchQuery\"" else "No incoming leads in this category."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredLeads, key = { it.leadId.ifBlank { it.hashCode().toString() } }) { lead ->
                    LeadCard(
                        lead = lead,
                        onUpdateStatus = { newStatus ->
                            onUpdateLeadStatus(lead.leadId, newStatus)
                        },
                        context = context
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun RowScope.LeadMetricBadge(
    title: String,
    count: Int,
    color: Color,
    bgColor: Color
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$title: ",
                fontSize = 11.sp,
                color = color.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$count",
                fontSize = 11.5.sp,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeadCard(
    lead: LeadInquiry,
    onUpdateStatus: (String) -> Unit,
    context: Context
) {
    var expanded by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }

    val formattedDate = remember(lead.createdAt) {
        lead.createdAt?.toDate()?.let {
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(it)
        } ?: "Recent"
    }

    val statusColor = when (lead.status.lowercase()) {
        "new" -> CtAmber
        "contacted" -> CtPrimaryBlue
        "proposal", "proposal_sent" -> Color(0xFF8B5CF6)
        "converted" -> CtGreen
        "lost" -> CtRed
        else -> TextMuted
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Source & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Source Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CtPrimaryBlue.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val isWeb = lead.source.contains("Web", ignoreCase = true)
                    Icon(
                        if (isWeb) Icons.Default.Language else Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = CtPrimaryBlue,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        lead.source.ifBlank { "Website Incoming" },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CtPrimaryBlue
                    )
                }

                // Status Badge with Dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.12f))
                            .clickable { showStatusMenu = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            lead.status.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Change Status",
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        listOf("new", "contacted", "proposal", "converted", "lost").forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                                onClick = {
                                    onUpdateStatus(s)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lead Name & Company
            Text(
                text = lead.name.ifBlank { "Anonymous Prospect" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            if (lead.company.isNotBlank()) {
                Text(
                    text = lead.company,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Interested Service
            if (lead.service.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Interested In: ", fontSize = 12.sp, color = TextMuted)
                    Text(
                        lead.service,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CtPrimaryBlue
                    )
                }
            }

            // Message excerpt
            if (lead.message.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CtBackground)
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = if (expanded || lead.message.length <= 120) lead.message else "${lead.message.take(120)}...",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                        if (lead.message.length > 120) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (expanded) "Show Less" else "Read More",
                                fontSize = 11.sp,
                                color = CtPrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { expanded = !expanded }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = TextMuted
                )

                // Quick Action Buttons (Call, WhatsApp, Email)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Call Button
                    if (lead.contact.isNotBlank()) {
                        FilledTonalIconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${lead.contact.trim()}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = CtGreenLight,
                                contentColor = CtGreen
                            )
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call Lead", modifier = Modifier.size(18.dp))
                        }

                        // WhatsApp Button
                        FilledTonalIconButton(
                            onClick = {
                                try {
                                    val cleanNum = lead.contact.replace(Regex("[^0-9]"), "")
                                    val finalNum = if (cleanNum.length == 10) "91$cleanNum" else cleanNum
                                    val greeting = "Hello ${lead.name.trim()}, thank you for connecting with ChittorTech regarding ${lead.service.ifBlank { "your IT project requirement" }}. We are glad to assist you!"
                                    val url = "https://wa.me/$finalNum?text=${URLEncoder.encode(greeting, "UTF-8")}"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not launch WhatsApp", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFFDCFCE7),
                                contentColor = Color(0xFF16A34A)
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "WhatsApp Lead", modifier = Modifier.size(18.dp))
                        }
                    }

                    // Email Button
                    if (lead.email.isNotBlank()) {
                        FilledTonalIconButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${lead.email}")
                                        putExtra(Intent.EXTRA_SUBJECT, "ChittorTech - Regarding your enquiry for ${lead.service}")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = CtAmberLight,
                                contentColor = CtAmber
                            )
                        ) {
                            Icon(Icons.Default.Email, contentDescription = "Email Lead", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
