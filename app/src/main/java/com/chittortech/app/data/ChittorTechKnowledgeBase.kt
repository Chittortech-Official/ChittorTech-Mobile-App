package com.chittortech.app.data

/**
 * ChittorTech — Comprehensive AI Knowledge Base
 * Mirrors official chittortechKnowledgeBase.js (website Source of Truth).
 *
 * ARCHITECTURE:
 *  - buildSystemPrompt(currentScreen, userName): Context-aware system prompt injection.
 *  - MASTER_KNOWLEDGE_BASE: Full 31-section canonical knowledge base.
 *  - getLocalFallbackResponse(): Rich offline fallback (15+ intent categories).
 *  - SYSTEM_PROMPT: Legacy alias -> buildSystemPrompt().
 */
object ChittorTechKnowledgeBase {

    // ── Master Knowledge Base ─────────────────────────────────────────────────
    private val MASTER_KNOWLEDGE_BASE = """
=== CHITTORTECH MASTER KNOWLEDGE BASE (SOURCE OF TRUTH) ===

0. CHATBOT MASTER IDENTITY
- Official Identity: I am ChittorTech GPT, the official AI assistant for ChittorTech. I help with AI solutions, software development, websites, mobile apps, RAG/LLM systems, enterprise software, SEO, Google Play publishing, and custom tech.
- Positioning: AI & software engineering company serving startups, SMEs, and enterprises.

1. COMPANY OVERVIEW & CONTACT
- Brand: ChittorTech
- Headquarters: Collectorate Circle, Chittorgarh, Rajasthan 312001, India.
- Phone / WhatsApp: +91 7597451057 | Kush Sharma (Founder & AI Specialist), Lav Sharma (Co-Founder & Tech Lead).
- Business Email: business@chittortech.in | General: contact@chittortech.in
- Vision: India most trusted AI engineering company.
- Core Strengths: 250+ projects, 99.8% satisfaction, 4.8+ rating. AI Engineering, Software Engineering, Security.
- Regions: India (Chittorgarh, Jaipur, Delhi, Bengaluru, Chennai, Jodhpur) & International (USA, UK, UAE, Saudi Arabia, Canada, Australia, Germany, Netherlands, Singapore, Turkey).

1.1. TRUST CENTER & GOVERNMENT ACCREDITATIONS
- Trust Center URL: https://chittortech.in/trust-center
- DGFT IEC Code: OTWPS1188A (zero-rated software exports, USD/EUR/GBP wire settlements).
- DPIIT Startup India: Officially recognized (80% patent / 50% trademark fee rebates, tax holidays).
- iStart Rajasthan: Q-Rate Score 32, Profile ID #11478.
- MSME / Udyam: 45-day buyer payment protection.
- Google Play Console Developer: Identity-verified, 12-tester 14-day compliance.
- Apple Developer Program: Enrollment in process.
- D-U-N-S: In progress. GST & LUT Zero-Rated Export Compliance.
- Turnkey Compliance Services for Clients: DGFT IEC, DPIIT, iStart, MSME/Udyam, Google Play Account, D-U-N-S, GST+LUT, GoodFirms/Clutch/GMB, LinkedIn Company Page, GoDaddy+Titan Business Email.

2. PRICING POLICY (MANDATORY)
- RULE: NEVER give static/fixed prices for web/software/IT/ERP/CRM/SEO.
- ALWAYS say: Our solutions are fully customized. Contact us at +91 7597451057 or business@chittortech.in for a personalized quotation.
- EXCEPTION - Google Play Publishing fixed packages:
  1. Publish on Your Account: Rs 10,299 / USD 129
  2. Publish on ChittorTech Account: Rs 25,999 / USD 299
  3. Full Account Setup & Launch: Rs 29,499 / USD 339

3. CORE AI SOLUTIONS
- AI Chatbots & Support Agents: Conversational AI, lead qualification, WhatsApp AI, multilingual (English, Hindi, Hinglish), CRM/API triggers.
- RAG Knowledge Base & Enterprise Search: PDF/DOCX/XLSX/Web ingestion, Vector DB (Pinecone, Weaviate, pgvector), Cited Answers. Timeline: 2-4 weeks standard, 6-8 weeks complex.
- Custom LLM Fine-Tuning, Agentic Workflows, Enterprise AI Automation.
- OCR & Document AI: Invoices, medical records, contracts, historical docs.
- Computer Vision: Retail detection, footfall heatmaps, quality inspection.
- Predictive Analytics: Sales forecasting, inventory demand, predictive maintenance.

4. WEB DEVELOPMENT & DESIGN
- Services: Custom websites, SaaS, corporate portals, CMS, e-commerce.
- Tech: React, Next.js, TypeScript, Node.js, Python, PostgreSQL, MongoDB, AWS, GCP, Firebase, Cloudflare, Vercel.
- Process: Discovery -> SRS -> UI/UX -> Agile Dev -> QA -> UAT -> Deployment -> Maintenance.

5. E-COMMERCE
- Payment gateways: Stripe, PayPal, Razorpay, Square. Shipping: FedEx, DHL, UPS.
- Platforms: Shopify, WooCommerce, Magento, Custom. Timeline: 4-12 weeks.

6. MOBILE APP DEVELOPMENT
- Native Android: Kotlin, Jetpack Compose, Android SDK.
- Native iOS: Swift, SwiftUI, App Store.
- Cross-Platform: Flutter, React Native, Expo Go.

7. SOFTWARE & ENTERPRISE SOLUTIONS
- SaaS, ERP, CRM, dashboards, billing, inventory.
- CRM: Lead pipeline, WhatsApp follow-up automation, quotation builder.
- ERP: Finance, inventory, manufacturing, HR, sales, customer support.
- GST Billing: E-invoices, E-way bills, P&L, Cash flow, Tally sync.

8. GOOGLE PLAY STORE PUBLISHING
- 12-Tester 14-Day Rule: 100% compliant with Google mandatory policy for personal accounts.
- Milestone: 50+ apps published globally, 100% first-attempt approval.
- NDA & Privacy: Client apps never showcased without written permission.
- Reference Apps: kunh (tech.kunh.app), Reward Club (com.rewardclub.app), Visit Chittorgarh (com.kushsharma.visitchittorgarh), Mewari Achaar (com.mewari.achaar).
- Free 15-min SDK 34+/35 compliance audit before submission.
- Excluded: Real-money gambling, predatory loans, malware, plagiarized apps.

9. SEO & DIGITAL MARKETING
- On-page, Technical SEO (Core Web Vitals, Schema), Off-page (Backlinks), Local SEO (Google Maps), E-commerce SEO.
- Google Ads, Bing Ads, PPC, Social Media (FB, Instagram, LinkedIn, YouTube), Email marketing.

10. MAINTENANCE, SUPPORT & CLOUD
- AWS, GCP, Firebase, Cloudflare CDN, SSL, CI/CD DevOps, 24/7 monitoring.

11. DEDICATED ENGINEERING TEAMS
- Pre-vetted pods, sprint-based, NDA, 100% IP assignment. 72-hr onboarding, 2-week trial.

12. INTERNATIONAL ENGINEERING
- USA, UK, UAE, Saudi Arabia, Canada, Australia, Germany, Netherlands, Singapore, Turkey. NDA & IP protection.

13. INDUSTRY VERTICALS
- E-Commerce, Healthcare, Manufacturing, Logistics, BFSI/Fintech, EdTech, Legal, Real Estate, Hospitality, Automotive, Sports, Media, Electronics, Kirana/FMCG, B2B Enterprise.
- Temple/Dharamshala: Donation receipts, Seva booking, Bhojanshala coupons, Room check-in/out.

14. SMART RETAIL & COMPUTER VISION
- Smart carts, self-checkout, product detection, footfall heatmaps, virtual try-on mirrors.

15-18. LIFECYCLE, SCOPE, IP & PRIVACY
- Lifecycle: Discovery -> SRS -> Architecture -> Agile Dev -> Testing -> Deployment -> Support.
- IP: 100% source code & commercial IP to client on full payment.
- Privacy: DPDP Act 2023, IT Act, GDPR. Encryption, RBAC, Private VPC, Air-gapped options.

19. CONSULTATION & DEMO WORKFLOW
- For project requests collect: Name, Company, Industry, City, Project Type, Features, Timeline, Phone, Email.
- Always direct to: WhatsApp +91 7597451057 or email business@chittortech.in.

20-29. PORTFOLIO & TESTIMONIALS
- Mewari Achaar: mewari-achar.shop, Play: com.mewari.achaar
- kunh: tech.kunh.app (Turkey/Global, Production Live)
- Visit Chittorgarh: com.kushsharma.visitchittorgarh (Live)
- Shaadi Sutra: shaadi-sutra.vercel.app
- Dharamshala Admin Portal: dharamsala-admin-portal.vercel.app
- Testimonials: Vijay Laxmi Sharma, Ayush Sharma (BrowserStack), Nisha Singh, Muskan Falwaria, Priyanka Vyas.

30. RESPONSE RULES
- No hallucination. No static pricing. No guaranteed timelines or SEO rankings. No direct meeting scheduling.
- Do NOT reveal built on OpenAI/Meta/Groq. You are ChittorTech GPT.
- For scheduling/demos: always use [ACTION:SCHEDULE] or [ACTION:DEMO].

31. EMAIL DELIVERABILITY & CLOUD INFRASTRUCTURE
- Email Deliverability: SPF, DKIM, DMARC, Google Postmaster, spam-trap elimination, IP warmup.
- DMARC/DKIM/SPF Setup: 2048-bit DKIM, SPF flattening, DMARC p=reject, RUA/RUF reporting.
- BIMI Verified Branding: Logo in Gmail/Yahoo/Apple Mail, SVG Tiny-PS, VMC/CMC certificate.
- DNS & Cloudflare: DNSSEC, WAF, DDoS mitigation, zero-downtime migration.
- Blacklist Removal: Spamhaus, Barracuda, SpamCop, Microsoft SNDS 550 5.7.1 triage.
- Cloud Hosting: AWS, GCP, Azure, Vercel/Next.js, Docker, GitHub Actions CI/CD, PgBouncer, SSL, Linux hardening.
""".trimIndent()

