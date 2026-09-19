package com.example.selfmanagement.domain.model

data class GrowthRecord(
    val id: String,
    val userId: String,
    val date: String, // yyyy-MM-dd
    val studyMinutes: Int,
    val xp: Int,
    val sourceId: String // 用于防止重复结算，对应 StudySession.id
)
