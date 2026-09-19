package com.example.selfmanagement.data.repository

import com.example.selfmanagement.domain.model.StudySession
import com.example.selfmanagement.domain.repository.StudySessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class InMemoryStudySessionRepository : StudySessionRepository {
    private val sessionsFlow = MutableStateFlow<Map<String, StudySession>>(emptyMap())

    override suspend fun startSession(session: StudySession) {
        sessionsFlow.value = sessionsFlow.value + (session.taskId to session)
    }

    override suspend fun updateSession(session: StudySession) {
        sessionsFlow.value = sessionsFlow.value + (session.taskId to session)
    }

    override suspend fun getActiveSession(taskId: String): StudySession? {
        return sessionsFlow.value[taskId]
    }

    override fun observeSession(taskId: String): Flow<StudySession?> {
        return sessionsFlow.map { it[taskId] }
    }
}
