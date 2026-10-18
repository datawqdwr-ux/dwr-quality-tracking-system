package com.aistudio.waterresources.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
    modifier: Modifier = Modifier
) {
    val isLao = state.language == Language.LO
    var searchQuery by remember { mutableStateOf("") }
    var selectedAction by remember { mutableStateOf("ALL") }
    var expandedDropdown by remember { mutableStateOf(false) }

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
                    Text(
                        text = if (isLao) "📋 ປະຫວັດການດຳເນີນງານລະບົບ (Audit Trail)" else "📋 System Audit Trail",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
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

                // Clear / Reset All Filters Button (ຫ້ອງ clear ການກັ່ນຕອງທັງໝົດ)
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
                                text = if (isLao) "🔄 ລ້າງການກັ່ນຕອງທັງໝົດ (Clear Filters)" else "🔄 Clear All Filters",
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
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(color = Indigo600.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = log.action,
                                    color = Indigo300,
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
                }
            }
        }
    }
}
