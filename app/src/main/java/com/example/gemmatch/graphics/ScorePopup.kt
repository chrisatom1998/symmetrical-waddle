package com.example.gemmatch.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

/**
 * Displays score popups when gems are matched
 */
class ScorePopup {

    data class Popup(
        var x: Float,
        var y: Float,
        val score: Int,
        var life: Float,
        val maxLife: Float = 60f
    )

    private val popups = mutableListOf<Popup>()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 32f
    }
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 32f
        alpha = 100
    }

    /**
     * Create a score popup
     */
    fun add(x: Float, y: Float, score: Int) {
        popups.add(Popup(x, y, score, 60f))
    }

    /**
     * Update all popups
     */
    fun update() {
        val iterator = popups.iterator()
        while (iterator.hasNext()) {
            val popup = iterator.next()

            // Move upward
            popup.y -= 2f

            // Reduce life
            popup.life--

            // Remove dead popups
            if (popup.life <= 0) {
                iterator.remove()
            }
        }
    }

    /**
     * Draw all popups
     */
    fun draw(canvas: Canvas) {
        popups.forEach { popup ->
            val alpha = (popup.life / popup.maxLife * 255).toInt().coerceIn(0, 255)
            val scale = if (popup.life > 50) {
                // Pop in effect
                0.5f + (60 - popup.life) / 10f * 0.5f
            } else {
                1f
            }

            textPaint.alpha = alpha
            textPaint.textSize = 32f * scale
            shadowPaint.alpha = (alpha * 0.5f).toInt()
            shadowPaint.textSize = 32f * scale

            val text = "+${popup.score}"

            // Draw shadow
            canvas.drawText(text, popup.x + 2, popup.y + 2, shadowPaint)

            // Draw text with gradient effect
            textPaint.shader = android.graphics.LinearGradient(
                popup.x, popup.y - 20,
                popup.x, popup.y + 20,
                intArrayOf(
                    Color.argb(alpha, 255, 255, 100),
                    Color.argb(alpha, 255, 200, 50)
                ),
                null,
                android.graphics.Shader.TileMode.CLAMP
            )

            canvas.drawText(text, popup.x, popup.y, textPaint)
        }
    }

    /**
     * Check if there are active popups
     */
    fun hasPopups(): Boolean = popups.isNotEmpty()

    /**
     * Clear all popups
     */
    fun clear() {
        popups.clear()
    }
}
