# ກົມຊັບພະຍາກອນນ້ຳ — ລະບົບຂໍ້ມູນຊັບພະຍາກອນນ້ຳ (Department of Water Resources Mobile App)

Modern Android application rewritten in **Kotlin** and **Jetpack Compose** following **Clean MVVM Architecture** and **Material Design 3**.

---

## 🌊 Overview & Core Systems

The application manages and unifies three vital sub-systems under the **Ministry of Agriculture and Environment / Department of Water Resources (DWR)**:

1. **🧪 Water Quality Monitoring System (ລະບົບຕິດຕາມຄຸນນະພາບນ້ຳ)**:
   - National surface water monitoring network (Mekong, Nam Ngum, Nam Khan, Xe Don).
   - Real-time parameter indicators: pH (6.5–8.5 standard), Dissolved Oxygen (DO ≥ 6.0 mg/L), Biochemical Oxygen Demand (BOD5 ≤ 1.5 mg/L), Chemical Oxygen Demand (COD ≤ 5.0 mg/L), Total Suspended Solids (TSS ≤ 25.0 mg/L), Conductivity, and Turbidity.
   - Live compliance status categorization: **Normal** (Green), **Warning** (Amber), and **Exceeded Standard** (Red).
   - Form for recording on-site inspection measurements with automatic compliance determination.

2. **💧 Water Resources Usage Monitoring System (ລະບົບຕິດຕາມຂໍ້ມູນການນຳໃຊ້ນ້ຳ)**:
   - Concession and permit tracking for water utilities (Vientiane Water Supply), industrial factories (Lao Brewery Co.), hydropower dams (Nam Ngum 2), and irrigation projects.
   - Detailed monitoring of extraction volumes ($m^3/\text{day}$), pumping capacities, coordinates, water source types (river, groundwater, reservoir), and permit validity periods.
   - Complete project CRUD and category/province filtering.

3. **🏭 Wastewater Discharge Monitoring System (ລະບົບຕິດຕາມໂຄງການປ່ອຍນ້ຳເປື້ອນ)**:
   - Effluent discharge compliance monitoring across industrial factories, processing plants, and special economic zones.
   - Treatment technology tracking (Activated Sludge, Aerated Lagoons, Oxidation Ponds, Chemical Coagulation).
   - Regulatory standards tracking according to Lao environmental decrees (pH, BOD, COD, TSS).

4. **📊 Analytics & Annual Reporting**:
   - National compliance rate calculation (Surface water quality % and industrial effluent compliance %).
   - Water balance volume metrics (raw water extraction vs. treated discharge volume).
   - Regulatory directives and corrective actions.

5. **🌐 Cross-Cutting & Session Features**:
   - Bilingual support: **Lao (ພາສາລາວ 🇱🇦)** and **English (🇬🇧)**.
   - Role-based authorization: **Admin** (Full CRUD), **Inspector** (Audit/Log entry), and **Guest Viewer** (Read-only).
   - Detailed audit trails for regulatory compliance.

---

## 🛠️ Architecture & Tech Stack

- **Target SDK**: Android SDK 36 (Android 15+)
- **Minimum SDK**: Android SDK 26 (Android 8.0+)
- **UI Framework**: Jetpack Compose with Material 3 Design tokens
- **Architecture**: MVVM with Kotlin Coroutines and `StateFlow`
- **Application ID**: `com.aistudio.waterresources.dwrportal`
- **Namespace**: `com.aistudio.waterresources`
- **Language**: Kotlin 2.1.0
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`)
