package com.example.selfmanagement.core

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface AppClock {
    fun now(): Instant
    fun nowMillis(): Long
    fun nowEpochSeconds(): Long
    fun today(): String // yyyy-MM-dd
    
    companion object Default : AppClock {
        override fun now(): Instant = Clock.System.now()
        override fun nowMillis(): Long = now().toEpochMilliseconds()
        override fun nowEpochSeconds(): Long = now().epochSeconds
        override fun today(): String {
            val localDateTime = now().toLocalDateTime(TimeZone.currentSystemDefault())
            val year = localDateTime.year
            val month = localDateTime.monthNumber.toString().padStart(2, '0')
            val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
            return "$year-$month-$day"
        }
    }
}
