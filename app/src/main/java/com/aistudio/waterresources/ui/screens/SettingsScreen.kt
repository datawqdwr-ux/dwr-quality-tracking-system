package com.aistudio.waterresources.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun SettingsScreen(
    state: WaterResourcesUiState,
    onSelectRole: (UserRole) -> Unit,
    onSelectLanguage: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLao = state.language == Language.LO

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

        // System Audit Logs
        item {
            Text(
                text = if (isLao) "📋 ປະຫວັດການດຳເນີນງານລະບົບ (Audit Trail)" else "📋 System Audit Trail",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate100
            )
        }

        items(state.auditLogs) { log ->
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
