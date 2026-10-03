package com.chittortech.app.data

import android.content.Context
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

enum class MessageSender {
    USER,
    BOT
}

class GroqChatService(private val context: Context) {

    private val prefs = context.getSharedPreferences("chittortech_groq_prefs", Context.MODE_PRIVATE)

    fun getApiKey(): String {
        return prefs.getString("groq_api_key", "").orEmpty()
    }

    fun saveApiKey(key: String) {
        prefs.edit().putString("groq_api_key", key.trim()).apply()
    }

    suspend fun sendMessage(userMessage: String, history: List<ChatMessage>): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()

        // If no Groq API Key has been supplied yet, use the intelligent Knowledge Base fallback
        if (apiKey.isBlank()) {
            return@withContext ChittorTechKnowledgeBase.getLocalFallbackResponse(userMessage)
        }

        try {
            val url = URL("https://api.groq.com/openai/v1/chat/completions")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $apiKey")
                connectTimeout = 15000
                readTimeout = 20000
                doOutput = true
                doInput = true
            }

            // Build conversation history
            val messagesArray = JSONArray()

            // System instructions with full knowledge base
            messagesArray.put(JSONObject().apply {
                put("role", "system")
                put("content", ChittorTechKnowledgeBase.SYSTEM_PROMPT)
            })

            // Last 6 context messages for conversation coherence
            val recentHistory = history.takeLast(6)
            for (msg in recentHistory) {
                messagesArray.put(JSONObject().apply {
                    put("role", if (msg.sender == MessageSender.USER) "user" else "assistant")
                    put("content", msg.text)
                })
            }

            // Current message
            messagesArray.put(JSONObject().apply {
                put("role", "user")
                put("content", userMessage)
            })

            val payload = JSONObject().apply {
                put("model", "llama-3.3-70b-versatile")
                put("messages", messagesArray)
                put("temperature", 0.5)
                put("max_tokens", 800)
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val responseStr = reader.readText()
                reader.close()

                val jsonResponse = JSONObject(responseStr)
                val choices = jsonResponse.getJSONArray("choices")
                if (choices.length() > 0) {
                    val messageObj = choices.getJSONObject(0).getJSONObject("message")
                    return@withContext messageObj.getString("content")
                }
            } else {
                // If API returns an error (e.g. invalid key or rate limit), fall back gracefully
                val errorStream = connection.errorStream
                if (errorStream != null) {
                    val errorReader = BufferedReader(InputStreamReader(errorStream))
                    val err = errorReader.readText()
                    errorReader.close()
                    android.util.Log.e("GroqChatService", "Groq error: $err")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("GroqChatService", "Network error calling Groq", e)
        }

        // Fallback to local knowledge base on any network or API issue
        return@withContext ChittorTechKnowledgeBase.getLocalFallbackResponse(userMessage)
    }
}
