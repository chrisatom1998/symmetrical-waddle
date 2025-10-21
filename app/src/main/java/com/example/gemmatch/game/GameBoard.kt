package com.example.gemmatch.game

import com.example.gemmatch.model.Gem
import com.example.gemmatch.model.GemType
import com.example.gemmatch.model.Position

/**
 * Manages the game board state and logic for the match-3 game
 */
class GameBoard(val size: Int = 8) {
    private val grid: Array<Array<Gem?>> = Array(size) { arrayOfNulls(size) }
    var score: Int = 0
        private set
    var moves: Int = 0
        private set

    init {
        initializeBoard()
        // Remove any initial matches
        while (hasMatches()) {
            removeMatches()
            applyGravity()
            fillEmptySpaces()
        }
    }

    /**
     * Initialize the board with random gems
     */
    private fun initializeBoard() {
        for (row in 0 until size) {
            for (col in 0 until size) {
                grid[row][col] = Gem(GemType.random(), row, col)
            }
        }
    }

    /**
     * Get the gem at a specific position
     */
    fun getGem(row: Int, col: Int): Gem? {
        if (row !in 0 until size || col !in 0 until size) return null
        return grid[row][col]
    }

    fun getGem(position: Position): Gem? = getGem(position.row, position.col)

    /**
     * Swap two gems on the board
     */
    fun swapGems(pos1: Position, pos2: Position): Boolean {
        if (!pos1.isValid(size) || !pos2.isValid(size)) return false
        if (!pos1.isAdjacent(pos2)) return false

        val gem1 = grid[pos1.row][pos1.col] ?: return false
        val gem2 = grid[pos2.row][pos2.col] ?: return false

        // Perform swap
        grid[pos1.row][pos1.col] = gem2
        grid[pos2.row][pos2.col] = gem1

        gem1.row = pos2.row
        gem1.col = pos2.col
        gem2.row = pos1.row
        gem2.col = pos1.col

        // Check if this swap creates any matches
        if (hasMatches()) {
            moves++
            return true
        } else {
            // Swap back if no matches
            grid[pos2.row][pos2.col] = gem2
            grid[pos1.row][pos1.col] = gem1
            gem1.row = pos1.row
            gem1.col = pos1.col
            gem2.row = pos2.row
            gem2.col = pos2.col
            return false
        }
    }

    /**
     * Process matches: mark them, update score, and handle cascading
     */
    fun processMatches(): Boolean {
        if (!hasMatches()) return false

        val matchedGems = findAllMatches()
        score += matchedGems.size * 10
        removeMatches()
        applyGravity()
        fillEmptySpaces()

        return true
    }

    /**
     * Check if there are any matches on the board
     */
    fun hasMatches(): Boolean {
        return findAllMatches().isNotEmpty()
    }

    /**
     * Find all matched gems on the board (public version for animations)
     */
    fun findAllMatchesPublic(): Set<Position> = findAllMatches()

    /**
     * Find all matched gems on the board
     */
    private fun findAllMatches(): Set<Position> {
        val matches = mutableSetOf<Position>()

        // Check horizontal matches
        for (row in 0 until size) {
            var col = 0
            while (col < size) {
                val gem = grid[row][col] ?: continue
                var matchCount = 1

                // Count consecutive gems of the same type
                while (col + matchCount < size) {
                    val nextGem = grid[row][col + matchCount] ?: break
                    if (nextGem.type != gem.type) break
                    matchCount++
                }

                if (matchCount >= 3) {
                    for (i in 0 until matchCount) {
                        matches.add(Position(row, col + i))
                    }
                }

                col += matchCount
            }
        }

        // Check vertical matches
        for (col in 0 until size) {
            var row = 0
            while (row < size) {
                val gem = grid[row][col] ?: continue
                var matchCount = 1

                // Count consecutive gems of the same type
                while (row + matchCount < size) {
                    val nextGem = grid[row + matchCount][col] ?: break
                    if (nextGem.type != gem.type) break
                    matchCount++
                }

                if (matchCount >= 3) {
                    for (i in 0 until matchCount) {
                        matches.add(Position(row + i, col))
                    }
                }

                row += matchCount
            }
        }

        return matches
    }

    /**
     * Remove matched gems from the board
     */
    private fun removeMatches() {
        val matches = findAllMatches()
        matches.forEach { pos ->
            grid[pos.row][pos.col] = null
        }
    }

    /**
     * Apply gravity to make gems fall down
     */
    private fun applyGravity() {
        for (col in 0 until size) {
            var emptyRow = size - 1
            for (row in size - 1 downTo 0) {
                grid[row][col]?.let { gem ->
                    if (row != emptyRow) {
                        grid[emptyRow][col] = gem
                        gem.row = emptyRow
                        gem.col = col
                        grid[row][col] = null
                    }
                    emptyRow--
                }
            }
        }
    }

    /**
     * Fill empty spaces with new random gems
     */
    private fun fillEmptySpaces() {
        for (row in 0 until size) {
            for (col in 0 until size) {
                if (grid[row][col] == null) {
                    grid[row][col] = Gem(GemType.random(), row, col)
                }
            }
        }
    }

    /**
     * Reset the game board
     */
    fun reset() {
        score = 0
        moves = 0
        initializeBoard()
        while (hasMatches()) {
            removeMatches()
            applyGravity()
            fillEmptySpaces()
        }
    }

    /**
     * Get all gems on the board (for rendering)
     */
    fun getAllGems(): List<Gem> {
        return grid.flatMap { row -> row.filterNotNull() }
    }
}
