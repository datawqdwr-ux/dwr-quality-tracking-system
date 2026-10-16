package com.aistudio.waterresources.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.waterresources.data.model.ComplianceStatus
import com.aistudio.waterresources.data.model.Language
import com.aistudio.waterresources.ui.theme.*
import com.aistudio.waterresources.ui.viewmodel.WaterResourcesUiState

@Composable
fun AnalyticsReportScreen(
    state: WaterResourcesUiState,
    modifier: Modifier = Modifier
) {
    val isLao = state.language == Language.LO

    val totalStations = state.stations.size
    val compliantStations = state.stations.count { it.overallStatus == ComplianceStatus.NORMAL }
    val wqComplianceRate = if (totalStations > 0) (compliantStations * 100 / totalStations) else 100

    val totalFacilities = state.wastewaterFacilities.size
    val compliantFacilities = state.wastewaterFacilities.count { it.complianceStatus == ComplianceStatus.NORMAL }
    val wwComplianceRate = if (totalFacilities > 0) (compliantFacilities * 100 / totalFacilities) else 100

    val totalUsageM3 = state.usageProjects.sumOf { it.waterVolume }
    val totalWastewaterM3 = state.wastewaterFacilities.sumOf { it.dischargeVolume }

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
                    text = if (isLao) "📊 ບົດວິເຄາະສະຖິຕິຊັບພະຍາກອນນ້ຳປະຈຳປີ" else "📊 Annual Water Resources Analytical Report",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Slate100
                )
                Text(
                    text = if (isLao) "ບົດສະຫຼຸບສັງລວມຄຸນນະພາບ, ການນຳໃຊ້ ແລະ ການປ່ອຍນ້ຳເປື້ອນແຫ່ງຊາດ" else "National Summary of Quality, Concessions, & Discharges",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate400
                )
            }
        }

        // Compliance Overview Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("compliance_overview_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isLao) "ອັດຕາການປະຕິບັດຕາມມາດຕະຖານແຫ່ງຊາດ" else "National Environmental Compliance Rate",
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate100,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = if (isLao) "ຄຸນນະພາບນ້ຳໜ້າດິນ:" else "Surface Water Quality:", color = Slate300, fontSize = 13.sp)
                        Text(text = "$wqComplianceRate%", color = Emerald400, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    LinearProgressIndicator(
                        progress = { wqComplianceRate / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Emerald400,
                        trackColor = Slate800
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = if (isLao) "ມາດຕະຖານນ້ຳເປື້ອນໂຮງງານ:" else "Industrial Effluent Standard:", color = Slate300, fontSize = 13.sp)
                        Text(text = "$wwComplianceRate%", color = Cyan400, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    LinearProgressIndicator(
                        progress = { wwComplianceRate / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Cyan400,
                        trackColor = Slate800
                    )
                }
            }
        }

        // Water Balance Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isLao) "ດຸ່ນດ່ຽງປະລິມານນ້ຳ (Water Balance Indicator)" else "Water Balance Volume",
                        style = MaterialTheme.typography.titleMedium,
                        color = Indigo400,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• ປະລິມານນ້ຳດິບນຳໃຊ້: %,.0f m³/day".format(totalUsageM3),
                        color = Slate200,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "• ປະລິມານນ້ຳເປື້ອນປ່ອຍອອກ: %,.0f m³/day".format(totalWastewaterM3),
                        color = Slate200,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "• ອັດຕາສ່ວນນ້ຳເປື້ອນຕໍ່ການນຳໃຊ້: %.2f%%".format(if (totalUsageM3 > 0) totalWastewaterM3 * 100 / totalUsageM3 else 0.0),
                        color = Slate400,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Key Findings
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isLao) "ຂໍ້ສະເໜີແນະ ແລະ ມາດຕະການຕິດຕາມ" else "Recommendations & Directives",
                        style = MaterialTheme.typography.titleMedium,
                        color = Rose400,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isLao)
                            "1. ສືບຕໍ່ຕິດຕາມກວດກາສະຖານີຈຸດສຸມເຂດ ນະຄອນປາກເຊ ທີ່ມີຄ່າ BOD ແລະ TSS ເກີນມາດຕະຖານ.\n2. ເລັ່ງລັດໃຫ້ໂຮງງານແປຮູບທີ່ຍັງບໍ່ທັນມີລະບົບ Telemetry ຕິດຕັ້ງເຄື່ອງວັດແທກອັດຕະໂນມັດ.\n3. ກວດກາຄືນບັນດາໂຄງການທີ່ໃບອະນຸຍາດນຳໃຊ້ນ້ຳໃກ້ໝົດອາຍຸ."
                        else
                            "1. Continue intense monitoring at Pakse station with elevated BOD and TSS.\n2. Mandate real-time telemetry sensors for all industrial effluent outfalls.\n3. Audit concession permits nearing expiration dates.",
                        color = Slate300,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
