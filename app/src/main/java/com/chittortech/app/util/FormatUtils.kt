package com.chittortech.app.util

import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.fmtAmount(): String {
    return when {
        this >= 10000000 -> "%.2f Cr".format(this / 10000000.0)
        this >= 100000 -> "%.1f L".format(this / 100000.0)
        this >= 1000 -> "%.1f K".format(this / 1000.0)
        else -> NumberFormat.getNumberInstance(Locale("en", "IN")).format(this)
    }
}

fun Long.fmtK(): String = fmtAmount()

/**
 * Universal date formatter that guarantees "dd-MM-yyyy" display format across the entire app.
 * Automatically parses "yyyy-MM-dd", "yyyy/MM/dd", ISO strings, etc. and formats to "dd-MM-yyyy".
 */
fun formatDateDDMMYYYY(dateStr: String, fallbackToTodayIfBlank: Boolean = false): String {
    if (dateStr.isBlank()) {
        return if (fallbackToTodayIfBlank) {
            SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Date())
        } else {
            ""
        }
    }
    val trimmed = dateStr.trim()
    // Already in dd-MM-yyyy format
    if (trimmed.matches(Regex("""^\d{2}-\d{2}-\d{4}$"""))) {
        return trimmed
    }
    // Handle dd/MM/yyyy
    if (trimmed.matches(Regex("""^\d{2}/\d{2}/\d{4}$"""))) {
        return trimmed.replace('/', '-')
    }
    // Fast path for yyyy-MM-dd or yyyy/MM/dd
    if (trimmed.matches(Regex("""^\d{4}[-/]\d{2}[-/]\d{2}$"""))) {
        val sep = if (trimmed.contains('-')) "-" else "/"
        val parts = trimmed.split(sep)
        return "${parts[2]}-${parts[1]}-${parts[0]}"
    }
    // Parsing various standard formats
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd",
        "dd-MM-yyyy",
        "dd MMM yyyy",
        "dd/MM/yyyy"
    )
    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
            sdf.isLenient = false
            val parsed = sdf.parse(trimmed)
            if (parsed != null) {
                return SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(parsed)
            }
        } catch (_: Exception) {}
    }
    return trimmed
}

/**
 * Formats a Firebase Timestamp to "dd-MM-yyyy"
 */
fun formatTimestampDDMMYYYY(timestamp: Timestamp?): String {
    if (timestamp == null) return ""
    return try {
        SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(timestamp.toDate())
    } catch (_: Exception) {
        ""
    }
}

/**
 * Formats a Firebase Timestamp to "dd-MM-yyyy, hh:mm a"
 */
fun formatTimestampDDMMYYYYTime(timestamp: Timestamp?): String {
    if (timestamp == null) return ""
    return try {
        SimpleDateFormat("dd-MM-yyyy, hh:mm a", Locale.ENGLISH).format(timestamp.toDate())
    } catch (_: Exception) {
        ""
    }
}
