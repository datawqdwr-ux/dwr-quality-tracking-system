package com.aistudio.waterresources.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.waterresources.data.model.Language
import com.aistudio.waterresources.data.model.UserRole
import com.aistudio.waterresources.data.model.UserSession
import com.aistudio.waterresources.ui.theme.*

@Composable
fun DwrTopBar(
    language: Language,
    userSession: UserSession,
    onToggleLanguage: () -> Unit,
    onSelectRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    Surface(
        color = Slate900,
        tonalElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand & Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(listOf(Rose500, Indigo600))
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.aistudio.waterresources.R.drawable.ministry_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                    )
                }

                Column {
                    Text(
                        text = if (language == Language.LO) "ກະຊວງກະສິກໍາ ແລະ ສິ່ງແວດລ້ອມ" else "Ministry of Agriculture & Env",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (language == Language.LO) "ກົມຊັບພະຍາກອນນໍ້າ" else "Dept of Water Resources",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate100,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Controls: Language Toggle & User Session Role
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language Switcher Button
                FilledTonalButton(
                    onClick = onToggleLanguage,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Slate800,
                        contentColor = Slate100
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("lang_toggle_button")
                ) {
                    Text(text = language.flag, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = language.label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // User Session Box with Dropdown Menu
                Box {
                    Surface(
                        color = Slate800,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showRoleMenu = true }
                            .testTag("user_role_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Emerald400)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = userSession.role.code.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate300
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false },
                        modifier = Modifier.background(Slate850)
                    ) {
                        UserRole.entries.forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (language == Language.LO) role.labelLao else role.labelEn,
                                        color = if (role == userSession.role) Rose400 else Slate100,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    onSelectRole(role)
                                    showRoleMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
