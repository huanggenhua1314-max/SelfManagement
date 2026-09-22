package com.example.selfmanagement.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.selfmanagement.db.AppDatabase
import com.example.selfmanagement.db.StudySessionEntity
import com.example.selfmanagement.domain.model.StudySession
import com.example.selfmanagement.domain.model.StudySessionStatus
import com.example.selfmanagement.domain.repository.StudySessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightStudySessionRepository(
    database: AppDatabase
) : StudySessionRepository {

    private val queries = database.appDatabaseQueries

    override suspend fun startSession(session: StudySession) {
        withContext(Dispatchers.IO) {
            queries.insertSession(session.toEntity())
        }
    }

    override suspend fun updateSession(session: StudySession) {
        withContext(Dispatchers.IO) {
            queries.updateSession(session.toEntity())
        }
    }

    override suspend fun getActiveSession(taskId: String): StudySession? = withContext(Dispatchers.IO) {
        queries.getActiveSession(taskId).executeAsOneOrNull()?.toDomain()
    }

    override fun observeSession(taskId: String): Flow<StudySession?> {
        // SQLDelight doesn't support watching with parameters in the same way as ROOM sometimes.
        // But getActiveSession query in .sq has `taskId` parameter.
        return queries.getActiveSession(taskId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    private fun StudySessionEntity.toDomain(): StudySession {
        return StudySession(
            id = id,
            taskId = taskId,
            startTimeMillis = startTimeMillis,
            endTimeMillis = endTimeMillis,
            pausedDurationMillis = pausedDurationMillis,
            lastPauseTimestamp = lastPauseTimestamp,
            durationSeconds = durationSeconds,
            status = StudySessionStatus.valueOf(status)
        )
    }

    private fun StudySession.toEntity(): StudySessionEntity {
        return StudySessionEntity(
            id = id,
            taskId = taskId,
            startTimeMillis = startTimeMillis,
            endTimeMillis = endTimeMillis,
            pausedDurationMillis = pausedDurationMillis,
            lastPauseTimestamp = lastPauseTimestamp,
            durationSeconds = durationSeconds,
            status = status.name
        )
    }
}
