package com.chittortech.app.ui.vyapar

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.R
import com.chittortech.app.model.CtUser
import com.chittortech.app.model.Invoice
import com.chittortech.app.theme.*

@Composable
fun VyaparHomeScreen(
    invoices: List<Invoice> = emptyList(),
    clients: List<CtUser> = emptyList(),
    onAddNewSale: () -> Unit = {},
    onAddNewParty: () -> Unit = {},
    onSaleReportClick: () -> Unit = {},
    onExploreServices: () -> Unit = {},
    onOpenAiChat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VyaparBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── 1. Hero Agency Profile Card ──────────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0F172A), // Slate 900
                                        Color(0xFF0369A1), // Ocean Dark
                                        Color(0xFF0284C7)  // Ocean Blue
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "OFFICIAL AGENCY APP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF38BDF8),
                                        letterSpacing = 0.8.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.chittortech_logo),
                                        contentDescription = "ChittorTech Logo",
                                        modifier = Modifier.padding(4.dp).fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Engineering Tomorrow’s Software & AI",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 27.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Enterprise AI Agents, Sub-500ms RAG, Native Apps & Guaranteed Google Play 12-Tester Launches.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // 2 Action Buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = onOpenAiChat,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ask ChittorTech GPT", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                }

                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/917597451057?text=Hello%20Kush%20and%20Lav%2C%20I%20want%20to%20consult%20for%20a%20project."))
                                        context.startActivity(intent)
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Talk to Founders", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // ── 2. Verified Metrics Bar ──────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(title = "250+", subtitle = "Delivered Projects", color = Color(0xFF0284C7), modifier = Modifier.weight(1f))
                    MetricCard(title = "50+", subtitle = "Play Store Apps", color = Color(0xFF16A34A), modifier = Modifier.weight(1f))
                    MetricCard(title = "99.8%", subtitle = "Client Satisfaction", color = Color(0xFFD97706), modifier = Modifier.weight(1f))
                    MetricCard(title = "4.8★", subtitle = "Global Rating", color = Color(0xFF7C3AED), modifier = Modifier.weight(1f))
                }
            }

            // ── 3. Quick Action Capabilities Grid ────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                    border = BorderStroke(1.dp, VyaparCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Core Engineering Capabilities",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = VyaparDark
                            )
                            Text(
                                text = "View All",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VyaparBlue,
                                modifier = Modifier.clickable { onExploreServices() }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            HomeCapabilityItem(
                                title = "AI & RAG",
                                subtitle = "Groq LPU",
                                icon = Icons.Outlined.SmartToy,
                                iconColor = Color(0xFF0284C7),
                                bgColor = Color(0xFFE0F2FE),
                                onClick = onOpenAiChat
                            )
                            HomeCapabilityItem(
                                title = "Mobile Apps",
                                subtitle = "Kotlin & Swift",
                                icon = Icons.Outlined.PhoneAndroid,
                                iconColor = Color(0xFF16A34A),
                                bgColor = Color(0xFFDCFCE7),
                                onClick = onExploreServices
                            )
                            HomeCapabilityItem(
                                title = "Play Store",
                                subtitle = "12 Testers",
                                icon = Icons.Outlined.RocketLaunch,
                                iconColor = Color(0xFFD97706),
                                bgColor = Color(0xFFFEF3C7),
                                onClick = onExploreServices
                            )
                            HomeCapabilityItem(
                                title = "Custom ERP",
                                subtitle = "CRM & POS",
                                icon = Icons.Outlined.ReceiptLong,
                                iconColor = Color(0xFF7C3AED),
                                bgColor = Color(0xFFF3E8FF),
                                onClick = onExploreServices
                            )
                        }
                    }
                }
            }

            // ── 4. Featured Portfolio Showcase ───────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                    border = BorderStroke(1.dp, VyaparCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Stars, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Featured Client Success Stories", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
                        }

                        PortfolioItemRow(
                            name = "künh (Global Client App)",
                            category = "Android Closed Testing & Production Approval",
                            status = "100% Approved",
                            statusColor = Color(0xFF16A34A)
                        )
                        PortfolioItemRow(
                            name = "Mewari Achaar",
                            category = "E-Commerce App & Custom Web Platform",
                            status = "Live on Play Store",
                            statusColor = Color(0xFF0284C7)
                        )
                        PortfolioItemRow(
                            name = "Visit Chittorgarh",
                            category = "Smart Heritage Tourism App",
                            status = "Live App",
                            statusColor = Color(0xFF7C3AED)
                        )
                        PortfolioItemRow(
                            name = "Sabarimala Temple Trust Hubballi",
                            category = "Pilgrimage Billing & Trust Management Portal",
                            status = "Enterprise Client",
                            statusColor = Color(0xFFEA580C)
                        )
                    }
                }
            }

            // ── 5. Government & Trust Accreditations Strip ────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Official Accreditations & Trust Center",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TrustPill(text = "DGFT IEC: OTWPS1188A", modifier = Modifier.weight(1f))
                            TrustPill(text = "DPIIT Startup India", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TrustPill(text = "iStart Rajasthan (Score: 32)", modifier = Modifier.weight(1f))
                            TrustPill(text = "MSME Registered", modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // ── 6. Free Consultation Banner ──────────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/917597451057?text=Hi%20ChittorTech%2C%20I%20would%20like%20to%20request%20a%20free%2015-minute%20project%20audit."))
                            context.startActivity(intent)
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Book Free 15-Min Project Audit", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("Connect directly with Kush & Lav Sharma on WhatsApp", fontSize = 11.5.sp, color = Color(0xFF475569))
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ── Metric Stat Card ─────────────────────────────────────────────────────────
@Composable
private fun MetricCard(title: String, subtitle: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color(0xFF64748B), textAlign = TextAlign.Center, lineHeight = 11.sp)
        }
    }
}

// ── Capability Icon Item ──────────────────────────────────────────────────────
@Composable
private fun HomeCapabilityItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    bgColor: Color,
    onClick: () -> Unit
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
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
        Text(text = subtitle, fontSize = 9.5.sp, color = Color(0xFF64748B))
    }
}

// ── Portfolio Item Row ────────────────────────────────────────────────────────
@Composable
private fun PortfolioItemRow(name: String, category: String, status: String, statusColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8FAFC))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(text = category, fontSize = 10.5.sp, color = Color(0xFF64748B))
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = statusColor.copy(alpha = 0.1f)
        ) {
            Text(
                text = status,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

// ── Trust Center Pill ─────────────────────────────────────────────────────────
@Composable
private fun TrustPill(text: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF334155),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
        )
    }
}
