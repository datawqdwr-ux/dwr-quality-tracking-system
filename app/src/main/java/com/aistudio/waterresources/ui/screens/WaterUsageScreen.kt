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
import androidx.compose.material.icons.filled.Delete
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
fun WaterUsageScreen(
    state: WaterResourcesUiState,
    onSelectProject: (WaterUsageProject?) -> Unit,
    onAddProject: (WaterUsageProject) -> Unit,
    onDeleteProject: (String) -> Unit,
    onSetCategoryFilter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLao = state.language == Language.LO
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "ALL",
        "ການປະປາ ແລະ ນໍ້າສະອາດ",
        "ອຸດສາຫະກຳ ແລະ ປຸງແຕ່ງ",
        "ພະລັງງານໄຟຟ້ານ້ຳຕົກ",
        "ກະສິກຳ ແລະ ຊົນລະປະທານ"
    )

    val filteredProjects = state.usageProjects.filter { p ->
        val matchesSearch = p.projectName.contains(searchQuery, ignoreCase = true) ||
                p.ownerName.contains(searchQuery, ignoreCase = true) ||
                p.waterSourceName.contains(searchQuery, ignoreCase = true) ||
                p.permitNumber.contains(searchQuery, ignoreCase = true)
        val matchesCategory = state.selectedCategory == "ALL" || p.projectCategory == state.selectedCategory
        matchesSearch && matchesCategory
    }

    // Total water volume sum
    val totalVolume = filteredProjects.sumOf { it.waterVolume }

    Scaffold(
        containerColor = Slate950,
        floatingActionButton = {
            if (!state.isGuestMode) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Indigo600,
                    contentColor = Slate100,
                    modifier = Modifier.testTag("add_project_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Project")
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
            // Screen Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isLao) "💧 ລະບົບຕິດຕາມຂໍ້ມູນການນຳໃຊ້ນ້ຳ" else "💧 Water Resources Usage System",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Slate100
                    )
                    Text(
                        text = if (isLao) "ຄຸ້ມຄອງໃບອະນຸຍາດ ແລະ ປະລິມານການນຳໃຊ້ນ້ຳ" else "Permits & Concessions Monitoring",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate400
                    )
                }
            }

            // Total Volume KPI Banner
            Surface(
                color = Slate900,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isLao) "ປະລິມານນ້ຳທີ່ໄດ້ຮັບອະນຸຍາດລວມ:" else "Total Authorized Daily Volume:",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                        Text(
                            text = "%,.0f m³/day".format(totalVolume),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = Indigo400
                        )
                    }
                    Surface(color = Indigo600.copy(alpha = 0.2f), shape = RoundedCornerShape(10.dp)) {
                        Text(
                            text = "${filteredProjects.size} " + if (isLao) "ໂຄງການ" else "projects",
                            color = Indigo400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                placeholder = {
                    Text(
                        text = if (isLao) "ຄົ້ນຫາໂຄງການ, ເຈົ້າຂອງ ຫຼື ເລກໃບອະນຸຍາດ..." else "Search project, owner or permit...",
                        color = Slate400,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("usage_search_field"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Slate900,
                    unfocusedContainerColor = Slate900,
                    focusedBorderColor = Indigo500,
                    unfocusedBorderColor = Slate800,
                    focusedTextColor = Slate100,
                    unfocusedTextColor = Slate100
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                items(categories) { cat ->
                    val selected = (state.selectedCategory == cat)
                    FilterChip(
                        selected = selected,
                        onClick = { onSetCategoryFilter(cat) },
                        label = {
                            Text(
                                text = if (cat == "ALL") (if (isLao) "ທຸກປະເພດ" else "All Categories") else cat,
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Indigo600,
                            selectedLabelColor = Slate100,
                            containerColor = Slate900,
                            labelColor = Slate300
                        )
                    )
                }
            }

            // Projects List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredProjects) { project ->
                    UsageProjectCard(
                        project = project,
                        isLao = isLao,
                        onClick = { onSelectProject(project) }
                    )
                }
            }
        }
    }

    // Add Project Dialog
    if (showAddDialog) {
        AddUsageProjectDialog(
            isLao = isLao,
            onDismiss = { showAddDialog = false },
            onConfirm = { project ->
                onAddProject(project)
                showAddDialog = false
            }
        )
    }

    // Project Detail Dialog
    if (state.selectedProject != null) {
        ProjectDetailDialog(
            project = state.selectedProject,
            isLao = isLao,
            isAdmin = state.userSession.role == UserRole.ADMIN,
            onDismiss = { onSelectProject(null) },
            onDelete = {
                onDeleteProject(state.selectedProject.id)
            }
        )
    }
}

@Composable
private fun UsageProjectCard(
    project: WaterUsageProject,
    isLao: Boolean,
    onClick: () -> Unit
) {
    val statusColor = when (project.status) {
        ProjectStatus.ACTIVE -> Emerald400
        ProjectStatus.PENDING_RENEWAL -> Amber400
        ProjectStatus.EXPIRED -> Rose500
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("project_card_${project.id}"),
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
                Surface(color = Indigo600.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = project.projectCategory,
                        color = Indigo400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Surface(color = statusColor.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = if (isLao) project.status.labelLao else project.status.labelEn,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = project.projectName,
                style = MaterialTheme.typography.titleMedium,
                color = Slate100,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "🏢 ${project.ownerName}",
                fontSize = 12.sp,
                color = Slate300
            )

            HorizontalDivider(color = Slate800)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "💧 %,.0f %s".format(project.waterVolume, project.waterVolumeUnit),
                    fontSize = 12.sp,
                    color = Cyan400,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "🌊 ${project.waterSourceName}",
                    fontSize = 12.sp,
                    color = Slate400
                )
            }

            Text(
                text = "📍 ${project.village}, ${project.district}, ${project.province}",
                fontSize = 11.sp,
                color = Slate400
            )
        }
    }
}

