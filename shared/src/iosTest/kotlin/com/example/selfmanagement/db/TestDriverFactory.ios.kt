package com.example.selfmanagement.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual fun createTestDriver(): SqlDriver {
    return NativeSqliteDriver(AppDatabase.Schema, "test.db")
}
