package com.chittortech.app.data

import com.chittortech.app.model.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class ChittorTechRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    // ─── Auth State ───────────────────────────────────────────────────────────

    val authState: Flow<FirebaseAuth> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    val isLoggedIn: Flow<Boolean> = authState.map { it.currentUser != null }
    val currentUid: String? get() = auth.currentUser?.uid

    suspend fun signInWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("UID null"))
            // Fetch role
            val doc = db.collection("users").document(uid).get().await()
            val role = doc.getString("role") ?: "client"
            Result.success(role)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): CtUser? {
        val uid = currentUid ?: return null
        return try {
            val doc = db.collection("users").document(uid).get().await()
            CtUser(
                uid = uid,
                email = doc.getString("email") ?: "",
                displayName = doc.getString("displayName") ?: "",
                companyName = doc.getString("companyName") ?: "",
                role = doc.getString("role") ?: "client",
                phone = doc.getString("phone") ?: "",
                createdAt = doc.getTimestamp("createdAt")
            )
        } catch (e: Exception) { null }
    }

    fun signOut() = auth.signOut()

    // ─── Projects ─────────────────────────────────────────────────────────────

    fun observeClientProject(clientId: String): Flow<Project?> = callbackFlow {
        val listener = db.collection("projects")
            .whereEqualTo("clientId", clientId)
            .limit(1)
            .addSnapshotListener { snap, _ ->
                val doc = snap?.documents?.firstOrNull()
                if (doc != null) {
                    trySend(doc.toProject())
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    fun observeAllProjects(): Flow<List<Project>> = callbackFlow {
        val listener = db.collection("projects")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { it.toProject() } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveProject(project: Project): Result<Unit> {
        return try {
            val data = hashMapOf(
                "clientId" to project.clientId,
                "name" to project.name,
                "domain" to project.domain,
                "domainRegistrar" to project.domainRegistrar,
                "domainRegistrarEmail" to project.domainRegistrarEmail,
                "domainRegistrar2FA" to project.domainRegistrar2FA,
                "domainExpiryDate" to project.domainExpiryDate,
                "hostingProvider" to project.hostingProvider,
                "hostingServerIp" to project.hostingServerIp,
                "hostingPanelLogin" to project.hostingPanelLogin,
                "hostingSpecs" to project.hostingSpecs,
                "hostingExpiryDate" to project.hostingExpiryDate,
                "sslStatus" to project.sslStatus,
                "githubRepo" to project.githubRepo,
                "buildKickoffDate" to project.buildKickoffDate,
                "launchDate" to project.launchDate,
                "annualRenewalFee" to project.annualRenewalFee,
                "status" to project.status,
                "currentPhase" to project.currentPhase,
                "milestoneProgress" to project.milestoneProgress
            )
            if (project.projectId.isBlank()) {
                db.collection("projects").add(data).await()
            } else {
                db.collection("projects").document(project.projectId).set(data).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Invoices ─────────────────────────────────────────────────────────────

    fun observeClientInvoices(clientId: String): Flow<List<Invoice>> = callbackFlow {
        val listener = db.collection("invoices")
            .whereEqualTo("clientId", clientId)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { it.toInvoice() } ?: emptyList()
                trySend(list.sortedByDescending { it.createdAt?.seconds })
            }
        awaitClose { listener.remove() }
    }

    fun observeAllInvoices(): Flow<List<Invoice>> = callbackFlow {
        val listener = db.collection("invoices")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { it.toInvoice() } ?: emptyList()
                trySend(list.sortedByDescending { it.createdAt?.seconds })
            }
        awaitClose { listener.remove() }
    }

    suspend fun createInvoice(invoice: Invoice): Result<Unit> {
        return try {
            val data = hashMapOf(
                "clientId" to invoice.clientId,
                "projectId" to invoice.projectId,
                "title" to invoice.title,
                "amount" to invoice.amount,
                "status" to invoice.status,
                "dueDate" to invoice.dueDate,
                "lineItems" to invoice.lineItems.map { mapOf("description" to it.description, "amount" to it.amount) },
                "invoicePdfUrl" to invoice.invoicePdfUrl,
                "signedSowUrl" to invoice.signedSowUrl,
                "createdAt" to Timestamp.now()
            )
            if (invoice.invoiceId.isBlank()) {
                db.collection("invoices").add(data).await()
            } else {
                db.collection("invoices").document(invoice.invoiceId).set(data).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateInvoiceStatus(invoiceId: String, status: String): Result<Unit> {
        return try {
            db.collection("invoices").document(invoiceId).update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Tickets ──────────────────────────────────────────────────────────────

    fun observeClientTickets(clientId: String): Flow<List<SupportTicket>> = callbackFlow {
        val listener = db.collection("tickets")
            .whereEqualTo("clientId", clientId)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { it.toTicket() } ?: emptyList()
                trySend(list.sortedByDescending { it.createdAt?.seconds })
            }
        awaitClose { listener.remove() }
    }

    fun observeAllTickets(): Flow<List<SupportTicket>> = callbackFlow {
        val listener = db.collection("tickets")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { it.toTicket() } ?: emptyList()
                trySend(list.sortedByDescending { it.createdAt?.seconds })
            }
        awaitClose { listener.remove() }
    }

    suspend fun createTicket(ticket: SupportTicket): Result<Unit> {
        return try {
            val data = hashMapOf(
                "clientId" to ticket.clientId,
                "projectId" to ticket.projectId,
                "clientName" to ticket.clientName,
                "companyName" to ticket.companyName,
                "title" to ticket.title,
                "category" to ticket.category,
                "priority" to ticket.priority,
                "status" to "OPEN",
                "description" to ticket.description,
                "resolutionNote" to "",
                "attachmentUrl" to ticket.attachmentUrl,
                "createdAt" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            )
            db.collection("tickets").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTicketStatus(
        ticketId: String,
        status: String,
        resolutionNote: String
    ): Result<Unit> {
        return try {
            db.collection("tickets").document(ticketId).update(
                mapOf(
                    "status" to status,
                    "resolutionNote" to resolutionNote,
                    "updatedAt" to Timestamp.now()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Admin KPIs ───────────────────────────────────────────────────────────

    fun observeAdminKpi(): Flow<AdminKpi> = combine(
        observeAllInvoices(),
        observeAllTickets(),
        observeAllProjects()
    ) { invoices, tickets, projects ->
        AdminKpi(
            totalMonthlyInvoiced = invoices.filter { it.status == "PAID" }.sumOf { it.amount },
            totalOutstandingDues = invoices.filter { it.status != "PAID" }.sumOf { it.amount },
            activeDeployments = projects.size,
            openTickets = tickets.count { it.status == "OPEN" }
        )
    }

    // ─── All Users (Admin) ────────────────────────────────────────────────────

    fun observeAllClients(): Flow<List<CtUser>> = callbackFlow {
        val listener = db.collection("users")
            .whereEqualTo("role", "client")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { doc ->
                    CtUser(
                        uid = doc.id,
                        email = doc.getString("email") ?: "",
                        displayName = doc.getString("displayName") ?: "",
                        companyName = doc.getString("companyName") ?: "",
                        role = doc.getString("role") ?: "client",
                        phone = doc.getString("phone") ?: "",
                        createdAt = doc.getTimestamp("createdAt")
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}

// ─── Extension Mappers ────────────────────────────────────────────────────────

private fun com.google.firebase.firestore.DocumentSnapshot.toProject(): Project {
    @Suppress("UNCHECKED_CAST")
    return Project(
        projectId = id,
        clientId = getString("clientId") ?: "",
        name = getString("name") ?: "",
        domain = getString("domain") ?: "",
        domainRegistrar = getString("domainRegistrar") ?: "",
        domainRegistrarEmail = getString("domainRegistrarEmail") ?: "",
        domainRegistrar2FA = getString("domainRegistrar2FA") ?: "",
        domainExpiryDate = getString("domainExpiryDate") ?: "",
        hostingProvider = getString("hostingProvider") ?: "",
        hostingServerIp = getString("hostingServerIp") ?: "",
        hostingPanelLogin = getString("hostingPanelLogin") ?: "",
        hostingSpecs = getString("hostingSpecs") ?: "",
        hostingExpiryDate = getString("hostingExpiryDate") ?: "",
        sslStatus = getString("sslStatus") ?: "Active",
        githubRepo = getString("githubRepo") ?: "",
        buildKickoffDate = getString("buildKickoffDate") ?: "",
        launchDate = getString("launchDate") ?: "",
        annualRenewalFee = getLong("annualRenewalFee") ?: 0L,
        status = getString("status") ?: "Live & Active",
        currentPhase = getString("currentPhase") ?: "",
        milestoneProgress = (getLong("milestoneProgress") ?: 0L).toInt(),
        sowPdfUrl = getString("sowPdfUrl") ?: ""
    )
}

private fun com.google.firebase.firestore.DocumentSnapshot.toInvoice(): Invoice {
    @Suppress("UNCHECKED_CAST")
    val rawItems = get("lineItems") as? List<Map<String, Any>> ?: emptyList()
    val lineItems = rawItems.map {
        InvoiceLineItem(
            description = it["description"] as? String ?: "",
            amount = (it["amount"] as? Long) ?: 0L
        )
    }
    return Invoice(
        invoiceId = id,
        clientId = getString("clientId") ?: "",
        projectId = getString("projectId") ?: "",
        title = getString("title") ?: "",
        lineItems = lineItems,
        amount = getLong("amount") ?: 0L,
        status = getString("status") ?: "UNPAID",
        dueDate = getString("dueDate") ?: "",
        invoicePdfUrl = getString("invoicePdfUrl") ?: "",
        signedSowUrl = getString("signedSowUrl"),
        createdAt = getTimestamp("createdAt")
    )
}

private fun com.google.firebase.firestore.DocumentSnapshot.toTicket(): SupportTicket {
    return SupportTicket(
        ticketId = id,
        clientId = getString("clientId") ?: "",
        projectId = getString("projectId") ?: "",
        clientName = getString("clientName") ?: "",
        companyName = getString("companyName") ?: "",
        title = getString("title") ?: "",
        category = getString("category") ?: "",
        priority = getString("priority") ?: "MEDIUM",
        status = getString("status") ?: "OPEN",
        description = getString("description") ?: "",
        resolutionNote = getString("resolutionNote") ?: "",
        attachmentUrl = getString("attachmentUrl") ?: "",
        createdAt = getTimestamp("createdAt"),
        updatedAt = getTimestamp("updatedAt")
    )
}
