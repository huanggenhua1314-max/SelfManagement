package com.example.selfmanagement.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

private var androidContext: Context? = null

fun initAndroidContext(context: Context) {
    androidContext = context
}

actual fun createDatabaseDriver(): SqlDriver {
    val context = androidContext ?: throw IllegalStateException("Context not initialized. Call initAndroidContext first.")
    return AndroidSqliteDriver(AppDatabase.Schema, context, "growth.db")
}

actual fun createInMemoryDatabaseDriver(): SqlDriver {
    val context = androidContext ?: throw IllegalStateException("Context not initialized. Call initAndroidContext first.")
    return AndroidSqliteDriver(AppDatabase.Schema, context, null)
}
