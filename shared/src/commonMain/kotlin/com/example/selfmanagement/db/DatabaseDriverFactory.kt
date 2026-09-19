package com.example.selfmanagement.db

import app.cash.sqldelight.db.SqlDriver

expect fun createDatabaseDriver(): SqlDriver
expect fun createInMemoryDatabaseDriver(): SqlDriver

object DatabaseProvider {
    private var database: AppDatabase? = null

    fun getDatabase(): AppDatabase {
        return database ?: AppDatabase(
            driver = createDatabaseDriver()
        ).also {
            database = it
        }
    }

    fun setDatabase(db: AppDatabase) {
        database = db
    }
}
