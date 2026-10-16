package com.aistudio.waterresources.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.waterresources.data.model.*
import com.aistudio.waterresources.ui.theme.*
import com.aistudio.waterresources.ui.viewmodel.WaterResourcesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterQualityScreen(
    state: WaterResourcesUiState,
    onSelectStation: (WaterQualityStation?) -> Unit,
    onAddReading: (String, WaterQualityReading) -> Unit,
    onSetProvinceFilter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLao = state.language == Language.LO
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val provinces = listOf("ALL", "ນະຄອນຫຼວງວຽງຈັນ", "ຫຼວງພະບາງ", "ຈຳປາສັກ")
    val tabs = if (isLao) {
        listOf("📍 ສະຖານີທັງໝົດ", "📈 ວິເຄາະຕົວຊີ້ວັດ", "📊 ແນວໂນ້ມ", "📝 ບັນທຶກ")
    } else {
        listOf("📍 Stations", "📈 Parameters", "📊 Trends", "📝 Log")
    }

    val filteredStations = state.stations.filter { station ->
        val matchesSearch = station.name.contains(searchQuery, ignoreCase = true) ||
                station.stationCode.contains(searchQuery, ignoreCase = true) ||
                station.river.contains(searchQuery, ignoreCase = true)
        val matchesProvince = state.selectedProvince == "ALL" || station.province == state.selectedProvince
        matchesSearch && matchesProvince
    }

    Scaffold(
        containerColor = Slate950,
        floatingActionButton = {
            if (!state.isGuestMode) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Rose600,
                    contentColor = Slate100,
                    modifier = Modifier.testTag("add_reading_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Reading")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isLao) "🧪 ລະບົບຕິດຕາມຄຸນນະພາບນ້ຳ" else "🧪 Water Quality Monitoring",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Slate100
                    )
                    Text(
                        text = if (isLao) "ເຄືອຂ່າຍຕິດຕາມຄຸນນະພາບນ້ຳໜ້າດິນແຫ່ງຊາດ" else "National Surface Water Quality Network",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate400
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                placeholder = {
                    Text(
                        text = if (isLao) "ຄົ້ນຫາສະຖານີ ຫຼື ແມ່ນ້ຳ..." else "Search station or river...",
                        color = Slate400,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wqs_search_field"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Slate900,
                    unfocusedContainerColor = Slate900,
                    focusedBorderColor = Rose500,
                    unfocusedBorderColor = Slate800,
                    focusedTextColor = Slate100,
                    unfocusedTextColor = Slate100
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Sub Navigation Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Slate900,
                contentColor = Rose400,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab),
                        color = Rose500
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Slate100 else Slate400
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Province filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(provinces) { prov ->
                    val selected = (state.selectedProvince == prov)
                    FilterChip(
                        selected = selected,
                        onClick = { onSetProvinceFilter(prov) },
                        label = {
                            Text(
                                text = if (prov == "ALL") (if (isLao) "ທັງໝົດ" else "All") else prov,
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Rose600,
                            selectedLabelColor = Slate100,
                            containerColor = Slate900,
                            labelColor = Slate300
                        )
                    )
                }
            }

            // Main Content based on selected Tab
            when (selectedTab) {
                0 -> {
                    // Stations List
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredStations) { station ->
                            StationCard(
                                station = station,
                                isLao = isLao,
                                onClick = { onSelectStation(station) }
                            )
                        }
                    }
                }
                1 -> {
                    // Parameter Analysis View
                    ParameterAnalysisView(stations = filteredStations, isLao = isLao)
                }
                2 -> {
                    // Trends View
                    TrendAnalysisView(stations = filteredStations, isLao = isLao)
                }
                3 -> {
                    // Records & Inspection View
                    InspectionRecordsView(stations = filteredStations, isLao = isLao)
                }
            }
        }
    }

    // Add Inspection Reading Dialog
    if (showAddDialog) {
        AddReadingDialog(
            stations = state.stations,
            isLao = isLao,
            onDismiss = { showAddDialog = false },
            onConfirm = { stationId, reading ->
                onAddReading(stationId, reading)
                showAddDialog = false
            }
        )
    }

    // Station Detail Dialog
    if (state.selectedStation != null) {
        StationDetailDialog(
            station = state.selectedStation,
            isLao = isLao,
            onDismiss = { onSelectStation(null) }
        )
    }
}

