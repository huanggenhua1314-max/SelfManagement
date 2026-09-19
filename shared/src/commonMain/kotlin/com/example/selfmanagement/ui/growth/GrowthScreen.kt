package com.example.selfmanagement.ui.growth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfmanagement.presentation.growth.GrowthViewModel

@Composable
fun GrowthScreen(viewModel: GrowthViewModel) {
    val state by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("成长统计", style = MaterialTheme.typography.headlineMedium)
            }

            // Level Section
            item {
                LevelCard(state.level, state.xpInLevel, state.xpThreshold, state.totalXp)
            }

            // Streak Section
            item {
                StreakSection(state.currentStreak, state.bestStreak)
            }

            // Weekly Completion
            item {
                WeeklySection(state.weeklyCompletion)
            }

            // Achievements
            item {
                Text("我的成就", style = MaterialTheme.typography.titleLarge)
            }

            if (state.achievements.isEmpty()) {
                item { Text("继续努力，解锁第一个成就", style = MaterialTheme.typography.bodyMedium, color = Color.Gray) }
            } else {
                items(state.achievements) { achievement ->
                    AchievementItem(achievement)
                }
            }
        }
    }
}

@Composable
fun LevelCard(level: Int, xpInLevel: Int, threshold: Int, totalXp: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("Lv.$level", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("总经验值: $totalXp", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { if (threshold > 0) xpInLevel.toFloat() / threshold else 0f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Text(
                "距离下一级还需 ${threshold - xpInLevel} XP",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun StreakSection(current: Int, best: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("当前连续", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 24.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("$current", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
                }
                Text("天", style = MaterialTheme.typography.labelSmall)
            }
        }
        Card(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("历史最高", style = MaterialTheme.typography.labelMedium)
                Text("$best", style = MaterialTheme.typography.displaySmall)
                Text("天", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun WeeklySection(weekly: List<Boolean>) {
    Column {
        Text("本周动态", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            weekly.forEach { completed ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (completed) Color.Green else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (completed) Text("✓", color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: com.example.selfmanagement.domain.model.Achievement) {
    ListItem(
        headlineContent = { Text(achievement.title) },
        supportingContent = { Text(achievement.description) },
        leadingContent = { 
            Box(Modifier.size(40.dp).background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                Text("🏆")
            }
        }
    )
}
