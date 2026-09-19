package com.example.selfmanagement.domain.repository

import com.example.selfmanagement.domain.model.StudySession
import kotlinx.coroutines.flow.Flow

interface StudySessionRepository {
    suspend fun startSession(session: StudySession)
    suspend fun updateSession(session: StudySession)
    suspend fun getActiveSession(taskId: String): StudySession?
    fun observeSession(taskId: String): Flow<StudySession?>
}