@Composable
private fun StationCard(
    station: WaterQualityStation,
    isLao: Boolean,
    onClick: () -> Unit
) {
    val statusColor = when (station.overallStatus) {
        ComplianceStatus.NORMAL -> Emerald400
        ComplianceStatus.WARNING -> Amber400
        ComplianceStatus.EXCEEDED -> Rose500
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("station_card_${station.id}"),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = station.stationCode,
                    style = MaterialTheme.typography.labelSmall,
                    color = Cyan400,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isLao) station.overallStatus.labelLao else station.overallStatus.labelEn,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = station.name,
                style = MaterialTheme.typography.titleMedium,
                color = Slate100,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🌊 ${station.river} • ${station.province}",
                    fontSize = 12.sp,
                    color = Slate400
                )
                Text(
                    text = "📅 ${station.lastInspectionDate}",
                    fontSize = 11.sp,
                    color = Slate400
                )
            }

            // Latest Reading Snapshot if available
            station.readings.firstOrNull()?.let { r ->
                HorizontalDivider(color = Slate800)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ParamChip(label = "pH", value = "${r.ph}")
                    ParamChip(label = "DO", value = "${r.dissolvedOxygen} mg/L")
                    ParamChip(label = "BOD5", value = "${r.bod5} mg/L")
                    ParamChip(label = "TSS", value = "${r.tss} mg/L")
                }
            }
        }
    }
}

@Composable
private fun ParamChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Slate400)
        Text(text = value, fontSize = 12.sp, color = Slate100, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ParameterAnalysisView(stations: List<WaterQualityStation>, isLao: Boolean) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isLao) "📊 ມາດຕະຖານຄຸນນະພາບນ້ຳໜ້າດິນແຫ່ງຊາດ" else "National Surface Water Standards",
                        style = MaterialTheme.typography.titleMedium,
                        color = Rose400,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "• pH: 6.5 – 8.5 (ມາດຕະຖານຊັ້ນ 2-3)", fontSize = 12.sp, color = Slate300)
                    Text(text = "• DO (ອົກຊີເຈນລະລາຍ): ≥ 6.0 mg/L", fontSize = 12.sp, color = Slate300)
                    Text(text = "• BOD5 (ຄວາມຕ້ອງການອົກຊີເຈນ): ≤ 1.5 mg/L", fontSize = 12.sp, color = Slate300)
                    Text(text = "• COD: ≤ 5.0 mg/L", fontSize = 12.sp, color = Slate300)
                    Text(text = "• TSS (ທາດແຂວນລອຍ): ≤ 25.0 mg/L", fontSize = 12.sp, color = Slate300)
                }
            }
        }

        items(stations) { station ->
            val r = station.readings.firstOrNull()
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = station.name, style = MaterialTheme.typography.titleMedium, color = Slate100)
                    if (r != null) {
                        MetricRow("pH (ຄວາມເປັນກົດ-ດ່າງ)", r.ph, 6.5, 8.5)
                        MetricRow("DO (ອົກຊີເຈນລະລາຍ)", r.dissolvedOxygen, 6.0, 14.0)
                        MetricRow("BOD5 (ສານອິນຊີ)", r.bod5, 0.0, 1.5)
                        MetricRow("TSS (ທາດແຂວນລອຍ)", r.tss, 0.0, 25.0)
                    } else {
                        Text(text = if (isLao) "ບໍ່ມີຂໍ້ມູນກວດກາ" else "No inspection data", color = Slate400, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: Double, minStd: Double, maxStd: Double) {
    val isOk = value in minStd..maxStd
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = Slate300)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "$value", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate100)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isOk) Emerald400 else Rose500)
            )
        }
    }
}

