package com.chittortech.app

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittortech.app.data.ChittorTechRepository
import com.chittortech.app.data.OtpAuthService
import com.chittortech.app.model.CtUser
import com.chittortech.app.theme.VyaparBlue
import com.chittortech.app.ui.main.AdminMainScreen
import com.chittortech.app.ui.main.ClientMainScreen
import com.chittortech.app.ui.screens.auth.LoginScreen
import com.chittortech.app.ui.screens.splash.ChittorTechSplashScreen
import com.chittortech.app.ui.vyapar.VyaparMainScreen
import kotlinx.coroutines.launch

@Composable
fun MainNavigation(
    repository: ChittorTechRepository = remember { ChittorTechRepository() }
) {
    val scope = rememberCoroutineScope()
    var showSplash by remember { mutableStateOf(true) }

    // Auth state
    val isLoggedIn by repository.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    var currentUser by remember { mutableStateOf<CtUser?>(null) }
    var isLoadingUser by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

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
        targetState = showSplash,
        label = "SplashTransition"
    ) { isSplash ->
        if (isSplash) {
            ChittorTechSplashScreen(
                onSplashFinished = { showSplash = false }
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
                        onRequestOtp = { email, password, role, onSessionReady, onError ->
                            loginError = null
                            scope.launch {
                                // 1. First validate credentials against Firestore
                                val credResult = repository.validateCredentials(email, password, role)
                                if (credResult.isFailure) {
                                    val err = credResult.exceptionOrNull()?.message ?: "Invalid email or password."
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
                                val result = repository.signInWithEmail(email, password)
                                result.onFailure { e ->
                                    loginError = e.message ?: "Sign-in failed. Please verify credentials."
                                }
                                result.onSuccess { assignedRole ->
                                    val user = repository.getCurrentUser() ?: repository.getUserByEmail(email)
                                    currentUser = user ?: CtUser(
                                        uid = email.trim().lowercase(),
                                        email = email,
                                        displayName = email.substringBefore("@"),
                                        role = assignedRole
                                    )
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
                                    currentUser = null
                                }
                            )
                        }
                        "admin" -> {
                            AdminMainScreen(
                                user = user,
                                repository = repository,
                                onSignOut = {
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
