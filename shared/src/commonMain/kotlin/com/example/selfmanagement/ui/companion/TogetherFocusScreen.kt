package com.example.selfmanagement.ui.companion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.selfmanagement.domain.model.CompanionActivityStatus
import com.example.selfmanagement.presentation.companion.CompanionViewModel
import com.example.selfmanagement.presentation.focus.FocusViewModel
import com.example.selfmanagement.ui.focus.FocusScreen
import kotlinx.coroutines.delay

@Composable
fun TogetherFocusScreen(
    focusViewModel: FocusViewModel,
    companionViewModel: CompanionViewModel,
    onBack: () -> Unit
) {
    val companionState by companionViewModel.uiState.collectAsState()
    val focusState by focusViewModel.uiState.collectAsState()
    var showFeedback by remember { mutableStateOf(false) }

    // 监听自己完成的状态，触发反馈
    LaunchedEffect(focusState.isFinished) {
        if (focusState.isFinished) {
            showFeedback = true
            // 模拟搭子也在稍后完成
            delay(1000)
            companionViewModel.simulateTogetherComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 复用核心计时 Logic
        FocusScreen(viewModel = focusViewModel, onBack = onBack)
        
        // 覆盖：双方学习状态面板
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
                .padding(horizontal = 24.dp)
        ) {
            StatusPanel(
                myName = "你",
                myStatus = if (focusState.isFinished) CompanionActivityStatus.COMPLETED else CompanionActivityStatus.FOCUSING,
                companionName = companionState.companionProfile?.nickname ?: "搭子",
                companionStatus = companionState.companionProfile?.status ?: CompanionActivityStatus.IDLE
            )
        }

        // 完成反馈弹窗
        if (showFeedback) {
            CompletionFeedbackDialog(
                myStudyMinutes = (focusState.elapsedSeconds / 60).toInt(),
                companionStatus = companionState.companionProfile?.status?.displayName ?: "已完成",
                onDismiss = {
                    showFeedback = false
                    onBack()
                }
            )
        }
    }
}

@Composable
fun StatusPanel(
    myName: String,
    myStatus: CompanionActivityStatus,
    companionName: String,
    companionStatus: CompanionActivityStatus
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusRow(myName, myStatus)
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            StatusRow(companionName, companionStatus)
        }
    }
}

@Composable
fun StatusRow(name: String, status: CompanionActivityStatus) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val color = when (status) {
            CompanionActivityStatus.FOCUSING -> Color.Green
            CompanionActivityStatus.COMPLETED -> MaterialTheme.colorScheme.primary
            else -> Color.Gray
        }
        Box(Modifier.size(10.dp).background(color, CircleShape))
        Spacer(Modifier.width(12.dp))
        Text(name, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Text(status.displayName, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun CompletionFeedbackDialog(
    myStudyMinutes: Int,
    companionStatus: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🎉 今天一起完成了") },
        text = {
            Column {
                Text("你的学习：$myStudyMinutes 分钟")
                Text("搭子的状态：$companionStatus")
                Spacer(Modifier.height(16.dp))
                Text("共同进步的感觉真好！", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("太棒了")
            }
        }
    )
}