    // ── Context-Aware System Prompt Builder ──────────────────────────────────
    fun buildSystemPrompt(
        currentScreen: String = "ChittorTech Mobile App",
        userName: String = "Guest"
    ): String = """
You are ChittorTech GPT, the official AI assistant and Customer Support Executive for ChittorTech inside the official mobile application.

CURRENT VISITOR CONTEXT:
- Platform: Mobile App (Android/iOS)
- Active Screen: ${'$'}currentScreen
- Visitor Name: ${'$'}userName
- Screen Focus: Answer queries respectfully, crisp, relevant to what the visitor is viewing.

CRITICAL RESPONSE FORMAT RULES:
1. TABLES: Strictly 2 columns (e.g. | Module | Highlights |), max 4 rows. Use tables for module/service comparisons.
2. WHY CHITTORTECH: Exactly 3 punchy bullet points, 1 sentence each.
3. NEXT STEPS: Always end with a friendly closing sentence and action tags. NEVER leave dangling words like 'or' or 'and' before the tags. Output clean like: "Ready to discuss your project? Let's connect! [ACTION:WHATSAPP] [ACTION:CONTACT]".
4. PRICING: NEVER give fixed prices for custom work. Exception: Google Play packages (Rs10299/Rs25999/Rs29499).
5. BREVITY: Mobile-first, crisp, no scrolling fatigue.
6. IDENTITY: Do NOT say you are OpenAI/Meta/Groq. You are ChittorTech GPT.
7. ACTION TAGS (app converts these to native buttons):
   [ACTION:WHATSAPP]   -> WhatsApp chat with team
   [ACTION:CONTACT]    -> Phone dialer
   [ACTION:DEMO]       -> Live demo request sheet
   [ACTION:SCHEDULE]   -> Schedule a call
   [ACTION:ESTIMATOR]  -> Project cost estimator

OFFICIAL CHITTORTECH KNOWLEDGE BASE (SOURCE OF TRUTH):
${'$'}{MASTER_KNOWLEDGE_BASE}
""".trimIndent()

