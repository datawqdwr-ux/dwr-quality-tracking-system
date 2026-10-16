package com.aistudio.waterresources.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.waterresources.data.model.*
import com.aistudio.waterresources.data.repository.WaterResourcesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WaterResourcesUiState(
    val currentSection: SystemSection = SystemSection.PORTAL_DASHBOARD,
    val language: Language = Language.LO,
    val userSession: UserSession = UserSession(),
    val stations: List<WaterQualityStation> = emptyList(),
    val usageProjects: List<WaterUsageProject> = emptyList(),
    val wastewaterFacilities: List<WastewaterFacility> = emptyList(),
    val auditLogs: List<AuditLog> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "ALL",
    val selectedProvince: String = "ALL",
    val selectedStatus: String = "ALL",
    val selectedStation: WaterQualityStation? = null,
    val selectedProject: WaterUsageProject? = null,
    val selectedFacility: WastewaterFacility? = null,
    val showAddRecordDialog: Boolean = false,
    val showAddProjectDialog: Boolean = false,
    val showAddFacilityDialog: Boolean = false,
    val showDetailsDialog: Boolean = false,
    val isGuestMode: Boolean = false
)

class WaterResourcesViewModel(
    private val repository: WaterResourcesRepository = WaterResourcesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaterResourcesUiState())
    val uiState: StateFlow<WaterResourcesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.stations,
                repository.usageProjects,
                repository.wastewaterFacilities,
                repository.auditLogs
            ) { stations, projects, facilities, logs ->
                _uiState.update { current ->
                    current.copy(
                        stations = stations,
                        usageProjects = projects,
                        wastewaterFacilities = facilities,
                        auditLogs = logs
                    )
                }
            }.collect()
        }
    }

    fun setSection(section: SystemSection) {
        _uiState.update { it.copy(currentSection = section, searchQuery = "") }
    }

    fun toggleLanguage() {
        _uiState.update { current ->
            val next = if (current.language == Language.LO) Language.EN else Language.LO
            current.copy(language = next)
        }
    }

    fun setLanguage(language: Language) {
        _uiState.update { it.copy(language = language) }
    }

    fun setUserRole(role: UserRole) {
        _uiState.update { current ->
            current.copy(
                userSession = current.userSession.copy(role = role),
                isGuestMode = (role == UserRole.GUEST)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setProvinceFilter(province: String) {
        _uiState.update { it.copy(selectedProvince = province) }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { it.copy(selectedStatus = status) }
    }

    fun selectStation(station: WaterQualityStation?) {
        _uiState.update { it.copy(selectedStation = station, showDetailsDialog = station != null) }
    }

    fun selectProject(project: WaterUsageProject?) {
        _uiState.update { it.copy(selectedProject = project, showDetailsDialog = project != null) }
    }

    fun selectFacility(facility: WastewaterFacility?) {
        _uiState.update { it.copy(selectedFacility = facility, showDetailsDialog = facility != null) }
    }

    fun setShowAddRecordDialog(show: Boolean) {
        _uiState.update { it.copy(showAddRecordDialog = show) }
    }

    fun setShowAddProjectDialog(show: Boolean) {
        _uiState.update { it.copy(showAddProjectDialog = show) }
    }

    fun setShowAddFacilityDialog(show: Boolean) {
        _uiState.update { it.copy(showAddFacilityDialog = show) }
    }

    fun closeDetailsDialog() {
        _uiState.update {
            it.copy(
                showDetailsDialog = false,
                selectedStation = null,
                selectedProject = null,
                selectedFacility = null
            )
        }
    }

    fun addStationReading(stationId: String, reading: WaterQualityReading) {
        repository.addStationReading(stationId, reading)
        setShowAddRecordDialog(false)
    }

    fun addUsageProject(project: WaterUsageProject) {
        repository.addUsageProject(project)
        setShowAddProjectDialog(false)
    }

    fun deleteUsageProject(projectId: String) {
        repository.deleteUsageProject(projectId)
        closeDetailsDialog()
    }

    fun addWastewaterFacility(facility: WastewaterFacility) {
        repository.addWastewaterFacility(facility)
        setShowAddFacilityDialog(false)
    }

    fun deleteWastewaterFacility(id: String) {
        repository.deleteWastewaterFacility(id)
        closeDetailsDialog()
    }
}
