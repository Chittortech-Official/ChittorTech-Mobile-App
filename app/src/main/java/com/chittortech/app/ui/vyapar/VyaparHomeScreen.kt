package com.chittortech.app.ui.vyapar

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.model.CtUser
import com.chittortech.app.model.Invoice
import com.chittortech.app.theme.*
import java.text.NumberFormat
import java.util.Locale

enum class HomeToggleTab {
    TRANSACTION_DETAILS,
    PARTY_DETAILS
}

@Composable
fun VyaparHomeScreen(
    invoices: List<Invoice>,
    clients: List<CtUser>,
    onAddNewSale: () -> Unit,
    onAddNewParty: () -> Unit,
    onSaleReportClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeToggle by remember { mutableStateOf(HomeToggleTab.TRANSACTION_DETAILS) }
    val indianFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VyaparBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Top Toggle Pills: [ Transaction Details ] | [ Party Details ] ──────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VyaparTogglePill(
                        text = "Transaction Details",
                        isSelected = activeToggle == HomeToggleTab.TRANSACTION_DETAILS,
                        onClick = { activeToggle = HomeToggleTab.TRANSACTION_DETAILS },
                        modifier = Modifier.weight(1f)
                    )
                    VyaparTogglePill(
                        text = "Party Details",
                        isSelected = activeToggle == HomeToggleTab.PARTY_DETAILS,
                        onClick = { activeToggle = HomeToggleTab.PARTY_DETAILS },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Quick Links Card ───────────────────────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                    border = BorderStroke(1.dp, VyaparCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Quick Links",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = VyaparDark
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        if (activeToggle == HomeToggleTab.TRANSACTION_DETAILS) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                QuickLinkItem(
                                    title = "60% OFF",
                                    icon = Icons.Default.ElectricBolt,
                                    badgeColor = Color(0xFFFF9800),
                                    iconBgColor = Color(0xFFFFEBEE)
                                )
                                QuickLinkItem(
                                    title = "Add Txn",
                                    icon = Icons.Outlined.AddBox,
                                    badgeColor = VyaparBlue,
                                    iconBgColor = Color(0xFFE3F2FD),
                                    onClick = onAddNewSale
                                )
                                QuickLinkItem(
                                    title = "Sale Report",
                                    icon = Icons.Outlined.Assessment,
                                    badgeColor = Color(0xFF0284C7),
                                    iconBgColor = Color(0xFFE0F2FE),
                                    onClick = onSaleReportClick
                                )
                                QuickLinkItem(
                                    title = "Show All",
                                    icon = Icons.Outlined.ArrowForwardIos,
                                    badgeColor = VyaparBlue,
                                    iconBgColor = Color(0xFFE2E8F0)
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                QuickLinkItem(
                                    title = "Import Party",
                                    icon = Icons.Outlined.ContactPhone,
                                    badgeColor = VyaparBlue,
                                    iconBgColor = Color(0xFFE3F2FD)
                                )
                                QuickLinkItem(
                                    title = "Party State...",
                                    icon = Icons.Outlined.ReceiptLong,
                                    badgeColor = Color(0xFF0284C7),
                                    iconBgColor = Color(0xFFE0F2FE)
                                )
                                QuickLinkItem(
                                    title = "Party Settings",
                                    icon = Icons.Outlined.Settings,
                                    badgeColor = VyaparBlue,
                                    iconBgColor = Color(0xFFE3F2FD)
                                )
                                QuickLinkItem(
                                    title = "Show All",
                                    icon = Icons.Outlined.ArrowForwardIos,
                                    badgeColor = VyaparBlue,
                                    iconBgColor = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }

            // ── Live List Content ──────────────────────────────────────────────────
            if (activeToggle == HomeToggleTab.TRANSACTION_DETAILS) {
                // Invoices / Sales List
                if (invoices.isEmpty()) {
                    // Demo card matching screenshot
                    item {
                        VyaparTransactionCard(
                            clientName = "Kush",
                            tag = "SALE",
                            invoiceNumber = "#1",
                            date = "30 Sept, 26",
                            total = "₹ 2,500.00",
                            balance = "₹ 100.00",
                            onShare = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "ChittorTech Invoice #1")
                                    putExtra(Intent.EXTRA_TEXT, "ChittorTech Invoice #1 for Kush: Total ₹ 2,500.00, Balance ₹ 100.00")
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Invoice"))
                            }
                        )
                    }
                } else {
                    items(invoices) { inv ->
                        VyaparTransactionCard(
                            clientName = inv.title.ifBlank { "Client Deployment" },
                            tag = if (inv.status == "PAID") "PAID" else "SALE",
                            invoiceNumber = "#${inv.invoiceId.takeLast(4).uppercase()}",
                            date = inv.dueDate.ifBlank { "Active" },
                            total = indianFormat.format(inv.amount),
                            balance = if (inv.status == "PAID") "₹ 0.00" else indianFormat.format(inv.amount),
                            onShare = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "ChittorTech Invoice")
                                    putExtra(Intent.EXTRA_TEXT, "ChittorTech Invoice: ${inv.title}, Amount: ${indianFormat.format(inv.amount)}")
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Invoice"))
                            }
                        )
                    }
                }
            } else {
                // Parties / Clients List
                if (clients.isEmpty()) {
                    // Demo card matching screenshot
                    item {
                        VyaparPartyCard(
                            clientName = "Kush",
                            date = "30 Sept, 26",
                            amount = "₹ 100",
                            status = "You'll Get"
                        )
                    }
                } else {
                    items(clients) { client ->
                        VyaparPartyCard(
                            clientName = client.companyName.ifBlank { client.displayName.ifBlank { "Corporate Client" } },
                            date = "Live Client",
                            amount = "₹ 2,500",
                            status = "You'll Get"
                        )
                    }
                }
            }
        }

        // ── Floating Action Red Pill Button (Exact copy of Vyapar) ─────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Button(
                onClick = {
                    if (activeToggle == HomeToggleTab.TRANSACTION_DETAILS) onAddNewSale()
                    else onAddNewParty()
                },
                colors = ButtonDefaults.buttonColors(containerColor = VyaparRed),
                shape = RoundedCornerShape(26.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 13.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(
                    imageVector = if (activeToggle == HomeToggleTab.TRANSACTION_DETAILS) Icons.Outlined.CurrencyRupee else Icons.Outlined.PersonAdd,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (activeToggle == HomeToggleTab.TRANSACTION_DETAILS) "Add New Sale" else "Add New Party",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ── Top Toggle Pill ──────────────────────────────────────────────────────────
@Composable
private fun VyaparTogglePill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFFFFECEF) else VyaparWhite,
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isSelected) VyaparRed else Color(0xFFCBD5E1)
        ),
        modifier = modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) VyaparRed else Color(0xFF64748B)
            )
        }
    }
}

