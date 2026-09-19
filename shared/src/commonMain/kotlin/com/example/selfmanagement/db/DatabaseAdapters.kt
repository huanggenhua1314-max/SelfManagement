package com.example.selfmanagement.db

import app.cash.sqldelight.ColumnAdapter

val booleanAdapter = object : ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long): Boolean = databaseValue != 0L
    override fun encode(value: Boolean): Long = if (value) 1L else 0L
}
