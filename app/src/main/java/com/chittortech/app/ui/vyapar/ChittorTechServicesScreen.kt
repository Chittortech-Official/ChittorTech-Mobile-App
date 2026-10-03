package com.chittortech.app.ui.vyapar

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.R
import com.chittortech.app.model.ChittorService
import com.chittortech.app.model.LeadInquiry
import com.chittortech.app.theme.*

// ── Service Data Repository (Mirrored from chittortech.in) ───────────────────

val ChittorTechServiceCatalog = listOf(
    ChittorService(
        id = "ai_rag_solutions",
        title = "Enterprise AI & RAG Knowledge Systems",
        category = "AI & Automation",
        shortDescription = "Custom LLMs, vector database retrieval (Pinecone/pgvector), autonomous AI agents, and private on-premise AI deployments.",
        keyFeatures = listOf(
            "RAG (Retrieval-Augmented Generation) with cited sources",
            "Autonomous multi-step Agentic workflows & tool use",
            "Document AI, Smart OCR & Contract extraction",
            "Enterprise multilingual WhatsApp & Web AI bots"
        ),
        techStack = listOf("Python", "LangChain", "OpenAI", "Llama 3", "Pinecone", "FastAPI"),
        popularBadge = "MOST POPULAR"
    ),
    ChittorService(
        id = "mobile_app_dev",
        title = "Native Android & iOS App Development",
        category = "Mobile Apps",
        shortDescription = "State-of-the-art native mobile applications built with Kotlin Jetpack Compose and Swift, backed by high-availability cloud architecture.",
        keyFeatures = listOf(
            "100% Native Jetpack Compose & Material 3 UI",
            "Offline-first synchronization with Cloud Firestore",
            "Real-time Firebase Cloud Messaging (FCM) notifications",
            "Biometric authentication & hardware-level security"
        ),
        techStack = listOf("Kotlin", "Jetpack Compose", "Swift", "Firebase", "Coroutines"),
        popularBadge = "ENTERPRISE GRADE"
    ),
    ChittorService(
        id = "google_play_publishing",
        title = "Google Play 12-Tester Publishing & Compliance",
        category = "Google Play Launch",
        shortDescription = "100% guaranteed compliance for Google's mandatory 12-tester 14-day closed testing policy with verified human testers and first-attempt approval.",
        keyFeatures = listOf(
            "12 verified human opt-in testers for 14 continuous days",
            "Target SDK 34+/35 and 64-bit architecture pre-audit",
            "Compliant privacy policy & Google Play Data Safety forms",
            "50+ Android apps successfully approved and launched globally"
        ),
        techStack = listOf("Google Play Console", "Closed Testing", "ASO", "Security Audit"),
        popularBadge = "100% APPROVAL TRACK"
    ),
    ChittorService(
        id = "web_saas_engineering",
        title = "Full-Stack Web & Scalable SaaS Platforms",
        category = "Web & SaaS",
        shortDescription = "Next-generation web applications, client portals, and multi-tenant SaaS architectures built for high concurrency and speed.",
        keyFeatures = listOf(
            "Next.js 15 App Router with zero-latency Server Components",
            "Payment gateway integration (Cashfree, Razorpay, Stripe)",
            "Enterprise Role-Based Access Control (RBAC)",
            "Automated CI/CD pipelines on AWS, GCP & Vercel"
        ),
        techStack = listOf("Next.js", "React", "TypeScript", "Node.js", "PostgreSQL", "AWS")
    ),
    ChittorService(
        id = "custom_erp_crm",
        title = "Custom Vyapar ERP, CRM & Billing Systems",
        category = "Enterprise ERP/CRM",
        shortDescription = "Tailored business software replacing generic tools with bespoke inventory, financial ledger, and customer relationship pipelines.",
        keyFeatures = listOf(
            "GST-ready digital invoices, e-way bills & payment receipts",
            "Multi-warehouse real-time inventory tracking",
            "Automated WhatsApp payment reminders & quotation dispatch",
            "Comprehensive P&L, balance sheet & cash flow analytics"
        ),
        techStack = listOf("Jetpack Compose", "Firestore", "Cloud Functions", "Tally API")
    ),
    ChittorService(
        id = "trust_center_compliance",
        title = "Government Startup Accreditations & Compliance",
        category = "Govt & Startup Compliance",
        shortDescription = "Turnkey corporate and legal certifications enabling startup tax holidays, zero-rated foreign software exports, and platform verification.",
        keyFeatures = listOf(
            "DGFT IEC Code setup for legal USD/EUR foreign wire settlement",
            "DPIIT Startup India recognition & 3-year tax exemptions",
            "iStart Rajasthan incubation mentorship & Q-Rate scorecard",
            "MSME / Udyam Enterprise 24-hr registration & payment protection"
        ),
        techStack = listOf("DGFT IEC", "DPIIT", "iStart", "MSME Samadhaan", "D-U-N-S"),
        popularBadge = "GOVT RECOGNIZED"
    ),
    ChittorService(
        id = "seo_growth_marketing",
        title = "Technical SEO & Search Engine Optimization",
        category = "SEO & Growth",
        shortDescription = "Data-driven organic search ranking, Core Web Vitals acceleration, and schema architecture to capture high-intent commercial keywords.",
        keyFeatures = listOf(
            "Schema markup (Organization, Service, FAQ, LocalBusiness)",
            "Google Search Console & Bing Webmaster indexing optimization",
            "Local map rank enhancement via Google Business Profile (GMB)",
            "High-authority B2B backlink acquisition & tech content strategy"
        ),
        techStack = listOf("Technical SEO", "Schema.org", "GMB", "Search Console", "PageSpeed")
    )
)

