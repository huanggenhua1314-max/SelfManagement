package com.example.selfmanagement.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Onboarding : Screen("onboarding", "欢迎")
    object Home : Screen("home", "今日")
    object Focus : Screen("focus/{taskId}", "专注")
    object TogetherFocus : Screen("together_focus/{taskId}", "一起专注")
    object CompanionItem : Screen("companion", "搭子")
    object Match : Screen("match", "找搭子")
    object CompanionDetail : Screen("companion_detail", "搭子详情")
    object Growth : Screen("growth", "成长")
    object Profile : Screen("profile", "我的")
    
    companion object {
        fun createFocusRoute(taskId: String) = "focus/$taskId"
        fun createTogetherFocusRoute(taskId: String) = "together_focus/$taskId"
    }
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.CompanionItem,
    Screen.Growth,
    Screen.Profile
)
