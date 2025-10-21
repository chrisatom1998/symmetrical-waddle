package com.example.gemmatch.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.random.Random

/**
 * Particle system for visual effects when gems are matched
 */
class ParticleSystem {

    private data class Particle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var life: Float,
        var maxLife: Float,
        var color: Int,
        var size: Float
    )

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * Create explosion effect at a position
     */
    fun explode(x: Float, y: Float, color: Int, count: Int = 20) {
        repeat(count) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2
            val speed = Random.nextFloat() * 5 + 2
            val vx = kotlin.math.cos(angle) * speed
            val vy = kotlin.math.sin(angle) * speed
            val life = Random.nextFloat() * 30 + 30

            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = vx,
                    vy = vy,
                    life = life,
                    maxLife = life,
                    color = color,
                    size = Random.nextFloat() * 4 + 2
                )
            )
        }
    }

    /**
     * Create sparkle effect
     */
    fun sparkle(x: Float, y: Float, radius: Float, color: Int, count: Int = 10) {
        repeat(count) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2
            val distance = Random.nextFloat() * radius
            val px = x + kotlin.math.cos(angle) * distance
            val py = y + kotlin.math.sin(angle) * distance
            val speed = Random.nextFloat() * 2 + 1
            val vx = kotlin.math.cos(angle) * speed
            val vy = kotlin.math.sin(angle) * speed
            val life = Random.nextFloat() * 20 + 20

            particles.add(
                Particle(
                    x = px,
                    y = py,
                    vx = vx,
                    vy = vy,
                    life = life,
                    maxLife = life,
                    color = Color.WHITE,
                    size = Random.nextFloat() * 3 + 1
                )
            )
        }
    }

    /**
     * Update all particles
     */
    fun update() {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()

            // Update position
            particle.x += particle.vx
            particle.y += particle.vy

            // Apply gravity
            particle.vy += 0.2f

            // Reduce life
            particle.life--

            // Remove dead particles
            if (particle.life <= 0) {
                iterator.remove()
            }
        }
    }

    /**
     * Draw all particles
     */
    fun draw(canvas: Canvas) {
        particles.forEach { particle ->
            val alpha = (particle.life / particle.maxLife * 255).toInt().coerceIn(0, 255)
            paint.color = Color.argb(
                alpha,
                Color.red(particle.color),
                Color.green(particle.color),
                Color.blue(particle.color)
            )

            // Draw particle as a circle
            canvas.drawCircle(particle.x, particle.y, particle.size, paint)

            // Draw glow for sparkle particles
            if (particle.color == Color.WHITE) {
                paint.alpha = alpha / 3
                canvas.drawCircle(particle.x, particle.y, particle.size * 2, paint)
            }
        }
    }

    /**
     * Check if there are active particles
     */
    fun hasParticles(): Boolean = particles.isNotEmpty()

    /**
     * Clear all particles
     */
    fun clear() {
        particles.clear()
    }
}
