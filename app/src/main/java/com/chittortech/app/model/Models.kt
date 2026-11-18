package com.chittortech.app.model

import com.google.firebase.Timestamp

// ─────────────────────────────────────────────────────────────────────────────
// User / Auth Models
// ─────────────────────────────────────────────────────────────────────────────

data class CtUser(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val companyName: String = "",
    val role: String = "client", // "client" | "admin"
    val phone: String = "",
    val createdAt: Timestamp? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// Project / Infrastructure Model
// ─────────────────────────────────────────────────────────────────────────────

data class Project(
    val projectId: String = "",
    val clientId: String = "",
    val name: String = "",
    val domain: String = "",
    val domainRegistrar: String = "",
    val domainRegistrarEmail: String = "",
    val domainRegistrar2FA: String = "",
    val domainExpiryDate: String = "",
    val hostingProvider: String = "",
    val hostingServerIp: String = "",
    val hostingPanelLogin: String = "",
    val hostingSpecs: String = "",
    val hostingExpiryDate: String = "",
    val sslStatus: String = "Active",
    val githubRepo: String = "",
    val buildKickoffDate: String = "",
    val launchDate: String = "",
    val annualRenewalFee: Long = 0L,
    val status: String = "Live & Active",
    val currentPhase: String = "",
    val milestoneProgress: Int = 0,
    val sowPdfUrl: String = "",
    val type: String = "",
    val platform: String = "",
    val targetKeywords: String = ""
)

enum class ProjectCategory(val label: String, val badgeColorHex: Long) {
    WEBSITE_SAAS("Web / SaaS", 0xFF0284C7),
    MOBILE_APP("Mobile App", 0xFF10B981),
    SEO_MARKETING("SEO & Marketing", 0xFFF59E0B),
    AI_SOLUTIONS("AI & Machine Learning", 0xFF8B5CF6),
    AI_CHATBOT("AI & WhatsApp Chatbot", 0xFF06B6D4),
    ENTERPRISE_ERP("Enterprise ERP / CRM", 0xFF6366F1)
}

fun Project.getCategory(): ProjectCategory {
    val t = type.lowercase().trim()
    val n = name.lowercase().trim()
    val p = platform.lowercase().trim()
    val d = domain.lowercase().trim()
    return when {
        // 1. Direct explicit type matching (matching Admin Portal 100%)
        t == "website / web saas" || t == "web / saas" || t.contains("website") || t.contains("saas") || (t.contains("web") && !t.contains("mobile")) ->
            ProjectCategory.WEBSITE_SAAS
        t == "mobile app (android / ios)" || t == "mobile app" || t.contains("mobile") ->
            ProjectCategory.MOBILE_APP
        t == "seo & digital marketing" || t == "seo & marketing" || t.contains("seo") || t.contains("marketing") ->
            ProjectCategory.SEO_MARKETING
        t == "ai & machine learning" || (t.contains("ai") && !t.contains("chatbot")) || t.contains("machine learning") ->
            ProjectCategory.AI_SOLUTIONS
        t == "ai & whatsapp chatbot" || t.contains("chatbot") || t.contains("bot") ->
            ProjectCategory.AI_CHATBOT
        t == "enterprise software / erp" || t.contains("erp") || t.contains("crm") ->
            ProjectCategory.ENTERPRISE_ERP

        // 2. Name-based heuristics (if legacy data)
        n.contains("website") || n.contains("saas") || n.contains("web ") ->
            ProjectCategory.WEBSITE_SAAS
        n.contains("seo") || n.contains("marketing") ->
            ProjectCategory.SEO_MARKETING
        n.contains("chatbot") || n.contains("bot") ->
            ProjectCategory.AI_CHATBOT
        n.contains("ai") || n.contains("ml") || n.contains("rag") ->
            ProjectCategory.AI_SOLUTIONS
        n.contains("erp") || n.contains("crm") || n.contains("vyapar") || n.contains("billing") ->
            ProjectCategory.ENTERPRISE_ERP
        n.contains("mobile app") || n.contains("apk") || d.contains("play.google.com") || ((p.contains("android") || p.contains("ios")) && !n.contains("website")) ->
            ProjectCategory.MOBILE_APP

        else -> ProjectCategory.WEBSITE_SAAS
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Invoice Model
// ─────────────────────────────────────────────────────────────────────────────

data class Invoice(
    val invoiceId: String = "",
    val clientId: String = "",
    val projectId: String = "",
    val projectName: String = "",
    val title: String = "",
    val lineItems: List<InvoiceLineItem> = emptyList(),
    val amount: Long = 0L,
    val status: String = "UNPAID", // "PAID" | "UNPAID" | "OVERDUE"
    val dueDate: String = "",
    val invoicePdfUrl: String = "",
    val signedSowUrl: String? = null,
    val createdAt: Timestamp? = null,
    val invoiceDate: String = "",
    val paymentMode: String = "Bank Transfer",
    val paymentInfo: String = "",
    val termsText: String = ""
)

data class InvoiceLineItem(
    val description: String = "",
    val amount: Long = 0L
)

// ─────────────────────────────────────────────────────────────────────────────
// Support Ticket Model
// ─────────────────────────────────────────────────────────────────────────────

data class SupportTicket(
    val ticketId: String = "",
    val clientId: String = "",
    val projectId: String = "",
    val clientName: String = "",
    val companyName: String = "",
    val title: String = "",
    val category: String = "",
    val priority: String = "MEDIUM", // "LOW" | "MEDIUM" | "HIGH" | "URGENT"
    val status: String = "OPEN", // "OPEN" | "IN_PROGRESS" | "RESOLVED"
    val description: String = "",
    val resolutionNote: String = "",
    val attachmentUrl: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// Admin Dashboard KPI Model
// ─────────────────────────────────────────────────────────────────────────────

data class AdminKpi(
    val totalMonthlyInvoiced: Long = 0L,
    val totalOutstandingDues: Long = 0L,
    val activeDeployments: Int = 0,
    val openTickets: Int = 0
)

// ─────────────────────────────────────────────────────────────────────────────
// Website Service & Lead Models
// ─────────────────────────────────────────────────────────────────────────────

data class ServicePlan(
    val name: String,
    val price: String,
    val description: String = "",
    val features: List<String> = emptyList(),
    val isPopular: Boolean = false
)

data class ChittorService(
    val id: String,
    val title: String,
    val category: String,
    val shortDescription: String,
    val keyFeatures: List<String>,
    val techStack: List<String>,
    val popularBadge: String? = null,
    val longDescription: String = "",
    val websiteSlug: String = "",
    val deliverables: List<String> = emptyList(),
    val benefits: List<Pair<String, String>> = emptyList(),
    val plans: List<ServicePlan> = emptyList(),
    val faqs: List<Pair<String, String>> = emptyList(),
    val metrics: List<Pair<String, String>> = emptyList()
)

data class LeadInquiry(
    val leadId: String = "",
    val name: String = "",
    val email: String = "",
    val contact: String = "",
    val company: String = "",
    val service: String = "",
    val message: String = "",
    val source: String = "ChittorTech Mobile App",
    val status: String = "new",
    val createdAt: Timestamp? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// Broadcast App Notification Model
// ─────────────────────────────────────────────────────────────────────────────

data class AppNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "INFO", // "INFO" | "UPDATE" | "ALERT"
    val category: String = "", // "Billing" | "Project" | "Support" | "General"
    val targetRole: String = "", // "client" | "admin" | "all"
    val targetEmail: String = "",
    val targetClientId: String = "",
    val timestamp: Timestamp? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// In-App AI Assistant Models
// ─────────────────────────────────────────────────────────────────────────────

enum class MessageSender { USER, BOT }

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender = MessageSender.BOT,
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)


