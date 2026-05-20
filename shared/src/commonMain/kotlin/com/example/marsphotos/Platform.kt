package com.example.marsphotos

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

// ==================== NUEVO ====================
expect class AppPreferences()

expect fun getAppPreferences(): AppPreferences

const val PREF_COOKIES = "PREF_COOKIES"