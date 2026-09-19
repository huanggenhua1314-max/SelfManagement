package com.example.selfmanagement.domain.repository

import com.example.selfmanagement.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(id: String): Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun updateNickname(id: String, nickname: String)
}
