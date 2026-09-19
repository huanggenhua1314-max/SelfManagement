package com.example.selfmanagement.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.selfmanagement.db.AppDatabase
import com.example.selfmanagement.db.UserEntity
import com.example.selfmanagement.domain.model.User
import com.example.selfmanagement.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightUserRepository(
    database: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {
    private val queries = database.appDatabaseQueries

    override fun observeUser(id: String): Flow<User?> {
        return queries.getUser(id)
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { entity ->
                entity?.let { User(it.id, it.nickname, it.avatarUrl) }
            }
    }

    override suspend fun saveUser(user: User) = withContext(ioDispatcher) {
        queries.insertUser(UserEntity(user.id, user.nickname, user.avatarUrl))
    }

    override suspend fun updateNickname(id: String, nickname: String) = withContext(ioDispatcher) {
        queries.updateNickname(nickname, id)
    }
}
