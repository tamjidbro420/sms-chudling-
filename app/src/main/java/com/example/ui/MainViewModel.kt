package com.example.ui

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CustomFormatEntity
import com.example.data.local.SmsLogEntity
import com.example.data.preferences.StorageHelper
import com.example.data.repository.SmsLogRepository
import com.example.data.utils.ExtractedFields
import com.example.data.utils.FormatAutoDetector
import com.example.data.utils.SmsParser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val storageHelper = StorageHelper.getInstance(application)
    private val repository = SmsLogRepository(application)

    val webhookUrl: StateFlow<String> = storageHelper.webhookUrl
    val secretToken: StateFlow<String> = storageHelper.secretToken
    val syncActive: StateFlow<Boolean> = storageHelper.syncActive
    val themeMode: StateFlow<String> = storageHelper.themeMode

    private val _selectedTab = MutableStateFlow(0) // 0 = Console, 1 = Formats, 2 = Settings
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val allLogs: StateFlow<List<SmsLogEntity>> = repository.allLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val customFormats: StateFlow<List<CustomFormatEntity>> = repository.allCustomFormats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Dynamic list of all payment gateway names in DB + standard gateways
    val availableGateways: StateFlow<List<String>> = customFormats.map { formats ->
        val defaultList = listOf("bKash", "Nagad", "Rocket", "Upay", "CellFin", "SureCash")
        val customGws = formats.map { it.gateway }.distinct().filter { it.isNotBlank() }
        (defaultList + customGws).distinct()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf("bKash", "Nagad", "Rocket", "Upay", "CellFin", "SureCash")
    )

    private val _selectedGatewayFilter = MutableStateFlow("bKash")
    val selectedGatewayFilter: StateFlow<String> = _selectedGatewayFilter.asStateFlow()

    val successCount: StateFlow<Int> = repository.successCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val todaySuccessCount: StateFlow<Int> = repository.getTodaySuccessCount(getStartOfDayTimestamp())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val pendingCount: StateFlow<Int> = repository.pendingCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val totalSuccessAmount: StateFlow<Double> = repository.totalSuccessAmount
        .combine(MutableStateFlow(0.0)) { amount, _ -> amount ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val todaySuccessAmount: StateFlow<Double> = repository.getTodaySuccessAmount(getStartOfDayTimestamp())
        .combine(MutableStateFlow(0.0)) { amount, _ -> amount ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    private val _isIgnoringBattery = MutableStateFlow(storageHelper.isIgnoringBatteryOptimizations())
    val isIgnoringBattery: StateFlow<Boolean> = _isIgnoringBattery.asStateFlow()

    private val _hasReceiveSmsPermission = MutableStateFlow(checkPermission(Manifest.permission.RECEIVE_SMS))
    val hasReceiveSmsPermission: StateFlow<Boolean> = _hasReceiveSmsPermission.asStateFlow()

    private val _hasReadSmsPermission = MutableStateFlow(checkPermission(Manifest.permission.READ_SMS))
    val hasReadSmsPermission: StateFlow<Boolean> = _hasReadSmsPermission.asStateFlow()

    private val _hasNotificationPermission = MutableStateFlow(
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            checkPermission(Manifest.permission.POST_NOTIFICATIONS)
        } else true
    )
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission.asStateFlow()

    private val _hasSmsPermission = MutableStateFlow(checkSmsPermissionInternal())
    val hasSmsPermission: StateFlow<Boolean> = _hasSmsPermission.asStateFlow()

    private val _networkStatus = MutableStateFlow(checkNetworkStatusInternal())
    val networkStatus: StateFlow<String> = _networkStatus.asStateFlow()

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    // Format Generator State
    private val _sampleSmsInput = MutableStateFlow("You have received Tk 35.00 from 01609441417. Fee Tk 0.00. Balance Tk 649.00. TrxID DGP1PK3V5R at 25/07/2026 17:28")
    val sampleSmsInput: StateFlow<String> = _sampleSmsInput.asStateFlow()

    private val _selectedGateway = MutableStateFlow("Auto")
    val selectedGateway: StateFlow<String> = _selectedGateway.asStateFlow()

    private val _formatNameInput = MutableStateFlow("bKash Payment Format")
    val formatNameInput: StateFlow<String> = _formatNameInput.asStateFlow()

    private val _autoDetectedFields = MutableStateFlow<ExtractedFields?>(null)
    val autoDetectedFields: StateFlow<ExtractedFields?> = _autoDetectedFields.asStateFlow()

    init {
        viewModelScope.launch {
            repository.perform48HourCleanup()
            repository.seedSampleLogsIfEmpty()
            updateAutoDetectedFields()
        }
        setupAutomaticNetworkCallback()
    }

    fun setSelectedGatewayFilter(gateway: String) {
        _selectedGatewayFilter.value = gateway
    }

    private fun setupAutomaticNetworkCallback() {
        try {
            val context = getApplication<Application>()
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _networkStatus.value = checkNetworkStatusInternal()
                    viewModelScope.launch {
                        // Automatic auto-send saved offline messages when network is restored!
                        repository.retryAllPendingLogs()
                    }
                }

                override fun onLost(network: Network) {
                    _networkStatus.value = "Offline"
                }
            })
        } catch (_: Exception) {
            // Callback registration fallback
        }
    }

    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun onSampleSmsChanged(input: String) {
        _sampleSmsInput.value = input
        updateAutoDetectedFields()
    }

    fun onSelectedGatewayChanged(gateway: String) {
        _selectedGateway.value = gateway
        updateAutoDetectedFields()
    }

    fun onFormatNameChanged(name: String) {
        _formatNameInput.value = name
    }

    private fun updateAutoDetectedFields() {
        val sample = _sampleSmsInput.value
        val gateway = _selectedGateway.value
        _autoDetectedFields.value = FormatAutoDetector.analyzeSampleSms(sample, gateway)
    }

    fun saveCustomFormat() {
        viewModelScope.launch {
            val sample = _sampleSmsInput.value.trim()
            if (sample.isBlank()) {
                emitEvent("Please enter a sample SMS first")
                return@launch
            }

            val extracted = FormatAutoDetector.analyzeSampleSms(sample, _selectedGateway.value)
            val name = _formatNameInput.value.ifBlank { "${extracted.gateway} Custom Format" }

            val newFormat = CustomFormatEntity(
                gateway = extracted.gateway,
                formatName = name,
                sampleSms = sample,
                regexPattern = extracted.generatedPattern,
                isEnabled = true
            )

            repository.addCustomFormat(newFormat)
            emitEvent("Custom SMS Format saved successfully")
        }
    }

    fun toggleCustomFormat(format: CustomFormatEntity) {
        viewModelScope.launch {
            val updated = format.copy(isEnabled = !format.isEnabled)
            repository.updateCustomFormat(updated)
            emitEvent("Format status updated: ${if (updated.isEnabled) "Enabled" else "Disabled"}")
        }
    }

    fun deleteCustomFormat(format: CustomFormatEntity) {
        viewModelScope.launch {
            repository.deleteCustomFormat(format)
            emitEvent("Custom format deleted")
        }
    }

    fun saveConfiguration(url: String, token: String) {
        if (url.isBlank()) {
            emitEvent("Webhook URL cannot be empty")
            return
        }
        storageHelper.saveConfiguration(url, token)
        emitEvent("Configuration saved successfully")
    }

    fun setSyncActive(active: Boolean) {
        storageHelper.setSyncActive(active)
        emitEvent(if (active) "Financial Sync Service Activated" else "Financial Sync Service Paused")
    }

    fun setThemeMode(mode: String) {
        storageHelper.setThemeMode(mode)
    }

    fun refreshBatteryOptimizationStatus() {
        _isIgnoringBattery.value = storageHelper.isIgnoringBatteryOptimizations()
        _hasReceiveSmsPermission.value = checkPermission(Manifest.permission.RECEIVE_SMS)
        _hasReadSmsPermission.value = checkPermission(Manifest.permission.READ_SMS)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            _hasNotificationPermission.value = checkPermission(Manifest.permission.POST_NOTIFICATIONS)
        }
        _hasSmsPermission.value = checkSmsPermissionInternal()
        _networkStatus.value = checkNetworkStatusInternal()
    }

    private fun checkPermission(permission: String): Boolean {
        val context = getApplication<Application>()
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun refreshDiagnostics() {
        refreshBatteryOptimizationStatus()
        emitEvent("System diagnostics status updated")
    }

    private fun getStartOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun checkSmsPermissionInternal(): Boolean {
        val context = getApplication<Application>()
        val receiveSms = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
        val readSms = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
        return receiveSms && readSms
    }

    private fun checkNetworkStatusInternal(): String {
        val context = getApplication<Application>()
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (cm != null) {
            val activeNetwork = cm.activeNetwork
            val caps = cm.getNetworkCapabilities(activeNetwork)
            if (caps != null) {
                return when {
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Online (Wi-Fi)"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Online (Cellular)"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Online (Ethernet)"
                    else -> "Online"
                }
            }
        }
        return "Offline"
    }

    fun retryPendingLogs() {
        viewModelScope.launch {
            emitEvent("Retrying pending & failed webhooks...")
            repository.retryAllPendingLogs()
            emitEvent("Retry attempt completed")
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllLogs()
            emitEvent("All logs cleared")
        }
    }

    fun simulateTestSms(senderHeader: String, messageBody: String) {
        viewModelScope.launch {
            val enabledFormats = repository.getEnabledFormats()
            val parsed = SmsParser.parseSms(senderHeader, messageBody, enabledFormats)
            if (parsed == null) {
                emitEvent("SMS ignored: Does not match any active payment format")
                return@launch
            }

            emitEvent("Processing test SMS from ${parsed.serviceName}...")
            val result = repository.processAndSendSms(parsed)
            if (result.status == "SUCCESS") {
                emitEvent("Test SMS sent successfully (HTTP ${result.responseCode})")
            } else {
                emitEvent("Test SMS failed: ${result.errorMessage}")
            }
        }
    }

    private fun emitEvent(message: String) {
        viewModelScope.launch {
            _eventFlow.emit(message)
        }
    }
}
