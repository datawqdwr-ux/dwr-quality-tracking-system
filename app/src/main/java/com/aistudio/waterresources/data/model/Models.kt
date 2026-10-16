package com.aistudio.waterresources.data.model

enum class Language(val code: String, val label: String, val flag: String) {
    LO("lo", "ລາວ", "🇱🇦"),
    EN("en", "English", "🇬🇧")
}

enum class UserRole(val code: String, val labelLao: String, val labelEn: String) {
    ADMIN("admin", "ຜູ້ຄຸ້ມຄອງລະບົບ (Admin)", "System Administrator"),
    INSPECTOR("officer", "ເຈົ້າໜ້າທີ່ກວດກາ (Officer)", "Inspection Officer"),
    GUEST("viewer", "ຜູ້ຊົມທົ່ວໄປ (Guest)", "Guest Viewer")
}

data class UserSession(
    val username: String = "datawqdwr",
    val email: String = "datawqdwr@gmail.com",
    val role: UserRole = UserRole.ADMIN,
    val isLoggedIn: Boolean = true
)

enum class SystemSection(val titleLao: String, val titleEn: String, val icon: String) {
    PORTAL_DASHBOARD("ໜ້າຫຼັກພອດທໍລ", "National Portal", "🌊"),
    WATER_QUALITY("ຄຸນນະພາບນ້ຳ", "Water Quality", "🧪"),
    WATER_USAGE("ການນຳໃຊ້ນ້ຳ", "Water Usage", "💧"),
    WASTEWATER("ການປ່ອຍນ້ຳເປື້ອນ", "Wastewater Discharge", "🏭"),
    ANALYTICS_REPORTS("ບົດລາຍງານ & ສະຖິຕິ", "Analytics & Reports", "📊"),
    SYSTEM_SETTINGS("ຕັ້ງຄ່າລະບົບ", "System Settings", "⚙️")
}

enum class ComplianceStatus(val labelLao: String, val labelEn: String) {
    NORMAL("ປົກກະຕິ", "Normal"),
    WARNING("ເຝົ້າລະວັງ", "Warning"),
    EXCEEDED("ເກີນມາດຕະຖານ", "Exceeded Standard")
}

enum class ProjectStatus(val labelLao: String, val labelEn: String) {
    ACTIVE("ກຳລັງດຳເນີນງານ", "Active"),
    PENDING_RENEWAL("ລໍຖ້າຕໍ່ອາຍຸ", "Pending Renewal"),
    EXPIRED("ໝົດອາຍຸ", "Expired")
}

data class WaterQualityReading(
    val id: String,
    val timestamp: String,
    val monthYear: String,
    val ph: Double,
    val dissolvedOxygen: Double, // mg/L
    val bod5: Double,            // mg/L
    val cod: Double,             // mg/L
    val tss: Double,             // mg/L
    val conductivity: Double,    // µS/cm
    val temperature: Double,     // °C
    val turbidity: Double,       // NTU
    val status: ComplianceStatus,
    val inspectorName: String,
    val notes: String = ""
)

data class WaterQualityStation(
    val id: String,
    val stationCode: String,
    val name: String,
    val river: String,
    val province: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    val lastInspectionDate: String,
    val readings: List<WaterQualityReading> = emptyList(),
    val overallStatus: ComplianceStatus = ComplianceStatus.NORMAL
)

data class WaterUsageProject(
    val id: String,
    val projectName: String,
    val projectCategory: String,
    val ownerName: String,
    val contactPhone: String,
    val contactEmail: String,
    val permitNumber: String,
    val permitIssueDate: String,
    val permitExpiryDate: String,
    val status: ProjectStatus,
    val waterVolume: Double,
    val waterVolumeUnit: String = "m³/day",
    val waterSourceType: String,
    val waterSourceName: String,
    val village: String,
    val district: String,
    val province: String,
    val latitude: Double,
    val longitude: Double,
    val pumpCapacity: String,
    val waterUsagePlan: String,
    val notes: String = "",
    val updatedAt: String = ""
)

data class WastewaterFacility(
    val id: String,
    val facilityName: String,
    val sector: String,
    val companyName: String,
    val permitNumber: String,
    val dischargeVolume: Double,
    val dischargeVolumeUnit: String = "m³/day",
    val receivingWaterBody: String,
    val treatmentSystem: String,
    val province: String,
    val district: String,
    val village: String,
    val latitude: Double,
    val longitude: Double,
    val lastInspectionDate: String,
    val complianceStatus: ComplianceStatus,
    val phValue: Double,
    val bodValue: Double,
    val codValue: Double,
    val tssValue: Double,
    val notes: String = ""
)

data class AuditLog(
    val id: String,
    val action: String,
    val user: String,
    val timestamp: String,
    val details: String
)
