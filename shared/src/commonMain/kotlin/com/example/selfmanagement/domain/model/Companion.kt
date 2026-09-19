package com.example.selfmanagement.domain.model

data class Companion(
    val id: String,
    val userId: String,
    val companionUserId: String,
    val goalId: String,
    val createdAt: Long,
    val status: CompanionStatus
)