@Composable
private fun AddUsageProjectDialog(
    isLao: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (WaterUsageProject) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ອຸດສາຫະກຳ ແລະ ປຸງແຕ່ງ") }
    var owner by remember { mutableStateOf("") }
    var permitNumber by remember { mutableStateOf("WTR-2025/001") }
    var volumeText by remember { mutableStateOf("1500") }
    var waterSource by remember { mutableStateOf("ແມ່ນໍ້າຂອງ") }
    var province by remember { mutableStateOf("ນະຄອນຫຼວງວຽງຈັນ") }
    var district by remember { mutableStateOf("ໄຊທານີ") }
    var village by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text(
                text = if (isLao) "💧 ເພີ່ມໂຄງການນຳໃຊ້ນ້ຳໃໝ່" else "Add Water Usage Project",
                color = Slate100,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isLao) "ຊື່ໂຄງການ" else "Project Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = owner,
                    onValueChange = { owner = it },
                    label = { Text(if (isLao) "ເຈົ້າຂອງໂຄງການ / ບໍລິສັດ" else "Owner / Company") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = volumeText,
                        onValueChange = { volumeText = it },
                        label = { Text("m³/day") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = waterSource,
                        onValueChange = { waterSource = it },
                        label = { Text(if (isLao) "ແຫຼ່ງນ້ຳ" else "Source") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = province,
                        onValueChange = { province = it },
                        label = { Text(if (isLao) "ແຂວງ" else "Province") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text(if (isLao) "ເມືອງ" else "District") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = permitNumber,
                    onValueChange = { permitNumber = it },
                    label = { Text(if (isLao) "ເລກທີໃບອະນຸຍາດ" else "Permit No.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val vol = volumeText.toDoubleOrNull() ?: 1000.0
                    val project = WaterUsageProject(
                        id = "proj-${System.currentTimeMillis()}",
                        projectName = if (name.isNotBlank()) name else "ໂຄງການໃໝ່",
                        projectCategory = category,
                        ownerName = if (owner.isNotBlank()) owner else "ບໍ່ລະບຸ",
                        contactPhone = "021 000000",
                        contactEmail = "contact@example.la",
                        permitNumber = permitNumber,
                        permitIssueDate = "2025-01-01",
                        permitExpiryDate = "2030-01-01",
                        status = ProjectStatus.ACTIVE,
                        waterVolume = vol,
                        waterVolumeUnit = "m³/day",
                        waterSourceType = "ແມ່ນໍ້າ",
                        waterSourceName = waterSource,
                        village = village,
                        district = district,
                        province = province,
                        latitude = 18.0,
                        longitude = 102.6,
                        pumpCapacity = "ຈັກດູດໄຟຟ້າ",
                        waterUsagePlan = "ນຳໃຊ້ໃນຂະບວນການຜະລິດ"
                    )
                    onConfirm(project)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
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
private fun ProjectDetailDialog(
    project: WaterUsageProject,
    isLao: Boolean,
    isAdmin: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
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
                Text(
                    text = project.projectName,
                    color = Slate100,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "ໝວດໝູ່: ${project.projectCategory}", color = Indigo400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "ເຈົ້າຂອງ: ${project.ownerName}", color = Slate200, fontSize = 13.sp)
                Text(text = "ຕິດຕໍ່: ${project.contactPhone} • ${project.contactEmail}", color = Slate400, fontSize = 12.sp)
                Text(text = "ເລກທີໃບອະນຸຍາດ: ${project.permitNumber}", color = Slate300, fontSize = 12.sp)
                Text(text = "ອາຍຸໃບອະນຸຍາດ: ${project.permitIssueDate} ຫາ ${project.permitExpiryDate}", color = Slate400, fontSize = 12.sp)

                HorizontalDivider(color = Slate800)

                Text(text = "ປະລິມານນ້ຳ: %,.0f %s".format(project.waterVolume, project.waterVolumeUnit), color = Cyan400, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = "ແຫຼ່ງນ້ຳ: ${project.waterSourceType} (${project.waterSourceName})", color = Slate200, fontSize = 12.sp)
                Text(text = "ສະຖານທີ່: ບ້ານ ${project.village}, ເມືອງ ${project.district}, ${project.province}", color = Slate300, fontSize = 12.sp)
                if (project.pumpCapacity.isNotBlank()) {
                    Text(text = "ກຳລັງສູບ: ${project.pumpCapacity}", color = Slate400, fontSize = 11.sp)
                }
                if (project.waterUsagePlan.isNotBlank()) {
                    Text(text = "ແຜນນຳໃຊ້: ${project.waterUsagePlan}", color = Slate400, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            if (isAdmin) {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isLao) "ລຶບໂຄງການ" else "Delete")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isLao) "ປິດ" else "Close", color = Slate400)
            }
        }
    )
}
