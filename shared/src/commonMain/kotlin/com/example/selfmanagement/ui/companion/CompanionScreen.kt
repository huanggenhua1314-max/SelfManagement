package com.example.selfmanagement.ui.companion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.selfmanagement.presentation.companion.CompanionViewModel
import com.example.selfmanagement.ui.components.AppButton

@Composable
fun CompanionScreen(
    viewModel: CompanionViewModel,
    onNavigateToMatch: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onNavigateToGoal: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            !state.hasActiveGoal -> {
                NoGoalView(onNavigateToGoal)
            }
            state.activeCompanion == null -> {
                NoCompanionView(onNavigateToMatch)
            }
            else -> {
                ActiveCompanionView(state.companionProfile?.nickname, onNavigateToDetail)
            }
        }
    }
}

@Composable
fun NoGoalView(onNavigateToGoal: () -> Unit) {
    Column(
        modifier = Modifier.padding(24.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("还没有设置目标", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("先设置一个目标，再寻找适合你的学习搭子。", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(48.dp))
        AppButton(text = "去设置目标", onClick = onNavigateToGoal)
    }
}

@Composable
fun NoCompanionView(onNavigateToMatch: () -> Unit) {
    Column(
        modifier = Modifier.padding(24.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("一个人坚持很难。", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("找到一个和你目标相似的人，一起完成今天的目标。", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(48.dp))
        AppButton(text = "找学习搭子", onClick = onNavigateToMatch)
    }
}

@Composable
fun ActiveCompanionView(nickname: String?, onNavigateToDetail: () -> Unit) {
    Column(
        modifier = Modifier.padding(24.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("你已拥有学习搭子：$nickname", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(32.dp))
        AppButton(text = "进入搭子房间", onClick = onNavigateToDetail)
    }
}
