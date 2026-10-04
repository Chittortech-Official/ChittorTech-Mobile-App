package com.chittortech.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class OtpSession(
    val email: String,
    val token: String,
    val expiresAt: Long,
    val message: String
)

object OtpAuthService {
    // Live Vercel Serverless Production Endpoint
    var vercelBaseUrl: String = "https://chittor-tech-mobile-app.vercel.app"

    /**
     * Dispatches a 6-digit OTP email from business@chittortech.in via Titan Mail
     * using the Vercel Serverless Function.
     */
    suspend fun sendOtp(
        email: String,
        name: String = "",
        role: String = "client"
    ): Result<OtpSession> = withContext(Dispatchers.IO) {
        try {
            val endpoint = "$vercelBaseUrl/api/send-otp"
            val url = URL(endpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15000
                readTimeout = 15000
                doOutput = true
                doInput = true
            }

            val jsonBody = JSONObject().apply {
                put("email", email.trim().lowercase())
                put("name", name.trim())
                put("role", role)
            }

            OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
                writer.write(jsonBody.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
            val responseText = BufferedReader(InputStreamReader(stream, "UTF-8")).use { it.readText() }

            val jsonResponse = JSONObject(responseText)
            val success = jsonResponse.optBoolean("success", false)

            if (success) {
                val token = jsonResponse.getString("token")
                val expiresAt = jsonResponse.getLong("expiresAt")
                val message = jsonResponse.optString("message", "Verification code sent to your email.")
                Result.success(OtpSession(email.trim().lowercase(), token, expiresAt, message))
            } else {
                val msg = jsonResponse.optString("message", "Failed to send verification code.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not connect to OTP service: ${e.localizedMessage ?: "Network error"}"))
        }
    }

    /**
     * Cryptographically validates the entered OTP against the signed HMAC token
     * on the Vercel server.
     */
    suspend fun verifyOtp(
        email: String,
        otp: String,
        token: String,
        expiresAt: Long
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val endpoint = "$vercelBaseUrl/api/verify-otp"
            val url = URL(endpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15000
                readTimeout = 15000
                doOutput = true
                doInput = true
            }

            val jsonBody = JSONObject().apply {
                put("email", email.trim().lowercase())
                put("otp", otp.trim())
                put("token", token)
                put("expiresAt", expiresAt)
            }

            OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
                writer.write(jsonBody.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
            val responseText = BufferedReader(InputStreamReader(stream, "UTF-8")).use { it.readText() }

            val jsonResponse = JSONObject(responseText)
            val success = jsonResponse.optBoolean("success", false)

            if (success) {
                Result.success(true)
            } else {
                val msg = jsonResponse.optString("message", "Invalid verification code.")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Verification service error: ${e.localizedMessage ?: "Network error"}"))
        }
    }
}
