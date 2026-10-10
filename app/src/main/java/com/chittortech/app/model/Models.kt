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
    val milestoneProgress: Int = 0, // 0-100
    val sowPdfUrl: String = ""
)

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
    val createdAt: Timestamp? = null
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
    val timestamp: Timestamp? = null
)

