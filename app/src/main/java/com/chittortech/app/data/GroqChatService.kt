package com.chittortech.app.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender { USER, BOT }

class GroqChatService(private val context: Context) {

    private val prefs = context.getSharedPreferences("chittortech_groq_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "GroqChatService"
        private const val DEFAULT_FALLBACK_KEY = ""

        // Primary: Ultra-fast 20B model (sub-second, preserves token bucket for continuous multi-turn chats)
        private const val PRIMARY_MODEL   = "openai/gpt-oss-20b"
        // Secondary: High-speed 27B model with separate rate-limit pool on Groq
        private const val SECONDARY_MODEL = "qwen/qwen3.8-27b"
        // Tertiary fallback
        private const val TERTIARY_MODEL  = "openai/gpt-oss-120b"

        private const val CONNECT_TIMEOUT_MS = 5_000
        private const val READ_TIMEOUT_MS    = 8_000
        // 1800 tokens: Allows long comprehensive answers with full tables while staying under Groq's 8000 TPM limit
        private const val MAX_OUTPUT_TOKENS  = 1800
    }

    fun getApiKey(): String {
        val saved = prefs.getString("groq_api_key", "").orEmpty().trim()
        if (saved.isNotBlank()) return saved
        val buildKey = try { com.chittortech.app.BuildConfig.GROQ_API_KEY.trim() } catch (e: Exception) { "" }
        if (buildKey.isNotBlank()) return buildKey
        return DEFAULT_FALLBACK_KEY
    }

    fun saveApiKey(key: String) {
        prefs.edit().putString("groq_api_key", key.trim()).apply()
    }

    /**
     * Send a user message to Groq and return the AI response in sub-second time.
     *
     * @param userMessage   The current user input.
     * @param history       Conversation history (sanitized, max last 8 messages).
     * @param currentScreen The currently visible screen name for context injection.
     * @param userName      Visitor name from lead capture. Defaults to "Guest".
     */
    suspend fun sendMessage(
        userMessage: String,
        history: List<ChatMessage>,
        currentScreen: String = "ChittorTech Mobile App",
        userName: String = "Guest"
    ): String = withContext(Dispatchers.IO) {

        val configuredKey = getApiKey()

        // Build context-aware system prompt with screen + user injection
        val systemPrompt = ChittorTechKnowledgeBase.buildSystemPrompt(
            currentScreen = currentScreen,
            userName = userName
        )

        val messagesArray = JSONArray().apply {
            // 1. Context-aware system prompt
            put(JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            })

            // 2. Sanitized chat history (exclude current user message to avoid duplicate turn)
            val filteredHistory = history
                .filter { it.text.trim() != userMessage.trim() }
                .takeLast(6)

            for (msg in filteredHistory) {
                val sanitized = msg.text
                    .replace("\\[[^\\]]*ACTION:[^\\]]*\\]".toRegex(RegexOption.IGNORE_CASE), "")
                    .replace("\\s{2,}".toRegex(), " ")
                    .trim()
                    .take(800)
                if (sanitized.isNotBlank()) {
                    put(JSONObject().apply {
                        put("role", if (msg.sender == MessageSender.USER) "user" else "assistant")
                        put("content", sanitized)
                    })
                }
            }

            // 3. Current user message (added exactly once)
            put(JSONObject().apply {
                put("role", "user")
                put("content", userMessage)
            })
        }

        // ── Attempt 1: Ultra-fast Primary (openai/gpt-oss-20b) ──
        var response = callGroqApi(configuredKey, PRIMARY_MODEL, messagesArray)

        // ── Attempt 2: Secondary with independent rate-limit pool (qwen/qwen3.8-27b) ──
        if (response == null) {
            Log.w(TAG, "Primary model failed or rate-limited, switching to $SECONDARY_MODEL")
            response = callGroqApi(configuredKey, SECONDARY_MODEL, messagesArray)
        }

        // ── Attempt 3: Tertiary fallback (openai/gpt-oss-120b) ──
        if (response == null) {
            Log.w(TAG, "Secondary model failed, switching to $TERTIARY_MODEL")
            response = callGroqApi(configuredKey, TERTIARY_MODEL, messagesArray)
        }

        if (!response.isNullOrBlank()) return@withContext response

        // ── Instant Rich Local Fallback (Guarantees zero hanging) ──
        Log.w(TAG, "All cloud models timed out / rate-limited — serving instant local fallback.")
        return@withContext ChittorTechKnowledgeBase.getLocalFallbackResponse(userMessage)
    }

    private fun callGroqApi(apiKey: String, model: String, messagesArray: JSONArray): String? {
        return try {
            val url = URL("https://api.groq.com/openai/v1/chat/completions")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $apiKey")
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout    = READ_TIMEOUT_MS
                doOutput = true
                doInput  = true
            }

            val payload = JSONObject().apply {
                put("model", model)
                put("messages", messagesArray)
                put("temperature", 0.7)
                put("max_tokens", MAX_OUTPUT_TOKENS)
            }

            OutputStreamWriter(conn.outputStream).use { w -> w.write(payload.toString()); w.flush() }

            val code = conn.responseCode
            if (code == HttpURLConnection.HTTP_OK) {
                val body = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                val choices = JSONObject(body).optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val content = choices.getJSONObject(0).optJSONObject("message")?.optString("content")
                    if (!content.isNullOrBlank()) return content
                }
            } else if (code == 429) {
                Log.w(TAG, "Groq 429 rate limit hit for $model")
            } else {
                val err = conn.errorStream?.let { BufferedReader(InputStreamReader(it)).use { r -> r.readText() } }.orEmpty()
                Log.e(TAG, "Groq API error ($model, HTTP $code): $err")
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Exception calling Groq API ($model): ${e.message}")
            null
        }
    }
}
