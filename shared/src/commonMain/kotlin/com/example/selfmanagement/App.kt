package com.example.selfmanagement

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.selfmanagement.core.*
import com.example.selfmanagement.data.repository.*
import com.example.selfmanagement.db.DatabaseProvider
import com.example.selfmanagement.domain.usecase.*
import com.example.selfmanagement.presentation.focus.FocusViewModel
import com.example.selfmanagement.presentation.growth.GrowthViewModel
import com.example.selfmanagement.presentation.home.HomeViewModel
import com.example.selfmanagement.presentation.onboarding.CreateGoalViewModel
import com.example.selfmanagement.presentation.profile.ProfileViewModel
import com.example.selfmanagement.ui.focus.FocusScreen
import com.example.selfmanagement.ui.growth.GrowthScreen
import com.example.selfmanagement.ui.home.HomeScreen
import com.example.selfmanagement.ui.navigation.Screen
import com.example.selfmanagement.ui.navigation.bottomNavItems
import com.example.selfmanagement.ui.onboarding.OnboardingScreen
import com.example.selfmanagement.ui.profile.ProfileScreen
import com.example.selfmanagement.ui.theme.AppTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument

import com.example.selfmanagement.presentation.companion.CompanionViewModel
import com.example.selfmanagement.ui.companion.CompanionDetailScreen
import com.example.selfmanagement.ui.companion.CompanionScreen
import com.example.selfmanagement.ui.companion.MatchScreen
import com.example.selfmanagement.ui.companion.TogetherFocusScreen

// 简单的依赖持有者 (Phase 6.5 RC)
private object AppContainer {
    val database = DatabaseProvider.getDatabase()
    val clock = AppClock.Default
    val analytics: AnalyticsTracker = DebugAnalyticsTracker
    
    val goalRepository = SqlDelightGoalRepository(database)
    val taskRepository = SqlDelightTaskRepository(database)
    val sessionRepository = SqlDelightStudySessionRepository(database)
    val growthRepository = SqlDelightGrowthRepository(database)
    val userRepository = SqlDelightUserRepository(database)
    val settingsRepository = SqlDelightSettingsRepository(database)

    val companionRepository = MockCompanionRepository(clock)
    val matchCompanionUseCase = MatchCompanionUseCase()
    
    val calculateLevelUseCase = CalculateLevelUseCase()
    val calculateStreakUseCase = CalculateStreakUseCase()
    val calculateAchievementUseCase = CalculateAchievementUseCase(clock)
    val calculateFocusTimeUseCase = CalculateFocusTimeUseCase()
    
    val recordGrowthUseCase = RecordGrowthUseCase(growthRepository, calculateStreakUseCase, calculateAchievementUseCase, clock)
    
    val createGoalUseCase = CreateGoalUseCase(goalRepository, clock)
    val updateGoalUseCase = UpdateGoalUseCase(goalRepository)
    val getActiveGoalUseCase = GetActiveGoalUseCase(goalRepository)
    val generateTodayTasksUseCase = GenerateTodayTasksUseCase(taskRepository)
    val completeTaskUseCase = CompleteTaskUseCase(taskRepository)
    
    val startFocusUseCase = StartFocusUseCase(sessionRepository, clock)
    val pauseFocusUseCase = PauseFocusUseCase(sessionRepository, clock)
    val resumeFocusUseCase = ResumeFocusUseCase(sessionRepository, clock)
    val completeFocusUseCase = CompleteFocusUseCase(sessionRepository, taskRepository, calculateFocusTimeUseCase, recordGrowthUseCase, clock)
}

