package com.example.selfmanagement.ui.companion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.selfmanagement.domain.usecase.MatchResult
import com.example.selfmanagement.presentation.companion.CompanionViewModel

@Composable
fun MatchScreen(
    viewModel: CompanionViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("推荐搭子") })
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.matchResults.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("暂无匹配搭子，换个目标试试？")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.matchResults) { result ->
                    MatchResultItem(result) {
                        viewModel.becomeCompanion(result.profile.id)
                        onBack()
                    }
                }
            }
        }
    }
}

@Composable
fun MatchResultItem(result: MatchResult, onSelect: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = result.profile.nickname,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${result.score}% 匹配",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "目标：${result.profile.goalCategory.displayName} · 每日 ${result.profile.dailyMinutes} 分钟",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))
            
            // 匹配理由标签
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                result.reasons.forEach { reason ->
                    SuggestionChip(onClick = {}, label = { Text(reason) })
                }
            }
            
            Spacer(Modifier.height(16.dp))
            Button(onClick = onSelect, modifier = Modifier.fillMaxWidth()) {
                Text("成为搭子")
            }
        }
    }
}
