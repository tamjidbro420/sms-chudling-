package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.PowerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StorageHelper(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _webhookUrl = MutableStateFlow(getWebhookUrl())
    val webhookUrl: StateFlow<String> = _webhookUrl.asStateFlow()

    private val _secretToken = MutableStateFlow(getSecretToken())
    val secretToken: StateFlow<String> = _secretToken.asStateFlow()

    private val _syncActive = MutableStateFlow(isSyncActive())
    val syncActive: StateFlow<Boolean> = _syncActive.asStateFlow()

    private val _themeMode = MutableStateFlow(getThemeMode())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun getWebhookUrl(): String {
        return prefs.getString(KEY_WEBHOOK_URL, DEFAULT_WEBHOOK_URL) ?: DEFAULT_WEBHOOK_URL
    }

    fun setWebhookUrl(url: String) {
        prefs.edit().putString(KEY_WEBHOOK_URL, url.trim()).apply()
        _webhookUrl.value = url.trim()
    }

    fun getSecretToken(): String {
        return prefs.getString(KEY_SECRET_TOKEN, DEFAULT_SECRET_TOKEN) ?: DEFAULT_SECRET_TOKEN
    }

    fun setSecretToken(token: String) {
        prefs.edit().putString(KEY_SECRET_TOKEN, token.trim()).apply()
        _secretToken.value = token.trim()
    }

    fun isSyncActive(): Boolean {
        return prefs.getBoolean(KEY_SYNC_ACTIVE, DEFAULT_SYNC_ACTIVE)
    }

    fun setSyncActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_SYNC_ACTIVE, active).apply()
        _syncActive.value = active
    }

    fun getThemeMode(): String {
        return prefs.getString(KEY_THEME_MODE, DEFAULT_THEME_MODE) ?: DEFAULT_THEME_MODE
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
        _themeMode.value = mode
    }

    fun saveConfiguration(url: String, token: String) {
        setWebhookUrl(url)
        setSecretToken(token)
    }

    fun isIgnoringBatteryOptimizations(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: true
    }

    companion object {
        private const val PREFS_NAME = "financial_sync_prefs"
        const val KEY_WEBHOOK_URL = "webhook_target_url"
        const val KEY_SECRET_TOKEN = "secret_authorization_token"
        const val KEY_SYNC_ACTIVE = "sync_status_active"
        const val KEY_THEME_MODE = "theme_mode_setting"

        const val DEFAULT_WEBHOOK_URL = "https://host-kira.onrender.com/api/sms-webhook"
        const val DEFAULT_SECRET_TOKEN = "z_dot_secret_90"
        const val DEFAULT_SYNC_ACTIVE = true
        const val DEFAULT_THEME_MODE = "DARK" // "DARK", "LIGHT", "SYSTEM"

        @Volatile
        private var instance: StorageHelper? = null

        fun getInstance(context: Context): StorageHelper {
            return instance ?: synchronized(this) {
                instance ?: StorageHelper(context.applicationContext).also { instance = it }
            }
        }
    }
}
