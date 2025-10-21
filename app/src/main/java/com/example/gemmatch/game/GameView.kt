package com.example.gemmatch.game

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.gemmatch.graphics.*
import com.example.gemmatch.model.Position
import kotlin.math.min

/**
 * Custom view for rendering and handling the match-3 game with enhanced graphics
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

    // Graphics components
    private val gemRenderer = GemRenderer()
    private val backgroundRenderer = BackgroundRenderer()
    private val animationManager = AnimationManager()
    private val particleSystem = ParticleSystem()
    private val scorePopup = ScorePopup()

    // Animation handler
    private val animationHandler = Handler(Looper.getMainLooper())
    private val animationRunnable = object : Runnable {
        override fun run() {
            if (animationManager.isAnimating || particleSystem.hasParticles() || scorePopup.hasPopups()) {
                animationManager.update()
                particleSystem.update()
                scorePopup.update()
                invalidate()
                animationHandler.postDelayed(this, 16) // ~60 FPS
            }
        }
    }

    // Callback for score and moves updates
    var onGameStateChanged: ((score: Int, moves: Int) -> Unit)? = null

    init {
        setOnTouchListener { _, event -> handleTouch(event) }
        setLayerType(LAYER_TYPE_SOFTWARE, null) // Enable software rendering for blur effects
        startAnimationLoop()
    }

    private fun startAnimationLoop() {
        animationHandler.post(animationRunnable)
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
        backgroundRenderer.drawBackground(canvas, width, height)

        // Draw board background
        backgroundRenderer.drawBoardBackground(
            canvas,
            boardOffsetX,
            boardOffsetY,
            boardOffsetX + cellSize * gameBoard.size,
            boardOffsetY + cellSize * gameBoard.size
        )

        // Draw cells and gems
        for (row in 0 until gameBoard.size) {
            for (col in 0 until gameBoard.size) {
                val x = boardOffsetX + col * cellSize
                val y = boardOffsetY + row * cellSize

                // Draw cell background
                backgroundRenderer.drawCell(canvas, x, y, x + cellSize, y + cellSize)

                // Draw gem
                gameBoard.getGem(row, col)?.let { gem ->
                    val anim = animationManager.getAnimation(row, col)

                    val centerX = x + cellSize / 2 + anim.offsetX
                    val centerY = y + cellSize / 2 + anim.offsetY
                    val radius = cellSize * 0.35f * anim.scale

                    canvas.save()
                    canvas.rotate(anim.rotation, centerX, centerY)

                    // Check if this gem is selected
                    val isSelected = selectedPosition?.row == row && selectedPosition?.col == col

                    if (isSelected && !animationManager.isAnimating) {
                        // Draw with selection effect
                        gemRenderer.drawSelectedGem(
                            canvas,
                            centerX,
                            centerY,
                            radius,
                            gem.type,
                            animationManager.getPulsePhase()
                        )
                    } else {
                        // Draw normal gem
                        gemRenderer.drawGem(
                            canvas,
                            centerX,
                            centerY,
                            radius,
                            gem.type,
                            anim.alpha
                        )
                    }

                    canvas.restore()
                }
            }
        }

        // Draw particles
        particleSystem.draw(canvas)

        // Draw score popups
        scorePopup.draw(canvas)
    }

    private fun handleTouch(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) return false
        if (animationManager.isAnimating) return false

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
                // Adjacent gem - attempt swap with animation
                attemptSwap(firstPos, clickedPosition)
            } else {
                // Non-adjacent gem - switch selection
                selectedPosition = clickedPosition
                invalidate()
            }
        }

        return true
    }

    private fun attemptSwap(from: Position, to: Position) {
        // Check if swap would create matches
        if (!gameBoard.swapGems(from, to)) {
            // Invalid swap - animate and swap back
            animationManager.animateSwap(from, to, cellSize) {
                invalidate()
            }
            selectedPosition = to
            return
        }

        // Valid swap - animate
        selectedPosition = null
        animationManager.animateSwap(from, to, cellSize) {
            processMatchesWithAnimation()
        }
    }

    private fun processMatchesWithAnimation() {
        if (!gameBoard.hasMatches()) {
            updateGameState()
            return
        }

        // Find matches before removing them
        val matches = gameBoard.findAllMatchesPublic()

        if (matches.isEmpty()) {
            updateGameState()
            return
        }

        // Create particle effects at match positions
        matches.forEach { pos ->
            gameBoard.getGem(pos)?.let { gem ->
                val x = boardOffsetX + pos.col * cellSize + cellSize / 2
                val y = boardOffsetY + pos.row * cellSize + cellSize / 2

                particleSystem.explode(x, y, gem.getColor(), 15)
                particleSystem.sparkle(x, y, cellSize * 0.4f, gem.getColor(), 8)
            }
        }

        // Calculate score for this match
        val matchScore = matches.size * 10
        val centerPos = matches.elementAt(matches.size / 2)
        val popupX = boardOffsetX + centerPos.col * cellSize + cellSize / 2
        val popupY = boardOffsetY + centerPos.row * cellSize + cellSize / 2
        scorePopup.add(popupX, popupY, matchScore)

        // Animate gems disappearing
        animationManager.animateDisappear(matches.toList()) {
            // Process matches in game logic
            gameBoard.processMatches()

            // Continue cascading
            invalidate()
            animationHandler.postDelayed({
                processMatchesWithAnimation()
            }, 100)
        }

        updateGameState()
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
        animationManager.clear()
        particleSystem.clear()
        scorePopup.clear()
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animationHandler.removeCallbacks(animationRunnable)
    }
}
