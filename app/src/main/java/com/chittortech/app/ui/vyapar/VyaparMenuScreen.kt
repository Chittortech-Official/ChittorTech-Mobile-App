package com.chittortech.app.ui.vyapar

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.theme.*

@Composable
fun VyaparMenuScreen(
    onSaleClick: () -> Unit = {},
    onPurchaseClick: () -> Unit = {},
    onExpensesClick: () -> Unit = {},
    onReportsClick: () -> Unit = {},
    onHelpdeskClick: () -> Unit = {},
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 2 })

    var saleExpanded by remember { mutableStateOf(false) }
    var purchaseExpanded by remember { mutableStateOf(false) }
    var growBusinessExpanded by remember { mutableStateOf(false) }
    var helpSupportExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VyaparBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── 1. Hero Promo Carousel (WA0032 & WA0024) ──────────────────────────────
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) { page ->
                    if (page == 0) {
                        // Slide 1: Deal Sale Maroon Card (WA0032)
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(145.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF881337), Color(0xFFBE123C))
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "DEAL SALE",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFBBF7D0))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "HURRY UP!",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF166534)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Now, become an Enterprise Client and get exclusive benefits at upto 60% off!",
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in"))
                                            context.startActivity(intent)
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.White,
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Buy Now",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF881337)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Slide 2: Join 10,000+ Businesses Yellow Card (WA0024)
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(145.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFFEF9C3))
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Join 10,000+ Businesses Already Using Our Enterprise App",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 18.sp,
                                            color = VyaparDark
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in"))
                                                context.startActivity(intent)
                                            },
                                            shape = RoundedCornerShape(20.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = VyaparRed),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("Try for Free", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.Computer,
                                        contentDescription = null,
                                        tint = VyaparBlue,
                                        modifier = Modifier.size(60.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Pager Dots Indicator
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(2) { idx ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == idx) 14.dp else 6.dp, 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (pagerState.currentPage == idx) VyaparBlue else Color(0xFFCBD5E1))
                        )
                    }
                }
            }
        }

        // ── 2. My Business Section (WA0024) ───────────────────────────────────────
        item {
            Column {
                Text(
                    text = "My Business",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyaparDark,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                    border = BorderStroke(1.dp, VyaparCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Sale Item (Expandable)
                        VyaparMenuRow(
                            icon = Icons.Outlined.CurrencyRupee,
                            title = "Sale",
                            isExpandable = true,
                            isExpanded = saleExpanded,
                            onClick = {
                                saleExpanded = !saleExpanded
                                onSaleClick()
                            }
                        )
                        AnimatedVisibility(visible = saleExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(VyaparBlueSoft)
                                    .padding(start = 48.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("• Sale Invoices (Billing)", fontSize = 13.sp, color = VyaparDark, modifier = Modifier.clickable { onSaleClick() })
                                Text("• SOW Estimates & Quotations", fontSize = 13.sp, color = VyaparDark)
                                Text("• Payment Receipts", fontSize = 13.sp, color = VyaparDark)
                            }
                        }
                        HorizontalDivider(color = VyaparDivider)

                        // Purchase Item (Expandable)
                        VyaparMenuRow(
                            icon = Icons.Outlined.ShoppingCart,
                            title = "Purchase",
                            isExpandable = true,
                            isExpanded = purchaseExpanded,
                            onClick = {
                                purchaseExpanded = !purchaseExpanded
                                onPurchaseClick()
                            }
                        )
                        AnimatedVisibility(visible = purchaseExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(VyaparBlueSoft)
                                    .padding(start = 48.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("• Vendor Cloud Invoices", fontSize = 13.sp, color = VyaparDark)
                                Text("• Domain & SSL Renewals", fontSize = 13.sp, color = VyaparDark)
                            }
                        }
                        HorizontalDivider(color = VyaparDivider)

                        // Expenses Item
                        VyaparMenuRow(
                            icon = Icons.Outlined.AccountBalanceWallet,
                            title = "Expenses",
                            isExpandable = false,
                            onClick = onExpensesClick
                        )
                        HorizontalDivider(color = VyaparDivider)

                        // My Online Store Item
                        VyaparMenuRow(
                            icon = Icons.Outlined.Storefront,
                            title = "My Online Store",
                            isExpandable = false,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in"))
                                context.startActivity(intent)
                            }
                        )
                        HorizontalDivider(color = VyaparDivider)

                        // Reports Item
                        VyaparMenuRow(
                            icon = Icons.Outlined.Assignment,
                            title = "Reports",
                            isExpandable = false,
                            onClick = onReportsClick
                        )
                    }
                }
            }
        }

        // ── 3. Cash & Bank Section (WA0024) ───────────────────────────────────────
        item {
            Column {
                Text(
                    text = "Cash & Bank",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyaparDark,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                    border = BorderStroke(1.dp, VyaparCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        VyaparMenuRow(
                            icon = Icons.Outlined.AccountBalance,
                            title = "Bank Accounts",
                            isExpandable = false,
                            onClick = {}
                        )
                        HorizontalDivider(color = VyaparDivider)
                        VyaparMenuRow(
                            icon = Icons.Outlined.Money,
                            title = "Cash In-Hand",
                            isExpandable = false,
                            onClick = {}
                        )
                    }
                }
            }
        }

        // ── 4. Account & Support Section (WA0033) ─────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                border = BorderStroke(1.dp, VyaparCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Grow Your Business (WhatsApp)
                    VyaparMenuRow(
                        icon = Icons.Outlined.Chat,
                        title = "Grow Your Business",
                        isExpandable = true,
                        isExpanded = growBusinessExpanded,
                        onClick = {
                            growBusinessExpanded = !growBusinessExpanded
                            val uri = Uri.parse("https://api.whatsapp.com/send?phone=917685535660&text=Hi%20ChittorTech%20team,%20I%20want%20to%20grow%20my%20business%20with%20your%20services")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    )
                    HorizontalDivider(color = VyaparDivider)

                    // Settings
                    VyaparMenuRow(
                        icon = Icons.Outlined.Settings,
                        title = "Settings",
                        isExpandable = false,
                        onClick = {}
                    )
                    HorizontalDivider(color = VyaparDivider)

                    // Help & Support
                    VyaparMenuRow(
                        icon = Icons.Outlined.HeadsetMic,
                        title = "Help & Support",
                        isExpandable = true,
                        isExpanded = helpSupportExpanded,
                        onClick = {
                            helpSupportExpanded = !helpSupportExpanded
                            onHelpdeskClick()
                        }
                    )
                    HorizontalDivider(color = VyaparDivider)

                    // Rate this app
                    VyaparMenuRow(
                        icon = Icons.Outlined.Grade,
                        title = "Rate this app",
                        isExpandable = false,
                        onClick = {}
                    )
                }
            }
        }

        // ── 5. App Version Card (WA0033) ──────────────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                border = BorderStroke(1.dp, VyaparCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("App Version", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = VyaparDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("1.0.0 (Production)", fontSize = 13.sp, color = Color(0xFF64748B))
                }
            }
        }

        // ── 6. Privacy Policy Link & Logout ───────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Privacy Policy",
                    fontSize = 14.sp,
                    color = VyaparBlue,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in/privacy"))
                        context.startActivity(intent)
                    }
                )

                Text(
                    text = "Logout",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyaparRed,
                    modifier = Modifier.clickable { onSignOut() }
                )
            }
        }

        // ── 7. Branding Footer (WA0033) ───────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.chittortech.app.R.drawable.chittortech_logo),
                    contentDescription = "ChittorTech Logo",
                    modifier = Modifier.size(52.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ChittorTech",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Crafted by ChittorTech Enterprise Solutions Pvt Ltd.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
private fun VyaparMenuRow(
    icon: ImageVector,
    title: String,
    isExpandable: Boolean,
    isExpanded: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF475569),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = VyaparDark
            )
        }

        Icon(
            imageVector = if (isExpandable) {
                if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown
            } else {
                Icons.Outlined.ChevronRight
            },
            contentDescription = null,
            tint = VyaparBlue,
            modifier = Modifier.size(20.dp)
        )
    }
}
