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

    init {
        try {
            db.collection("settings").document("auth").addSnapshotListener { snap, _ ->
                if (snap != null && snap.exists()) {
                    val url = snap.getString("vercelBaseUrl") ?: snap.getString("apiUrl")
                    if (!url.isNullOrBlank()) {
                        OtpAuthService.vercelBaseUrl = url.trim().removeSuffix("/")
                    }
                }
            }
        } catch (_: Exception) {}
    }

    val authState: Flow<FirebaseAuth> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    val isLoggedIn: Flow<Boolean> = authState.map { it.currentUser != null }
    val currentUid: String? get() = auth.currentUser?.uid

    suspend fun signInWithEmail(email: String, password: String, expectedRole: String = ""): Result<String> {
        val cleanEmail = email.trim().lowercase()
        return try {
            // 1. Try Firebase Auth first
            val authUser = try {
                val res = auth.signInWithEmailAndPassword(cleanEmail, password).await()
                res.user
            } catch (_: Exception) {
                null
            }

            // 2. Fetch the user document from Firestore (doc ID is email or uid)
            val doc = fetchUserDoc(cleanEmail, email.trim(), authUser?.uid ?: "")

            if (doc != null && doc.exists()) {
                val firestorePassword = doc.getString("Password") ?: doc.getString("password")
                if (authUser == null) {
                    if (firestorePassword != null && firestorePassword != password) {
                        return Result.failure(Exception("Incorrect password. Please verify credentials."))
                    }
                    // Attempt linking with Firebase Auth in the background
                    try {
                        auth.createUserWithEmailAndPassword(cleanEmail, password).await()
                    } catch (_: Exception) {
                        try {
                            auth.signInWithEmailAndPassword(cleanEmail, password).await()
                        } catch (_: Exception) {}
                    }
                }
                val role = doc.getString("role") ?: doc.getString("Role") ?: "client"
                if (expectedRole.isNotBlank()) {
                    if (expectedRole.equals("admin", ignoreCase = true) && !role.equals("admin", ignoreCase = true)) {
                        return Result.failure(Exception("Access denied. You do not have administrator privileges."))
                    }
                    if (expectedRole.equals("client", ignoreCase = true) && !role.equals("client", ignoreCase = true)) {
                        return Result.failure(Exception("Account not authorized for Corporate Portal. Please check your credentials."))
                    }
                }
                Result.success(role)
            } else if (authUser != null) {
                if (expectedRole.isNotBlank() && expectedRole.equals("admin", ignoreCase = true)) {
                    return Result.failure(Exception("Access denied. You do not have administrator privileges."))
                }
                Result.success("client")
            } else {
                Result.failure(Exception("Account not found. Please verify your email & password."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun validateCredentials(email: String, password: String, expectedRole: String = ""): Result<CtUser> {
        val cleanEmail = email.trim().lowercase()
        val doc = fetchUserDoc(cleanEmail, email.trim())
            ?: return Result.failure(Exception("Account not found. Please verify your registered email address."))

        val storedPass = doc.getString("Password") ?: doc.getString("password")
        if (storedPass != null && storedPass != password.trim()) {
            return Result.failure(Exception("Incorrect password. Please verify your credentials."))
        }

        val role = doc.getString("role") ?: doc.getString("Role") ?: "client"
        if (expectedRole.isNotBlank()) {
            if (expectedRole.equals("admin", ignoreCase = true)) {
                if (!role.equals("admin", ignoreCase = true)) {
                    return Result.failure(Exception("Access denied. You do not have administrator privileges."))
                }
            } else if (expectedRole.equals("client", ignoreCase = true)) {
                if (!role.equals("client", ignoreCase = true)) {
                    // Do not expose admin existence; strictly reject corporate access
                    return Result.failure(Exception("Account not authorized for Corporate Portal. Please check your credentials."))
                }
            }
        }

        val user = CtUser(
            uid = doc.id,
            email = doc.getString("Email") ?: doc.getString("email") ?: cleanEmail,
            displayName = doc.getString("Name")
                ?: doc.getString("name")
                ?: doc.getString("displayName")
                ?: cleanEmail.substringBefore("@"),
            companyName = doc.getString("companyName") ?: "",
            role = role,
            phone = doc.getString("phone") ?: ""
        )
        return Result.success(user)
    }

    suspend fun getUserByEmail(email: String): CtUser? {
        val cleanEmail = email.trim().lowercase()
        val doc = fetchUserDoc(cleanEmail, email.trim()) ?: return null
        return CtUser(
            uid = doc.id,
            email = doc.getString("Email") ?: doc.getString("email") ?: email,
            displayName = doc.getString("Name")
                ?: doc.getString("name")
                ?: doc.getString("displayName")
                ?: doc.getString("fullName")
                ?: email.substringBefore("@"),
            companyName = doc.getString("companyName")
                ?: doc.getString("company")
                ?: doc.getString("businessName")
                ?: "",
            role = doc.getString("role") ?: doc.getString("Role") ?: "client",
            phone = doc.getString("phone") ?: doc.getString("phoneNumber") ?: ""
        )
    }

    suspend fun updateUserProfile(user: CtUser): Result<Unit> {
        return try {
            val cleanEmail = user.email.trim().lowercase()
            val docKey = cleanEmail.ifBlank { user.uid }
            val updates = hashMapOf<String, Any>(
                "Name" to user.displayName,
                "phone" to user.phone,
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                // Automatically clean up duplicate lowercase/camelCase keys from Firestore
                "name" to com.google.firebase.firestore.FieldValue.delete(),
                "displayName" to com.google.firebase.firestore.FieldValue.delete(),
                "phoneNumber" to com.google.firebase.firestore.FieldValue.delete()
            )
            val docRef = db.collection("users").document(docKey)
            docRef.set(updates, com.google.firebase.firestore.SetOptions.merge()).await()

            // If an accidental UID-based document was created previously, delete it to keep database clean
            if (user.uid.isNotBlank() && user.uid != docKey && !user.uid.contains("@")) {
                try {
                    db.collection("users").document(user.uid).delete().await()
                } catch (_: Exception) {}
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(
        oldPassword: String,
        newPassword: String,
        userEmail: String = ""
    ): Result<Unit> {
        return try {
            val authUser = auth.currentUser
            val cleanEmail = (authUser?.email ?: userEmail).trim().lowercase()

            // 1. Fetch user doc to verify old password against Firestore
            val doc = if (cleanEmail.isNotBlank()) {
                fetchUserDoc(cleanEmail, userEmail.trim(), authUser?.uid ?: "")
            } else null

            val firestorePassword = doc?.getString("Password") ?: doc?.getString("password")

            // 2. If document has a password stored, verify it matches the entered old password
            if (!firestorePassword.isNullOrBlank()) {
                if (firestorePassword != oldPassword.trim()) {
                    return Result.failure(Exception("Incorrect current password. Please enter your valid current password."))
                }
            }

            // 3. If signed into Firebase Auth, re-authenticate with the old password
            if (authUser != null && cleanEmail.isNotBlank()) {
                try {
                    val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(cleanEmail, oldPassword.trim())
                    authUser.reauthenticate(credential).await()
                } catch (authEx: Exception) {
                    if (firestorePassword.isNullOrBlank()) {
                        return Result.failure(Exception("Incorrect current password. Authentication failed."))
                    }
                }
            }

            // 4. Update password in Firebase Auth
            try {
                authUser?.updatePassword(newPassword.trim())?.await()
            } catch (_: Exception) {}

            // 5. Update password in Firestore with clean standardized field
            if (cleanEmail.isNotBlank()) {
                val updates = mapOf(
                    "Password" to newPassword.trim(),
                    "password" to com.google.firebase.firestore.FieldValue.delete(),
                    "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
                try {
                    db.collection("users").document(cleanEmail).set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
                } catch (_: Exception) {}

                // Clean up orphaned UID document if it exists
                if (authUser?.uid != null && authUser.uid.isNotBlank() && authUser.uid != cleanEmail && !authUser.uid.contains("@")) {
                    try {
                        db.collection("users").document(authUser.uid).delete().await()
                    } catch (_: Exception) {}
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim().lowercase()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): CtUser? {
        val authUser = auth.currentUser
        val uid = authUser?.uid ?: currentUid
        val email = authUser?.email ?: ""
        return try {
            val doc = fetchUserDoc(email.trim().lowercase(), email.trim(), uid ?: "")
            if (doc != null && doc.exists()) {
                CtUser(
                    uid = uid ?: doc.id,
                    email = doc.getString("Email") ?: doc.getString("email") ?: email,
                    displayName = doc.getString("Name")
                        ?: doc.getString("name")
                        ?: doc.getString("displayName")
                        ?: doc.getString("fullName")
                        ?: authUser?.displayName
                        ?: email.substringBefore("@"),
                    companyName = doc.getString("companyName")
                        ?: doc.getString("company")
                        ?: doc.getString("businessName")
                        ?: "",
                    role = doc.getString("role") ?: doc.getString("Role") ?: "client",
                    phone = doc.getString("phone")
                        ?: doc.getString("phoneNumber")
                        ?: doc.getString("mobile")
                        ?: authUser?.phoneNumber
                        ?: "",
                    createdAt = doc.getTimestamp("createdAt")
                )
            } else if (authUser != null) {
                CtUser(
                    uid = authUser.uid,
                    email = authUser.email ?: "",
                    displayName = authUser.displayName?.ifBlank { null } ?: authUser.email?.substringBefore("@") ?: "Client",
                    companyName = "",
                    role = "client",
                    phone = authUser.phoneNumber ?: ""
                )
            } else null
        } catch (e: Exception) {
            if (authUser != null) {
                CtUser(
                    uid = authUser.uid,
                    email = authUser.email ?: "",
                    displayName = authUser.displayName?.ifBlank { null } ?: authUser.email?.substringBefore("@") ?: "Client",
                    companyName = "",
                    role = "client",
                    phone = authUser.phoneNumber ?: ""
                )
            } else null
        }
    }

    private suspend fun fetchUserDoc(vararg keys: String): com.google.firebase.firestore.DocumentSnapshot? {
        for (key in keys) {
            if (key.isBlank()) continue
            try {
                val doc = db.collection("users").document(key).get().await()
                if (doc.exists()) return doc
            } catch (_: Exception) {}
        }
        val emailKey = keys.firstOrNull { it.contains("@") }
        if (emailKey != null) {
            try {
                val q1 = db.collection("users").whereEqualTo("Email", emailKey).limit(1).get().await()
                if (!q1.isEmpty) return q1.documents.first()
                val q2 = db.collection("users").whereEqualTo("email", emailKey).limit(1).get().await()
                if (!q2.isEmpty) return q2.documents.first()
            } catch (_: Exception) {}
        }
        return null
    }

    fun signOut() = auth.signOut()

    // ─── Projects ─────────────────────────────────────────────────────────────

    fun observeClientProject(clientId: String, clientEmail: String = ""): Flow<Project?> = callbackFlow {
        val cleanEmail = clientEmail.trim().lowercase()
        val cleanId = clientId.trim()
        val ids = listOf(cleanEmail, cleanId).filter { it.isNotBlank() }.distinct()

        if (ids.isEmpty()) {
            trySend(null)
            awaitClose {}
            return@callbackFlow
        }

        val listener = db.collection("projects")
            .whereIn("clientId", ids)
            .addSnapshotListener { snap, _ ->
                if (snap != null && !snap.isEmpty) {
                    trySend(snap.documents.first().toProject())
                } else {
                    // Fallback to checking document with ID == cleanEmail or cleanId
                    val docKey = cleanEmail.ifBlank { cleanId }
                    db.collection("projects").document(docKey).get()
                        .addOnSuccessListener { doc ->
                            if (doc != null && doc.exists()) {
                                trySend(doc.toProject())
                            } else {
                                trySend(null)
                            }
                        }
                        .addOnFailureListener { trySend(null) }
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
            val docKey = project.clientId.trim().lowercase().ifBlank { project.projectId.ifBlank { "proj_default" } }
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
            db.collection("projects").document(docKey).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Invoices ─────────────────────────────────────────────────────────────

    fun observeClientInvoices(clientId: String, clientEmail: String = ""): Flow<List<Invoice>> = callbackFlow {
        val cleanEmail = clientEmail.trim().lowercase()
        val cleanId = clientId.trim()
        val ids = listOf(cleanEmail, cleanId).filter { it.isNotBlank() }.distinct()

        if (ids.isEmpty()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }

        val listener = db.collection("invoices")
            .whereIn("clientId", ids)
            .addSnapshotListener { snap, _ ->
                val list = mutableListOf<Invoice>()
                if (snap != null && !snap.isEmpty) {
                    for (doc in snap.documents) {
                        if (doc.contains("invoices")) {
                            list.addAll(doc.toInvoiceList())
                        } else {
                            list.add(doc.toInvoice())
                        }
                    }
                }
                // Also check if a document with docKey == cleanEmail exists for legacy nested structure
                val docKey = cleanEmail.ifBlank { cleanId }
                if (list.isEmpty()) {
                    db.collection("invoices").document(docKey).get()
                        .addOnSuccessListener { legacyDoc ->
                            if (legacyDoc.exists() && legacyDoc.contains("invoices")) {
                                trySend(legacyDoc.toInvoiceList().sortedByDescending { it.createdAt?.seconds })
                            } else {
                                trySend(emptyList())
                            }
                        }
                        .addOnFailureListener { trySend(emptyList()) }
                } else {
                    trySend(list.sortedByDescending { it.createdAt?.seconds })
                }
            }
        awaitClose { listener.remove() }
    }

    fun observeAllInvoices(): Flow<List<Invoice>> = callbackFlow {
        val listener = db.collection("invoices")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.flatMap { doc ->
                    if (doc.contains("invoices")) doc.toInvoiceList() else listOfNotNull(doc.toInvoice())
                } ?: emptyList()
                trySend(list.sortedByDescending { it.createdAt?.seconds })
            }
        awaitClose { listener.remove() }
    }

    suspend fun createInvoice(invoice: Invoice): Result<Unit> {
        return try {
            val docKey = invoice.clientId.trim().lowercase().ifBlank { currentUid ?: "general" }
            val invoiceMap = hashMapOf<String, Any>(
                "invoiceId" to invoice.invoiceId.ifBlank { "INV-" + System.currentTimeMillis().toString().takeLast(6) },
                "projectId" to invoice.projectId,
                "title" to invoice.title,
                "amount" to invoice.amount,
                "status" to invoice.status,
                "dueDate" to invoice.dueDate,
                "lineItems" to invoice.lineItems.map { mapOf("description" to it.description, "amount" to it.amount) },
                "invoicePdfUrl" to invoice.invoicePdfUrl,
                "signedSowUrl" to (invoice.signedSowUrl ?: ""),
                "createdAt" to Timestamp.now()
            )
            val docRef = db.collection("invoices").document(docKey)
            val docSnap = docRef.get().await()
            if (docSnap.exists() && docSnap.contains("invoices")) {
                docRef.update("invoices", com.google.firebase.firestore.FieldValue.arrayUnion(invoiceMap)).await()
            } else {
                docRef.set(mapOf("clientId" to docKey, "invoices" to listOf(invoiceMap))).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateInvoiceStatus(invoiceId: String, status: String): Result<Unit> {
        return try {
            val allInvoiceDocs = db.collection("invoices").get().await()
            for (doc in allInvoiceDocs.documents) {
                if (doc.contains("invoices")) {
                    @Suppress("UNCHECKED_CAST")
                    val rawList = doc.get("invoices") as? List<Map<String, Any>> ?: emptyList()
                    val targetIdx = rawList.indexOfFirst { (it["invoiceId"] as? String) == invoiceId }
                    if (targetIdx != -1) {
                        val updatedList = rawList.toMutableList()
                        val oldInv = HashMap(updatedList[targetIdx])
                        oldInv["status"] = status
                        updatedList[targetIdx] = oldInv
                        doc.reference.update("invoices", updatedList).await()
                        return Result.success(Unit)
                    }
                } else if (doc.id == invoiceId) {
                    doc.reference.update("status", status).await()
                    return Result.success(Unit)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProjectMilestone(
        projectId: String,
        clientId: String,
        progress: Int,
        phase: String,
        status: String
    ): Result<Unit> {
        return try {
            val cleanEmail = clientId.trim().lowercase()
            val docKey = cleanEmail.ifBlank { projectId.ifBlank { "proj_default" } }
            val docRef = db.collection("projects").document(docKey)
            val docSnap = docRef.get().await()
            if (docSnap.exists()) {
                docRef.update(
                    mapOf(
                        "milestoneProgress" to progress,
                        "currentPhase" to phase,
                        "status" to status
                    )
                ).await()
                return Result.success(Unit)
            }
            val q = db.collection("projects").whereEqualTo("clientId", clientId).get().await()
            for (d in q.documents) {
                d.reference.update(
                    mapOf(
                        "milestoneProgress" to progress,
                        "currentPhase" to phase,
                        "status" to status
                    )
                ).await()
                return Result.success(Unit)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Tickets ──────────────────────────────────────────────────────────────

    fun observeClientTickets(clientId: String, clientEmail: String = ""): Flow<List<SupportTicket>> = callbackFlow {
        val cleanEmail = clientEmail.trim().lowercase()
        val cleanId = clientId.trim()
        val docKey = cleanEmail.ifBlank { cleanId }

        // Auto-migration: If a stray document was created with UID (e.g. qv9mgvyb...), merge tickets into cleanEmail and clean up!
        if (cleanEmail.isNotBlank() && cleanId.isNotBlank() && cleanId != cleanEmail) {
            db.collection("tickets").document(cleanId).get()
                .addOnSuccessListener { uidSnap ->
                    if (uidSnap.exists() && uidSnap.contains("tickets")) {
                        @Suppress("UNCHECKED_CAST")
                        val rawMaps = uidSnap.get("tickets") as? List<Map<String, Any>> ?: emptyList()
                        if (rawMaps.isNotEmpty()) {
                            val targetRef = db.collection("tickets").document(cleanEmail)
                            targetRef.update("tickets", com.google.firebase.firestore.FieldValue.arrayUnion(*rawMaps.toTypedArray()))
                                .addOnSuccessListener {
                                    uidSnap.reference.delete()
                                }
                        } else {
                            uidSnap.reference.delete()
                        }
                    }
                }
        }

        val listener = db.collection("tickets").document(docKey)
            .addSnapshotListener { snap, _ ->
                if (snap != null && snap.exists() && snap.contains("tickets")) {
                    val list = snap.toTicketList()
                    trySend(list.sortedByDescending { it.createdAt?.seconds })
                } else {
                    // Fallback to query across possible IDs
                    val ids = listOf(cleanId, cleanEmail).filter { it.isNotBlank() }.distinct()
                    if (ids.isNotEmpty()) {
                        db.collection("tickets").whereIn("clientId", ids).get()
                            .addOnSuccessListener { qSnap ->
                                val list = qSnap.documents.flatMap { doc ->
                                    if (doc.contains("tickets")) doc.toTicketList() else listOfNotNull(doc.toTicket())
                                }
                                trySend(list.sortedByDescending { it.createdAt?.seconds })
                            }
                            .addOnFailureListener { trySend(emptyList()) }
                    } else {
                        trySend(emptyList())
                    }
                }
            }
        awaitClose { listener.remove() }
    }

    fun observeAllTickets(): Flow<List<SupportTicket>> = callbackFlow {
        val listener = db.collection("tickets")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.flatMap { doc ->
                    if (doc.contains("tickets")) doc.toTicketList() else listOfNotNull(doc.toTicket())
                } ?: emptyList()
                trySend(list.sortedByDescending { it.createdAt?.seconds })
            }
        awaitClose { listener.remove() }
    }

    suspend fun createTicket(ticket: SupportTicket, clientEmail: String = ""): Result<String> {
        return try {
            val cleanEmail = clientEmail.trim().lowercase().ifBlank {
                if (ticket.clientId.contains("@")) ticket.clientId.trim().lowercase() else ""
            }
            val docKey = cleanEmail.ifBlank { ticket.clientId.trim().lowercase().ifBlank { currentUid ?: "general" } }
            val tktId = "TKT-" + System.currentTimeMillis().toString().takeLast(6)
            val ticketMap = hashMapOf<String, Any>(
                "ticketId" to tktId,
                "clientId" to docKey,
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
            val docRef = db.collection("tickets").document(docKey)
            val docSnap = docRef.get().await()
            if (docSnap.exists() && docSnap.contains("tickets")) {
                docRef.update("tickets", com.google.firebase.firestore.FieldValue.arrayUnion(ticketMap)).await()
            } else {
                docRef.set(mapOf("clientId" to docKey, "tickets" to listOf(ticketMap))).await()
            }
            Result.success(tktId)
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
            val allTicketDocs = db.collection("tickets").get().await()
            for (doc in allTicketDocs.documents) {
                if (doc.contains("tickets")) {
                    @Suppress("UNCHECKED_CAST")
                    val rawList = doc.get("tickets") as? List<Map<String, Any>> ?: emptyList()
                    val targetIdx = rawList.indexOfFirst { (it["ticketId"] as? String) == ticketId }
                    if (targetIdx != -1) {
                        val updatedList = rawList.toMutableList()
                        val oldTicket = HashMap(updatedList[targetIdx])
                        oldTicket["status"] = status
                        oldTicket["resolutionNote"] = resolutionNote
                        oldTicket["updatedAt"] = Timestamp.now()
                        updatedList[targetIdx] = oldTicket
                        doc.reference.update("tickets", updatedList).await()
                        return Result.success(Unit)
                    }
                } else if (doc.id == ticketId) {
                    doc.reference.update(
                        mapOf(
                            "status" to status,
                            "resolutionNote" to resolutionNote,
                            "updatedAt" to Timestamp.now()
                        )
                    ).await()
                    return Result.success(Unit)
                }
            }
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
        val adminEmails = setOf(
            "kushsharma.cor@gmail.com",
            "lavsharma.cor@gmail.com",
            "business@chittortech.in",
            "contact@chittortech.in"
        )
        val listener = db.collection("users")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { doc ->
                    val email = doc.getString("Email") ?: doc.getString("email") ?: ""
                    val role = doc.getString("role") ?: doc.getString("Role") ?: "client"
                    if (role.equals("admin", ignoreCase = true) || adminEmails.contains(email.lowercase())) {
                        null
                    } else {
                        CtUser(
                            uid = doc.id,
                            email = email,
                            displayName = doc.getString("Name")
                                ?: doc.getString("name")
                                ?: doc.getString("displayName")
                                ?: doc.getString("fullName")
                                ?: email.substringBefore("@"),
                            companyName = doc.getString("companyName")
                                ?: doc.getString("company")
                                ?: doc.getString("businessName")
                                ?: "",
                            role = "client",
                            phone = doc.getString("phone") ?: doc.getString("phoneNumber") ?: "",
                            createdAt = doc.getTimestamp("createdAt")
                        )
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    // ─── Leads & Inquiries (Website & App CRM) ────────────────────────────────
    fun observeIncomingLeads(): Flow<List<LeadInquiry>> = callbackFlow {
        val listener = db.collection("leads")
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { doc ->
                    val source = doc.getString("source") ?: ""
                    // Filter: Only include incoming leads (website or app), NOT outbound
                    if (source.contains("outbound", ignoreCase = true)) {
                        null
                    } else {
                        LeadInquiry(
                            leadId = doc.id,
                            name = doc.getString("name") ?: "",
                            email = doc.getString("email") ?: "",
                            contact = doc.getString("contact") ?: doc.getString("phone") ?: "",
                            company = doc.getString("company") ?: "",
                            service = doc.getString("service") ?: "",
                            message = doc.getString("message") ?: "",
                            source = if (source.isBlank()) "Website / App" else source,
                            status = doc.getString("status") ?: "new",
                            createdAt = doc.getTimestamp("createdAt")
                        )
                    }
                }?.sortedByDescending { it.createdAt?.seconds } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateLeadStatus(leadId: String, status: String): Result<Unit> {
        return try {
            db.collection("leads").document(leadId).update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitLead(lead: LeadInquiry): Result<Unit> {
        return try {
            val data = hashMapOf(
                "name" to lead.name,
                "email" to lead.email,
                "contact" to lead.contact,
                "company" to lead.company,
                "service" to lead.service,
                "message" to lead.message,
                "source" to lead.source,
                "status" to "new",
                "createdAt" to Timestamp.now()
            )
            db.collection("leads").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Broadcast Notifications ──────────────────────────────────────────────

    fun observeNotifications(): Flow<List<AppNotification>> = callbackFlow {
        val listener = db.collection("notifications")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { doc ->
                    AppNotification(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        message = doc.getString("message") ?: "",
                        type = doc.getString("type") ?: "INFO",
                        timestamp = doc.getTimestamp("timestamp")
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendNotification(title: String, message: String, type: String = "INFO"): Result<Unit> {
        return try {
            val data = hashMapOf(
                "title" to title.trim(),
                "message" to message.trim(),
                "type" to type,
                "timestamp" to Timestamp.now()
            )
            db.collection("notifications").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotification(id: String): Result<Unit> {
        return try {
            db.collection("notifications").document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


// ─── Extension Mappers ────────────────────────────────────────────────────────

private fun com.google.firebase.firestore.DocumentSnapshot.toProject(): Project {
    val rawProgress = get("milestoneProgress")
    val progressInt = when (rawProgress) {
        is Number -> rawProgress.toInt()
        is String -> rawProgress.toIntOrNull() ?: 0
        else -> 0
    }
    val rawRenewal = get("annualRenewalFee")
    val renewalLong = when (rawRenewal) {
        is Number -> rawRenewal.toLong()
        is String -> rawRenewal.toDoubleOrNull()?.toLong() ?: 0L
        else -> 0L
    }
    return Project(
        projectId = id,
        clientId = getString("clientId") ?: "",
        name = getString("name") ?: getString("title") ?: "",
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
        annualRenewalFee = renewalLong,
        status = getString("status") ?: "In Progress",
        currentPhase = getString("currentPhase") ?: "",
        milestoneProgress = progressInt,
        sowPdfUrl = getString("sowPdfUrl") ?: ""
    )
}

private fun com.google.firebase.firestore.DocumentSnapshot.toInvoice(): Invoice {
    @Suppress("UNCHECKED_CAST")
    val rawItems = get("lineItems") as? List<Map<String, Any>> ?: emptyList()
    val lineItems = rawItems.map {
        InvoiceLineItem(
            description = it["description"] as? String ?: "",
            amount = (it["amount"] as? Number)?.toLong() ?: 0L
        )
    }
    val rawAmount = get("amount")
    val amountLong = when (rawAmount) {
        is Number -> rawAmount.toLong()
        is String -> rawAmount.toDoubleOrNull()?.toLong() ?: 0L
        else -> 0L
    }
    return Invoice(
        invoiceId = id,
        clientId = getString("clientId") ?: "",
        projectId = getString("projectId") ?: "",
        projectName = getString("projectName") ?: getString("project") ?: "",
        title = getString("title") ?: "",
        lineItems = lineItems,
        amount = amountLong,
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

private fun com.google.firebase.firestore.DocumentSnapshot.toInvoiceList(): List<Invoice> {
    @Suppress("UNCHECKED_CAST")
    val rawList = get("invoices") as? List<Map<String, Any>> ?: emptyList()
    return rawList.map { map ->
        val rawItems = map["lineItems"] as? List<Map<String, Any>> ?: emptyList()
        val lineItems = rawItems.map {
            InvoiceLineItem(
                description = it["description"] as? String ?: "",
                amount = (it["amount"] as? Number)?.toLong() ?: 0L
            )
        }
        val ts = map["createdAt"]
        val timestamp = when (ts) {
            is Timestamp -> ts
            is String -> try {
                Timestamp(java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).parse(ts) ?: java.util.Date())
            } catch (_: Exception) { null }
            else -> null
        }
        Invoice(
            invoiceId = map["invoiceId"] as? String ?: "",
            clientId = id,
            projectId = map["projectId"] as? String ?: "",
            projectName = map["projectName"] as? String ?: "",
            title = map["title"] as? String ?: "",
            lineItems = lineItems,
            amount = (map["amount"] as? Number)?.toLong() ?: 0L,
            status = map["status"] as? String ?: "UNPAID",
            dueDate = map["dueDate"] as? String ?: "",
            invoicePdfUrl = map["invoicePdfUrl"] as? String ?: "",
            signedSowUrl = map["signedSowUrl"] as? String,
            createdAt = timestamp
        )
    }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toTicketList(): List<SupportTicket> {
    @Suppress("UNCHECKED_CAST")
    val rawList = get("tickets") as? List<Map<String, Any>> ?: emptyList()
    return rawList.map { map ->
        val ts = map["createdAt"]
        val timestamp = when (ts) {
            is Timestamp -> ts
            is String -> try {
                Timestamp(java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).parse(ts) ?: java.util.Date())
            } catch (_: Exception) { null }
            else -> null
        }
        SupportTicket(
            ticketId = map["ticketId"] as? String ?: "",
            clientId = id,
            projectId = map["projectId"] as? String ?: "",
            clientName = map["clientName"] as? String ?: "",
            companyName = map["companyName"] as? String ?: "",
            title = map["title"] as? String ?: "",
            category = map["category"] as? String ?: "",
            priority = map["priority"] as? String ?: "MEDIUM",
            status = map["status"] as? String ?: "OPEN",
            description = map["description"] as? String ?: "",
            resolutionNote = map["resolutionNote"] as? String ?: "",
            attachmentUrl = map["attachmentUrl"] as? String ?: "",
            createdAt = timestamp
        )
    }
}
