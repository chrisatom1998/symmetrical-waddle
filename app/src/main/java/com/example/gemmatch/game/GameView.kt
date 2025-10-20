package com.example.gemmatch.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.gemmatch.model.Position
import kotlin.math.min

/**
 * Custom view for rendering and handling the match-3 game
 */
class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val gameBoard = GameBoard(8)
    private var cellSize = 0f
    private var boardOffsetX = 0f
    private var boardOffsetY = 0f

    private var selectedPosition: Position? = null

    // Paints for drawing
    private val gemPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#FF16213E")
        style = Paint.Style.FILL
    }
    private val gridPaint = Paint().apply {
        color = Color.parseColor("#FF0F1626")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val selectedPaint = Paint().apply {
        color = Color.parseColor("#80FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    // Callback for score and moves updates
    var onGameStateChanged: ((score: Int, moves: Int) -> Unit)? = null

    init {
        setOnTouchListener { _, event -> handleTouch(event) }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateDimensions()
    }

    private fun calculateDimensions() {
        val minDimension = min(width, height).toFloat()
        cellSize = minDimension / gameBoard.size
        boardOffsetX = (width - minDimension) / 2
        boardOffsetY = (height - minDimension) / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background
        canvas.drawRect(
            boardOffsetX,
            boardOffsetY,
            boardOffsetX + cellSize * gameBoard.size,
            boardOffsetY + cellSize * gameBoard.size,
            backgroundPaint
        )

        // Draw grid and gems
        for (row in 0 until gameBoard.size) {
            for (col in 0 until gameBoard.size) {
                val x = boardOffsetX + col * cellSize
                val y = boardOffsetY + row * cellSize

                // Draw cell background
                canvas.drawRect(x, y, x + cellSize, y + cellSize, gridPaint)

                // Draw gem
                gameBoard.getGem(row, col)?.let { gem ->
                    gemPaint.color = gem.getColor()
                    val centerX = x + cellSize / 2
                    val centerY = y + cellSize / 2
                    val radius = cellSize * 0.35f
                    canvas.drawCircle(centerX, centerY, radius, gemPaint)

                    // Draw selection highlight
                    if (selectedPosition?.row == row && selectedPosition?.col == col) {
                        canvas.drawCircle(centerX, centerY, radius + 5, selectedPaint)
                    }
                }
            }
        }
    }

    private fun handleTouch(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) return false

        val col = ((event.x - boardOffsetX) / cellSize).toInt()
        val row = ((event.y - boardOffsetY) / cellSize).toInt()

        if (row !in 0 until gameBoard.size || col !in 0 until gameBoard.size) {
            return false
        }

        val clickedPosition = Position(row, col)

        if (selectedPosition == null) {
            // First gem selection
            selectedPosition = clickedPosition
            invalidate()
        } else {
            // Second gem selection - attempt swap
            val firstPos = selectedPosition!!

            if (firstPos == clickedPosition) {
                // Clicked the same gem - deselect
                selectedPosition = null
                invalidate()
            } else if (firstPos.isAdjacent(clickedPosition)) {
                // Adjacent gem - attempt swap
                if (gameBoard.swapGems(firstPos, clickedPosition)) {
                    // Valid swap - process matches
                    processMatches()
                    selectedPosition = null
                } else {
                    // Invalid swap - switch selection to new gem
                    selectedPosition = clickedPosition
                }
                invalidate()
            } else {
                // Non-adjacent gem - switch selection
                selectedPosition = clickedPosition
                invalidate()
            }
        }

        return true
    }

    private fun processMatches() {
        // Keep processing matches until no more matches exist (cascading)
        while (gameBoard.processMatches()) {
            // Animation would go here in a more advanced version
        }
        updateGameState()
        invalidate()
    }

    private fun updateGameState() {
        onGameStateChanged?.invoke(gameBoard.score, gameBoard.moves)
    }

    /**
     * Reset the game
     */
    fun resetGame() {
        gameBoard.reset()
        selectedPosition = null
        updateGameState()
        invalidate()
    }

    /**
     * Get current score
     */
    fun getScore(): Int = gameBoard.score

    /**
     * Get current moves count
     */
    fun getMoves(): Int = gameBoard.moves
}
