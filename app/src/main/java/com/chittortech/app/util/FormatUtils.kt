package com.chittortech.app.util

import java.text.NumberFormat
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
