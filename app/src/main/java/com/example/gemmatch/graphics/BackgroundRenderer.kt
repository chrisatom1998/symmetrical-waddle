package com.example.gemmatch.graphics

import android.graphics.*

/**
 * Renders enhanced background for the game
 */
class BackgroundRenderer {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val boardBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(30, 255, 255, 255)
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    /**
     * Draw the game background
     */
    fun drawBackground(canvas: Canvas, width: Int, height: Int) {
        // Create gradient background
        val gradient = LinearGradient(
            0f, 0f,
            0f, height.toFloat(),
            intArrayOf(
                Color.parseColor("#1A1A2E"),
                Color.parseColor("#16213E"),
                Color.parseColor("#0F1626")
            ),
            null,
            Shader.TileMode.CLAMP
        )

        backgroundPaint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // Draw subtle stars/sparkles
        drawStars(canvas, width, height)
    }

    /**
     * Draw the game board background
     */
    fun drawBoardBackground(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        val width = right - left
        val height = bottom - top

        // Board shadow
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(100, 0, 0, 0)
            maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawRoundRect(
            left + 5, top + 5, right + 5, bottom + 5,
            16f, 16f, shadowPaint
        )

        // Board gradient background
        val boardGradient = LinearGradient(
            left, top,
            left, bottom,
            intArrayOf(
                Color.parseColor("#253A5E"),
                Color.parseColor("#1A2F4F"),
                Color.parseColor("#16213E")
            ),
            null,
            Shader.TileMode.CLAMP
        )

        boardBackgroundPaint.shader = boardGradient
        canvas.drawRoundRect(left, top, right, bottom, 16f, 16f, boardBackgroundPaint)

        // Border highlight
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            shader = LinearGradient(
                left, top,
                left, bottom,
                Color.argb(100, 255, 255, 255),
                Color.argb(20, 255, 255, 255),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(left, top, right, bottom, 16f, 16f, borderPaint)
    }

    /**
     * Draw cell background
     */
    fun drawCell(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        // Cell gradient
        val cellGradient = RadialGradient(
            (left + right) / 2,
            (top + bottom) / 2,
            (right - left) / 2,
            intArrayOf(
                Color.parseColor("#2A4365"),
                Color.parseColor("#1E3450"),
                Color.parseColor("#16213E")
            ),
            floatArrayOf(0f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )

        cellPaint.shader = cellGradient
        val inset = 2f
        canvas.drawRoundRect(
            left + inset,
            top + inset,
            right - inset,
            bottom - inset,
            8f, 8f,
            cellPaint
        )

        // Cell border
        canvas.drawRoundRect(
            left + inset,
            top + inset,
            right - inset,
            bottom - inset,
            8f, 8f,
            gridLinePaint
        )
    }

    /**
     * Draw decorative stars in the background
     */
    private fun drawStars(canvas: Canvas, width: Int, height: Int) {
        val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(30, 255, 255, 255)
        }

        // Use deterministic positions based on dimensions
        val seed = (width + height).toLong()
        val random = kotlin.random.Random(seed)

        repeat(50) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val size = random.nextFloat() * 2 + 1

            starPaint.alpha = (random.nextFloat() * 100 + 30).toInt()
            canvas.drawCircle(x, y, size, starPaint)

            // Some stars get a subtle glow
            if (random.nextFloat() > 0.7f) {
                starPaint.alpha = 15
                canvas.drawCircle(x, y, size * 2, starPaint)
            }
        }
    }
}
