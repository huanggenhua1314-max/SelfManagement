package com.example.selfmanagement.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.selfmanagement.domain.model.DailyTask
import com.example.selfmanagement.domain.model.Goal
import com.example.selfmanagement.domain.model.TaskStatus
import com.example.selfmanagement.presentation.home.HomeViewModel
import com.example.selfmanagement.ui.components.AppButton

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToFocus: (String) -> Unit,
    onNavigateToEditGoal: (Goal) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.goal == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无活跃目标")
            }
        } else {
            HomeScreenContent(
                state = state,
                onTaskClick = { task ->
                    if (task.status == TaskStatus.TODO) {
                        onNavigateToFocus(task.id)
                    }
                },
                onNavigateToFocus = onNavigateToFocus,
                onEditGoal = { onNavigateToEditGoal(state.goal!!) }
            )
        }
    }
}

@Composable
fun HomeScreenContent(
    state: com.example.selfmanagement.presentation.home.HomeUiState,
    onTaskClick: (DailyTask) -> Unit,
    onNavigateToFocus: (String) -> Unit,
    onEditGoal: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("早上好 👋", style = MaterialTheme.typography.headlineMedium)
            state.goal?.let { GoalMiniCard(it, onEditGoal) }
        }

        state.ongoingSession?.let { session ->
            item {
                OngoingFocusCard(
                    taskTitle = state.ongoingTask?.title ?: "正在进行的专注",
                    onContinue = { onNavigateToFocus(session.taskId) }
                )
            }
        }

        item {
            ProgressSection(state.completedMinutes, state.targetMinutes, state.progress)
        }

        item {
            Text("今日任务", style = MaterialTheme.typography.titleLarge)
        }

        items(state.todayTasks) { task ->
            TaskItem(task, onTaskClick)
        }

        item {
            Spacer(Modifier.height(24.dp))
            val allCompleted = state.todayTasks.isNotEmpty() && state.todayTasks.all { it.status == TaskStatus.COMPLETED }
            if (allCompleted) {
                Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(56.dp), enabled = false) {
                    Text("今天已经完成 🎉")
                }
            } else {
                val nextTask = state.todayTasks.find { it.status == TaskStatus.TODO }
                AppButton(
                    text = "完成下一个任务",
                    onClick = { nextTask?.let { onTaskClick(it) } }
                )
            }
        }
    }
}

@Composable
fun OngoingFocusCard(taskTitle: String, onContinue: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("正在进行中的专注", style = MaterialTheme.typography.labelMedium)
                Text(taskTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onContinue) {
                Text("继续")
            }
        }
    }
}

@Composable
fun GoalMiniCard(goal: Goal, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))
            Row {
                Text(
                    text = "${goal.category.displayName} · ${goal.targetDays}天周期",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ProgressSection(completed: Int, target: Int, progress: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("今日进度", style = MaterialTheme.typography.bodyMedium)
            Text("$completed / $target 分钟", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Composable
fun TaskItem(task: DailyTask, onClick: (DailyTask) -> Unit) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    Card(
        onClick = { onClick(task) },
        enabled = !isCompleted,
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isCompleted) "✓" else "○",
                style = MaterialTheme.typography.titleMedium,
                color = if (isCompleted) Color.Green else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                )
                Text("${task.durationMinutes}分钟", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
