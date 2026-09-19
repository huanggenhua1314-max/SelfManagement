package com.example.selfmanagement.domain.usecase

class CalculateLevelUseCase {
    operator fun invoke(totalXp: Int): Int {
        return when {
            totalXp >= 2000 -> 6
            totalXp >= 1000 -> 5
            totalXp >= 500 -> 4
            totalXp >= 250 -> 3
            totalXp >= 100 -> 2
            else -> 1
        }
    }
    
    fun getXpThreshold(level: Int): Int {
        return when (level) {
            1 -> 0
            2 -> 100
            3 -> 250
            4 -> 500
            5 -> 1000
            6 -> 2000
            else -> 2000
        }
    }
}
