package com.chittortech.app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ─── Top-Level Navigation Keys ───────────────────────────────────────────────

@Serializable data object Login : NavKey

// ─── Client Portal Keys ───────────────────────────────────────────────────────

@Serializable data object ClientHome : NavKey

// ─── Admin Command Center Keys ────────────────────────────────────────────────

@Serializable data object AdminHome : NavKey
