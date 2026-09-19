package com.example.selfmanagement.ui.focus

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfmanagement.domain.model.StudySessionStatus
import com.example.selfmanagement.presentation.focus.FocusViewModel
import com.example.selfmanagement.ui.components.AppButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    viewModel: FocusViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onBack()
    }

    val handleBack = {
        if (state.session != null && !state.isFinished) {
            showExitDialog = true
        } else {
            onBack()
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("离开专注？") },
            text = { Text("当前专注还在进行中。离开后专注状态会保留，你可以在首页随时回来继续。") },
            confirmButton = {
                TextButton(onClick = onBack) { Text("确认离开") }
            },
            dismissButton = {
                Button(onClick = { showExitDialog = false }) { Text("继续专注") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("专注") },
                navigationIcon = {
                    IconButton(onClick = handleBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = state.task?.title ?: "正在加载...",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(Modifier.height(48.dp))
            
            // 倒计时显示
            Text(
                text = formatTime(state.remainingSeconds),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 80.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(Modifier.height(24.dp))
            
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth().height(12.dp),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            
            Spacer(Modifier.height(64.dp))
            
            FocusControls(
                status = state.session?.status,
                onStart = { viewModel.start() },
                onPause = { viewModel.pause() },
                onResume = { viewModel.resume() },
                onComplete = { viewModel.complete() }
            )
        }
    }
}

@Composable
fun FocusControls(
    status: StudySessionStatus?,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onComplete: () -> Unit
) {
    when (status) {
        null -> {
            AppButton(text = "开始专注", onClick = onStart)
        }
        StudySessionStatus.RUNNING -> {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onPause,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("暂停")
                }
                AppButton(text = "手动完成", onClick = onComplete, modifier = Modifier.weight(1f))
            }
        }
        StudySessionStatus.PAUSED -> {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppButton(text = "继续", onClick = onResume, modifier = Modifier.weight(1f))
                AppButton(text = "放弃", onClick = onComplete, modifier = Modifier.weight(1f)) // 暂时复用 complete 逻辑
            }
        }
        StudySessionStatus.COMPLETED -> {
            Text("专注已完成")
        }
        else -> {}
    }
}

private fun formatTime(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}
