package com.chittortech.app.data

import android.content.Context
import android.content.SharedPreferences

sealed class SessionValidationResult {
    data class Valid(val email: String, val role: String) : SessionValidationResult()
    data class Expired(val message: String) : SessionValidationResult()
    object NoSession : SessionValidationResult()
}

object SessionManager {

    private const val PREFS_NAME = "chittortech_session_prefs"
    private const val KEY_LOGIN_TIMESTAMP = "login_timestamp"
    private const val KEY_LAST_ACTIVE_TIMESTAMP = "last_active_timestamp"
    private const val KEY_CLIENT_EMAIL = "client_email"
    private const val KEY_CLIENT_ROLE = "client_role"

    // 5 Days Inactivity Timeout (5 days * 24h * 60m * 60s * 1000ms)
    const val INACTIVITY_TIMEOUT_MS: Long = 5L * 24 * 60 * 60 * 1000L

    // 7 Days Max Session Lifetime (7 days * 24h * 60m * 60s * 1000ms)
    const val MAX_SESSION_LIFETIME_MS: Long = 7L * 24 * 60 * 60 * 1000L

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Saves new session timestamps upon successful OTP verification and login.
     */
    fun saveSession(context: Context, email: String, role: String = "client") {
        val now = System.currentTimeMillis()
        getPrefs(context).edit()
            .putLong(KEY_LOGIN_TIMESTAMP, now)
            .putLong(KEY_LAST_ACTIVE_TIMESTAMP, now)
            .putString(KEY_CLIENT_EMAIL, email.trim().lowercase())
            .putString(KEY_CLIENT_ROLE, role.trim().lowercase())
            .apply()
    }

    /**
     * Updates the last active timestamp whenever client interacts with dashboard or app resumes.
     */
    fun updateLastActive(context: Context) {
        val prefs = getPrefs(context)
        if (prefs.contains(KEY_CLIENT_EMAIL)) {
            prefs.edit()
                .putLong(KEY_LAST_ACTIVE_TIMESTAMP, System.currentTimeMillis())
                .apply()
        }
    }

    /**
     * Evaluates corporate session against the 5-day inactivity and 7-day max lifetime policy.
     */
    fun validateSession(context: Context): SessionValidationResult {
        val prefs = getPrefs(context)
        val email = prefs.getString(KEY_CLIENT_EMAIL, null)
        val role = prefs.getString(KEY_CLIENT_ROLE, "client") ?: "client"
        val loginTs = prefs.getLong(KEY_LOGIN_TIMESTAMP, 0L)
        val lastActiveTs = prefs.getLong(KEY_LAST_ACTIVE_TIMESTAMP, 0L)

        if (email.isNullOrBlank() || loginTs == 0L || lastActiveTs == 0L) {
            return SessionValidationResult.NoSession
        }

        val currentTime = System.currentTimeMillis()

        // 1. 5-Day Inactivity Check
        val inactiveDuration = currentTime - lastActiveTs
        if (inactiveDuration > INACTIVITY_TIMEOUT_MS) {
            clearSession(context)
            return SessionValidationResult.Expired(
                "Your corporate session has expired due to 5 days of inactivity. Please verify with OTP to continue."
            )
        }

        // 2. 7-Day Max Lifetime Check
        val sessionLifetime = currentTime - loginTs
        if (sessionLifetime > MAX_SESSION_LIFETIME_MS) {
            clearSession(context)
            return SessionValidationResult.Expired(
                "Your corporate session has reached its 7-day security limit. Please verify with OTP to continue."
            )
        }

        // Active & Valid Session -> Refresh last active timestamp
        updateLastActive(context)
        return SessionValidationResult.Valid(email, role)
    }

    /**
     * Clears all session data upon logout or session expiry.
     */
    fun clearSession(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
