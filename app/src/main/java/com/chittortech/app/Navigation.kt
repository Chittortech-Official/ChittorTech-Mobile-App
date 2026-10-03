package com.chittortech.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chittortech.app.data.ChittorTechRepository
import com.chittortech.app.model.CtUser
import com.chittortech.app.theme.VyaparBlue
import com.chittortech.app.ui.screens.auth.LoginScreen
import com.chittortech.app.ui.vyapar.VyaparMainScreen
import kotlinx.coroutines.launch

@Composable
fun MainNavigation(
    repository: ChittorTechRepository = remember { ChittorTechRepository() }
) {
    val scope = rememberCoroutineScope()

    // Auth state
    val isLoggedIn by repository.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    var currentUser by remember { mutableStateOf<CtUser?>(null) }
    var isLoadingUser by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // When auth state changes (user logged in), fetch user profile
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && currentUser == null) {
            isLoadingUser = true
            currentUser = repository.getCurrentUser()
            isLoadingUser = false
        } else if (!isLoggedIn && currentUser?.uid != "demo_user") {
            currentUser = null
        }
    }

    when {
        isLoadingUser -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VyaparBlue)
            }
        }
        currentUser == null -> {
            LoginScreen(
                isLoading    = false,
                errorMessage = loginError,
                onLoginSuccess = { email, password ->
                    loginError = null
                    scope.launch {
                        val result = repository.signInWithEmail(email, password)
                        result.onFailure { e ->
                            loginError = e.message ?: "Login failed. Please try again."
                        }
                        result.onSuccess {
                            currentUser = repository.getCurrentUser()
                        }
                    }
                },
                onDemoAccess = {
                    currentUser = CtUser(
                        uid = "demo_user",
                        email = "demo@chittortech.in",
                        displayName = "Gautam malik",
                        companyName = "Gautam malik",
                        role = "admin",
                        phone = "+91 76855 35660"
                    )
                }
            )
        }
        else -> {
            val user = currentUser!!
            VyaparMainScreen(
                user = user,
                repository = repository
            )
        }
    }
}