// ── Screen Composable ────────────────────────────────────────────────────────

@Composable
fun ChittorTechServicesScreen(
    onContactUs: () -> Unit = {},
    onSubmitLead: (LeadInquiry) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedServiceForModal by remember { mutableStateOf<ChittorService?>(null) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    val categories = remember {
        listOf("All", "AI & Automation", "Mobile Apps", "Google Play Launch", "Web & SaaS", "Enterprise ERP/CRM", "Govt & Startup Compliance", "SEO & Growth")
    }

    val filteredServices = remember(selectedCategory) {
        if (selectedCategory == "All") ChittorTechServiceCatalog
        else ChittorTechServiceCatalog.filter { it.category == selectedCategory }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VyaparBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── 1. Hero Banner: ChittorTech Agency Profile ────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0F172A), // Slate 900
                                        Color(0xFF1E293B), // Slate 800
                                        Color(0xFF0369A1)  // Ocean Blue
                                    )
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 22.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.chittortech_logo),
                                            contentDescription = "ChittorTech Logo",
                                            modifier = Modifier
                                                .padding(6.dp)
                                                .fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "ChittorTech",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "chittortech.in",
                                            fontSize = 12.sp,
                                            color = Color(0xFF38BDF8)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, Color(0xFF10B981))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF10B981))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "OFFICIAL CATALOG",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF34D399)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Enterprise AI, Cloud Systems & Custom Software Engineering",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "From autonomous RAG agents to native mobile applications and Play Store testing, discover our complete engineering capabilities.",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Stats Pill Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatBadge(value = "250+", label = "Projects Delivered")
                                StatBadge(value = "100%", label = "Play Approval")
                                StatBadge(value = "4.8★", label = "Client Rating")
                                StatBadge(value = "DGFT", label = "Govt Recognized")
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quick Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/917597451057?text=Hello+ChittorTech+Team,+I+am+interested+in+your+services"))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("WhatsApp Us", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in"))
                                        context.startActivity(intent)
                                    },
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Visit Website", fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // ── 2. Category Filter Pills ──────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "EXPLORE BY DOMAIN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) VyaparBlue else Color.White,
                                border = BorderStroke(1.dp, if (isSelected) VyaparBlue else Color(0xFFE2E8F0)),
                                modifier = Modifier.clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF334155),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── 3. Services Feed ──────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    filteredServices.forEach { service ->
                        ServiceCard(
                            service = service,
                            onEnquire = { selectedServiceForModal = service },
                            onWhatsApp = {
                                val text = "Hello ChittorTech Team, I would like to enquire about: ${service.title}"
                                val url = "https://wa.me/917597451057?text=${Uri.encode(text)}"
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        )
                    }
                }
            }
        }

        // ── 4. Lead Inquiry Modal Dialog ──────────────────────────────────────────
        selectedServiceForModal?.let { service ->
            ServiceInquiryDialog(
                service = service,
                onDismiss = { selectedServiceForModal = null },
                onSubmit = { inquiry ->
                    onSubmitLead(inquiry)
                    selectedServiceForModal = null
                    showSuccessSnackbar = true
                }
            )
        }

        // ── 5. Instant Success Notification ───────────────────────────────────────
        if (showSuccessSnackbar) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { showSuccessSnackbar = false }) {
                        Text("OK", color = Color.White)
                    }
                },
                containerColor = Color(0xFF0F766E)
            ) {
                Text("Inquiry submitted! Our engineering lead will contact you shortly.", color = Color.White)
            }
        }
    }
}

// ── Service Card ─────────────────────────────────────────────────────────────

@Composable
private fun ServiceCard(
    service: ChittorService,
    onEnquire: () -> Unit,
    onWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row with category & popular badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Text(
                        text = service.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                service.popularBadge?.let { badge ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFF93C5FD))
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1D4ED8),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = service.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Short Description
            Text(
                text = service.shortDescription,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Key Deliverables Checklist
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                service.keyFeatures.forEach { feature ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier
                                .size(14.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = feature,
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tech Stack Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(service.techStack) { tech ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = tech,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onWhatsApp,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF25D366)),
                    modifier = Modifier.weight(1f).height(38.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF16A34A))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onEnquire,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VyaparBlue),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Enquire Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── Lead Inquiry Dialog ──────────────────────────────────────────────────────

@Composable
private fun ServiceInquiryDialog(
    service: ChittorService,
    onDismiss: () -> Unit,
    onSubmit: (LeadInquiry) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Request Project Consultation",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = service.title,
                    fontSize = 12.sp,
                    color = VyaparBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Phone / WhatsApp Number *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Corporate Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Company / Startup Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Project Details / Specific Requirements") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && contact.isNotBlank()) {
                        isSubmitting = true
                        onSubmit(
                            LeadInquiry(
                                name = name.trim(),
                                contact = contact.trim(),
                                email = email.trim(),
                                company = company.trim(),
                                service = service.title,
                                message = message.trim(),
                                source = "ChittorTech Mobile App - Services Catalog"
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() && contact.isNotBlank() && !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = VyaparBlue)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Submit to Engineering Lead")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF64748B))
            }
        }
    )
}

// ── Helper Stat Badge ────────────────────────────────────────────────────────

@Composable
private fun StatBadge(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = Color(0xFF94A3B8)
        )
    }
}