    // ── Legacy alias ──────────────────────────────────────────────────────────
    val SYSTEM_PROMPT: String get() = buildSystemPrompt()

    // ── Rich Local Fallback (offline / API unreachable) ───────────────────────
    fun getLocalFallbackResponse(query: String): String {
        val q = query.lowercase().trim()
        return when {
            (q.contains("price") || q.contains("cost") || q.contains("rate") || q.contains("quote") || q.contains("quotation") || q.contains("charges")) -> {
                if (q.contains("play") || q.contains("publish") || q.contains("tester")) {
                    "**Google Play Publishing — Official Packages:**\n\n" +
                    "1. **Publish on Your Account**: Rs 10,299 / \$129 USD\n   (12 testers × 14 days, console audit, SDK 34+ check, privacy policy)\n" +
                    "2. **Publish on ChittorTech Account**: Rs 25,999 / \$299 USD\n   (No console needed, enterprise keys, lifetime support)\n" +
                    "3. **Full Account Setup & Launch**: Rs 29,499 / \$339 USD\n   (Console registration + D-U-N-S + testers + live launch)\n\n" +
                    "Ready to publish? [ACTION:WHATSAPP]"
                } else {
                    "Our web development, software, and enterprise AI solutions at ChittorTech are **fully customized** based on your exact project scope.\n\n" +
                    "Contact Kush Sharma or Lav Sharma for a **personalized quotation**:\n📞 +91 7597451057\n📧 business@chittortech.in [ACTION:WHATSAPP]"
                }
            }
            (q.contains("website") || q.contains("web dev") || q.contains("web app") || q.contains("saas") || q.contains("frontend") || q.contains("backend")) -> {
                "**ChittorTech Web Engineering:**\n\n" +
                "• **Stack:** Next.js 15, React, TypeScript, Node.js, Python, PostgreSQL, Supabase, Cloudflare\n" +
                "• **Solutions:** Corporate sites, SaaS platforms, E-commerce portals, Admin dashboards\n" +
                "• **Delivery:** Agile sprints, live previews, sub-second Core Web Vitals\n\n" +
                "**Why ChittorTech?**\n" +
                "• 250+ projects, 99.8% satisfaction rate.\n" +
                "• 100% commercial IP transfer to you.\n" +
                "• DPIIT & iStart government-recognized company.\n\n" +
                "Let's discuss your project! [ACTION:WHATSAPP]"
            }
            (q.contains("mobile") || q.contains("android") || q.contains("ios") || (q.contains("app") && !q.contains("play"))) -> {
                "**ChittorTech Mobile App Development:**\n\n" +
                "• **Native Android:** Kotlin, Jetpack Compose, Material 3\n" +
                "• **Native iOS:** Swift, SwiftUI, App Store\n" +
                "• **Cross-Platform:** Flutter, React Native\n" +
                "• **Google Play Publishing:** 12-tester 14-day guarantee\n\n" +
                "Build or publish your app today! [ACTION:WHATSAPP]"
            }
            (q.contains("play") || q.contains("publish") || q.contains("tester") || q.contains("google play")) -> {
                "**ChittorTech Google Play 12-Tester Division:**\n\n" +
                "• 50+ apps published globally, 100% first-attempt approval\n" +
                "• 100% verified real human testers, daily telemetry\n" +
                "• Free 15-min SDK 34+ & privacy policy audit\n\n" +
                "**Packages:** Rs10,299 / Rs25,999 / Rs29,499 [ACTION:WHATSAPP]"
            }
            (q.contains("demo") || q.contains("live demo")) -> {
                "I'd love to arrange a live demo!\n\n" +
                "Our team will show you:\n" +
                "• AI chatbot & RAG system in action\n" +
                "• Web/mobile portfolio samples\n" +
                "• ERP/CRM workflows for your industry\n\n" +
                "Request your free demo now! [ACTION:DEMO]"
            }
            (q.contains("call") || q.contains("phone") || q.contains("dial") || q.contains("schedule") || q.contains("book")) -> {
                "**Connect directly with Kush & Lav Sharma:**\n\n" +
                "📞 **Phone / Call:** +91 7597451057\n" +
                "💬 **WhatsApp:** +91 7597451057\n" +
                "📧 **Email:** business@chittortech.in\n\n" +
                "Tap below to call our tech team or chat on WhatsApp! [ACTION:CONTACT] [ACTION:WHATSAPP]"
            }
            (q.contains("ai") || q.contains("rag") || q.contains("llm") || q.contains("gpt") || q.contains("chatbot") || q.contains("agent")) -> {
                "**ChittorTech Enterprise AI & Automation:**\n\n" +
                "• **AI Chatbots:** Groq LPU powered, sub-second response\n" +
                "• **Private RAG:** Pinecone/pgvector, cited answers on your data\n" +
                "• **Autonomous Agents:** Document OCR, invoice parsing, sales outreach\n" +
                "• **WhatsApp AI:** 24/7 automated lead capture & booking\n\n" +
                "See it live! [ACTION:DEMO]"
            }
            (q.contains("erp") || q.contains("crm") || q.contains("billing") || q.contains("dharamshala") || q.contains("inventory")) -> {
                "**ChittorTech ERP, CRM & Billing Systems:**\n\n" +
                "• **CRM:** WhatsApp follow-ups, quotation generator, deal pipeline\n" +
                "• **Temple/Dharamshala:** Donation receipts, Seva booking, Bhojanshala coupons\n" +
                "• **GST Billing:** E-invoices, E-way bills, Tally sync, multi-store inventory\n\n" +
                "[ACTION:WHATSAPP]"
            }
            (q.contains("seo") || q.contains("digital marketing") || q.contains("google ads")) -> {
                "**ChittorTech SEO & Digital Marketing:**\n\n" +
                "• Technical SEO: Core Web Vitals, Schema, Indexing\n" +
                "• Local SEO: Google Maps ranking\n" +
                "• Performance Marketing: Google Ads, Meta Ads, PPC\n" +
                "• Social: LinkedIn B2B, Instagram, YouTube, Email\n\n" +
                "[ACTION:WHATSAPP]"
            }
            (q.contains("email") || q.contains("dmarc") || q.contains("dkim") || q.contains("spf") || q.contains("blacklist") || q.contains("deliverability")) -> {
                "**ChittorTech Email Deliverability & Infrastructure:**\n\n" +
                "• DMARC/DKIM/SPF setup, 2048-bit DKIM, SPF flattening\n" +
                "• Blacklist removal: Spamhaus, Barracuda, SpamCop\n" +
                "• BIMI verified brand logo in Gmail & Yahoo\n" +
                "• Cloudflare WAF, DDoS mitigation, DNS management\n\n" +
                "[ACTION:WHATSAPP]"
            }
            (q.contains("trust") || q.contains("dpiit") || q.contains("istart") || q.contains("msme") || q.contains("iec") || q.contains("udyam")) -> {
                "**ChittorTech Government Accreditations:**\n\n" +
                "• **DPIIT Startup India** — 80% patent fee rebates, tax holidays\n" +
                "• **iStart Rajasthan** — Q-Rate Score: 32, Profile #11478\n" +
                "• **DGFT IEC:** OTWPS1188A — USD/EUR/GBP exports\n" +
                "• **MSME/Udyam** — 45-day payment protection\n\n" +
                "We deliver these registrations for clients too! [ACTION:WHATSAPP]"
            }
            (q.contains("portfolio") || q.contains("work") || q.contains("sample")) -> {
                "**ChittorTech Portfolio:**\n\n" +
                "• **Mewari Achaar** — E-commerce (mewari-achar.shop)\n" +
                "• **kunh** — Global client app (tech.kunh.app), Turkey, Production Live\n" +
                "• **Visit Chittorgarh** — Tourism app, Live on Play Store\n" +
                "• **Shaadi Sutra** — Wedding SaaS (shaadi-sutra.vercel.app)\n\n" +
                "See live demos! [ACTION:DEMO]"
            }
            (q.contains("contact") || q.contains("phone") || q.contains("whatsapp") || q.contains("address") || q.contains("reach")) -> {
                "**Connect with ChittorTech:**\n\n" +
                "📞 Phone / WhatsApp: +91 7597451057\n" +
                "📧 Business: business@chittortech.in\n" +
                "📧 General: contact@chittortech.in\n" +
                "📍 Collectorate Circle, Chittorgarh, Rajasthan 312001\n" +
                "🌐 https://chittortech.in [ACTION:WHATSAPP]"
            }
            (q.contains("about") || q.contains("who") || q.contains("founder") || q.contains("chittortech")) -> {
                "**ChittorTech** — AI & Software Engineering, Chittorgarh, Rajasthan.\n\n" +
                "• Founded by **Kush Sharma** (AI Specialist) & **Lav Sharma** (Tech Lead)\n" +
                "• 250+ projects, 99.8% satisfaction, 4.8+ rating\n" +
                "• DPIIT Startup India + iStart Rajasthan + DGFT + MSME recognized\n\n" +
                "[ACTION:WHATSAPP]"
            }
            (q.contains("hi") || q.contains("hello") || q.contains("hey") || q.contains("namaste")) -> {
                "Namaste! 🙏 I'm **ChittorTech GPT**, your official AI Assistant.\n\n" +
                "Ask me about:\n" +
                "• Website & SaaS Development\n" +
                "• Android & iOS Mobile Apps\n" +
                "• Google Play 12-Tester Publishing\n" +
                "• Enterprise AI & RAG Systems\n" +
                "• ERP, CRM & GST Billing\n" +
                "• SEO & Digital Marketing\n\n" +
                "How can I help? [ACTION:WHATSAPP]"
            }
            else -> {
                "ChittorTech delivers Enterprise AI, Web & SaaS, Mobile Apps, Google Play Publishing, ERP/CRM, and Business Automation.\n\nWould you like a free technical consultation? [ACTION:WHATSAPP]"
            }
        }
    }
}
