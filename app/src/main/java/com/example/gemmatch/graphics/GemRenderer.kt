package com.example.gemmatch.graphics

import android.graphics.*
import com.example.gemmatch.model.GemType

/**
 * Handles rendering of gems with enhanced visual effects
 */
class GemRenderer {

    private val gemPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(80, 0, 0, 0)
        maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
    }
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shinePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * Draw a gem with gradient, shadow, and highlight effects
     */
    fun drawGem(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        gemType: GemType,
        alpha: Float = 1f
    ) {
        // Draw shadow
        canvas.drawCircle(centerX + 3, centerY + 3, radius, shadowPaint)

        // Create radial gradient for gem body
        val gradient = RadialGradient(
            centerX - radius * 0.3f,
            centerY - radius * 0.3f,
            radius * 1.2f,
            intArrayOf(
                lightenColor(gemType.color, 0.4f),
                gemType.color,
                darkenColor(gemType.color, 0.3f)
            ),
            floatArrayOf(0f, 0.6f, 1f),
            Shader.TileMode.CLAMP
        )

        gemPaint.shader = gradient
        gemPaint.alpha = (alpha * 255).toInt()

        // Draw gem as hexagon for more gem-like appearance
        val path = createHexagonPath(centerX, centerY, radius)
        canvas.drawPath(path, gemPaint)

        // Draw inner shine (smaller hexagon)
        val shineRadius = radius * 0.7f
        shinePaint.color = Color.argb(
            (80 * alpha).toInt(),
            255, 255, 255
        )
        val shinePath = createHexagonPath(centerX - radius * 0.2f, centerY - radius * 0.2f, shineRadius)
        canvas.drawPath(shinePath, shinePaint)

        // Draw edge highlight
        highlightPaint.style = Paint.Style.STROKE
        highlightPaint.strokeWidth = 3f
        highlightPaint.color = Color.argb(
            (100 * alpha).toInt(),
            255, 255, 255
        )
        highlightPaint.shader = LinearGradient(
            centerX, centerY - radius,
            centerX, centerY + radius,
            Color.argb((150 * alpha).toInt(), 255, 255, 255),
            Color.argb(0, 255, 255, 255),
            Shader.TileMode.CLAMP
        )
        canvas.drawPath(path, highlightPaint)

        // Reset shader for future draws
        gemPaint.shader = null
        highlightPaint.shader = null
    }

    /**
     * Draw a selected gem with glowing effect
     */
    fun drawSelectedGem(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        gemType: GemType,
        pulsePhase: Float
    ) {
        // Pulsing glow effect
        val glowRadius = radius + 10 + kotlin.math.sin(pulsePhase) * 5
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(100, 255, 255, 255)
            maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawCircle(centerX, centerY, glowRadius, glowPaint)

        // Draw the gem normally
        drawGem(canvas, centerX, centerY, radius, gemType)

        // Draw selection ring
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 4f
            color = Color.WHITE
            shader = LinearGradient(
                centerX, centerY - radius - 15,
                centerX, centerY + radius + 15,
                intArrayOf(
                    Color.argb(200, 255, 255, 255),
                    Color.argb(100, 255, 255, 255),
                    Color.argb(200, 255, 255, 255)
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(centerX, centerY, radius + 12, ringPaint)
    }

    /**
     * Create hexagon path for gem shape
     */
    private fun createHexagonPath(centerX: Float, centerY: Float, radius: Float): Path {
        val path = Path()
        for (i in 0..5) {
            val angle = Math.toRadians((60 * i - 30).toDouble())
            val x = centerX + (radius * kotlin.math.cos(angle)).toFloat()
            val y = centerY + (radius * kotlin.math.sin(angle)).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        return path
    }

    /**
     * Lighten a color by a factor
     */
    private fun lightenColor(color: Int, factor: Float): Int {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        val nr = kotlin.math.min(255, (r + (255 - r) * factor).toInt())
        val ng = kotlin.math.min(255, (g + (255 - g) * factor).toInt())
        val nb = kotlin.math.min(255, (b + (255 - b) * factor).toInt())

        return Color.rgb(nr, ng, nb)
    }

    /**
     * Darken a color by a factor
     */
    private fun darkenColor(color: Int, factor: Float): Int {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        val nr = kotlin.math.max(0, (r * (1 - factor)).toInt())
        val ng = kotlin.math.max(0, (g * (1 - factor)).toInt())
        val nb = kotlin.math.max(0, (b * (1 - factor)).toInt())

        return Color.rgb(nr, ng, nb)
    }
}
