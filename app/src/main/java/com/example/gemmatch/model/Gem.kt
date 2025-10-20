package com.example.gemmatch.model

import android.graphics.Color

/**
 * Represents different types of gems in the game
 */
enum class GemType(val color: Int) {
    RED(Color.parseColor("#FFE74C3C")),
    BLUE(Color.parseColor("#FF3498DB")),
    GREEN(Color.parseColor("#FF2ECC71")),
    YELLOW(Color.parseColor("#FFF1C40F")),
    PURPLE(Color.parseColor("#FF9B59B6")),
    ORANGE(Color.parseColor("#FFE67E22"));

    companion object {
        fun random(): GemType {
            return values().random()
        }
    }
}

/**
 * Represents a gem at a specific position on the board
 */
data class Gem(
    val type: GemType,
    var row: Int,
    var col: Int,
    var isSelected: Boolean = false,
    var isMatched: Boolean = false
) {
    fun getColor(): Int = type.color
}

/**
 * Represents a position on the game board
 */
data class Position(val row: Int, val col: Int) {
    fun isValid(gridSize: Int): Boolean {
        return row in 0 until gridSize && col in 0 until gridSize
    }

    fun isAdjacent(other: Position): Boolean {
        val rowDiff = kotlin.math.abs(row - other.row)
        val colDiff = kotlin.math.abs(col - other.col)
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1)
    }
}
