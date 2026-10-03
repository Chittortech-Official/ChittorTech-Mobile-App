package com.chittortech.app.ui.vyapar

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.R
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

    var growBusinessExpanded by remember { mutableStateOf(true) }
    var helpSupportExpanded by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VyaparBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── 1. Company Profile Banner ─────────────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                border = BorderStroke(1.dp, VyaparCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.5.dp, Color(0xFF0284C7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.chittortech_logo),
                            contentDescription = "ChittorTech Logo",
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ChittorTech",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = VyaparDark
                        )
                        Text(
                            text = "Collectorate Circle, Chittorgarh, Rajasthan",
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "User: Guest • chittortech.in",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VyaparBlue
                        )
                    }
                }
            }
        }

        // ── 2. GROW YOUR BUSINESS (Services Catalog) ──────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                border = BorderStroke(1.dp, VyaparCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    MenuSectionHeader(
                        title = "Grow Your Business",
                        icon = Icons.Outlined.TrendingUp,
                        iconColor = Color(0xFF16A34A),
                        isExpanded = growBusinessExpanded,
                        onToggle = { growBusinessExpanded = !growBusinessExpanded }
                    )

                    AnimatedVisibility(visible = growBusinessExpanded) {
                        Column {
                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            MenuItemRow(
                                title = "Enterprise AI & RAG Solutions",
                                subtitle = "Sub-500ms Groq LPUs, vector databases & chatbots",
                                icon = Icons.Outlined.SmartToy,
                                iconColor = Color(0xFF0284C7),
                                onClick = onExpensesClick
                            )
                            MenuItemRow(
                                title = "Google Play 12-Tester Publishing",
                                subtitle = "100% production approval guarantee with real testers",
                                icon = Icons.Outlined.RocketLaunch,
                                iconColor = Color(0xFFD97706),
                                onClick = onExpensesClick
                            )
                            MenuItemRow(
                                title = "Mobile App & Web Development",
                                subtitle = "Native Kotlin/iOS & Next.js SaaS platforms",
                                icon = Icons.Outlined.PhoneAndroid,
                                iconColor = Color(0xFF16A34A),
                                onClick = onExpensesClick
                            )
                            MenuItemRow(
                                title = "Custom ERP & CRM Systems",
                                subtitle = "Smart billing, multi-store inventory & lead pipelines",
                                icon = Icons.Outlined.ReceiptLong,
                                iconColor = Color(0xFF7C3AED),
                                onClick = onExpensesClick
                            )
                            MenuItemRow(
                                title = "Government & Startup Compliance",
                                subtitle = "DPIIT Startup India, iStart Rajasthan & DGFT IEC",
                                icon = Icons.Outlined.Verified,
                                iconColor = Color(0xFF0F766E),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in/trust-center"))
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }

        // ── 3. HELP & SUPPORT ─────────────────────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                border = BorderStroke(1.dp, VyaparCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    MenuSectionHeader(
                        title = "Help & Support",
                        icon = Icons.Outlined.SupportAgent,
                        iconColor = VyaparBlue,
                        isExpanded = helpSupportExpanded,
                        onToggle = { helpSupportExpanded = !helpSupportExpanded }
                    )

                    AnimatedVisibility(visible = helpSupportExpanded) {
                        Column {
                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            MenuItemRow(
                                title = "WhatsApp Direct Chat",
                                subtitle = "+91 7597451057 (Instant Founder Support)",
                                icon = Icons.AutoMirrored.Filled.Send,
                                iconColor = Color(0xFF25D366),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/917597451057?text=Hello%20ChittorTech%20Support%2C%20I%20need%20assistance."))
                                    context.startActivity(intent)
                                }
                            )
                            MenuItemRow(
                                title = "Direct Call Support",
                                subtitle = "+91 75974 51057",
                                icon = Icons.Outlined.Call,
                                iconColor = Color(0xFF0284C7),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+917597451057"))
                                    context.startActivity(intent)
                                }
                            )
                            MenuItemRow(
                                title = "Email Inquiries",
                                subtitle = "business@chittortech.in",
                                icon = Icons.Outlined.Email,
                                iconColor = Color(0xFFEA580C),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:business@chittortech.in"))
                                    context.startActivity(intent)
                                }
                            )
                            MenuItemRow(
                                title = "Official Website",
                                subtitle = "https://chittortech.in",
                                icon = Icons.Outlined.Language,
                                iconColor = Color(0xFF475569),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in"))
                                    context.startActivity(intent)
                                }
                            )
                            MenuItemRow(
                                title = "Privacy Policy & Terms",
                                subtitle = "Data safety & compliance documentation",
                                icon = Icons.Outlined.Security,
                                iconColor = Color(0xFF64748B),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in/privacy-policy"))
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }

        // ── 4. RATE THIS APP ──────────────────────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                border = BorderStroke(1.dp, VyaparCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        Toast.makeText(context, "Coming Soon! Google Play Store reviews will be enabled upon public launch.", Toast.LENGTH_LONG).show()
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.StarRate, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Rate This App", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFEF3C7),
                                border = BorderStroke(1.dp, Color(0xFFFDE68A))
                            ) {
                                Text("COMING SOON", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("Google Play Store reviews will open with public release", fontSize = 11.5.sp, color = Color(0xFF64748B))
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                }
            }
        }

        // ── 6. SIGN OUT / SWITCH PORTAL ───────────────────────────────────────
        item {
            OutlinedButton(
                onClick = onSignOut,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFDC2626)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out / Switch Portal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

// ── Menu Section Header ───────────────────────────────────────────────────────
@Composable
private fun MenuSectionHeader(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
        }
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}

// ── Menu Item Row ─────────────────────────────────────────────────────────────
@Composable
private fun MenuItemRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = VyaparDark)
            Text(text = subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(13.dp)
        )
    }
}
