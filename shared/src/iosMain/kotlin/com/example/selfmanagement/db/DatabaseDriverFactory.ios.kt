package com.example.selfmanagement.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual fun createDatabaseDriver(): SqlDriver {
    return NativeSqliteDriver(AppDatabase.Schema, "growth.db")
}

actual fun createInMemoryDatabaseDriver(): SqlDriver {
    return NativeSqliteDriver(AppDatabase.Schema, "inmemory.db")
}
