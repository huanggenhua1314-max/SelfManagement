package com.example.selfmanagement.domain.model

data class User(
    val id: String,
    val nickname: String,
    val avatarUrl: String? = null
)