@Composable
private fun TrendAnalysisView(stations: List<WaterQualityStation>, isLao: Boolean) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text(
                text = if (isLao) "📅 ປະຫວັດແນວໂນ້ມຄຸນນະພາບນ້ຳ" else "📅 Water Quality Historical Trends",
                style = MaterialTheme.typography.titleMedium,
                color = Slate100,
                fontWeight = FontWeight.Bold
            )
        }

        items(stations) { station ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "${station.stationCode} — ${station.name}", fontWeight = FontWeight.Bold, color = Slate100)
                    station.readings.forEach { reading ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate850, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = reading.timestamp, fontSize = 11.sp, color = Slate400)
                            Text(text = "pH: ${reading.ph}", fontSize = 11.sp, color = Cyan400)
                            Text(text = "DO: ${reading.dissolvedOxygen}", fontSize = 11.sp, color = Emerald400)
                            Text(text = "BOD: ${reading.bod5}", fontSize = 11.sp, color = Amber400)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InspectionRecordsView(stations: List<WaterQualityStation>, isLao: Boolean) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(stations.flatMap { it.readings }) { reading ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "🗓️ ${reading.timestamp}", fontSize = 12.sp, color = Cyan400, fontWeight = FontWeight.Bold)
                        Text(text = "👤 ${reading.inspectorName}", fontSize = 11.sp, color = Slate400)
                    }
                    Text(
                        text = "pH: ${reading.ph} | DO: ${reading.dissolvedOxygen} mg/L | BOD: ${reading.bod5} mg/L | TSS: ${reading.tss} mg/L",
                        fontSize = 12.sp,
                        color = Slate200
                    )
                    if (reading.notes.isNotBlank()) {
                        Text(text = "💬 ${reading.notes}", fontSize = 11.sp, color = Slate400)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddReadingDialog(
    stations: List<WaterQualityStation>,
    isLao: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, WaterQualityReading) -> Unit
) {
    var selectedStationId by remember { mutableStateOf(stations.firstOrNull()?.id ?: "") }
    var phText by remember { mutableStateOf("7.2") }
    var doText by remember { mutableStateOf("6.5") }
    var bodText by remember { mutableStateOf("1.2") }
    var codText by remember { mutableStateOf("4.0") }
    var tssText by remember { mutableStateOf("18.0") }
    var inspectorName by remember { mutableStateOf("ເຈົ້າໜ້າທີ່ ກຊນ") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text(
                text = if (isLao) "📝 ບັນທຶກຜົນກວດກາຄຸນນະພາບນ້ຳ" else "Add Water Quality Reading",
                color = Slate100,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = if (isLao) "ເລືອກສະຖານີ:" else "Select Station:", fontSize = 12.sp, color = Slate400)
                // Station selection row
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(stations) { st ->
                        FilterChip(
                            selected = st.id == selectedStationId,
                            onClick = { selectedStationId = st.id },
                            label = { Text(st.stationCode, fontSize = 11.sp) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phText,
                        onValueChange = { phText = it },
                        label = { Text("pH") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = doText,
                        onValueChange = { doText = it },
                        label = { Text("DO (mg/L)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bodText,
                        onValueChange = { bodText = it },
                        label = { Text("BOD5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tssText,
                        onValueChange = { tssText = it },
                        label = { Text("TSS (mg/L)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = inspectorName,
                    onValueChange = { inspectorName = it },
                    label = { Text(if (isLao) "ຜູ້ກວດກາ" else "Inspector") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isLao) "ໝາຍເຫດ" else "Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ph = phText.toDoubleOrNull() ?: 7.0
                    val doVal = doText.toDoubleOrNull() ?: 6.0
                    val bod = bodText.toDoubleOrNull() ?: 1.5
                    val cod = codText.toDoubleOrNull() ?: 4.0
                    val tss = tssText.toDoubleOrNull() ?: 20.0

                    val status = if (ph in 6.5..8.5 && doVal >= 6.0 && bod <= 1.5 && tss <= 25.0) {
                        ComplianceStatus.NORMAL
                    } else if (ph in 6.0..9.0 && doVal >= 4.0 && bod <= 3.0) {
                        ComplianceStatus.WARNING
                    } else {
                        ComplianceStatus.EXCEEDED
                    }

                    val reading = WaterQualityReading(
                        id = "R-${System.currentTimeMillis()}",
                        timestamp = "2025-02-14",
                        monthYear = "02/2025",
                        ph = ph,
                        dissolvedOxygen = doVal,
                        bod5 = bod,
                        cod = cod,
                        tss = tss,
                        conductivity = 200.0,
                        temperature = 26.0,
                        turbidity = 10.0,
                        status = status,
                        inspectorName = inspectorName,
                        notes = notes
                    )
                    onConfirm(selectedStationId, reading)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Rose600)
            ) {
                Text(if (isLao) "ບັນທຶກ" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isLao) "ຍົກເລີກ" else "Cancel", color = Slate400)
            }
        }
    )
}

@Composable
private fun StationDetailDialog(
    station: WaterQualityStation,
    isLao: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = station.name, color = Slate100, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "ລະຫັດ: ${station.stationCode}", color = Cyan400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "ແຫຼ່ງນ້ຳ: ${station.river}", color = Slate300, fontSize = 13.sp)
                Text(text = "ສະຖານທີ່: ${station.district}, ${station.province}", color = Slate300, fontSize = 13.sp)
                Text(text = "ພິກັດ: ${station.latitude}, ${station.longitude}", color = Slate400, fontSize = 12.sp)

                HorizontalDivider(color = Slate800)

                Text(
                    text = if (isLao) "ປະຫວັດການກວດກາລ່າສຸດ:" else "Latest Inspection History:",
                    fontWeight = FontWeight.Bold,
                    color = Slate100,
                    fontSize = 13.sp
                )

                station.readings.forEach { r ->
                    Surface(
                        color = Slate850,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "📅 ${r.timestamp} • 👤 ${r.inspectorName}", fontSize = 11.sp, color = Slate400)
                            Text(text = "pH: ${r.ph} | DO: ${r.dissolvedOxygen} mg/L | BOD5: ${r.bod5} mg/L", fontSize = 12.sp, color = Slate200, fontWeight = FontWeight.Medium)
                            if (r.notes.isNotBlank()) {
                                Text(text = "ໝາຍເຫດ: ${r.notes}", fontSize = 11.sp, color = Slate400)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Slate800)) {
                Text(if (isLao) "ປິດ" else "Close", color = Slate100)
            }
        }
    )
}