// ── Quick Link Icon Item ─────────────────────────────────────────────────────
@Composable
private fun QuickLinkItem(
    title: String,
    icon: ImageVector,
    badgeColor: Color,
    iconBgColor: Color,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = badgeColor,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = VyaparDark
        )
    }
}

// ── Transaction Card (Exact copy of WA0028) ──────────────────────────────────
@Composable
fun VyaparTransactionCard(
    clientName: String,
    tag: String,
    invoiceNumber: String,
    date: String,
    total: String,
    balance: String,
    onPrint: () -> Unit = {},
    onShare: () -> Unit = {},
    onMore: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = VyaparWhite),
        border = BorderStroke(1.dp, VyaparCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Client Name + #Number & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = clientName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = VyaparDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VyaparGreenLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VyaparGreen
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = invoiceNumber,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = date,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Totals and Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Column {
                        Text("Total", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(total, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
                    }
                    Column {
                        Text("Balance", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(balance, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
                    }
                }

                // Action Icons: Print, Share, More
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onPrint, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Print,
                            contentDescription = "Print",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onMore, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Party Card (Exact copy of WA0029) ────────────────────────────────────────
@Composable
fun VyaparPartyCard(
    clientName: String,
    date: String,
    amount: String,
    status: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = VyaparWhite),
        border = BorderStroke(1.dp, VyaparCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = clientName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyaparDark
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyaparGreen
                )
                Text(
                    text = status,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
