package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.CustomFormatEntity
import com.example.data.local.SmsLogEntity
import com.example.data.utils.ExtractedFields
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onRequestBatteryOptimization: () -> Unit = {},
    onRequestSmsPermissions: () -> Unit = {},
    onOpenAppSettings: () -> Unit = {}
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val syncActive by viewModel.syncActive.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { eventMsg ->
            scope.launch {
                snackbarHostState.showSnackbar(eventMsg)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF5B7692),
                                            Color(0xFF48607A),
                                            Color(0xFF364C64)
                                        )
                                    )
                                )
                                .border(1.dp, Color(0xFF7E9BB8).copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(3) {
                                        Box(
                                            modifier = Modifier
                                                .size(3.5.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF2C4056))
                                        )
                                    }
                                }
                            }
                        }
                        Column {
                            Text(
                                text = "SMS Forwarder Pro",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GlowingTerminalStatusDot(
                                    isActive = syncActive,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (syncActive) "Live SMS Sync Active" else "Sync Paused",
                                    fontSize = 11.sp,
                                    color = if (syncActive) EmeraldGreen else WarningAmber,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (syncActive) "ON" else "OFF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (syncActive) EmeraldGreen else WarningAmber
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = syncActive,
                            onCheckedChange = { viewModel.setSyncActive(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmeraldGreen,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    icon = { Icon(Icons.Default.Terminal, contentDescription = "Console") },
                    label = { Text("Console", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricBlue,
                        selectedTextColor = ElectricBlue,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = ElectricBlue.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    icon = { Icon(Icons.Default.Code, contentDescription = "Formats") },
                    label = { Text("Formats", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanAccent,
                        selectedTextColor = CyanAccent,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = CyanAccent.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricBlue,
                        selectedTextColor = ElectricBlue,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = ElectricBlue.copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> ConsoleTab(
                    viewModel = viewModel,
                    onRequestBatteryOptimization = onRequestBatteryOptimization,
                    onRequestSmsPermissions = onRequestSmsPermissions,
                    onOpenAppSettings = onOpenAppSettings
                )
                1 -> FormatsTab(viewModel = viewModel)
                2 -> SettingsTab(
                    viewModel = viewModel,
                    onRequestBatteryOptimization = onRequestBatteryOptimization,
                    onRequestSmsPermissions = onRequestSmsPermissions,
                    onOpenAppSettings = onOpenAppSettings
                )
            }
        }
    }
}

// ==========================================
// TAB 0: CONSOLE & LIVE LOGS
// ==========================================
@Composable
fun ConsoleTab(
    viewModel: MainViewModel,
    onRequestBatteryOptimization: () -> Unit = {},
    onRequestSmsPermissions: () -> Unit = {},
    onOpenAppSettings: () -> Unit = {}
) {
    val syncActive by viewModel.syncActive.collectAsStateWithLifecycle()
    val hasSmsPermission by viewModel.hasSmsPermission.collectAsStateWithLifecycle()
    val hasReceiveSms by viewModel.hasReceiveSmsPermission.collectAsStateWithLifecycle()
    val hasReadSms by viewModel.hasReadSmsPermission.collectAsStateWithLifecycle()
    val logs by viewModel.allLogs.collectAsStateWithLifecycle()
    val successCount by viewModel.successCount.collectAsStateWithLifecycle()
    val todaySuccessCount by viewModel.todaySuccessCount.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingCount.collectAsStateWithLifecycle()
    val totalSuccessAmount by viewModel.totalSuccessAmount.collectAsStateWithLifecycle()
    val todaySuccessAmount by viewModel.todaySuccessAmount.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showSimulator by remember { mutableStateOf(false) }

    val filteredLogs = remember(logs, searchQuery, selectedFilter) {
        logs.filter { log ->
            val matchesFilter = when (selectedFilter) {
                "SUCCESS" -> log.status == "SUCCESS"
                "PENDING" -> log.status == "PENDING" || log.status == "FAILED"
                "bKash" -> log.serviceName.contains("bKash", ignoreCase = true)
                "Nagad" -> log.serviceName.contains("Nagad", ignoreCase = true)
                "Rocket" -> log.serviceName.contains("Rocket", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    log.trxId.contains(searchQuery, ignoreCase = true) ||
                    log.senderNumber.contains(searchQuery, ignoreCase = true) ||
                    log.rawSms.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Dynamic SMS Runtime Permission Banner
        if (!hasSmsPermission) {
            item {
                DynamicSmsPermissionBanner(
                    hasReceiveSms = hasReceiveSms,
                    hasReadSms = hasReadSms,
                    onRequestPermissions = onRequestSmsPermissions,
                    onOpenAppSettings = onOpenAppSettings
                )
            }
        }

        // Section 1: Delivery Counters Overview
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Delivery Count Overview",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Total Delivery",
                        value = "$successCount",
                        subtitle = "All-Time Delivered",
                        color = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Today Delivery",
                        value = "$todaySuccessCount",
                        subtitle = "Delivered Today",
                        color = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Pending Queue",
                        value = "$pendingCount",
                        subtitle = "Retry Queue",
                        color = if (pendingCount > 0) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section 2: Sent Amount (Separate Section)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Total & Today Sent Financial Volume",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Total Sent Amount",
                        value = "Tk ${String.format(Locale.US, "%.2f", totalSuccessAmount)}",
                        subtitle = "All-Time Transferred",
                        color = CyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Today Sent Amount",
                        value = "Tk ${String.format(Locale.US, "%.2f", todaySuccessAmount)}",
                        subtitle = "Today Transferred",
                        color = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Live Log Controls Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlowingTerminalStatusDot(isActive = syncActive)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Terminal Console",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { viewModel.retryPendingLogs() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue.copy(alpha = 0.2f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Retry", fontSize = 11.sp, color = ElectricBlue)
                            }

                            Button(
                                onClick = { showSimulator = !showSimulator },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent.copy(alpha = 0.2f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (showSimulator) "Hide Sim" else "+ Test Sim", fontSize = 11.sp, color = CyanAccent)
                            }
                        }
                    }

                    // Search Input
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search TrxID, Sender, or Text...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )

                    // Filter Chips
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ALL", "SUCCESS", "PENDING", "bKash", "Nagad", "Rocket").forEach { filter ->
                            val isSelected = selectedFilter == filter
                            val selectedBrandColor = when (filter) {
                                "bKash" -> BkashPink
                                "Nagad" -> NagadOrange
                                "Rocket" -> RocketPurple
                                "SUCCESS" -> EmeraldGreen
                                "PENDING" -> WarningAmber
                                else -> ElectricBlue
                            }
                            val bgColor = if (isSelected) selectedBrandColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgColor)
                                    .clickable { selectedFilter = filter }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = filter, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
                            }
                        }
                    }
                }
            }
        }

        // Test Simulator Expansion
        if (showSimulator) {
            item {
                TestSimulatorCard(
                    viewModel = viewModel,
                    onClose = { showSimulator = false }
                )
            }
        }

        // Log Items List
        if (filteredLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Article,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No SMS logs match current filter", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(filteredLogs, key = { it.id }) { log ->
                SmsLogItemCard(log = log)
            }
        }
    }
}

