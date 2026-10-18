package com.aistudio.waterresources.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.waterresources.data.model.Language
import com.aistudio.waterresources.data.model.UserRole
import com.aistudio.waterresources.ui.theme.*
import com.aistudio.waterresources.ui.viewmodel.WaterResourcesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: WaterResourcesUiState,
    onSelectRole: (UserRole) -> Unit,
    onSelectLanguage: (Language) -> Unit,
    onDeleteAuditLogs: (Set<String>) -> Unit = {},
    onClearAllAuditLogs: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isLao = state.language == Language.LO
    var searchQuery by remember { mutableStateOf("") }
    var selectedAction by remember { mutableStateOf("ALL") }
    var expandedDropdown by remember { mutableStateOf(false) }

    val selectedLogIds = remember { mutableStateListOf<String>() }
    var showConfirmDeleteSelectedDialog by remember { mutableStateOf(false) }
    var showConfirmClearAllDialog by remember { mutableStateOf(false) }

    val distinctActions = remember(state.auditLogs) {
        listOf("ALL") + state.auditLogs.map { it.action }.distinct()
    }

    val filteredLogs = remember(state.auditLogs, searchQuery, selectedAction) {
        state.auditLogs.filter { log ->
            val matchesAction = selectedAction == "ALL" || log.action.equals(selectedAction, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                log.details.contains(searchQuery, ignoreCase = true) ||
                log.user.contains(searchQuery, ignoreCase = true) ||
                log.action.contains(searchQuery, ignoreCase = true)
            matchesAction && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = if (isLao) "⚙️ ຕັ້ງຄ່າລະບົບ & ບັນທຶກການເຮັດວຽກ" else "⚙️ Settings & Audit Logs",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Slate100
                )
                Text(
                    text = if (isLao) "ຈັດການສິດທິ, ພາສາ ແລະ ປະຫວັດການດຳເນີນງານ" else "Manage permissions, language, and system audit log",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate400
                )
            }
        }

        // Language Selection
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isLao) "🌐 ເລືອກພາສາ / Language" else "🌐 Display Language",
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Language.entries.forEach { lang ->
                            val selected = state.language == lang
                            FilledTonalButton(
                                onClick = { onSelectLanguage(lang) },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (selected) Rose600 else Slate800,
                                    contentColor = if (selected) Slate100 else Slate300
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("${lang.flag} ${lang.label}")
                            }
                        }
                    }
                }
            }
        }

        // User Role Switcher
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("user_role_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isLao) "👤 ສິດການນຳໃຊ້ (User Role)" else "👤 User Access Role",
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                    UserRole.entries.forEach { role ->
                        val selected = state.userSession.role == role
                        Surface(
                            color = if (selected) Rose600.copy(alpha = 0.2f) else Slate850,
                            shape = RoundedCornerShape(12.dp),
                            border = if (selected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Rose500)) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isLao) role.labelLao else role.labelEn,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Rose300 else Slate200,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (role == UserRole.ADMIN) "Full Read/Write/Delete" else if (role == UserRole.INSPECTOR) "Read/Write Inspection" else "Read-only",
                                        fontSize = 11.sp,
                                        color = Slate400
                                    )
                                }
                                RadioButton(
                                    selected = selected,
                                    onClick = { onSelectRole(role) },
                                    colors = RadioButtonDefaults.colors(selectedColor = Rose500, unselectedColor = Slate400)
                                )
                            }
                        }
                    }
                }
            }
        }

        // System Audit Logs Header & Filter Controls
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isLao) "📋 ປະຫວັດການດຳເນີນງານລະບົບ (Audit Trail)" else "📋 System Audit Trail",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )
                        Text(
                            text = if (isLao) "ບັນທຶກທຸກການເຄື່ອນໄຫວ ເຊັ່ນ ການເພີ່ມ, ແກ້ໄຂ, ລຶບຂໍ້ມູນ ຫຼື ການເຂົ້າລະບົບ" else "Audit history of create, update, delete, and login events",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                    Surface(
                        color = Slate800,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isLao) "ລາຍການ: ${filteredLogs.size} / ${state.auditLogs.size}" else "Logs: ${filteredLogs.size} / ${state.auditLogs.size}",
                            color = Slate300,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // ບ໋ອກ select: Action Filter Dropdown Box
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (selectedAction == "ALL") {
                            if (isLao) "ທຸກປະເພດການກະທຳ (ທັງໝົດ)" else "All Actions (All Types)"
                        } else {
                            selectedAction
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                if (isLao) "ບ໋ອກເລືອກປະເພດການກະທຳ (Select Action)" else "Select Action Filter",
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900,
                            focusedBorderColor = Rose500,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate200
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.background(Slate900)
                    ) {
                        distinctActions.forEach { action ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (action == "ALL") {
                                            if (isLao) "ທຸກປະເພດການກະທຳ (ທັງໝົດ)" else "All Actions (All Types)"
                                        } else {
                                            action
                                        },
                                        color = if (action == selectedAction) Rose400 else Slate200,
                                        fontWeight = if (action == selectedAction) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedAction = action
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                // ຫ້ອງ clear: Search input with Clear icon button
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            if (isLao) "🔍 ຄົ້ນຫາປະຫວັດ (ລາຍລະອຽດ, ຜູ້ໃຊ້...)" else "🔍 Search logs (details, user...)",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Search", tint = Slate400)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate900,
                        unfocusedContainerColor = Slate900,
                        focusedBorderColor = Rose500,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Toolbar for Bulk Selection and Clear All Logs (ບ໋ອກ Select & ຫ້ອງ Clear ປະຫວັດທັງໝົດ)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate900, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val allFilteredSelected = filteredLogs.isNotEmpty() && filteredLogs.all { it.id in selectedLogIds }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            if (allFilteredSelected) {
                                filteredLogs.forEach { selectedLogIds.remove(it.id) }
                            } else {
                                filteredLogs.forEach {
                                    if (it.id !in selectedLogIds) selectedLogIds.add(it.id)
                                }
                            }
                        }
                    ) {
                        Checkbox(
                            checked = allFilteredSelected,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    filteredLogs.forEach {
                                        if (it.id !in selectedLogIds) selectedLogIds.add(it.id)
                                    }
                                } else {
                                    filteredLogs.forEach { selectedLogIds.remove(it.id) }
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Rose500,
                                uncheckedColor = Slate400
                            )
                        )
                        Text(
                            text = if (isLao) "ເລືອກທັງໝົດ" else "Select All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate200
                        )
                    }

                    // Clear All Logs button (ລ້າງປະຫວັດທັງໝົດ)
                    if (state.auditLogs.isNotEmpty()) {
                        Button(
                            onClick = { showConfirmClearAllDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Rose500.copy(alpha = 0.2f),
                                contentColor = Rose300
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLao) "ລ້າງທັງໝົດ (Clear All)" else "Clear All Logs",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Banner when logs are selected: Delete Selected & Clear Selection
                if (selectedLogIds.isNotEmpty()) {
                    Surface(
                        color = Rose600.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Rose500.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isLao) "ເລືອກແລ້ວ: ${selectedLogIds.size} ລາຍການ" else "Selected: ${selectedLogIds.size}",
                                color = Rose300,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(
                                    onClick = { selectedLogIds.clear() },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Slate300)
                                ) {
                                    Text(
                                        text = if (isLao) "ຍົກເລີກການເລືອກ" else "Deselect",
                                        fontSize = 11.sp
                                    )
                                }
                                Button(
                                    onClick = { showConfirmDeleteSelectedDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Rose600,
                                        contentColor = Slate100
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isLao) "ລົບລາຍການທີ່ເລືອກ (${selectedLogIds.size})" else "Delete Selected (${selectedLogIds.size})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Clear Filter Button if filter is applied
                if (selectedAction != "ALL" || searchQuery.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                selectedAction = "ALL"
                                searchQuery = ""
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Rose400)
                        ) {
                            Text(
                                text = if (isLao) "🔄 ລ້າງການກັ່ນຕອງ (Clear Filter)" else "🔄 Clear Filter",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (filteredLogs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isLao) "ບໍ່ພົບປະຫວັດການດຳເນີນງານທີ່ກົງກັບເງື່ອນໄຂ" else "No audit logs matching criteria",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(filteredLogs) { log ->
                val isSelected = log.id in selectedLogIds
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Slate900.copy(alpha = 0.95f) else Slate900
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Rose500) else null,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) selectedLogIds.remove(log.id)
                            else selectedLogIds.add(log.id)
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ບ໋ອກ Select (Checkbox per log entry)
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    if (log.id !in selectedLogIds) selectedLogIds.add(log.id)
                                } else {
                                    selectedLogIds.remove(log.id)
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Rose500,
                                uncheckedColor = Slate400
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = when {
                                        log.action.contains("DELETE", ignoreCase = true) -> Rose500.copy(alpha = 0.2f)
                                        log.action.contains("ADD", ignoreCase = true) || log.action.contains("CREATE", ignoreCase = true) -> Emerald500.copy(alpha = 0.2f)
                                        log.action.contains("UPDATE", ignoreCase = true) -> Amber500.copy(alpha = 0.2f)
                                        else -> Indigo600.copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = log.action,
                                        color = when {
                                            log.action.contains("DELETE", ignoreCase = true) -> Rose300
                                            log.action.contains("ADD", ignoreCase = true) || log.action.contains("CREATE", ignoreCase = true) -> Emerald400
                                            log.action.contains("UPDATE", ignoreCase = true) -> Amber400
                                            else -> Indigo300
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(text = log.timestamp, fontSize = 11.sp, color = Slate400)
                            }
                            Text(text = log.details, fontSize = 12.sp, color = Slate200)
                            Text(text = "ໂດຍ: ${log.user}", fontSize = 11.sp, color = Slate400)
                        }
                        IconButton(
                            onClick = {
                                onDeleteAuditLogs(setOf(log.id))
                                selectedLogIds.remove(log.id)
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Single Log",
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Delete Selected
    if (showConfirmDeleteSelectedDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteSelectedDialog = false },
            title = {
                Text(
                    text = if (isLao) "ຢືນຢັນການລົບລາຍການທີ່ເລືອກ" else "Confirm Delete Selected",
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            },
            text = {
                Text(
                    text = if (isLao) "ທ່ານແນ່ໃຈບໍ່ວ່າຕ້ອງການລົບ ${selectedLogIds.size} ປະຫວັດການດໍາເນີນງານທີ່ເລືອກໄວ້?" else "Are you sure you want to delete ${selectedLogIds.size} selected audit log(s)?",
                    color = Slate300
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAuditLogs(selectedLogIds.toSet())
                        selectedLogIds.clear()
                        showConfirmDeleteSelectedDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                ) {
                    Text(if (isLao) "ແມ່ນ, ລົບລາຍການທີ່ເລືອກ" else "Delete", color = Slate100)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDeleteSelectedDialog = false }) {
                    Text(if (isLao) "ຍົກເລີກ" else "Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    // Confirmation Dialog for Clear All Logs
    if (showConfirmClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmClearAllDialog = false },
            title = {
                Text(
                    text = if (isLao) "ຢືນຢັນການລ້າງປະຫວັດທັງໝົດ (Clear All Logs)" else "Confirm Clear All Logs",
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            },
            text = {
                Text(
                    text = if (isLao) "ທ່ານແນ່ໃຈບໍ່ວ່າຕ້ອງການລົບປະຫວັດການດຳເນີນງານທັງໝົດອອກຈາກລະບົບ? ຂໍ້ມູນທັງໝົດຈະຖືກລຶບ ແລະ ບໍ່ສາມາດກູ້ຄືນໄດ້." else "Are you sure you want to delete all audit logs? This action cannot be undone.",
                    color = Slate300
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllAuditLogs()
                        selectedLogIds.clear()
                        showConfirmClearAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                ) {
                    Text(if (isLao) "ແມ່ນ, ລ້າງທັງໝົດ" else "Clear All", color = Slate100)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClearAllDialog = false }) {
                    Text(if (isLao) "ຍົກເລີກ" else "Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }
}