@Composable
fun App() {
    val themeMode by AppContainer.settingsRepository.observeThemeMode("local-user").collectAsState(initial = com.example.selfmanagement.domain.model.ThemeMode.SYSTEM)

    LaunchedEffect(Unit) {
        AppContainer.analytics.track(AnalyticsEvents.APP_OPEN)
    }

    AppTheme(themeMode = themeMode) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        val activeGoal by AppContainer.getActiveGoalUseCase("local-user").collectAsState(initial = null)
        
        LaunchedEffect(activeGoal) {
            if (activeGoal != null && currentRoute == Screen.Onboarding.route) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }

        Scaffold(
            bottomBar = {
                val hideBottomBar = currentRoute?.startsWith("focus") == true || 
                                   currentRoute?.startsWith("together_focus") == true ||
                                   currentRoute == Screen.Onboarding.route ||
                                   currentRoute == Screen.CompanionDetail.route // RC 细节优化
                
                if (!hideBottomBar) {
                    NavigationBar {
                        bottomNavItems.forEach { screen ->
                            NavigationBarItem(
                                icon = { Text(screen.title.take(1).uppercase()) },
                                label = { Text(screen.title) },
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            val companionViewModel = remember {
                CompanionViewModel(
                    "local-user",
                    AppContainer.companionRepository,
                    AppContainer.goalRepository,
                    AppContainer.taskRepository,
                    AppContainer.matchCompanionUseCase,
                    AppContainer.clock
                )
            }

            NavHost(
                navController = navController,
                startDestination = Screen.Onboarding.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Onboarding.route) {
                    LaunchedEffect(Unit) { AppContainer.analytics.track(AnalyticsEvents.ONBOARDING_START) }
                    val viewModel = remember { CreateGoalViewModel(AppContainer.createGoalUseCase, AppContainer.updateGoalUseCase) }
                    OnboardingScreen(viewModel) {
                        AppContainer.analytics.track(AnalyticsEvents.GOAL_CREATED)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = remember {
                            HomeViewModel(
                                AppContainer.getActiveGoalUseCase,
                                AppContainer.taskRepository,
                                AppContainer.sessionRepository,
                                AppContainer.generateTodayTasksUseCase,
                                AppContainer.completeTaskUseCase,
                                AppContainer.calculateFocusTimeUseCase,
                                AppContainer.clock
                            )
                        },
                        onNavigateToFocus = { taskId ->
                            AppContainer.analytics.track(AnalyticsEvents.TASK_STARTED)
                            navController.navigate(Screen.createFocusRoute(taskId))
                        },
                        onNavigateToEditGoal = { goal ->
                            navController.navigate("edit_goal")
                        }
                    )
                }
                composable("edit_goal") {
                    val viewModel = remember { CreateGoalViewModel(AppContainer.createGoalUseCase, AppContainer.updateGoalUseCase) }
                    LaunchedEffect(activeGoal) {
                        activeGoal?.let { viewModel.loadInitialGoal(it) }
                    }
                    OnboardingScreen(viewModel, isEditMode = true) {
                        AppContainer.analytics.track(AnalyticsEvents.GOAL_UPDATED)
                        navController.popBackStack()
                    }
                }
                composable(
                    route = Screen.Focus.route,
                    arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                    LaunchedEffect(taskId) { AppContainer.analytics.track(AnalyticsEvents.FOCUS_STARTED) }
                    val viewModel = remember(taskId) {
                        FocusViewModel(
                            taskId,
                            AppContainer.taskRepository,
                            AppContainer.sessionRepository,
                            AppContainer.startFocusUseCase,
                            AppContainer.pauseFocusUseCase,
                            AppContainer.resumeFocusUseCase,
                            AppContainer.completeFocusUseCase,
                            AppContainer.calculateFocusTimeUseCase,
                            AppContainer.clock
                        )
                    }
                    FocusScreen(viewModel, onBack = { 
                        if (viewModel.uiState.value.isFinished) {
                            AppContainer.analytics.track(AnalyticsEvents.FOCUS_COMPLETED)
                        } else {
                            AppContainer.analytics.track(AnalyticsEvents.FOCUS_ABANDONED)
                        }
                        navController.popBackStack() 
                    })
                }
                composable(
                    route = Screen.TogetherFocus.route,
                    arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                    LaunchedEffect(taskId) { 
                        AppContainer.analytics.track(AnalyticsEvents.TOGETHER_FOCUS_STARTED)
                    }
                    val focusViewModel = remember(taskId) {
                        FocusViewModel(
                            taskId,
                            AppContainer.taskRepository,
                            AppContainer.sessionRepository,
                            AppContainer.startFocusUseCase,
                            AppContainer.pauseFocusUseCase,
                            AppContainer.resumeFocusUseCase,
                            AppContainer.completeFocusUseCase,
                            AppContainer.calculateFocusTimeUseCase,
                            AppContainer.clock
                        )
                    }
                    TogetherFocusScreen(
                        focusViewModel = focusViewModel,
                        companionViewModel = companionViewModel,
                        onBack = { 
                            if (focusViewModel.uiState.value.isFinished) {
                                AppContainer.analytics.track(AnalyticsEvents.TOGETHER_FOCUS_COMPLETED)
                            }
                            navController.popBackStack() 
                        }
                    )
                }
                composable(Screen.CompanionItem.route) {
                    LaunchedEffect(Unit) { AppContainer.analytics.track(AnalyticsEvents.COMPANION_VIEWED) }
                    CompanionScreen(
                        viewModel = companionViewModel,
                        onNavigateToMatch = { navController.navigate(Screen.Match.route) },
                        onNavigateToDetail = { navController.navigate(Screen.CompanionDetail.route) },
                        onNavigateToGoal = { navController.navigate("edit_goal") }
                    )
                }
                composable(Screen.Match.route) {
                    MatchScreen(viewModel = companionViewModel, onBack = { 
                        if (companionViewModel.uiState.value.activeCompanion != null) {
                            AppContainer.analytics.track(AnalyticsEvents.COMPANION_MATCHED)
                        }
                        navController.popBackStack() 
                    })
                }
                composable(Screen.CompanionDetail.route) {
                    CompanionDetailScreen(
                        viewModel = companionViewModel,
                        onBack = { navController.popBackStack() },
                        onStartTogether = { taskId ->
                            navController.navigate(Screen.createTogetherFocusRoute(taskId))
                        }
                    )
                }
                composable(Screen.Growth.route) {
                    LaunchedEffect(Unit) { AppContainer.analytics.track(AnalyticsEvents.GROWTH_VIEWED) }
                    val viewModel = remember {
                        GrowthViewModel(
                            "local-user",
                            AppContainer.growthRepository,
                            AppContainer.calculateLevelUseCase,
                            AppContainer.calculateStreakUseCase,
                            AppContainer.clock
                        )
                    }
                    GrowthScreen(viewModel)
                }
                composable(Screen.Profile.route) { 
                    LaunchedEffect(Unit) { AppContainer.analytics.track(AnalyticsEvents.PROFILE_VIEWED) }
                    val viewModel = remember {
                        ProfileViewModel(
                            "local-user",
                            AppContainer.userRepository,
                            AppContainer.goalRepository,
                            AppContainer.growthRepository,
                            AppContainer.companionRepository,
                            AppContainer.settingsRepository,
                            AppContainer.calculateLevelUseCase,
                            AppContainer.calculateStreakUseCase,
                            AppContainer.clock
                        )
                    }
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToGrowth = { navController.navigate(Screen.Growth.route) },
                        onNavigateToGoal = { navController.navigate("edit_goal") },
                        onNavigateToCompanion = {
                            navController.navigate(Screen.CompanionItem.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}
