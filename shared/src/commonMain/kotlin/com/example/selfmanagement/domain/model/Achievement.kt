package com.example.selfmanagement.domain.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val unlockedAt: Long // 时间戳
)
