package com.example.selfmanagement.domain.model

enum class CompanionActivityStatus(val displayName: String) {
    IDLE("空闲"),
    READY("准备中"),
    FOCUSING("专注中"),
    COMPLETED("已完成")
}
