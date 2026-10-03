package com.chittortech.app.ui.vyapar

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.R
import com.chittortech.app.model.CtUser
import com.chittortech.app.model.Invoice
import com.chittortech.app.theme.*
import kotlinx.coroutines.delay

// ── Data Models ───────────────────────────────────────────────────────────────
data class TechStackItem(
    val name: String,
    val category: String,
    val icon: ImageVector,
    val iconColor: Color,
    val bgColor: Color
)

data class AccreditationItem(
    val title: String,
    val authority: String,
    val idNumber: String,
    val status: String,
    val statusColor: Color,
    val statusBg: Color,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBg: Color,
    val verifyUrl: String
)

data class ClientStoryItem(
    val name: String,
    val category: String,
    val statusBadge: String,
    val highlight: String,
    val techChips: List<String>,
    val icon: ImageVector,
    val iconColor: Color
)

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
    var showAllCapabilities by remember { mutableStateOf(true) }

    // ── Frameworks List (from chittortech.in technologies.json) ───────────────
    val frameworks = remember {
        listOf(
            TechStackItem("Kotlin", "Android Native", Icons.Outlined.PhoneAndroid, Color(0xFF7F52FF), Color(0xFFF3E8FF)),
            TechStackItem("Compose", "Material 3 UI", Icons.Outlined.Widgets, Color(0xFF4285F4), Color(0xFFEFF6FF)),
            TechStackItem("Next.js 15", "App Router & SSR", Icons.Outlined.Web, Color(0xFF0F172A), Color(0xFFF1F5F9)),
            TechStackItem("TypeScript", "Strict Type Safety", Icons.Outlined.Code, Color(0xFF3178C6), Color(0xFFEFF6FF)),
            TechStackItem("Groq LPU", "Sub-500ms AI", Icons.Outlined.SmartToy, Color(0xFFF97316), Color(0xFFFFF7ED)),
            TechStackItem("Python", "RAG & LLM Agents", Icons.Outlined.Terminal, Color(0xFF3776AB), Color(0xFFE0F2FE)),
            TechStackItem("Firebase", "Firestore Cloud", Icons.Outlined.CloudSync, Color(0xFFFFB300), Color(0xFFFEF9C3)),
            TechStackItem("PostgreSQL", "Enterprise DB", Icons.Outlined.Storage, Color(0xFF336791), Color(0xFFE0F2FE)),
            TechStackItem("Docker", "CI/CD & DevOps", Icons.Outlined.Dns, Color(0xFF2496ED), Color(0xFFE0F2FE)),
            TechStackItem("Cloudflare", "Global Anycast CDN", Icons.Outlined.Shield, Color(0xFFF6821F), Color(0xFFFFF7ED)),
            TechStackItem("Swift (iOS)", "Apple Native", Icons.Outlined.LaptopMac, Color(0xFFF05138), Color(0xFFFFEDD5)),
            TechStackItem("Node.js", "Microservices", Icons.Outlined.DataObject, Color(0xFF339933), Color(0xFFDCFCE7)),
            TechStackItem("Redis", "In-Memory Cache", Icons.Outlined.Speed, Color(0xFFDC382D), Color(0xFFFEE2E2)),
            TechStackItem("Razorpay", "UPI & Card POS", Icons.Outlined.AccountBalanceWallet, Color(0xFF0C2340), Color(0xFFE0E7FF))
        )
    }

    // ── Exact 16 Accreditations from chittortech.in/trust-center ──────────────
    val accreditations = remember {
        listOf(
            // Statutory & Government Recognition (Section 1)
            AccreditationItem(
                title = "Importer-Exporter Code (IEC)",
                authority = "DGFT Jaipur, Ministry of Commerce & Industry, GoI",
                idNumber = "OTWPS1188A (File: ...346AM27)",
                status = "Officially Issued",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.Public,
                iconColor = Color(0xFF2563EB),
                iconBg = Color(0xFFEFF6FF),
                verifyUrl = "https://www.dgft.gov.in/"
            ),
            AccreditationItem(
                title = "Startup India Recognition",
                authority = "DPIIT, Ministry of Commerce & Industry, GoI",
                idNumber = "DPIIT Startup India Recognized (IT & AI)",
                status = "Recognized Startup",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.Lightbulb,
                iconColor = Color(0xFF2563EB),
                iconBg = Color(0xFFEFF6FF),
                verifyUrl = "https://www.startupindia.gov.in/"
            ),
            AccreditationItem(
                title = "iStart Rajasthan Recognition",
                authority = "DoIT&C, Government of Rajasthan",
                idNumber = "Profile ID: 11478 (Techno Hub / iStart)",
                status = "32 Q-Rate Score",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.WorkspacePremium,
                iconColor = Color(0xFF2563EB),
                iconBg = Color(0xFFEFF6FF),
                verifyUrl = "https://istart.rajasthan.gov.in/profile/11478/startups"
            ),
            AccreditationItem(
                title = "MSME / Udyam Enterprise",
                authority = "Ministry of MSME, Government of India",
                idNumber = "Micro (IT Services) • 45-Day Statutory Protection",
                status = "Active & Verified",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.Handshake,
                iconColor = Color(0xFF2563EB),
                iconBg = Color(0xFFEFF6FF),
                verifyUrl = "https://udyamregistration.gov.in/"
            ),
            AccreditationItem(
                title = "Google Play Verified Developer",
                authority = "Google Play Console • Android App Publishing",
                idNumber = "12 Testers for 14 Days • Play Integrity API",
                status = "Identity Verified",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.PlayArrow,
                iconColor = Color(0xFF059669),
                iconBg = Color(0xFFF0FDF4),
                verifyUrl = "https://chittortech.in/google-play-publishing"
            ),
            AccreditationItem(
                title = "Apple Developer Program (iOS)",
                authority = "Apple Inc. • iOS, iPadOS, macOS Ecosystem",
                idNumber = "Apple App Store & TestFlight Beta Tracks",
                status = "In Progress",
                statusColor = Color(0xFFB45309),
                statusBg = Color(0xFFFFFBEB),
                icon = Icons.Outlined.LaptopMac,
                iconColor = Color(0xFF0F172A),
                iconBg = Color(0xFFF1F5F9),
                verifyUrl = "https://chittortech.in/trust-center"
            ),
            AccreditationItem(
                title = "Dun & Bradstreet (D-U-N-S®)",
                authority = "Dun & Bradstreet India Registry",
                idNumber = "Mandated by Apple Org & Global Enterprise Procurement",
                status = "Coming Soon",
                statusColor = Color(0xFFB45309),
                statusBg = Color(0xFFFFFBEB),
                icon = Icons.Outlined.CorporateFare,
                iconColor = Color(0xFF059669),
                iconBg = Color(0xFFF0FDF4),
                verifyUrl = "https://www.dnb.co.in/"
            ),
            AccreditationItem(
                title = "GST & Financial Compliance",
                authority = "CBIC, Government of India",
                idNumber = "Full B2B Input Tax Credit (ITC) Pass-Through",
                status = "Coming Soon",
                statusColor = Color(0xFFB45309),
                statusBg = Color(0xFFFFFBEB),
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                iconColor = Color(0xFF2563EB),
                iconBg = Color(0xFFEFF6FF),
                verifyUrl = "https://chittortech.in/payment-terms"
            ),

            // Third-Party Verified B2B Directories & Engineering Footprint (Section 2)
            AccreditationItem(
                title = "GoodFirms B2B IT Directory",
                authority = "GoodFirms.co Global Directory (Software & Mobile)",
                idNumber = "Verified Client Software & Mobile Agency",
                status = "Verified Profile",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.Star,
                iconColor = Color(0xFF1D4ED8),
                iconBg = Color(0xFFEFF6FF),
                verifyUrl = "https://www.goodfirms.co/company/chittortech"
            ),
            AccreditationItem(
                title = "Clutch.co B2B Ratings",
                authority = "Clutch.co (Washington DC) Global IT Platform",
                idNumber = "Custom Software & Enterprise AI Ratings",
                status = "Listed Agency",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.Grade,
                iconColor = Color(0xFFEF4444),
                iconBg = Color(0xFFFEF2F2),
                verifyUrl = "https://clutch.co/profile/chittortech"
            ),
            AccreditationItem(
                title = "Google Business Profile (GMB)",
                authority = "Google Maps Verified Entity (Pan-India & Global)",
                idNumber = "Headquarters: Chittorgarh, Rajasthan 312021",
                status = "Verified Local Entity",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.Place,
                iconColor = Color(0xFF16A34A),
                iconBg = Color(0xFFF0FDF4),
                verifyUrl = "https://maps.google.com/?q=ChittorTech+Chittorgarh"
            ),
            AccreditationItem(
                title = "LinkedIn Company Presence",
                authority = "LinkedIn Corporation B2B Enterprise Network",
                idNumber = "Engineering Releases & Product Milestones",
                status = "Official Organization",
                statusColor = Color(0xFF1D4ED8),
                statusBg = Color(0xFFEFF6FF),
                icon = Icons.Outlined.Business,
                iconColor = Color(0xFF0077B5),
                iconBg = Color(0xFFEFF6FF),
                verifyUrl = "https://www.linkedin.com/company/chittortech"
            ),
            AccreditationItem(
                title = "GitHub Engineering Hub",
                authority = "GitHub Inc. Public & Enterprise Code Vault",
                idNumber = "Next.js, Node.js, Python, Kotlin Production Blueprints",
                status = "Open Source & Code",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.Code,
                iconColor = Color(0xFFFFFFFF),
                iconBg = Color(0xFF0F172A),
                verifyUrl = "https://github.com/sharmakush2003"
            ),
            AccreditationItem(
                title = "Cloudflare & Vercel Infrastructure",
                authority = "Cloudflare Global WAF, DNS & Enterprise CDN",
                idNumber = "99.98% High Availability Uptime Record",
                status = "Global Edge Live",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.Cloud,
                iconColor = Color(0xFF0284C7),
                iconBg = Color(0xFFEFF6FF),
                verifyUrl = "https://chittortech.in/dns-cloudflare-management"
            ),
            AccreditationItem(
                title = "WhatsApp Verified Business",
                authority = "Official WhatsApp Business API Communication",
                idNumber = "Direct Line: +91 75974 51057 (<15 Min Response)",
                status = "Official Business",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.Chat,
                iconColor = Color(0xFF22C55E),
                iconBg = Color(0xFFF0FDF4),
                verifyUrl = "https://wa.me/917597451057"
            ),
            AccreditationItem(
                title = "GoDaddy & Titan Business Email",
                authority = "GoDaddy Corporate Registrar & Titan Mail Suite",
                idNumber = "SPF, DKIM, DMARC Verified & TLS Encryption",
                status = "Enterprise Email",
                statusColor = Color(0xFF059669),
                statusBg = Color(0xFFECFDF5),
                icon = Icons.Outlined.MarkEmailRead,
                iconColor = Color(0xFF0D9488),
                iconBg = Color(0xFFF0FDFA),
                verifyUrl = "https://chittortech.in/business-email-branding-bimi"
            )
        )
    }

    // ── Client Success Stories List (from Real ChittorTech Portfolio) ─────────
    val clientStories = remember {
        listOf(
            ClientStoryItem(
                name = "künh (Global Client App)",
                category = "Fashion & Lifestyle D2C",
                statusBadge = "100% Approved",
                highlight = "Completed 14-day closed testing with 12 real testers. Production release approved and live on Google Play.",
                techChips = listOf("Google Play", "Kotlin", "Play Console"),
                icon = Icons.Outlined.CheckCircle,
                iconColor = Color(0xFF10B981)
            ),
            ClientStoryItem(
                name = "Mewari Achaar",
                category = "Heritage D2C Brand",
                highlight = "Engineered custom direct-to-consumer store with automated multi-courier dispatch and Razorpay UPI settlements.",
                statusBadge = "Production Live",
                techChips = listOf("Next.js", "Razorpay", "Logistics API"),
                icon = Icons.Outlined.ShoppingCart,
                iconColor = Color(0xFF0284C7)
            ),
            ClientStoryItem(
                name = "Visit Chittorgarh",
                category = "Tourism & Regional Guide",
                highlight = "High-speed guide directory with interactive heritage monument maps serving 50,000+ monthly cultural tourists.",
                statusBadge = "50K+ Monthly Users",
                techChips = listOf("React", "Google Maps", "SEO Engine"),
                icon = Icons.Outlined.Explore,
                iconColor = Color(0xFFF59E0B)
            ),
            ClientStoryItem(
                name = "Sabarimala Temple Hubballi",
                category = "Institutional Devotee POS",
                highlight = "Offline-first counter billing engine handling 10,000+ devotees on peak festival days with zero network downtime.",
                statusBadge = "Zero-Downtime POS",
                techChips = listOf("IndexedDB", "Offline-First", "Thermal Print"),
                icon = Icons.Outlined.AccountBalance,
                iconColor = Color(0xFF7C3AED)
            ),
            ClientStoryItem(
                name = "Smart Nagar Parishad Chittorgarh",
                category = "Municipal E-Governance",
                highlight = "Citizen grievance redressal portal and real-time municipal ward maintenance tracking with automated SMS alerts.",
                statusBadge = "Govt. Active",
                techChips = listOf("Supabase", "GIS Mapping", "Govt APIs"),
                icon = Icons.Outlined.Apartment,
                iconColor = Color(0xFF0D9488)
            )
        )
    }

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
                                        text = "CHITTORTECH OFFICIAL",
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
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxSize()
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

                            // 2 Equal-Size Responsive Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onOpenAiChat,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Ask ChittorTech GPT",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/917597451057?text=Hello%20Kush%20and%20Lav%2C%20I%20want%20to%20consult%20for%20a%20project."))
                                        context.startActivity(intent)
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.18f)),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Talk to Founders",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── 2. Verified Metrics Bar (Exact Equal Height 2x2 Grid) ───────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        UniformMetricCard(title = "250+", subtitle = "Delivered Projects", color = Color(0xFF0284C7), modifier = Modifier.weight(1f))
                        UniformMetricCard(title = "50+", subtitle = "Play Store Apps", color = Color(0xFF16A34A), modifier = Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        UniformMetricCard(title = "99.8%", subtitle = "Client Satisfaction", color = Color(0xFFD97706), modifier = Modifier.weight(1f))
                        UniformMetricCard(title = "4.8★", subtitle = "Global Rating", color = Color(0xFF7C3AED), modifier = Modifier.weight(1f))
                    }
                }
            }

            // ── 3. Core Engineering Capabilities (8 Full Capabilities) ───────────
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
                                text = if (showAllCapabilities) "Collapse" else "View All (8)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VyaparBlue,
                                modifier = Modifier.clickable { showAllCapabilities = !showAllCapabilities }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Row 1: First 4 Capabilities
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
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in/ai-chatbot-development"))
                                    context.startActivity(intent)
                                }
                            )
                            HomeCapabilityItem(
                                title = "Mobile Apps",
                                subtitle = "Kotlin & Swift",
                                icon = Icons.Outlined.PhoneAndroid,
                                iconColor = Color(0xFF16A34A),
                                bgColor = Color(0xFFDCFCE7),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in/android-application"))
                                    context.startActivity(intent)
                                }
                            )
                            HomeCapabilityItem(
                                title = "Play Store",
                                subtitle = "12 Testers",
                                icon = Icons.Outlined.RocketLaunch,
                                iconColor = Color(0xFFD97706),
                                bgColor = Color(0xFFFEF3C7),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in/google-play-publishing"))
                                    context.startActivity(intent)
                                }
                            )
                            HomeCapabilityItem(
                                title = "Custom ERP",
                                subtitle = "CRM & POS",
                                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                                iconColor = Color(0xFF7C3AED),
                                bgColor = Color(0xFFF3E8FF),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in/erp"))
                                    context.startActivity(intent)
                                }
                            )
                        }

                        // Row 2: Additional 4 Capabilities
                        AnimatedVisibility(visible = showAllCapabilities) {
                            Column {
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    HomeCapabilityItem(
                                        title = "Web SaaS",
                                        subtitle = "Next.js 15",
                                        icon = Icons.Outlined.Web,
                                        iconColor = Color(0xFF0F172A),
                                        bgColor = Color(0xFFF1F5F9),
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in/web-development-services"))
                                            context.startActivity(intent)
                                        }
                                    )
                                    HomeCapabilityItem(
                                        title = "Cloud DevOps",
                                        subtitle = "Docker & GCP",
                                        icon = Icons.Outlined.Cloud,
                                        iconColor = Color(0xFF0284C7),
                                        bgColor = Color(0xFFE0F2FE),
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in/cloud-hosting-deployment"))
                                            context.startActivity(intent)
                                        }
                                    )
                                    HomeCapabilityItem(
                                        title = "E-Commerce",
                                        subtitle = "D2C Stores",
                                        icon = Icons.Outlined.ShoppingCart,
                                        iconColor = Color(0xFF059669),
                                        bgColor = Color(0xFFD1FAE5),
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in/e-commerce-website-development"))
                                            context.startActivity(intent)
                                        }
                                    )
                                    HomeCapabilityItem(
                                        title = "Compliance",
                                        subtitle = "iStart & DPIIT",
                                        icon = Icons.Outlined.Verified,
                                        iconColor = Color(0xFF7C3AED),
                                        bgColor = Color(0xFFEDE9FE),
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
            }

            // ── 4. Enterprise Tech Stack: AUTO-SCROLLING HORIZONTAL MARQUEE ──────
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Memory, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Enterprise Tech Stack",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VyaparDark
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                            ) {
                                Text(
                                    text = "LIVE ROTATING",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Infinite Auto-Scrolling Marquee Component
                        AutoScrollingTechMarquee(frameworks = frameworks)
                    }
                }
            }

            // ── 5. Featured Client Success Stories (Real Deployments) ─────────────
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                    border = BorderStroke(1.dp, VyaparCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Featured Client Success Stories",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VyaparDark
                                )
                            }
                            Text(
                                text = "${clientStories.size} Verified",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }

                        // Render Client Story Cards
                        clientStories.forEach { story ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(story.iconColor.copy(alpha = 0.12f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(story.icon, contentDescription = null, tint = story.iconColor, modifier = Modifier.size(18.dp))
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(text = story.name, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
                                                Text(text = story.category, fontSize = 11.sp, color = Color(0xFF64748B))
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFDCFCE7),
                                            border = BorderStroke(1.dp, Color(0xFF86EFAC))
                                        ) {
                                            Text(
                                                text = story.statusBadge,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF166534),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = story.highlight,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = Color(0xFF475569)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        story.techChips.forEach { chip ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color.White,
                                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                            ) {
                                                Text(
                                                    text = chip,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF475569),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── 6. Statutory & Trust Center (Exact 16 Accreditations) ─────────────
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                    border = BorderStroke(1.dp, VyaparCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Statutory & Trust Center",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VyaparDark
                                )
                            }
                            Text(
                                text = "16 Accreditations",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                        }

                        Text(
                            text = "ChittorTech operates with 100% legal transparency and regulatory compliance across Government statutory trade codes, state incubation, and verified developer ecosystems.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFF64748B)
                        )

                        // Render All 16 Verified Accreditations
                        accreditations.forEach { acc ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(acc.verifyUrl))
                                        context.startActivity(intent)
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    // Distinctive Icon Box (from website styles)
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(acc.iconBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(acc.icon, contentDescription = null, tint = acc.iconColor, modifier = Modifier.size(22.dp))
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = acc.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = VyaparDark,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )

                                            Spacer(modifier = Modifier.width(6.dp))

                                            // Exact Status Pill
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = acc.statusBg,
                                                border = BorderStroke(1.dp, acc.statusColor.copy(alpha = 0.4f))
                                            ) {
                                                Text(
                                                    text = acc.status,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = acc.statusColor,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(text = acc.authority, fontSize = 11.sp, color = Color(0xFF64748B), lineHeight = 15.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = acc.idNumber,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── 7. Book Free 15-Minute Audit CTA ─────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Scale Your Business With ChittorTech",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Direct founder-led architecture consultation. From 12-Tester Google Play releases to enterprise AI workflows.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/917597451057?text=Hello%20ChittorTech%2C%20I%20would%20like%20to%20book%20a%2015-minute%20software%20consultation."))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Book Free 15-Min Tech Audit", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

// ── Auto-Scrolling Marquee Component ──────────────────────────────────────────
@Composable
private fun AutoScrollingTechMarquee(frameworks: List<TechStackItem>) {
    val scrollState = rememberScrollState()

    // Smooth infinite continuous drift
    LaunchedEffect(Unit) {
        while (true) {
            val max = scrollState.maxValue
            if (max > 0) {
                val current = scrollState.value
                val remaining = max - current
                val duration = (remaining * 35).coerceAtLeast(1000)
                scrollState.animateScrollTo(
                    value = max,
                    animationSpec = tween(durationMillis = duration, easing = LinearEasing)
                )
                scrollState.scrollTo(0)
            } else {
                delay(100)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Triple list for smooth infinite loop illusion
        (frameworks + frameworks + frameworks).forEach { item ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.width(115.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(item.bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, contentDescription = null, tint = item.iconColor, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = item.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VyaparDark, maxLines = 1)
                    Text(text = item.category, fontSize = 10.sp, color = Color(0xFF64748B), maxLines = 1)
                }
            }
        }
    }
}

// ── Uniform Metric Card Component (Guaranteed Same Height & Padding) ──────────
@Composable
private fun UniformMetricCard(
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = VyaparWhite,
        border = BorderStroke(1.dp, VyaparCardBorder),
        shadowElevation = 1.dp,
        modifier = modifier.height(72.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── Capability Item Component ─────────────────────────────────────────────────
@Composable
private fun HomeCapabilityItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = VyaparDark,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = subtitle,
            fontSize = 9.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
