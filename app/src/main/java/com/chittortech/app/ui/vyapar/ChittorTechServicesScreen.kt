package com.chittortech.app.ui.vyapar

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import com.chittortech.app.model.ServicePlan
import com.chittortech.app.theme.*

// ── Complete Service Data Catalog (Directly Mirrored from chittortech.in) ─────

val ChittorTechServiceCatalog = listOf(
    // 1. Android & Mobile App Development
    ChittorService(
        id = "mobile_app_dev",
        title = "Native Android & Mobile App Development",
        category = "Mobile Apps",
        shortDescription = "State-of-the-art native Android apps built with Kotlin Jetpack Compose, backed by scalable cloud architecture.",
        longDescription = "Leading mobile app development company in India. With Android holding over 87% global market share, we engineer custom native Android apps, scalable iOS apps, and cross-platform solutions built for high speed, hardware biometric security, and offline-first state synchronization.",
        websiteSlug = "android-application",
        keyFeatures = listOf(
            "100% Native Jetpack Compose & Material 3 declarative UI",
            "Offline-first synchronization with Cloud Firestore & Room",
            "Real-time Firebase Cloud Messaging (FCM) push notifications",
            "Hardware biometric authentication & AES-256 local vault"
        ),
        techStack = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Firebase", "Room DB", "Target SDK 35", "Material 3"),
        popularBadge = "ENTERPRISE GRADE",
        metrics = listOf(
            "87%" to "Android Market Reach",
            "100%" to "SDK 35 Compliant",
            "4.9★" to "App Rating Average",
            "60-120fps" to "Fluid Compose UI"
        ),
        deliverables = listOf(
            "Native Jetpack Compose & Material 3 declarative UI architecture",
            "Offline-first local cache synchronization with Cloud Firestore / Room",
            "Real-time Firebase Cloud Messaging (FCM) & rich interactive push alerts",
            "Hardware biometric authentication (Fingerprint / Face Unlock) & AES-256 vault",
            "Target SDK 34/35 optimization with 64-bit native binaries & ProGuard obfuscation",
            "End-to-end Google Play Console deployment & release lifecycle management"
        ),
        benefits = listOf(
            "100% Code Ownership" to "Full intellectual property rights with clean Git repository transfer upon delivery.",
            "Sub-100ms Response Times" to "Engineered for 60-120fps fluid scrolling and instant user touch feedback.",
            "Enterprise Zero-Trust Security" to "ProGuard/R8 obfuscation, SSL pinning, and root detection protection built-in."
        ),
        plans = listOf(
            ServicePlan(
                name = "Starter App MVP",
                price = "₹49,999",
                description = "Turnkey mobile app MVP for validation",
                features = listOf("Up to 8 Core Screens", "Firebase Auth & Database", "Offline Caching", "Play Store APK Ready", "30 Days Support")
            ),
            ServicePlan(
                name = "Business Production App",
                price = "₹1,25,000",
                description = "Full-featured production Android application",
                features = listOf("Unlimited Custom Screens", "Real-Time Sync Engine", "Payment Gateway (UPI/Cards)", "Push Notifications Hub", "Play Console Submission", "90 Days Warranty"),
                isPopular = true
            ),
            ServicePlan(
                name = "Enterprise Suite",
                price = "Custom Quote",
                description = "High-concurrency multi-platform app with custom APIs",
                features = listOf("Native Android + iOS", "Microservices Backend", "Role-Based Permissions", "CI/CD Deployment", "SLA Guarantee", "Dedicated Tech Lead")
            )
        ),
        faqs = listOf(
            "Do you develop native Android apps using Kotlin?" to "Yes, we build 100% modern native applications using Kotlin, Jetpack Compose, Kotlin Coroutines, and Android Jetpack libraries adhering to Google's official recommended architecture.",
            "Will I own the complete source code?" to "Absolutely. 100% of the intellectual property, source code repositories, and developer assets are transferred directly to your organization.",
            "Do you help with Google Play Store publishing?" to "Yes, we handle complete release audits, target SDK compliance, privacy policies, data safety forms, and Google Play Console publishing."
        )
    ),

    // 2. Enterprise AI & RAG Solutions
    ChittorService(
        id = "ai_rag_solutions",
        title = "Enterprise AI & RAG Knowledge Systems",
        category = "AI & Automation",
        shortDescription = "Custom LLMs, vector database retrieval (Pinecone/pgvector), autonomous AI agents, and private on-premise AI deployments.",
        longDescription = "High-performance enterprise AI chatbots and agentic workflows powered by Groq LPUs, Meta Llama 3 70B, and Retrieval-Augmented Generation (RAG). Deliver sub-500ms intelligent customer service, automated document OCR, and multilingual knowledge synthesis without hallucination.",
        websiteSlug = "ai-chatbot-development",
        keyFeatures = listOf(
            "RAG (Retrieval-Augmented Generation) with cited sources",
            "Autonomous multi-step Agentic workflows & tool use",
            "Document AI, Smart OCR & Contract extraction",
            "Enterprise multilingual WhatsApp & Web AI bots"
        ),
        techStack = listOf("Groq LPUs", "Llama 3 70B", "LangChain", "Pinecone", "Python", "FastAPI", "WhatsApp API"),
        popularBadge = "MOST POPULAR",
        metrics = listOf(
            "<500ms" to "Groq LPU Latency",
            "99.4%" to "RAG Answer Accuracy",
            "50+" to "Languages Supported",
            "24/7" to "Automated Resolution"
        ),
        deliverables = listOf(
            "Sub-500ms inference powered by dedicated Groq LPU compute engines",
            "Retrieval-Augmented Generation (RAG) referencing internal enterprise PDFs/docs",
            "Autonomous multi-step tool-calling agents for CRM & database actions",
            "Seamless omnichannel integration (WhatsApp, Web Widget, Mobile App)",
            "Strict hallucination prevention guards & cited knowledge retrieval",
            "Self-hosted private LLM deployment for total data privacy"
        ),
        benefits = listOf(
            "Zero Data Leakage" to "Zero retention API architecture keeping sensitive client data confidential.",
            "Hyper-Fast Responses" to "500+ tokens/second Groq acceleration eliminates user waiting times.",
            "Round-The-Clock Support" to "Automate 80%+ of incoming tier-1 support queries instantly."
        ),
        plans = listOf(
            ServicePlan(
                name = "Starter AI Bot",
                price = "₹35,000",
                description = "Custom trained web chatbot with company knowledge base",
                features = listOf("Web Embed Widget", "Custom Knowledge Vectorization", "Up to 500 Pages Ingested", "OpenAI / Llama 3 Backbone", "Analytics Dashboard")
            ),
            ServicePlan(
                name = "Growth RAG Platform",
                price = "₹85,000",
                description = "Enterprise RAG with WhatsApp & CRM integrations",
                features = listOf("WhatsApp Cloud API Integration", "Groq Sub-500ms Inference", "Pinecone Vector Store", "Multi-Turn Memory", "Human Agent Handover", "Priority Tech Support"),
                isPopular = true
            ),
            ServicePlan(
                name = "Autonomous Agentic System",
                price = "Custom Quote",
                description = "Multi-agent automation connecting your ERP, email & CRM",
                features = listOf("Autonomous Tool Calling", "Private LLM / On-Premise", "Custom Fine-Tuning", "Enterprise SSO & RBAC", "Dedicated Engineering Pod")
            )
        ),
        faqs = listOf(
            "How does ChittorTech prevent AI chatbots from hallucinating?" to "We use strict Retrieval-Augmented Generation (RAG) with similarity thresholds and citation verification so the AI only answers based on verified enterprise source documents.",
            "Can the chatbot be integrated into WhatsApp Business?" to "Yes, we integrate with official WhatsApp Cloud APIs to deliver automated conversational AI support directly on your customer's phones."
        )
    ),

    // 3. Google Play 12-Tester Publishing & Compliance
    ChittorService(
        id = "google_play_publishing",
        title = "Google Play 12-Tester Publishing & Compliance",
        category = "Google Play Launch",
        shortDescription = "100% guaranteed compliance for Google's mandatory 12-tester 14-day closed testing policy with verified human testers and first-attempt approval.",
        longDescription = "Navigating Google Play's strict 12-tester 14-day closed testing policy is difficult for individual developers and startups. ChittorTech provides 100% guaranteed compliance with real human opt-in testers, daily app engagement, Target SDK 34/35 pre-audits, compliant Data Safety forms, and end-to-end console publishing.",
        websiteSlug = "google-play-publishing",
        keyFeatures = listOf(
            "12 verified human opt-in testers for 14 continuous days",
            "Target SDK 34+/35 and 64-bit architecture pre-audit",
            "Compliant privacy policy & Google Play Data Safety forms",
            "50+ Android apps successfully approved and launched globally"
        ),
        techStack = listOf("Google Play Console", "Closed Testing", "ASO", "Security Audit", "D-U-N-S", "Data Safety"),
        popularBadge = "100% APPROVAL TRACK",
        metrics = listOf(
            "100%" to "Approval Guarantee",
            "50+" to "Apps Successfully Launched",
            "14 Days" to "Mandatory Testing Window",
            "12+" to "Real Human Testers"
        ),
        deliverables = listOf(
            "12 verified human opt-in testers testing your app actively for 14 continuous days",
            "Target SDK 34/35 & 64-bit architecture pre-submission technical audit",
            "Google Play Data Safety declaration & privacy policy compliance",
            "Store listing optimization (ASO), high-res graphics & screenshot setup",
            "Option to publish on your own developer console or ChittorTech's verified account",
            "Appeal assistance and policy remediation if app faced prior rejection"
        ),
        benefits = listOf(
            "Guaranteed Approval" to "If your app is rejected due to testing metrics, we re-test at zero additional cost.",
            "No Friend Harassment" to "You don't have to beg friends or family to install and open your app every day.",
            "Full Play Store Verification" to "Complete guidance for D-U-N-S registration and organization accounts."
        ),
        plans = listOf(
            ServicePlan(
                name = "Publish on Your Account",
                price = "$129 / ₹10,299",
                description = "For developers with their own Play Console needing compliance & testers",
                features = listOf("12 Real Human Testers for 14 Days", "Pre-Submission Policy & Compliance Check", "AAB/APK Compilation & SDK Target Check", "Privacy Policy Hosting Setup", "Review Process Management", "1 Free Update within 30 days")
            ),
            ServicePlan(
                name = "Full Account Setup & Launch",
                price = "$339 / ₹29,499",
                description = "Complete console creation, verification & launch",
                features = listOf("Organization / Individual Console Registration", "D-U-N-S Number Guidance", "Complete 12-Tester 14-Day Cohort", "Full Store Listing & ASO", "Data Safety Form Compliance", "100% Approval Guarantee"),
                isPopular = true
            ),
            ServicePlan(
                name = "Publish on ChittorTech Account",
                price = "$299 / ₹25,999",
                description = "Skip the $25 fee & verification; launch under our organization",
                features = listOf("1-Year Hosting on ChittorTech Console", "Complete Store Asset Setup", "Strict Policy & Security Audit", "Crash Alerts & Monitoring", "2 Free Updates per year")
            )
        ),
        faqs = listOf(
            "Are the 12 testers real people or bots?" to "100% real human testers using genuine Android devices across varied Android OS versions and screen densities.",
            "What happens if Google rejects the app during closed testing?" to "We identify the exact policy concern flagged by Google, rectify it with you, and conduct re-testing without charging extra fees until approved."
        )
    ),

    // 4. Full-Stack Web & Next.js SaaS Engineering
    ChittorService(
        id = "web_saas_engineering",
        title = "Full-Stack Web & Next.js SaaS Platforms",
        category = "Web & SaaS",
        shortDescription = "Next-generation web applications, client portals, and multi-tenant SaaS architectures built for high concurrency and speed.",
        longDescription = "We engineer cutting-edge web applications using Next.js 15, React 19, TypeScript, and modern headless architectures. Designed for lightning-fast Core Web Vitals, server components rendering, and bank-grade data security with integrated payment infrastructure.",
        websiteSlug = "web-development-services",
        keyFeatures = listOf(
            "Next.js 15 App Router with zero-latency Server Components",
            "Payment gateway integration (Cashfree, Razorpay, Stripe)",
            "Enterprise Role-Based Access Control (RBAC)",
            "Automated CI/CD pipelines on AWS, GCP & Vercel"
        ),
        techStack = listOf("Next.js 15", "React 19", "TypeScript", "Node.js", "PostgreSQL", "Prisma", "AWS", "Vercel"),
        metrics = listOf(
            "<1.2s" to "Largest Contentful Paint",
            "100/100" to "Lighthouse Performance",
            "99.99%" to "SaaS Cloud Uptime",
            "Zero" to "Downtime Deployments"
        ),
        deliverables = listOf(
            "Next.js 15 App Router architecture with React Server Components",
            "PostgreSQL, Prisma ORM, and Supabase / Firebase scalable backends",
            "Multi-provider payment gateway integration (Razorpay, Cashfree, Stripe)",
            "Role-Based Access Control (RBAC) with secure JWT / NextAuth sessions",
            "Fully responsive fluid layouts tailored for Mobile, Tablet, and Desktop",
            "Automated CI/CD deployment pipelines on Vercel, AWS, or Cloudflare Edge"
        ),
        benefits = listOf(
            "Blazing Speed" to "Sub-second page loads that maximize Google search rankings and user conversions.",
            "Zero Vendor Lock-In" to "Built on open-source standards with pure portable TypeScript code.",
            "Bank-Grade Encryption" to "HTTPS/TLS 1.3, CSRF tokens, rate limiting, and SQL injection prevention."
        ),
        plans = listOf(
            ServicePlan(
                name = "Corporate Web Showcase",
                price = "₹30,000",
                description = "High-performance company website with CMS",
                features = listOf("Up to 10 High-Conversion Pages", "Next.js 15 + Tailwind Design", "100/100 PageSpeed Score", "Contact Forms & Lead capture", "Basic SEO & Social Metadata")
            ),
            ServicePlan(
                name = "Full-Stack SaaS MVP",
                price = "₹95,000",
                description = "Production-ready software platform with user auth & billing",
                features = listOf("Complete Auth (Google/Email)", "Subscription Billing (Stripe/Razorpay)", "PostgreSQL / Firebase Database", "User Dashboard & Admin Panel", "API Webhooks & Email Alerts"),
                isPopular = true
            ),
            ServicePlan(
                name = "Enterprise Cloud Portal",
                price = "Custom Quote",
                description = "High-volume multi-tenant platform with dedicated architecture",
                features = listOf("Microservices Architecture", "Custom CRM / ERP Integration", "High-Availability Load Balancing", "SOC2 / GDPR Compliance Audit", "SLA & 24/7 Infrastructure Support")
            )
        ),
        faqs = listOf(
            "Which web framework do you use?" to "We primarily use Next.js 15 (React 19) with TypeScript for speed, SEO, and robust type safety.",
            "Can you integrate Indian payment gateways like UPI and NetBanking?" to "Yes, we natively integrate Cashfree, Razorpay, and PayU supporting UPI QR, auto-debit mandates, cards, and net banking."
        )
    ),

    // 5. Custom CRM Solutions & Automation
    ChittorService(
        id = "custom_crm",
        title = "Custom Vyapar CRM & Lead Pipelines",
        category = "Enterprise ERP/CRM",
        shortDescription = "Bespoke CRM platforms eliminating monthly subscription fees while centralizing leads, customer conversations, and sales funnels.",
        longDescription = "Stop paying thousands of dollars every month for rigid CRM tools. ChittorTech engineers custom, private CRM platforms built specifically around your sales workflow. Seamlessly track leads from website forms, WhatsApp, Facebook Ads, and phone calls, with automated follow-ups and performance metrics.",
        websiteSlug = "custom-crm-solutions",
        keyFeatures = listOf(
            "Multi-channel lead ingestion (Website, WhatsApp, Meta Ads)",
            "Automated WhatsApp follow-up templates & SMS dispatch",
            "Sales representative pipeline stages & deal tracking",
            "Zero per-seat licensing fees for unlimited team members"
        ),
        techStack = listOf("Jetpack Compose", "Next.js", "Firestore", "WhatsApp Cloud API", "FastAPI"),
        metrics = listOf(
            "45%" to "Lead Conversion Increase",
            "100%" to "Private Data Ownership",
            "Zero" to "Monthly Per-Seat Fees",
            "<2s" to "Lead Routing Speed"
        ),
        deliverables = listOf(
            "Multi-channel lead ingestion (Website, WhatsApp, Meta Ads, Google Forms)",
            "Automated WhatsApp follow-up templates & SMS notification dispatch",
            "Sales representative assignment, pipeline stages & deal value tracking",
            "Custom role hierarchies (Super Admin, Branch Manager, Sales Executive)",
            "Exportable Excel/PDF reports, revenue projections & audit logs"
        ),
        benefits = listOf(
            "Never Pay Per-User Licenses" to "Own your CRM completely with unlimited team members and zero SaaS billing.",
            "Tailored to Your Flow" to "Every stage matches your exact business model, not generic industry templates."
        ),
        plans = listOf(
            ServicePlan(
                name = "Team CRM",
                price = "₹55,000",
                description = "Private CRM for teams of up to 15 sales reps",
                features = listOf("Lead Management Pipeline", "WhatsApp Web Click-to-Chat", "Role Permissions", "Search & Filter Vault", "Daily Sales Summary Email")
            ),
            ServicePlan(
                name = "Automated Enterprise CRM",
                price = "₹1,40,000",
                description = "Advanced CRM with automated bots & telephony",
                features = listOf("Cloud Telephony (IVR / Click-to-Call)", "Automated WhatsApp Bot Follow-ups", "Multi-Branch Management", "Invoicing & Payment Status Sync", "Executive Mobile Companion App"),
                isPopular = true
            )
        ),
        faqs = listOf(
            "Can this replace Salesforce or HubSpot?" to "Yes! For businesses that want a fast, focused, and intuitive CRM without unnecessary bloat or costly monthly subscriptions."
        )
    ),

    // 6. Custom ERP & Billing Systems
    ChittorService(
        id = "custom_erp",
        title = "Custom Vyapar ERP & Billing Infrastructure",
        category = "Enterprise ERP/CRM",
        shortDescription = "Tailored business software replacing generic tools with bespoke inventory, financial ledger, and customer relationship pipelines.",
        longDescription = "Enterprise Resource Planning engineered for modern Indian and global businesses. Integrates smart GST e-invoicing, multi-location warehouse management, automated vendor payments, and live balance sheet reconciliations in real-time.",
        websiteSlug = "erp",
        keyFeatures = listOf(
            "GST-ready digital invoices, e-way bills & payment receipts",
            "Multi-warehouse real-time inventory tracking with barcode scanning",
            "Automated WhatsApp invoice dispatch & payment reminders",
            "Comprehensive P&L, balance sheet & cash flow analytics"
        ),
        techStack = listOf("Kotlin", "Jetpack Compose", "Firestore", "Tally API", "Cloud Functions", "PDF Engine"),
        metrics = listOf(
            "100%" to "GST Compliance",
            "Zero" to "Inventory Discrepancy",
            "Real-Time" to "Cloud Ledger Sync",
            "Multi-Store" to "Unified Control"
        ),
        deliverables = listOf(
            "GST-compliant invoices, debit/credit notes, quotations & e-way bill generation",
            "Multi-warehouse real-time inventory tracking with low-stock alerts",
            "Customer and vendor ledger accounting with auto-reconciled statements",
            "Barcode & QR scanning support for rapid POS billing",
            "Role-based branch administration with granular financial safeguards"
        ),
        plans = listOf(
            ServicePlan(
                name = "Vyapar Cloud ERP",
                price = "₹75,000",
                description = "Core ERP for retail, wholesale and distribution",
                features = listOf("POS Billing & Invoicing", "Multi-Location Inventory", "Customer/Supplier Ledgers", "Barcode Scanning Engine", "WhatsApp Invoice Dispatch")
            ),
            ServicePlan(
                name = "Enterprise Manufacturing ERP",
                price = "Custom Quote",
                description = "End-to-end ERP for factories & supply chains",
                features = listOf("Bill of Materials (BOM) & Work Orders", "Raw Material Procurement Workflow", "Batch & Expiry Tracking", "Tally & Accounting API Sync", "Dedicated Desktop + Mobile Apps"),
                isPopular = true
            )
        ),
        faqs = listOf(
            "Can I export data directly to Tally or Excel?" to "Yes, our ERP supports one-click export to Tally XML/Excel, as well as live bi-directional sync."
        )
    ),

    // 7. Cloud Hosting, DevOps & Infrastructure
    ChittorService(
        id = "cloud_hosting",
        title = "Cloud Infrastructure, DevOps & Edge",
        category = "Cloud & DevOps",
        shortDescription = "High-availability cloud architecture on AWS, Google Cloud, and Vercel with Cloudflare edge security and automated CI/CD.",
        longDescription = "Deploy mission-critical applications on global cloud infrastructure engineered for 99.99% uptime. We configure Docker containers, auto-scaling Kubernetes clusters, PostgreSQL database clustering, SSL certificates, and Cloudflare WAF protection against DDoS attacks.",
        websiteSlug = "cloud-hosting-deployment",
        keyFeatures = listOf(
            "AWS, GCP & Vercel cloud environment optimization",
            "Cloudflare Enterprise DNS, DDoS mitigation & SSL/TLS",
            "Docker containerization & CI/CD deployment pipelines",
            "Automated hourly database backups & disaster recovery"
        ),
        techStack = listOf("AWS", "Google Cloud", "Cloudflare", "Docker", "Kubernetes", "PostgreSQL", "GitHub Actions"),
        metrics = listOf(
            "99.99%" to "Target SLA Uptime",
            "<50ms" to "Edge CDN Latency",
            "AES-256" to "Data Encryption",
            "Zero" to "DDoS Vulnerability"
        ),
        deliverables = listOf(
            "AWS / GCP / Vercel cloud environment setup & cost optimization",
            "Cloudflare Enterprise DNS, DDoS mitigation & SSL/TLS certificate automation",
            "Docker containerization & CI/CD deployment pipelines (GitHub Actions)",
            "High-availability database replication & automated hourly backups",
            "Real-time server health monitoring, crash telemetry & alert triggers"
        ),
        benefits = listOf(
            "Cut Cloud Bills by 30-50%" to "We optimize server instances and eliminate unused cloud resources.",
            "Zero-Downtime Releases" to "Deploy code updates smoothly without interrupting active users."
        ),
        plans = listOf(
            ServicePlan(
                name = "Cloud Setup & Hardening",
                price = "₹25,000",
                description = "Turnkey server setup with security & SSL",
                features = listOf("Cloudflare DNS & WAF Setup", "SSL / TLS 1.3 Enforcement", "Automated Daily Backups", "Firewall & Port Hardening", "Domain & Email Records (DKIM/SPF)")
            ),
            ServicePlan(
                name = "Managed DevOps & Scaling",
                price = "₹45,000 / mo",
                description = "Continuous infrastructure management & uptime guarantee",
                features = listOf("24/7 Server Health Monitoring", "Load Balancing & Auto-Scaling", "Docker CI/CD Pipeline Maintenance", "Emergency Incident Response <15m", "Monthly Performance Optimization"),
                isPopular = true
            )
        ),
        faqs = listOf(
            "Which cloud providers do you support?" to "We architect solutions on AWS, Google Cloud Platform (GCP), DigitalOcean, Vercel, and Cloudflare Edge."
        )
    ),

    // 8. E-Commerce Platforms & Storefronts
    ChittorService(
        id = "ecommerce_platforms",
        title = "High-Conversion E-Commerce & Storefronts",
        category = "E-Commerce",
        shortDescription = "High-conversion online shopping platforms with lightning-fast UPI checkout, automated shipping labels, and real-time inventory.",
        longDescription = "Elevate your brand with custom e-commerce storefronts engineered for maximum conversion. Powered by headless Next.js or modern Shopify Plus, featuring one-click UPI checkout, automated abandoned cart recovery, and seamless integrations with Delhivery, Shiprocket, and Bluedart.",
        websiteSlug = "e-commerce-website-development",
        keyFeatures = listOf(
            "One-click UPI & Cards checkout reducing cart abandonment",
            "Shiprocket / Delhivery automated shipping label sync",
            "Discount coupons, tiered deals & product variant matrix",
            "Real-time multi-channel inventory reconciliation"
        ),
        techStack = listOf("Next.js", "Shopify Plus", "Razorpay", "Cashfree", "Shiprocket API", "TailwindCSS"),
        metrics = listOf(
            "3.2x" to "Higher Conversion Rate",
            "<1.5s" to "Checkout Completion",
            "100%" to "Mobile Optimized",
            "Auto" to "Shipping Label Sync"
        ),
        deliverables = listOf(
            "High-conversion product catalog with variant filters & instant search",
            "One-click UPI & Cards checkout reducing cart abandonment",
            "Shiprocket / Delhivery API integration for automated courier booking",
            "Coupon codes, tiered discounts, and bundle deal promotion engine",
            "Customer accounts, order tracking timeline & WhatsApp shipping alerts"
        ),
        plans = listOf(
            ServicePlan(
                name = "Direct-to-Consumer Store",
                price = "₹45,000",
                description = "Complete online store for growing brands",
                features = listOf("Custom Next.js / Shopify Design", "Payment Gateway (UPI/Cards)", "Automated Shipping Integration", "Discount & Coupon Engine", "Mobile Responsive Layout")
            ),
            ServicePlan(
                name = "Multi-Vendor Marketplace",
                price = "₹1,50,000",
                description = "Amazon/Flipkart style platform with vendor portal",
                features = listOf("Multi-Vendor Product Dashboards", "Automated Vendor Commission Payouts", "Advanced Search & Filters", "Customer Reviews & Q&A Vault", "Dedicated Mobile App Ready"),
                isPopular = true
            )
        ),
        faqs = listOf(
            "Can customers pay easily via Google Pay and PhonePe?" to "Yes, our checkout features native UPI intent flow allowing customers to pay in under 5 seconds directly from their phone."
        )
    ),

    // 9. Technical SEO & Search Engine Optimization
    ChittorService(
        id = "seo_growth",
        title = "Technical SEO & Search Optimization",
        category = "SEO & Growth",
        shortDescription = "Data-driven organic search ranking, Core Web Vitals acceleration, and schema architecture to capture high-intent commercial keywords.",
        longDescription = "Dominate Google search results for your high-value commercial keywords. We execute comprehensive technical SEO audits, implement structured JSON-LD schema markup, accelerate Core Web Vitals, and optimize your Google Business Profile (GMB) for local and national dominance.",
        websiteSlug = "search-engine-optimization",
        keyFeatures = listOf(
            "Schema.org markup (Organization, Service, FAQ, LocalBusiness)",
            "Google Search Console & Bing Webmaster indexing optimization",
            "Local map rank enhancement via Google Business Profile (GMB)",
            "High-authority B2B backlink acquisition & tech content strategy"
        ),
        techStack = listOf("Technical SEO", "Schema.org", "GMB", "Search Console", "PageSpeed Insights", "Ahrefs"),
        metrics = listOf(
            "Top 3" to "Target Google Rankings",
            "+240%" to "Average Organic Traffic",
            "100%" to "White-Hat Practices",
            "Zero" to "Algorithmic Penalties"
        ),
        deliverables = listOf(
            "Complete technical SEO audit resolving crawl errors, broken links & redirects",
            "Schema.org structured data (Organization, LocalBusiness, FAQ, Product)",
            "Core Web Vitals optimization achieving green scores on Google PageSpeed",
            "Google Business Profile (GMB) optimization for local map pack dominance",
            "Targeted keyword research & competitive search gap analysis"
        ),
        plans = listOf(
            ServicePlan(
                name = "Local Business SEO",
                price = "₹18,000 / mo",
                description = "Dominate local search & Google Maps in your region",
                features = listOf("Google Business Profile Optimization", "Local Citation Building", "Target 10 Commercial Keywords", "Monthly Keyword Ranking Reports", "On-Page Metadata Tuning")
            ),
            ServicePlan(
                name = "National Authority SEO",
                price = "₹38,000 / mo",
                description = "Comprehensive organic growth for competitive industries",
                features = listOf("Target 30 High-Intent Keywords", "Technical Core Web Vitals Audits", "High-Authority Tech Backlinks", "Content & Schema Optimization", "Competitor Keyword Hijacking"),
                isPopular = true
            )
        ),
        faqs = listOf(
            "How long does it take to see SEO results?" to "Typical technical fixes show ranking improvements within 3 to 6 weeks, with substantial organic traffic growth achieved over 3 to 6 months."
        )
    ),

    // 10. Government Accreditations & Trust Center
    ChittorService(
        id = "trust_center",
        title = "Government Accreditations & Compliance",
        category = "Govt & Startup Compliance",
        shortDescription = "Turnkey corporate and legal certifications enabling startup tax holidays, zero-rated foreign software exports, and platform verification.",
        longDescription = "Official corporate compliance and government accreditations handled by experts. From DGFT Import Export Code (IEC) enabling foreign wire transfer settlement to DPIIT Startup India 3-year income tax holiday, iStart Rajasthan mentorship, and Dun & Bradstreet D-U-N-S registration.",
        websiteSlug = "trust-center",
        keyFeatures = listOf(
            "DGFT IEC Code setup for legal USD/EUR foreign wire settlement",
            "DPIIT Startup India recognition & 3-year tax exemptions",
            "iStart Rajasthan incubation mentorship & Q-Rate scorecard",
            "MSME / Udyam Enterprise 24-hr registration & payment protection"
        ),
        techStack = listOf("DGFT IEC", "DPIIT", "iStart", "MSME Samadhaan", "D-U-N-S", "GST LUT"),
        popularBadge = "GOVT RECOGNIZED",
        metrics = listOf(
            "OTWPS1188A" to "DGFT IEC Code",
            "32 Points" to "iStart Q-Rate",
            "100%" to "Legal Compliance",
            "24-48h" to "Fast Track Filing"
        ),
        deliverables = listOf(
            "DGFT IEC Code setup for legal USD/EUR foreign wire settlement & bank FIRC",
            "DPIIT Startup India recognition & 3-year income tax exemptions (Section 80-IAC)",
            "iStart Rajasthan incubation mentorship & Q-Rate scorecard certification",
            "MSME / Udyam Enterprise 24-hr registration & delayed payment protection",
            "Dun & Bradstreet (D-U-N-S) registration guidance for Apple Developer & Google Play"
        ),
        plans = listOf(
            ServicePlan(
                name = "Startup Compliance Bundle",
                price = "₹15,000",
                description = "Essential legal foundation for tech startups",
                features = listOf("DGFT IEC Registration", "MSME / Udyam Certification", "Startup India DPIIT Application", "Bank FIRC Guidance", "Consultation with Founders")
            ),
            ServicePlan(
                name = "Global Corporate Desk",
                price = "₹35,000",
                description = "Full enterprise compliance for international operations",
                features = listOf("All Startup Certifications", "D-U-N-S Number Guidance", "GST & LUT Filing for Export", "Trademark & Brand Protection", "Annual Compliance Calendar"),
                isPopular = true
            )
        ),
        faqs = listOf(
            "Why do I need a DGFT IEC code?" to "DGFT IEC is mandatory in India to legally receive foreign remittances (USD, EUR, GBP) from international software clients and Google Play/Apple Developer payouts."
        )
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
    var selectedServiceDetail by remember { mutableStateOf<ChittorService?>(null) }
    var selectedServiceForModal by remember { mutableStateOf<ChittorService?>(null) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    // If a service detail page is selected, display the full dedicated screen!
    if (selectedServiceDetail != null) {
        ServiceDetailScreen(
            service = selectedServiceDetail!!,
            onBack = { selectedServiceDetail = null },
            onEnquire = {
                selectedServiceForModal = selectedServiceDetail
            }
        )
        return
    }

    val categories = remember {
        listOf("All", "Mobile Apps", "AI & Automation", "Google Play Launch", "Web & SaaS", "Enterprise ERP/CRM", "Cloud & DevOps", "E-Commerce", "SEO & Growth", "Govt & Startup Compliance")
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
            // ── 1. Hero Banner: ChittorTech Agency Profile ────────────────────
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
                                text = "Every service features a dedicated architectural page, deliverables breakdown, and real client FAQs. Tap any card below to explore.",
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
                                StatBadge(value = "250+", label = "Delivered")
                                StatBadge(value = "100%", label = "Approval")
                                StatBadge(value = "4.9★", label = "Rating")
                                StatBadge(value = "DGFT", label = "IEC Certified")
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
                                    border = BorderStroke(1.dp, Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Icon(Icons.Outlined.Language, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("chittortech.in", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // ── 2. Category Filter Chips ──────────────────────────────────────
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = (selectedCategory == category),
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VyaparBlue,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = VyaparDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = (selectedCategory == category),
                                borderColor = Color(0xFFE2E8F0),
                                selectedBorderColor = VyaparBlue,
                                borderWidth = 1.dp
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // ── 3. Services List Cards ────────────────────────────────────────
            items(filteredServices, key = { it.id }) { service ->
                ServiceCard(
                    service = service,
                    onClick = { selectedServiceDetail = service },
                    onEnquire = { selectedServiceForModal = service },
                    onWhatsApp = {
                        val msg = Uri.encode("Hello ChittorTech, I am inquiring about ${service.title}.")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/917597451057?text=$msg"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // ── 4. Lead Inquiry Dialog Modal ──────────────────────────────────────
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

        // ── 5. Instant Success Notification ───────────────────────────────────
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
    onClick: () -> Unit,
    onEnquire: () -> Unit,
    onWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                service.keyFeatures.take(3).forEach { feature ->
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
                            lineHeight = 15.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
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

            Spacer(modifier = Modifier.height(12.dp))

            // Dedicated Page Banner Button
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Explore Dedicated Page & Architecture",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

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

// ── Stat Badge ───────────────────────────────────────────────────────────────

@Composable
private fun StatBadge(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF38BDF8)
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = Color(0xFF94A3B8)
        )
    }
}

// ── Service Inquiry Dialog ───────────────────────────────────────────────────

@Composable
private fun ServiceInquiryDialog(
    service: ChittorService,
    onDismiss: () -> Unit,
    onSubmit: (LeadInquiry) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Request Consultation",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyaparDark
                )
                Text(
                    text = service.title,
                    fontSize = 12.sp,
                    color = VyaparBlue
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
                    label = { Text("Your Name / Organization", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone / WhatsApp *", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Project Details / Requirements", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3,
                    maxLines = 5
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Shield, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Direct founder audit by Kush Sharma & Lav Sharma. We reply within 24 hours.",
                            fontSize = 10.5.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (phone.isNotBlank()) {
                        isSubmitting = true
                        val lead = LeadInquiry(
                            name = name,
                            email = email,
                            contact = phone,
                            service = service.title,
                            message = message
                        )
                        onSubmit(lead)
                    }
                },
                enabled = phone.isNotBlank() && !isSubmitting,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VyaparBlue)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Submit Inquiry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp, color = Color(0xFF64748B))
            }
        },
        shape = RoundedCornerShape(18.dp),
        containerColor = Color.White
    )
}
