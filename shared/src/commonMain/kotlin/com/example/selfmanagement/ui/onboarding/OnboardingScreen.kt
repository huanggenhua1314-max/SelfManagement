package com.example.selfmanagement.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.selfmanagement.domain.model.GoalCategory
import com.example.selfmanagement.presentation.onboarding.CreateGoalViewModel
import com.example.selfmanagement.ui.components.AppButton

@Composable
fun OnboardingScreen(
    viewModel: CreateGoalViewModel,
    isEditMode: Boolean = false,
    onComplete: () -> Unit
) {
    var step by remember { mutableStateOf(if (isEditMode) 1 else 0) }
    val isSuccess by viewModel.isSuccess.collectAsState()

    LaunchedEffect(isSuccess) {
        if (isSuccess) onComplete()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (step) {
                0 -> WelcomeStep { step = 1 }
                1 -> CategoryStep(viewModel) { step = 2 }
                2 -> DescriptionStep(viewModel) { step = 3 }
                3 -> FinalStep(viewModel) { viewModel.submit() }
            }
        }
    }
}

@Composable
fun WelcomeStep(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(100.dp))
        Text("欢迎来到成长", style = MaterialTheme.typography.headlineLarge)
        Text("让我们把长期目标变成每日小行动", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.weight(1f))
        AppButton("开始设定目标", onClick = onNext)
    }
}

@Composable
fun CategoryStep(viewModel: CreateGoalViewModel, onNext: () -> Unit) {
    val selected by viewModel.category.collectAsState()
    Column {
        Text("选择你的成长方向", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))
        GoalCategory.entries.forEach { category ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected == category, onClick = { viewModel.updateCategory(category) })
                Text(category.displayName)
            }
        }
        Spacer(Modifier.weight(1f))
        AppButton("下一步", onClick = onNext)
    }
}

@Composable
fun DescriptionStep(viewModel: CreateGoalViewModel, onNext: () -> Unit) {
    val title by viewModel.title.collectAsState()
    Column {
        Text("你的具体目标是什么？", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { viewModel.updateTitle(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("例如：3个月提升英语交流能力") }
        )
        Spacer(Modifier.weight(1f))
        AppButton("下一步", onClick = onNext, enabled = title.isNotBlank())
    }
}

@Composable
fun FinalStep(viewModel: CreateGoalViewModel, onFinish: () -> Unit) {
    val targetDays by viewModel.targetDays.collectAsState()
    val dailyMinutes by viewModel.dailyMinutes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Column {
        Text("最后一步设置", style = MaterialTheme.typography.headlineSmall)
        
        Text("目标周期")
        Row {
            listOf(30, 60, 90).forEach { days ->
                FilterChip(
                    selected = targetDays == days,
                    onClick = { viewModel.updateTargetDays(days) },
                    label = { Text("${days}天") }
                )
                Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("每日投入时间")
        Row {
            listOf(10, 20, 30, 60).forEach { mins ->
                FilterChip(
                    selected = dailyMinutes == mins,
                    onClick = { viewModel.updateDailyMinutes(mins) },
                    label = { Text("${mins}分钟") }
                )
                Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(Modifier.weight(1f))
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            AppButton("开启成长之旅", onClick = onFinish)
        }
    }
}
