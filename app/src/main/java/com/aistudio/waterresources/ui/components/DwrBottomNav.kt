package com.aistudio.waterresources.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aistudio.waterresources.data.model.Language
import com.aistudio.waterresources.data.model.SystemSection
import com.aistudio.waterresources.ui.theme.*

@Composable
fun DwrBottomNav(
    currentSection: SystemSection,
    language: Language,
    onSelectSection: (SystemSection) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = Slate900,
        contentColor = Slate300,
        windowInsets = WindowInsets.navigationBars,
        modifier = modifier.testTag("dwr_bottom_nav")
    ) {
        val items = listOf(
            SystemSection.PORTAL_DASHBOARD,
            SystemSection.WATER_QUALITY,
            SystemSection.WATER_USAGE,
            SystemSection.WASTEWATER,
            SystemSection.ANALYTICS_REPORTS
        )

        items.forEach { section ->
            val selected = currentSection == section
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectSection(section) },
                icon = {
                    Text(
                        text = section.icon,
                        fontSize = if (selected) 20.sp else 18.sp
                    )
                },
                label = {
                    Text(
                        text = if (language == Language.LO) section.titleLao else section.titleEn,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Slate100,
                    selectedTextColor = Rose400,
                    unselectedIconColor = Slate400,
                    unselectedTextColor = Slate400,
                    indicatorColor = Rose600.copy(alpha = 0.35f)
                ),
                modifier = Modifier.testTag("nav_item_${section.name.lowercase()}")
            )
        }
    }
}
