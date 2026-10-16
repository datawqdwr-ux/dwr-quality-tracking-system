package com.aistudio.waterresources.data.repository

import com.aistudio.waterresources.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WaterResourcesRepository {

    private val _stations = MutableStateFlow<List<WaterQualityStation>>(createInitialStations())
    val stations: StateFlow<List<WaterQualityStation>> = _stations.asStateFlow()

    private val _usageProjects = MutableStateFlow<List<WaterUsageProject>>(createInitialUsageProjects())
    val usageProjects: StateFlow<List<WaterUsageProject>> = _usageProjects.asStateFlow()

    private val _wastewaterFacilities = MutableStateFlow<List<WastewaterFacility>>(createInitialWastewaterFacilities())
    val wastewaterFacilities: StateFlow<List<WastewaterFacility>> = _wastewaterFacilities.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(createInitialAuditLogs())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    fun addStationReading(stationId: String, reading: WaterQualityReading) {
        _stations.update { currentList ->
            currentList.map { station ->
                if (station.id == stationId) {
                    val updatedReadings = listOf(reading) + station.readings
                    station.copy(
                        readings = updatedReadings,
                        lastInspectionDate = reading.timestamp,
                        overallStatus = reading.status
                    )
                } else station
            }
        }
        logAction("ADD_READING", "ບັນທຶກຄຸນນະພາບນ້ຳສະຖານີ: $stationId (pH: ${reading.ph}, DO: ${reading.dissolvedOxygen})")
    }

    fun addUsageProject(project: WaterUsageProject) {
        _usageProjects.update { listOf(project) + it }
        logAction("ADD_PROJECT", "ເພີ່ມໂຄງການນຳໃຊ້ນ້ຳ: ${project.projectName}")
    }

    fun updateUsageProject(project: WaterUsageProject) {
        _usageProjects.update { current ->
            current.map { if (it.id == project.id) project else it }
        }
        logAction("UPDATE_PROJECT", "ອັບເດດໂຄງການນຳໃຊ້ນ້ຳ: ${project.projectName}")
    }

    fun deleteUsageProject(projectId: String) {
        _usageProjects.update { current ->
            current.filterNot { it.id == projectId }
        }
        logAction("DELETE_PROJECT", "ລຶບໂຄງການນຳໃຊ້ນ້ຳ: $projectId")
    }

    fun addWastewaterFacility(facility: WastewaterFacility) {
        _wastewaterFacilities.update { listOf(facility) + it }
        logAction("ADD_WASTEWATER", "ເພີ່ມໂຄງການປ່ອຍນ້ຳເປື້ອນ: ${facility.facilityName}")
    }

    fun updateWastewaterFacility(facility: WastewaterFacility) {
        _wastewaterFacilities.update { current ->
            current.map { if (it.id == facility.id) facility else it }
        }
        logAction("UPDATE_WASTEWATER", "ອັບເດດໂຄງການປ່ອຍນ້ຳເປື້ອນ: ${facility.facilityName}")
    }

    fun deleteWastewaterFacility(id: String) {
        _wastewaterFacilities.update { current ->
            current.filterNot { it.id == id }
        }
        logAction("DELETE_WASTEWATER", "ລຶບໂຄງການປ່ອຍນ້ຳເປື້ອນ: $id")
    }

    private fun logAction(action: String, details: String) {
        val newLog = AuditLog(
            id = "LOG-${System.currentTimeMillis()}",
            action = action,
            user = "datawqdwr@gmail.com",
            timestamp = "2025-02-14 10:15",
            details = details
        )
        _auditLogs.update { listOf(newLog) + it }
    }

    companion object {
        fun createInitialStations(): List<WaterQualityStation> = listOf(
            WaterQualityStation(
                id = "ST-VTE-01",
                stationCode = "WQS-VTE-01",
                name = "ສະຖານີ ດົງໂດກ (ແມ່ນ້ຳງື່ມ)",
                river = "ແມ່ນ້ຳງື່ມ",
                province = "ນະຄອນຫຼວງວຽງຈັນ",
                district = "ໄຊທານີ",
                latitude = 18.0652,
                longitude = 102.6841,
                lastInspectionDate = "2025-02-10",
                overallStatus = ComplianceStatus.NORMAL,
                readings = listOf(
                    WaterQualityReading(
                        id = "R-01",
                        timestamp = "2025-02-10",
                        monthYear = "02/2025",
                        ph = 7.4,
                        dissolvedOxygen = 6.8,
                        bod5 = 1.2,
                        cod = 3.8,
                        tss = 18.5,
                        conductivity = 210.0,
                        temperature = 26.5,
                        turbidity = 12.0,
                        status = ComplianceStatus.NORMAL,
                        inspectorName = "ສົມຈິດ ສີສຸວັນ",
                        notes = "ສະພາບນ້ຳປົກກະຕິ, ໃສ, ບໍ່ພົບສິ່ງປົນເປື້ອນຜິດປົກກະຕິ"
                    ),
                    WaterQualityReading(
                        id = "R-02",
                        timestamp = "2025-01-15",
                        monthYear = "01/2025",
                        ph = 7.2,
                        dissolvedOxygen = 7.1,
                        bod5 = 1.4,
                        cod = 4.1,
                        tss = 22.0,
                        conductivity = 225.0,
                        temperature = 25.0,
                        turbidity = 14.5,
                        status = ComplianceStatus.NORMAL,
                        inspectorName = "ສົມຈິດ ສີສຸວັນ"
                    )
                )
            ),
            WaterQualityStation(
                id = "ST-VTE-02",
                stationCode = "WQS-VTE-02",
                name = "ສະຖານີ ທ່າເດື່ອ (ແມ່ນ້ຳຂອງ)",
                river = "ແມ່ນ້ຳຂອງ",
                province = "ນະຄອນຫຼວງວຽງຈັນ",
                district = "ຫາດຊາຍຟອງ",
                latitude = 17.8980,
                longitude = 102.6515,
                lastInspectionDate = "2025-02-08",
                overallStatus = ComplianceStatus.WARNING,
                readings = listOf(
                    WaterQualityReading(
                        id = "R-03",
                        timestamp = "2025-02-08",
                        monthYear = "02/2025",
                        ph = 6.8,
                        dissolvedOxygen = 5.2,
                        bod5 = 2.4,
                        cod = 6.8,
                        tss = 32.0,
                        conductivity = 340.0,
                        temperature = 27.2,
                        turbidity = 28.0,
                        status = ComplianceStatus.WARNING,
                        inspectorName = "ບຸນມີ ວົງສາ",
                        notes = "ລະດັບ DO ຫຼຸດລົງເລັກນ້ອຍ ເນື່ອງຈາກມີນ້ຳໄຫຼລົງຈາກຄອງລະບາຍນ້ຳ"
                    )
                )
            ),
            WaterQualityStation(
                id = "ST-LPB-01",
                stationCode = "WQS-LPB-01",
                name = "ສະຖານີ ປາກຄານ (ແມ່ນ້ຳຄານ)",
                river = "ແມ່ນ້ຳຄານ",
                province = "ຫຼວງພະບາງ",
                district = "ນະຄອນຫຼວງພະບາງ",
                latitude = 19.8920,
                longitude = 102.1380,
                lastInspectionDate = "2025-02-05",
                overallStatus = ComplianceStatus.NORMAL,
                readings = listOf(
                    WaterQualityReading(
                        id = "R-04",
                        timestamp = "2025-02-05",
                        monthYear = "02/2025",
                        ph = 7.8,
                        dissolvedOxygen = 7.4,
                        bod5 = 0.9,
                        cod = 2.5,
                        tss = 12.0,
                        conductivity = 180.0,
                        temperature = 23.5,
                        turbidity = 8.0,
                        status = ComplianceStatus.NORMAL,
                        inspectorName = "ຄຳຫຼ້າ ໄຊຍະສິດ"
                    )
                )
            ),
            WaterQualityStation(
                id = "ST-CPS-01",
                stationCode = "WQS-CPS-01",
                name = "ສະຖານີ ປາກເຊ (ນ້ຳເຊໂດນ ໄຫຼລົງແມ່ນ້ຳຂອງ)",
                river = "ນ້ຳເຊໂດນ",
                province = "ຈຳປາສັກ",
                district = "ນະຄອນປາກເຊ",
                latitude = 15.1215,
                longitude = 105.7820,
                lastInspectionDate = "2025-02-01",
                overallStatus = ComplianceStatus.EXCEEDED,
                readings = listOf(
                    WaterQualityReading(
                        id = "R-05",
                        timestamp = "2025-02-01",
                        monthYear = "02/2025",
                        ph = 6.2,
                        dissolvedOxygen = 3.8,
                        bod5 = 4.2,
                        cod = 11.5,
                        tss = 45.0,
                        conductivity = 490.0,
                        temperature = 28.0,
                        turbidity = 42.0,
                        status = ComplianceStatus.EXCEEDED,
                        inspectorName = "ວຽງໄຊ ດວງມາລາ",
                        notes = "ຄ່າ BOD ແລະ TSS ສູງເກີນມາດຕະຖານແຫ່ງຊາດ, ແຈ້ງເຕືອນໜ່ວຍງານກວດກາດ່ວນ"
                    )
                )
            )
        )

        fun createInitialUsageProjects(): List<WaterUsageProject> = listOf(
            WaterUsageProject(
                id = "proj-001",
                projectName = "ໂຄງການຜະລິດນໍ້າປະປາ ນະຄອນຫຼວງວຽງຈັນ (ດົງບັງ)",
                projectCategory = "ການປະປາ ແລະ ນໍ້າສະອາດ",
                ownerName = "ລັດວິສາຫະກິດ ນໍ້າປະປາ ນະຄອນຫຼວງວຽງຈັນ",
                contactPhone = "021 412345",
                contactEmail = "nampapa.vte@gmail.com",
                permitNumber = "WTR-VTE-2022/014",
                permitIssueDate = "2022-04-01",
                permitExpiryDate = "2027-03-31",
                status = ProjectStatus.ACTIVE,
                waterVolume = 35000.0,
                waterVolumeUnit = "m³/day",
                waterSourceType = "ແມ່ນໍ້າ",
                waterSourceName = "ແມ່ນໍ້າງື່ມ",
                village = "ດົງບັງ",
                district = "ໄຊທານີ",
                province = "ນະຄອນຫຼວງວຽງຈັນ",
                latitude = 18.0652,
                longitude = 102.6841,
                pumpCapacity = "1,800 m³/ຊົ່ວໂມງ (ຈັກສູບ 250 kW ຈຳນວນ 3 ຊຸດ)",
                waterUsagePlan = "ດູດນໍ້າດິບຈາກແມ່ນໍ້າງື່ມ ເຂົ້າສູ່ລະບົບບໍາບັດ ແລະ ກັ່ນຕອງນໍ້າສະອາດ ເພື່ອສະໜອງໃຫ້ແກ່ຊຸມຊົນ",
                notes = "ມີການກວດກາຄຸນນະພາບນໍ້າທຸກໆເດືອນ ແລະ ລາຍງານໃຫ້ພະແນກ ຊສ ແຂວງ",
                updatedAt = "2025-01-10"
            ),
            WaterUsageProject(
                id = "proj-002",
                projectName = "ໂຮງງານເບຍລາວ ແລະ ເຄື່ອງດື່ມ (ສາຂາວຽງຈັນ)",
                projectCategory = "ອຸດສາຫະກຳ ແລະ ປຸງແຕ່ງ",
                ownerName = "ບໍລິສັດ ເບຍລາວ ຈໍາກັດ",
                contactPhone = "021 812100",
                contactEmail = "beerlao.ops@beerlao.la",
                permitNumber = "WTR-VTE-2021/088",
                permitIssueDate = "2021-06-15",
                permitExpiryDate = "2026-11-15",
                status = ProjectStatus.ACTIVE,
                waterVolume = 4200.0,
                waterVolumeUnit = "m³/day",
                waterSourceType = "ນໍ້າໃຕ້ດິນ ແລະ ແມ່ນໍ້າຂອງ",
                waterSourceName = "ແມ່ນໍ້າຂອງ",
                village = "ສະຖານີ ຫຼັກ 12",
                district = "ຫາດຊາຍຟອງ",
                province = "ນະຄອນຫຼວງວຽງຈັນ",
                latitude = 17.9254,
                longitude = 102.6621,
                pumpCapacity = "350 m³/ຊົ່ວໂມງ (ລະບົບຈັກດູດໄຟຟ້າ)",
                waterUsagePlan = "ນໍາໃຊ້ນໍ້າໃນຂະບວນການຜະລິດເຄື່ອງດື່ມ, ລ້າງອຸປະກອນ ແລະ ລະບົບຫຼໍ່ເຢັນ; ມີລະບົບບໍາບັດນໍ້າເສຍ WWTP 100%",
                notes = "ໃບອະນຸຍາດໃກ້ຈະໝົດອາຍຸໃນອີກ 2 ເດືອນຂ້າງໜ້າ",
                updatedAt = "2025-02-14"
            ),
            WaterUsageProject(
                id = "proj-003",
                projectName = "ໂຄງການເຂື່ອນໄຟຟ້ານໍ້າຕົກ ນໍ້າງື່ມ 2",
                projectCategory = "ພະລັງງານໄຟຟ້ານ້ຳຕົກ",
                ownerName = "ບໍລິສັດ ໄຟຟ້າ ນໍ້າງື່ມ 2 ຈໍາກັດ",
                contactPhone = "021 264555",
                contactEmail = "namngum2@power.la",
                permitNumber = "WTR-VTP-2020/003",
                permitIssueDate = "2020-09-01",
                permitExpiryDate = "2030-08-31",
                status = ProjectStatus.ACTIVE,
                waterVolume = 1250000.0,
                waterVolumeUnit = "m³/day",
                waterSourceType = "ອ່າງເກັບນໍ້າ",
                waterSourceName = "ແມ່ນໍ້າງື່ມ",
                village = "ຫ້ວຍຫອຍ",
                district = "ເມືອງທຸລະຄົມ",
                province = "ແຂວງວຽງຈັນ",
                latitude = 18.5284,
                longitude = 102.7758,
                pumpCapacity = "ກັງຫັນນ້ຳ 615 MW ຜະລິດກະແສໄຟຟ້າ",
                waterUsagePlan = "ໄຫຼຜ່ານກັງຫັນນໍ້າເພື່ອປັ່ນໄຟຟ້າ ແລ້ວປ່ອຍລົງສູ່ແມ່ນໍ້າງື່ມຕອນລຸ່ມໂດຍບໍ່ມີການສູນເສຍປະລິມານນ້ຳ",
                notes = "ມີລະບົບ Telemetry ສົ່ງຂໍ້ມູນອັດຕາການໄຫຼຕະຫຼອດ 24 ຊົ່ວໂມງ",
                updatedAt = "2025-02-12"
            )
        )

        fun createInitialWastewaterFacilities(): List<WastewaterFacility> = listOf(
            WastewaterFacility(
                id = "WW-001",
                facilityName = "ໂຮງງານແປຮູບຢາງພາລາ ໄຊທານີ",
                sector = "ໂຮງງານອຸດສາຫະກຳ",
                companyName = "ບໍລິສັດ ລາວຢາງພາລາ ພັດທະນາ ຈຳກັດ",
                permitNumber = "WW-VTE-2023/007",
                dischargeVolume = 450.0,
                receivingWaterBody = "ຫ້ວຍຊາຍ (ໄຫຼລົງແມ່ນ້ຳງື່ມ)",
                treatmentSystem = "Aerated Lagoon & Sedimentation Pond",
                province = "ນະຄອນຫຼວງວຽງຈັນ",
                district = "ໄຊທານີ",
                village = "ທ່າຈຳປາ",
                latitude = 18.1120,
                longitude = 102.7210,
                lastInspectionDate = "2025-02-12",
                complianceStatus = ComplianceStatus.NORMAL,
                phValue = 7.1,
                bodValue = 18.0,
                codValue = 55.0,
                tssValue = 35.0,
                notes = "ຜ່ານມາດຕະຖານນ້ຳປ່ອຍອອກສິ່ງແວດລ້ອມຕາມດຳລັດ 81/ລບ"
            ),
            WastewaterFacility(
                id = "WW-002",
                facilityName = "ເຂດອຸດສາຫະກຳ ແລະ ນິຄົມປຸງແຕ່ງ ຫຼວງພະບາງ",
                sector = "ເຂດເສດຖະກິດພິເສດ",
                companyName = "ກຸ່ມລົງທຶນອຸດສາຫະກຳພາກເໜືອ",
                permitNumber = "WW-LPB-2022/015",
                dischargeVolume = 1200.0,
                receivingWaterBody = "ແມ່ນ້ຳຂອງ",
                treatmentSystem = "Activated Sludge (ລະບົບຊີວະພາບ)",
                province = "ຫຼວງພະບາງ",
                district = "ນະຄອນຫຼວງພະບາງ",
                village = "ຊຽງແມນ",
                latitude = 19.8845,
                longitude = 102.1240,
                lastInspectionDate = "2025-02-09",
                complianceStatus = ComplianceStatus.WARNING,
                phValue = 8.6,
                bodValue = 28.5,
                codValue = 88.0,
                tssValue = 48.0,
                notes = "pH ແລະ BOD ສູງໃກ້ຂີດຈຳກັດສູງສຸດ, ຕັກເຕືອນໃຫ້ເພີ່ມປະລິມານອົກຊີເຈນໃນຖັງບຳບັດ"
            ),
            WastewaterFacility(
                id = "WW-003",
                facilityName = "ໂຮງງານຟອກຍ້ອມ ແລະ ຕັດຫຍິບ ນະຄອນປາກເຊ",
                sector = "ໂຮງງານແປຮູບ",
                companyName = "ບໍລິສັດ ຕັດຫຍິບສາກົນ ຈຳກັດ",
                permitNumber = "WW-CPS-2024/002",
                dischargeVolume = 320.0,
                receivingWaterBody = "ນ້ຳເຊໂດນ",
                treatmentSystem = "Chemical Coagulation & Membrane Filtration",
                province = "ຈຳປາສັກ",
                district = "ນະຄອນປາກເຊ",
                village = "ປາກເຊໃຕ້",
                latitude = 15.1180,
                longitude = 105.7950,
                lastInspectionDate = "2025-01-28",
                complianceStatus = ComplianceStatus.EXCEEDED,
                phValue = 9.2,
                bodValue = 45.0,
                codValue = 130.0,
                tssValue = 85.0,
                notes = "ພົບຄ່າ COD ແລະ TSS ເກີນມາດຕະຖານ 2 ເທົ່າຕົວ, ສັ່ງການໃຫ້ປັບປຸງລະບົບພາຍໃນ 15 ວັນ"
            )
        )

        fun createInitialAuditLogs(): List<AuditLog> = listOf(
            AuditLog("LOG-01", "LOGIN", "datawqdwr@gmail.com", "2025-02-14 08:30", "ເຂົ້າສູ່ລະບົບສຳເລັດ (Session Started)"),
            AuditLog("LOG-02", "INSPECTION", "datawqdwr@gmail.com", "2025-02-14 09:15", "ກວດກາສະຖານີ ST-VTE-01 ຄຸນນະພາບນ້ຳປົກກະຕິ"),
            AuditLog("LOG-03", "ALERT", "system_alert", "2025-02-14 09:40", "ແຈ້ງເຕືອນ: ໂຮງງານ WW-003 ຄ່າ COD ເກີນມາດຕະຖານ")
        )
    }
}
