package com.aistudio.waterresources.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.waterresources.data.model.ComplianceStatus
import com.aistudio.waterresources.data.model.Language
import com.aistudio.waterresources.data.model.SystemSection
import com.aistudio.waterresources.ui.theme.*
import com.aistudio.waterresources.ui.viewmodel.WaterResourcesUiState

@Composable
fun PortalDashboardScreen(
    state: WaterResourcesUiState,
    onNavigateSection: (SystemSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLao = state.language == Language.LO

    // Calculated summary stats
    val totalStations = state.stations.size
    val totalUsageProjects = state.usageProjects.size
    val totalWastewater = state.wastewaterFacilities.size
    val alertCount = state.stations.count { it.overallStatus != ComplianceStatus.NORMAL } +
            state.wastewaterFacilities.count { it.complianceStatus != ComplianceStatus.NORMAL }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isLao) "ລະບົບຂໍ້ມູນຊັບພະຍາກອນນ້ຳ" else "Water Resources Information Portal",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Slate100,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isLao)
                        "ສູນລວມການຕິດຕາມກວດກາ ແລະ ຄຸ້ມຄອງຂໍ້ມູນຄຸນນະພາບນ້ຳ, ການນຳໃຊ້ນ້ຳ ແລະ ໂຄງການປ່ອຍນ້ຳເປື້ອນ ຂອງບັນດາໂຄງການ ຫຼື ກິດຈະການ ພາຍໃຕ້ການຊີ້ນຳຂອງ ກົມຊັບພະຍາກອນນ້ຳ."
                    else
                        "Comprehensive monitoring and management center for water quality, water usage concessions, and industrial wastewater discharge under the Department of Water Resources.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate300,
                    textAlign = TextAlign.Center
                )
            }
        }

        // 4 KPI Summary Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = if (isLao) "ສະຖານີຄຸນນະພາບ" else "WQS Stations",
                    value = "$totalStations",
                    icon = "🧪",
                    color = Cyan400,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (isLao) "ໂຄງການນຳໃຊ້ນ້ຳ" else "Usage Projects",
                    value = "$totalUsageProjects",
                    icon = "💧",
                    color = Indigo400,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = if (isLao) "ໂຮງງານປ່ອຍນ້ຳເປື້ອນ" else "Discharge Units",
                    value = "$totalWastewater",
                    icon = "🏭",
                    color = Rose400,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (isLao) "ຈຸດແຈ້ງເຕືອນ/ເກີນ" else "Active Alerts",
                    value = "$alertCount",
                    icon = "⚠️",
                    color = if (alertCount > 0) Amber400 else Emerald400,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section Title: 3 Core Systems
        item {
            Text(
                text = if (isLao) "ລະບົບຫຼັກ 3 ພາກສ່ວນ" else "Core Sub-Systems",
                style = MaterialTheme.typography.titleMedium,
                color = Slate100,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // System 1: Water Quality Monitoring Card
        item {
            SystemPortalCard(
                icon = "🧪",
                title = if (isLao) "ລະບົບຕິດຕາມຄຸນນະພາບນ້ຳ" else "Water Quality Monitoring System",
                badge = if (isLao) "ຄຸນນະພາບນ້ຳ" else "Water Quality",
                description = if (isLao)
                    "ຕິດຕາມກວດກາຕົວຊີ້ວັດ pH, DO, BOD, COD, TSS, ໂລຫະໜັກ ແລະ ແຜນທີ່ GIS ສະພາບນ້ຳທົ່ວປະເທດ"
                else
                    "Real-time monitoring of pH, DO, BOD, COD, TSS, and national GIS water stations.",
                accentColor = Cyan400,
                onEnter = { onNavigateSection(SystemSection.WATER_QUALITY) },
                isLao = isLao,
                testTag = "enter_water_quality_btn"
            )
        }

        // System 2: Water Resources Usage Card
        item {
            SystemPortalCard(
                icon = "💧",
                title = if (isLao) "ລະບົບຕິດຕາມຂໍ້ມູນການນຳໃຊ້ນ້ຳ" else "Water Resources Usage System",
                badge = if (isLao) "ການນຳໃຊ້ນ້ຳ" else "Water Usage",
                description = if (isLao)
                    "ຄຸ້ມຄອງໃບອະນຸຍາດນຳໃຊ້ນ້ຳຂອງໂຄງການປະປາ, ອຸດສາຫະກຳ, ເຂື່ອນໄຟຟ້າ ແລະ ກະສິກຳ"
                else
                    "Management of water extraction concessions, permits, pumping volumes, and river basins.",
                accentColor = Indigo400,
                onEnter = { onNavigateSection(SystemSection.WATER_USAGE) },
                isLao = isLao,
                testTag = "enter_water_usage_btn"
            )
        }

        // System 3: Wastewater Discharge Card
        item {
            SystemPortalCard(
                icon = "🏭",
                title = if (isLao) "ລະບົບຕິດຕາມໂຄງການປ່ອຍນ້ຳເປື້ອນ" else "Wastewater Discharge Monitoring",
                badge = if (isLao) "ການປ່ອຍນ້ຳເປື້ອນ" else "Wastewater",
                description = if (isLao)
                    "ກວດກາມາດຕະຖານນ້ຳເສຍປ່ອຍອອກຈາກໂຮງງານອຸດສາຫະກຳ, ບໍ່ແຮ່ ແລະ ເຂດເສດຖະກິດພິເສດ"
                else
                    "Compliance tracking for factory effluent discharges, WWTP standards, and environmental audits.",
                accentColor = Rose500,
                onEnter = { onNavigateSection(SystemSection.WASTEWATER) },
                isLao = isLao,
                testTag = "enter_wastewater_btn"
            )
        }

        // Quick Link: Analytics & Settings
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigateSection(SystemSection.ANALYTICS_REPORTS) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300)
                ) {
                    Text(text = "📑 ", fontSize = 16.sp)
                    Text(
                        text = if (isLao) "ບົດວິເຄາະສະຖິຕິ" else "Analytics",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = { onNavigateSection(SystemSection.SYSTEM_SETTINGS) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300)
                ) {
                    Text(text = "⚙️ ", fontSize = 16.sp)
                    Text(
                        text = if (isLao) "ຕັ້ງຄ່າລະບົບ" else "Settings",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Slate800, Slate700)))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 20.sp)
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SystemPortalCard(
    icon: String,
    title: String,
    badge: String,
    description: String,
    accentColor: Color,
    onEnter: () -> Unit,
    isLao: Boolean,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onEnter() },
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Slate800, accentColor.copy(alpha = 0.3f)))),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 24.sp)
                }

                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = badge,
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                color = Slate100,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate300
            )

            HorizontalDivider(color = Slate800)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Emerald400)
                    )
                    Text(
                        text = if (isLao) "ພ້ອມໃຊ້ງານ" else "Ready",
                        fontSize = 11.sp,
                        color = Emerald400,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onEnter,
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag(testTag)
                ) {
                    Text(
                        text = if (isLao) "🔑 ເຂົ້າສູ່ລະບົບ" else "Enter",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
