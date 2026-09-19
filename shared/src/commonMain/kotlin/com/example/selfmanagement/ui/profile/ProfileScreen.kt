package com.example.selfmanagement.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.selfmanagement.domain.model.Goal
import com.example.selfmanagement.domain.model.ThemeMode
import com.example.selfmanagement.presentation.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToGrowth: () -> Unit,
    onNavigateToGoal: () -> Unit,
    onNavigateToCompanion: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showEditNickname by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("我的") }) }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    ProfileHeader(state.nickname, state.avatarUrl) { showEditNickname = true }
                }

                item {
                    GrowthSummaryCard(
                        level = state.level,
                        xp = state.totalXp,
                        streak = state.currentStreak,
                        minutes = state.totalStudyMinutes,
                        onClick = onNavigateToGrowth
                    )
                }

                item {
                    SectionTitle("当前目标")
                    ActiveGoalCard(state.activeGoal, onNavigateToGoal)
                }

                item {
                    SectionTitle("学习搭子")
                    CompanionSummaryCard(state.companionProfile?.nickname, onNavigateToCompanion)
                }

                item {
                    SectionTitle("学习设置")
                    StudySettings(
                        dailyMinutes = state.activeGoal?.dailyMinutes ?: 30,
                        preferredHour = state.activeGoal?.preferredHour ?: 20
                    )
                }

                item {
                    SectionTitle("系统设置")
                    AppSettings(
                        themeMode = state.themeMode,
                        onThemeChange = { viewModel.updateThemeMode(it) },
                        notificationsEnabled = state.notificationsEnabled,
                        onNotificationsToggle = { viewModel.updateNotificationsEnabled(it) }
                    )
                }

                item {
                    Spacer(Modifier.height(32.dp))
                    Text(
                        "成长 MVP V1.0",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    if (showEditNickname) {
        EditNicknameDialog(
            currentNickname = state.nickname,
            onDismiss = { showEditNickname = false },
            onConfirm = {
                viewModel.updateNickname(it)
                showEditNickname = false
            }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun ProfileHeader(nickname: String, avatarUrl: String?, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(64.dp).clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f).clickable { onEditClick() }) {
            Text(nickname, style = MaterialTheme.typography.headlineSmall)
            Text("点击修改昵称", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
    }
}

@Composable
fun GrowthSummaryCard(level: Int, xp: Int, streak: Int, minutes: Int, onClick: () -> Unit) {
    Card(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GrowthItem("Lv.$level", "等级")
            GrowthItem("$xp", "经验值")
            GrowthItem("🔥 $streak", "连续天")
            val hours = minutes / 60
            val mins = minutes % 60
            GrowthItem("${hours}h ${mins}m", "专注时长")
        }
    }
}

@Composable
fun GrowthItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ActiveGoalCard(goal: Goal?, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        if (goal != null) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(goal.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${goal.category.displayName} · ${goal.targetDays}天",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else {
            Text("暂无活跃目标", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun CompanionSummaryCard(nickname: String?, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Face, contentDescription = null)
            Spacer(Modifier.width(16.dp))
            Text(nickname ?: "还没有学习搭子", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
fun StudySettings(dailyMinutes: Int, preferredHour: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            SettingRow(Icons.Default.CheckCircle, "每日目标", "$dailyMinutes 分钟")
            HorizontalDivider()
            SettingRow(Icons.Default.DateRange, "习惯时间", "${preferredHour}:00")
        }
    }
}

@Composable
fun AppSettings(
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            var showThemeDialog by remember { mutableStateOf(false) }
            
            SettingRow(
                Icons.Default.Settings, 
                "主题模式", 
                when(themeMode) {
                    ThemeMode.SYSTEM -> "跟随系统"
                    ThemeMode.LIGHT -> "浅色"
                    ThemeMode.DARK -> "深色"
                },
                onClick = { showThemeDialog = true }
            )
            HorizontalDivider()
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null)
                Spacer(Modifier.width(16.dp))
                Text("学习提醒", modifier = Modifier.weight(1f))
                Switch(checked = notificationsEnabled, onCheckedChange = onNotificationsToggle)
            }
            
            if (showThemeDialog) {
                AlertDialog(
                    onDismissRequest = { showThemeDialog = false },
                    title = { Text("选择主题") },
                    text = {
                        Column {
                            ThemeOption("跟随系统", themeMode == ThemeMode.SYSTEM) { onThemeChange(ThemeMode.SYSTEM); showThemeDialog = false }
                            ThemeOption("浅色", themeMode == ThemeMode.LIGHT) { onThemeChange(ThemeMode.LIGHT); showThemeDialog = false }
                            ThemeOption("深色", themeMode == ThemeMode.DARK) { onThemeChange(ThemeMode.DARK); showThemeDialog = false }
                        }
                    },
                    confirmButton = {}
                )
            }
        }
    }
}

@Composable
fun ThemeOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun SettingRow(icon: ImageVector, title: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(enabled = onClick != null) { onClick?.invoke() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f))
        Text(value, color = MaterialTheme.colorScheme.outline)
        if (onClick != null) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun EditNicknameDialog(currentNickname: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var nickname by remember { mutableStateOf(currentNickname) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("修改昵称") },
        text = {
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("昵称") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { if (nickname.isNotBlank()) onConfirm(nickname) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
