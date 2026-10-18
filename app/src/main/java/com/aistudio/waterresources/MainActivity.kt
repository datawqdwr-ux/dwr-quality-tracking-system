package com.aistudio.waterresources

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.aistudio.waterresources.data.model.SystemSection
import com.aistudio.waterresources.ui.components.DwrBottomNav
import com.aistudio.waterresources.ui.components.DwrTopBar
import com.aistudio.waterresources.ui.screens.*
import com.aistudio.waterresources.ui.theme.WaterResourcesTheme
import com.aistudio.waterresources.ui.viewmodel.WaterResourcesViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WaterResourcesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WaterResourcesTheme(darkTheme = true) {
                val state by viewModel.uiState.collectAsState()

                // Back navigation handler: return to dashboard if on sub-screen
                BackHandler(enabled = state.currentSection != SystemSection.PORTAL_DASHBOARD) {
                    viewModel.setSection(SystemSection.PORTAL_DASHBOARD)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        DwrTopBar(
                            language = state.language,
                            userSession = state.userSession,
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onSelectRole = { viewModel.setUserRole(it) }
                        )
                    },
                    bottomBar = {
                        DwrBottomNav(
                            currentSection = state.currentSection,
                            language = state.language,
                            onSelectSection = { viewModel.setSection(it) }
                        )
                    }
                ) { innerPadding ->
                    when (state.currentSection) {
                        SystemSection.PORTAL_DASHBOARD -> {
                            PortalDashboardScreen(
                                state = state,
                                onNavigateSection = { viewModel.setSection(it) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        SystemSection.WATER_QUALITY -> {
                            WaterQualityScreen(
                                state = state,
                                onSelectStation = { viewModel.selectStation(it) },
                                onAddReading = { id, reading -> viewModel.addStationReading(id, reading) },
                                onSetProvinceFilter = { viewModel.setProvinceFilter(it) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        SystemSection.WATER_USAGE -> {
                            WaterUsageScreen(
                                state = state,
                                onSelectProject = { viewModel.selectProject(it) },
                                onAddProject = { viewModel.addUsageProject(it) },
                                onDeleteProject = { viewModel.deleteUsageProject(it) },
                                onSetCategoryFilter = { viewModel.setCategoryFilter(it) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        SystemSection.WASTEWATER -> {
                            WastewaterScreen(
                                state = state,
                                onSelectFacility = { viewModel.selectFacility(it) },
                                onAddFacility = { viewModel.addWastewaterFacility(it) },
                                onDeleteFacility = { viewModel.deleteWastewaterFacility(it) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        SystemSection.ANALYTICS_REPORTS -> {
                            AnalyticsReportScreen(
                                state = state,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        SystemSection.SYSTEM_SETTINGS -> {
                            SettingsScreen(
                                state = state,
                                onSelectRole = { viewModel.setUserRole(it) },
                                onSelectLanguage = { viewModel.setLanguage(it) },
                                onDeleteAuditLogs = { viewModel.deleteAuditLogs(it) },
                                onClearAllAuditLogs = { viewModel.clearAllAuditLogs() },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
