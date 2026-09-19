package com.example.selfmanagement.ui.companion

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.selfmanagement.presentation.companion.CompanionViewModel
import com.example.selfmanagement.ui.components.AppButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionDetailScreen(
    viewModel: CompanionViewModel,
    onBack: () -> Unit,
    onStartTogether: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val profile = state.companionProfile

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("搭子详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(profile?.nickname ?: "未知", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("共同目标：${profile?.goalCategory?.displayName}", style = MaterialTheme.typography.bodyMedium)
                    Text("每日投入：${profile?.dailyMinutes}分钟", style = MaterialTheme.typography.bodyMedium)
                    Text("习惯时间：${profile?.preferredHour}:00", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    Text("共同坚持：3 天", color = MaterialTheme.colorScheme.secondary) // Mock 数据
                    Text("当前状态：${profile?.status?.displayName}", color = MaterialTheme.colorScheme.primary)
                }
            }
            
            Spacer(Modifier.weight(1f))
            AppButton(text = "一起开始", onClick = {
                // 实际逻辑中这里应该跳转，ViewModel 已经处理了 ID 查找
                // 这里我们暂且让外部处理
                onStartTogether("todo_first") 
            })
        }
    }
}