// ==========================================
// TAB 1: FORMATS & AUTO-GENERATOR
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormatsTab(viewModel: MainViewModel) {
    val customFormats by viewModel.customFormats.collectAsStateWithLifecycle()
    val availableGateways by viewModel.availableGateways.collectAsStateWithLifecycle()
    val selectedFilterGateway by viewModel.selectedGatewayFilter.collectAsStateWithLifecycle()
    val sampleSmsInput by viewModel.sampleSmsInput.collectAsStateWithLifecycle()
    val selectedGateway by viewModel.selectedGateway.collectAsStateWithLifecycle()
    val formatNameInput by viewModel.formatNameInput.collectAsStateWithLifecycle()
    val extractedFields by viewModel.autoDetectedFields.collectAsStateWithLifecycle()

    var showGeneratorForm by remember { mutableStateOf(false) }

    val filteredFormats = remember(customFormats, selectedFilterGateway) {
        if (selectedFilterGateway == "All") customFormats
        else customFormats.filter { it.gateway.equals(selectedFilterGateway, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Gateway Selection Header Row
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyanAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ListAlt, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Select Payment Gateway Provider", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Click gateway name to manage & add formats", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gateway Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (listOf("All") + availableGateways).forEach { gw ->
                            val isSelected = selectedFilterGateway.equals(gw, ignoreCase = true)
                            val gwColor = when (gw.uppercase(Locale.ROOT)) {
                                "BKASH" -> BkashPink
                                "NAGAD" -> NagadOrange
                                "ROCKET" -> RocketPurple
                                "UPAY" -> CyanAccent
                                else -> ElectricBlue
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) gwColor else MaterialTheme.colorScheme.background)
                                    .border(1.dp, if (isSelected) gwColor else MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.setSelectedGatewayFilter(gw)
                                        if (gw != "All") {
                                            viewModel.onSelectedGatewayChanged(gw)
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = gw,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (gw != "All") {
                                        val count = customFormats.count { it.gateway.equals(gw, ignoreCase = true) }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) Color.White.copy(alpha = 0.3f) else gwColor.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "$count",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else gwColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Gateway Banner & Action Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanAccent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (selectedFilterGateway == "All") "All Gateway Formats" else "$selectedFilterGateway Gateway",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Text(
                            text = "${filteredFormats.size} format(s) configured",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showGeneratorForm = !showGeneratorForm },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (showGeneratorForm) RoseRed else EmeraldGreen),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (showGeneratorForm) Icons.Default.Clear else Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showGeneratorForm) "Hide Form" else "+ Add Format",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Auto Generator Form Card (Expandable or always available)
        if (showGeneratorForm) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                    .background(CyanAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("New Format Generator", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Paste sample SMS to auto-detect parser regex", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.onSampleSmsChanged("You have received Tk 35.00 from 01609441417. Fee Tk 0.00. Balance Tk 649.00. TrxID DGP1PK3V5R at 25/07/2026 17:28")
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue.copy(alpha = 0.2f)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Sample", fontSize = 10.sp, color = ElectricBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Target Gateway Input / Selection
                    Text("Selected Gateway Provider:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Auto", "bKash", "Nagad", "Rocket", "Upay", "CellFin", "SureCash", "Custom").forEach { gw ->
                            val isSelected = selectedGateway.equals(gw, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyanAccent else MaterialTheme.colorScheme.background)
                                    .border(1.dp, if (isSelected) CyanAccent else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.onSelectedGatewayChanged(gw) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = gw,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Format Name Input
                    OutlinedTextField(
                        value = formatNameInput,
                        onValueChange = { viewModel.onFormatNameChanged(it) },
                        label = { Text("Format Label / Title", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sample SMS Input Area
                    OutlinedTextField(
                        value = sampleSmsInput,
                        onValueChange = { viewModel.onSampleSmsChanged(it) },
                        label = { Text("Input Example SMS Message", fontSize = 11.sp) },
                        minLines = 3,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Extracted Fields Live Preview Card
                    extractedFields?.let { fields ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, CyanAccent.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Auto-Detected Data Fields:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                                    Text("Gateway: ${fields.gateway}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                                }
                                Spacer(modifier = Modifier.height(6.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ExtractedChip(label = "Amount", value = fields.amount?.let { "Tk $it" } ?: "Not Found", color = EmeraldGreen)
                                    ExtractedChip(label = "Sender", value = fields.senderNumber ?: "Not Found", color = ElectricBlue)
                                    ExtractedChip(label = "Fee", value = fields.fee?.let { "Tk $it" } ?: "Tk 0.0", color = WarningAmber)
                                    ExtractedChip(label = "Balance", value = fields.balance?.let { "Tk $it" } ?: "N/A", color = CyanAccent)
                                    ExtractedChip(label = "TrxID", value = fields.trxId ?: "Not Found", color = BkashPink)
                                    ExtractedChip(label = "Date", value = fields.date ?: "N/A", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    ExtractedChip(label = "Time", value = fields.time ?: "N/A", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.saveCustomFormat()
                            showGeneratorForm = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Format To Engine", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Past Added Formats List
    if (filteredFormats.isEmpty()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No saved formats for gateway '$selectedFilterGateway'. Click '+ Add Format' to add one.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        items(filteredFormats, key = { it.id }) { format ->
            FormatItemCard(
                format = format,
                onToggle = { viewModel.toggleCustomFormat(format) },
                onDelete = { viewModel.deleteCustomFormat(format) }
            )
        }
    }
}
}

// Helper Chip for Extracted Fields Preview
@Composable
fun ExtractedChip(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.5.dp, color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$label: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
            Text(value, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// Custom Format Card
@Composable
fun FormatItemCard(
    format: CustomFormatEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val gwColor = when (format.gateway.uppercase(Locale.ROOT)) {
        "BKASH" -> BkashPink
        "NAGAD" -> NagadOrange
        "ROCKET" -> RocketPurple
        "UPAY" -> CyanAccent
        else -> ElectricBlue
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(gwColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(format.gateway, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = gwColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = format.formatName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = format.isEnabled,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmeraldGreen,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Format", tint = RoseRed, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Sample SMS:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(8.dp)
            ) {
                Text(
                    text = format.sampleSms,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ==========================================
// TAB 2: SETTINGS & WEBHOOK & THEME
// ==========================================
@Composable
fun SettingsTab(
    viewModel: MainViewModel,
    onRequestBatteryOptimization: () -> Unit = {},
    onRequestSmsPermissions: () -> Unit = {},
    onOpenAppSettings: () -> Unit = {}
) {
    val webhookUrl by viewModel.webhookUrl.collectAsStateWithLifecycle()
    val secretToken by viewModel.secretToken.collectAsStateWithLifecycle()
    val isIgnoringBattery by viewModel.isIgnoringBattery.collectAsStateWithLifecycle()
    val hasSmsPermission by viewModel.hasSmsPermission.collectAsStateWithLifecycle()
    val hasReceiveSms by viewModel.hasReceiveSmsPermission.collectAsStateWithLifecycle()
    val hasReadSms by viewModel.hasReadSmsPermission.collectAsStateWithLifecycle()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsStateWithLifecycle()
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    var urlInput by remember(webhookUrl) { mutableStateOf(webhookUrl) }
    var tokenInput by remember(secretToken) { mutableStateOf(secretToken) }
    var showToken by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // System Guarantee & Readiness Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EmeraldGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("System Guarantee & Permissions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Real-time status of SMS forwarding permissions", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        IconButton(onClick = { viewModel.refreshDiagnostics() }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh Status", tint = ElectricBlue, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. RECEIVE_SMS Permission Status
                    DiagnosticRowItem(
                        icon = Icons.AutoMirrored.Filled.Message,
                        title = "SMS Broadcast Interceptor",
                        subtitle = if (hasReceiveSms) "RECEIVE_SMS Granted (Listens for incoming MFS transactions)" else "RECEIVE_SMS missing: cannot detect incoming messages",
                        isOk = hasReceiveSms,
                        okText = "Active",
                        actionText = "Grant",
                        onAction = onRequestSmsPermissions
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. READ_SMS Permission Status
                    DiagnosticRowItem(
                        icon = Icons.AutoMirrored.Filled.Article,
                        title = "SMS Payload Reader",
                        subtitle = if (hasReadSms) "READ_SMS Granted (Parses TrxID, Amount, Fee & Number)" else "READ_SMS missing: cannot parse message details",
                        isOk = hasReadSms,
                        okText = "Active",
                        actionText = "Grant",
                        onAction = onRequestSmsPermissions
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Notification Permission Status
                    DiagnosticRowItem(
                        icon = Icons.Default.ElectricBolt,
                        title = "Push Notifications & Alerts",
                        subtitle = if (hasNotificationPermission) "POST_NOTIFICATIONS Granted" else "Notifications disabled (Optional)",
                        isOk = hasNotificationPermission,
                        okText = "Active",
                        actionText = "Grant",
                        onAction = onRequestSmsPermissions
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Battery Optimization Permission
                    DiagnosticRowItem(
                        icon = Icons.Default.BatteryChargingFull,
                        title = "Background Battery Optimization",
                        subtitle = if (isIgnoringBattery) "Unrestricted Background Processing Active" else "Optimized (Background delivery delays possible)",
                        isOk = isIgnoringBattery,
                        okText = "Unrestricted",
                        actionText = "Allow",
                        onAction = onRequestBatteryOptimization
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // 5. App Settings Shortcut
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Application Settings Manager", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Manage permissions directly in Android system settings", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = onOpenAppSettings,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Open Settings", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // 6. Network Connectivity Status
                    DiagnosticRowItem(
                        icon = Icons.Default.SwapVert,
                        title = "Network Connectivity",
                        subtitle = if (networkStatus != "Offline") "Connected: Ready to post to webhook" else "Offline: Local DB retry queue will buffer",
                        isOk = networkStatus != "Offline",
                        okText = networkStatus,
                        actionText = "Offline",
                        onAction = { viewModel.refreshDiagnostics() }
                    )
                }
            }
        }
        // Webhook Endpoints Settings Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ElectricBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Target Webhook Server Configuration", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Extracted payment payloads are posted to this URL", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Webhook URL
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("Webhook Endpoint URL", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secret Token
                    OutlinedTextField(
                        value = tokenInput,
                        onValueChange = { tokenInput = it },
                        label = { Text("Bearer / Authorization Secret Token", fontSize = 11.sp) },
                        singleLine = true,
                        visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        trailingIcon = {
                            IconButton(onClick = { showToken = !showToken }) {
                                Icon(
                                    imageVector = if (showToken) Icons.Default.Clear else Icons.Default.Security,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.saveConfiguration(urlInput, tokenInput) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Webhook Credentials", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // App Appearance & Dark Mode Settings Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("App Visual Theme", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("System-wide visual mode and comfort settings", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionButton(
                            title = "Dark",
                            icon = Icons.Default.DarkMode,
                            isSelected = themeMode == "DARK",
                            onClick = { viewModel.setThemeMode("DARK") },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeOptionButton(
                            title = "Light",
                            icon = Icons.Default.LightMode,
                            isSelected = themeMode == "LIGHT",
                            onClick = { viewModel.setThemeMode("LIGHT") },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeOptionButton(
                            title = "System",
                            icon = Icons.Default.Palette,
                            isSelected = themeMode == "SYSTEM",
                            onClick = { viewModel.setThemeMode("SYSTEM") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Battery Optimization Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isIgnoringBattery) EmeraldGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BatteryChargingFull,
                                    contentDescription = null,
                                    tint = if (isIgnoringBattery) EmeraldGreen else WarningAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Battery Optimization Status", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    text = if (isIgnoringBattery) "Unrestricted Background Processing Enabled" else "Restricted (Background SMS delivery delays possible)",
                                    fontSize = 11.sp,
                                    color = if (isIgnoringBattery) EmeraldGreen else WarningAmber
                                )
                            }
                        }

                        if (!isIgnoringBattery) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onRequestBatteryOptimization,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                            ) {
                                Text("Allow", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Data Storage & Auto-Cleanup Info Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Storage & Privacy Policy", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• 48-Hour Auto-Purge: All SMS logs are automatically purged after 48 hours.\n" +
                                "• Zero External Storage: Data stays on device DB and target webhook.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.clearAllLogs() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoseRed.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = RoseRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Clear All Local Logs Now", color = RoseRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Theme Option Selection Button
@Composable
fun ThemeOptionButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.background
    val borderColor = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.outline
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val iconTint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

// Metric Card Component
@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// Log Item Card
@Composable
fun SmsLogItemCard(log: SmsLogEntity) {
    var expanded by remember { mutableStateOf(false) }

    val statusColor = when (log.status) {
        "SUCCESS" -> EmeraldGreen
        "PENDING" -> WarningAmber
        else -> RoseRed
    }

    val gwColor = when {
        log.serviceName.contains("bKash", ignoreCase = true) -> BkashPink
        log.serviceName.contains("Nagad", ignoreCase = true) -> NagadOrange
        log.serviceName.contains("Rocket", ignoreCase = true) -> RocketPurple
        else -> ElectricBlue
    }

    val dateFormatted = remember(log.timestamp) {
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(gwColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(log.serviceName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = gwColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tk ${String.format(Locale.US, "%.2f", log.amount)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(log.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sender: ${log.senderNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("TrxID: ${log.trxId}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Fee: Tk ${log.fee ?: 0.0}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                log.balance?.let { Text("Balance: Tk $it", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(dateFormatted, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Raw SMS Content:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(8.dp)
                ) {
                    Text(log.rawSms, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface, fontFamily = FontFamily.Monospace)
                }

                log.errorMessage?.let { errorMsg ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Error Log: $errorMsg", fontSize = 10.sp, color = RoseRed)
                }
            }
        }
    }
}

// Test Simulator Card Component
@Composable
fun TestSimulatorCard(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    var simSender by remember { mutableStateOf("bKash") }
    var simText by remember { mutableStateOf("You have received Tk 35.00 from 01609441417. Fee Tk 0.00. Balance Tk 649.00. TrxID DGP1PK3V5R at 25/07/2026 17:28") }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyanAccent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Interactive SMS Tester", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Clear, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 1-Click Presets
            Text("Select Quick Presets:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        simSender = "bKash"
                        simText = "You have received Tk 35.00 from 01609441417. Fee Tk 0.00. Balance Tk 649.00. TrxID DGP1PK3V5R at 25/07/2026 17:28"
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BkashPink.copy(alpha = 0.2f)),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    Text("bKash 35 Tk", fontSize = 10.sp, color = BkashPink, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        simSender = "Nagad"
                        simText = "Cash In Tk 1,000.00 from 01711223344 successful. Fee Tk 0.00. Balance Tk 2,500.00. TrxID N7G8H9J0K1"
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NagadOrange.copy(alpha = 0.2f)),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    Text("Nagad 1k", fontSize = 10.sp, color = NagadOrange, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        simSender = "Rocket"
                        simText = "RCV Tk 500.00 from 01812345678-1 Balance Tk 1,200.00 TxnId R6T7Y8U9I0"
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RocketPurple.copy(alpha = 0.2f)),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    Text("Rocket 500", fontSize = 10.sp, color = RocketPurple, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = simText,
                onValueChange = { simText = it },
                minLines = 2,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.simulateTestSms(simSender, simText)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simulate & Send Webhook", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// Diagnostic Row Item Component
@Composable
fun DiagnosticRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isOk: Boolean,
    okText: String,
    actionText: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isOk) EmeraldGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isOk) EmeraldGreen else WarningAmber,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 10.sp, color = if (isOk) MaterialTheme.colorScheme.onSurfaceVariant else WarningAmber)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (isOk) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(EmeraldGreen.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(okText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                }
            }
        } else {
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(actionText, fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GlowingTerminalStatusDot(
    modifier: Modifier = Modifier,
    isActive: Boolean = true
) {
    if (!isActive) {
        Box(
            modifier = modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(WarningAmber.copy(alpha = 0.5f))
                .border(1.dp, WarningAmber.copy(alpha = 0.8f), CircleShape)
        )
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "terminal_status_pulse")

    // 1. Radar Pulse Expansion (Subtle, low-amplitude expanding wave)
    val radarScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarScale"
    )

    // 2. Radar Wave Alpha (Subtle low-light fade out)
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAlpha"
    )

    // 3. Smooth Dimming Pulse Color Transition (Between gentle dark emerald and radiant green)
    val dynamicDotColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF059669), // Low light soft emerald
        targetValue = Color(0xFF00FF88),  // Radiant bright green
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dynamicDotColor"
    )

    // 4. Glow Intensity / Luminance breathing for smooth halo
    val haloGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.60f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloGlowAlpha"
    )

    // 5. Specular highlight glint opacity
    val specularAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "specularAlpha"
    )

    Box(
        modifier = modifier.size(18.dp),
        contentAlignment = Alignment.Center
    ) {
        // Layer 1: Low-amplitude Subtle Radar Glow Ring
        Box(
            modifier = Modifier
                .size(11.dp)
                .graphicsLayer {
                    scaleX = radarScale
                    scaleY = radarScale
                    alpha = radarAlpha
                }
                .clip(CircleShape)
                .background(Color(0xFF00FF88).copy(alpha = 0.5f))
                .border(1.dp, Color(0xFF00FF88).copy(alpha = radarAlpha), CircleShape)
        )

        // Layer 2: High Light Glow Outer Halo
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(dynamicDotColor.copy(alpha = haloGlowAlpha * 0.35f))
        )

        // Layer 3: Inner Glow Ring
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(dynamicDotColor.copy(alpha = haloGlowAlpha * 0.65f))
        )

        // Layer 4: Visible Status Dot Core
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(dynamicDotColor)
                .border(0.75.dp, Color(0xFF6EE7B7).copy(alpha = specularAlpha * 0.8f), CircleShape)
        )

        // Layer 5: High-Light Glint Specular Center
        Box(
            modifier = Modifier
                .size(3.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = specularAlpha))
        )
    }
}

@Composable
fun DynamicSmsPermissionBanner(
    hasReceiveSms: Boolean,
    hasReadSms: Boolean,
    onRequestPermissions: () -> Unit,
    onOpenAppSettings: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, WarningAmber.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(WarningAmber.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "SMS Permission Required",
                        tint = WarningAmber,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SMS Permissions Required",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Auto-forwarding requires permission to receive and read carrier transaction SMS",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Permission status chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (hasReceiveSms) EmeraldGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (hasReceiveSms) Icons.Default.Check else Icons.Default.Clear,
                            contentDescription = null,
                            tint = if (hasReceiveSms) EmeraldGreen else WarningAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasReceiveSms) "RECEIVE_SMS: OK" else "RECEIVE_SMS: Needed",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasReceiveSms) EmeraldGreen else WarningAmber
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (hasReadSms) EmeraldGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (hasReadSms) Icons.Default.Check else Icons.Default.Clear,
                            contentDescription = null,
                            tint = if (hasReadSms) EmeraldGreen else WarningAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasReadSms) "READ_SMS: OK" else "READ_SMS: Needed",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasReadSms) EmeraldGreen else WarningAmber
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRequestPermissions,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Grant SMS Access", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOpenAppSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Settings", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

