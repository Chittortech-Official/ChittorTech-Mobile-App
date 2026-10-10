package com.chittortech.app

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittortech.app.data.ChittorTechRepository
import com.chittortech.app.data.OtpAuthService
import com.chittortech.app.data.SessionManager
import com.chittortech.app.data.SessionValidationResult
import com.chittortech.app.model.CtUser
import com.chittortech.app.theme.VyaparBlue
import com.chittortech.app.ui.main.AdminMainScreen
import com.chittortech.app.ui.main.ClientMainScreen
import com.chittortech.app.ui.screens.auth.LoginScreen
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.chittortech.app.ui.screens.splash.ChittorTechOnboardingScreen
import com.chittortech.app.ui.screens.splash.ChittorTechSplashScreen
import com.chittortech.app.ui.vyapar.VyaparMainScreen
import kotlinx.coroutines.launch

@Composable
fun MainNavigation(
    repository: ChittorTechRepository = remember { ChittorTechRepository() }
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("chittortech_app_prefs", Context.MODE_PRIVATE) }
    var hasCompletedOnboarding by remember {
        mutableStateOf(prefs.getBoolean("has_completed_onboarding", false))
    }

    val scope = rememberCoroutineScope()
    var showSplash by remember { mutableStateOf(true) }

    // Auth state
    val isLoggedIn by repository.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    var currentUser by remember { mutableStateOf<CtUser?>(null) }
    var isLoadingUser by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // ── Corporate Session Management (5-Day Inactivity & 7-Day Lifetime Policy) ──
    LaunchedEffect(showSplash) {
        if (!showSplash && currentUser == null) {
            val sessionResult = SessionManager.validateSession(context)
            when (sessionResult) {
                is SessionValidationResult.Expired -> {
                    SessionManager.clearSession(context)
                    repository.signOut()
                    currentUser = null
                    loginError = sessionResult.message
                }
                is SessionValidationResult.Valid -> {
                    isLoadingUser = true
                    val user = repository.getCurrentUser() ?: repository.getUserByEmail(sessionResult.email)
                    if (user != null) {
                        currentUser = user
                        SessionManager.updateLastActive(context)
                    } else {
                        SessionManager.clearSession(context)
                        repository.signOut()
                    }
                    isLoadingUser = false
                }
                is SessionValidationResult.NoSession -> {
                    // Normal login flow
                }
            }
        }
    }

    // When auth state changes (user logged in via Firebase Auth), fetch user profile
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && currentUser == null) {
            isLoadingUser = true
            currentUser = repository.getCurrentUser()
            isLoadingUser = false
        } else if (!isLoggedIn &&
            currentUser?.uid?.startsWith("guest_") != true &&
            currentUser?.uid?.startsWith("demo_") != true &&
            currentUser?.uid?.startsWith("founder_") != true
        ) {
            currentUser = null
        }
    }

    AnimatedContent(
        targetState = showSplash to hasCompletedOnboarding,
        transitionSpec = {
            fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
        },
        label = "SplashAndOnboardingTransition"
    ) { (isSplash, isBoarded) ->
        if (isSplash) {
            ChittorTechSplashScreen(
                onSplashFinished = { showSplash = false }
            )
        } else if (!isBoarded) {
            ChittorTechOnboardingScreen(
                onComplete = {
                    prefs.edit().putBoolean("has_completed_onboarding", true).apply()
                    hasCompletedOnboarding = true
                }
            )
        } else {
            when {
                isLoadingUser -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VyaparBlue)
                    }
                }
                currentUser == null -> {
                    LoginScreen(
                        isLoading = false,
                        errorMessage = loginError,
                        onShowOnboarding = {
                            hasCompletedOnboarding = false
                        },
                        onRequestOtp = { email, password, role, onSessionReady, onError ->
                            loginError = null
                            scope.launch {
                                // 1. First validate credentials against Firestore
                                val credResult = repository.validateCredentials(email, password, role)
                                if (credResult.isFailure) {
                                    val err = credResult.exceptionOrNull()?.message ?: "Invalid email or password."
                                    
                                    // If failed login was on the Admin portal, trigger intrusion alert to founders
                                    if (role.equals("admin", ignoreCase = true)) {
                                        launch {
                                            OtpAuthService.sendSecurityAlert(
                                                attemptedEmail = email,
                                                reason = err
                                            )
                                        }
                                    }
                                    onError(err)
                                    return@launch
                                }
                                val user = credResult.getOrNull()!!

                                // 2. Dispatch OTP email via Titan Mail / Vercel Serverless
                                val otpResult = OtpAuthService.sendOtp(
                                    email = email,
                                    name = user.displayName,
                                    role = role
                                )
                                otpResult.onSuccess { session ->
                                    onSessionReady(session)
                                }
                                otpResult.onFailure { err ->
                                    onError(err.message ?: "Failed to dispatch verification code.")
                                }
                            }
                        },
                        onVerifyOtp = { email, otp, token, expiresAt, onSuccess, onError ->
                            loginError = null
                            scope.launch {
                                val verifyResult = OtpAuthService.verifyOtp(email, otp, token, expiresAt)
                                verifyResult.onSuccess {
                                    onSuccess()
                                }
                                verifyResult.onFailure { err ->
                                    onError(err.message ?: "Invalid verification code.")
                                }
                            }
                        },
                        onLoginSuccess = { email, password, role ->
                            loginError = null
                            scope.launch {
                                val result = repository.signInWithEmail(email, password, role)
                                result.onFailure { e ->
                                    loginError = e.message ?: "Sign-in failed. Please verify credentials."
                                }
                                result.onSuccess { assignedRole ->
                                    if (role.isNotBlank() && !assignedRole.equals(role, ignoreCase = true)) {
                                        loginError = "Account not authorized for this portal."
                                        repository.signOut()
                                        SessionManager.clearSession(context)
                                        return@launch
                                    }
                                    val user = repository.getCurrentUser() ?: repository.getUserByEmail(email)
                                    val finalUser = user ?: CtUser(
                                        uid = email.trim().lowercase(),
                                        email = email,
                                        displayName = email.substringBefore("@"),
                                        role = assignedRole
                                    )
                                    if (assignedRole.equals("client", ignoreCase = true)) {
                                        SessionManager.saveSession(context, email, "client")
                                    }
                                    currentUser = finalUser
                                }
                            }
                        },
                        onDirectRoleAccess = { _, user ->
                            currentUser = user
                        }
                    )
                }
                else -> {
                    val user = currentUser!!
                    when (user.role) {
                        "client" -> {
                            ClientMainScreen(
                                user = user,
                                repository = repository,
                                onSignOut = {
                                    SessionManager.clearSession(context)
                                    repository.signOut()
                                    currentUser = null
                                }
                            )
                        }
                        "admin" -> {
                            AdminMainScreen(
                                user = user,
                                repository = repository,
                                onSignOut = {
                                    SessionManager.clearSession(context)
                                    repository.signOut()
                                    currentUser = null
                                }
                            )
                        }
                        else -> {
                            // Guest Explorer / Public Mode
                            VyaparMainScreen(
                                user = user,
                                repository = repository,
                                onSignOut = {
                                    currentUser = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
