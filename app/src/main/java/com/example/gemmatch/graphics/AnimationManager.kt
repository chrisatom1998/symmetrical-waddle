package com.example.gemmatch.graphics

import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.example.gemmatch.model.Position

/**
 * Manages animations for gem movements, swaps, and effects
 */
class AnimationManager {

    data class GemAnimation(
        val row: Int,
        val col: Int,
        var offsetX: Float = 0f,
        var offsetY: Float = 0f,
        var scale: Float = 1f,
        var alpha: Float = 1f,
        var rotation: Float = 0f
    )

    private val gemAnimations = mutableMapOf<Position, GemAnimation>()
    private var pulsePhase = 0f

    var isAnimating = false
        private set

    /**
     * Update animation state
     */
    fun update() {
        pulsePhase += 0.1f
        if (pulsePhase > Math.PI * 2) {
            pulsePhase = 0f
        }
    }

    /**
     * Get pulse phase for pulsing effects
     */
    fun getPulsePhase(): Float = pulsePhase

    /**
     * Get animation for a gem at position
     */
    fun getAnimation(row: Int, col: Int): GemAnimation {
        val pos = Position(row, col)
        return gemAnimations.getOrPut(pos) { GemAnimation(row, col) }
    }

    /**
     * Animate gem swap
     */
    fun animateSwap(
        from: Position,
        to: Position,
        cellSize: Float,
        onComplete: () -> Unit
    ) {
        isAnimating = true

        val fromAnim = getAnimation(from.row, from.col)
        val toAnim = getAnimation(to.row, to.col)

        val deltaX = (to.col - from.col) * cellSize
        val deltaY = (to.row - from.row) * cellSize

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener { animator ->
                val progress = animator.animatedValue as Float
                fromAnim.offsetX = deltaX * progress
                fromAnim.offsetY = deltaY * progress
                toAnim.offsetX = -deltaX * progress
                toAnim.offsetY = -deltaY * progress
            }

            doOnEnd {
                fromAnim.offsetX = 0f
                fromAnim.offsetY = 0f
                toAnim.offsetX = 0f
                toAnim.offsetY = 0f
                isAnimating = false
                onComplete()
            }

            start()
        }
    }

    /**
     * Animate gem disappearance (when matched)
     */
    fun animateDisappear(positions: List<Position>, onComplete: () -> Unit) {
        if (positions.isEmpty()) {
            onComplete()
            return
        }

        isAnimating = true

        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener { animator ->
                val progress = animator.animatedValue as Float
                positions.forEach { pos ->
                    val anim = getAnimation(pos.row, pos.col)
                    anim.scale = progress
                    anim.alpha = progress
                    anim.rotation = (1f - progress) * 180f
                }
            }

            doOnEnd {
                positions.forEach { pos ->
                    val anim = getAnimation(pos.row, pos.col)
                    anim.scale = 1f
                    anim.alpha = 1f
                    anim.rotation = 0f
                }
                isAnimating = false
                onComplete()
            }

            start()
        }
    }

    /**
     * Animate gems falling down
     */
    fun animateFall(movements: Map<Position, Int>, cellSize: Float, onComplete: () -> Unit) {
        if (movements.isEmpty()) {
            onComplete()
            return
        }

        isAnimating = true

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 400
            interpolator = OvershootInterpolator()

            addUpdateListener { animator ->
                val progress = animator.animatedValue as Float
                movements.forEach { (pos, distance) ->
                    val anim = getAnimation(pos.row, pos.col)
                    anim.offsetY = -distance * cellSize * (1f - progress)
                }
            }

            doOnEnd {
                movements.forEach { (pos, _) ->
                    getAnimation(pos.row, pos.col).offsetY = 0f
                }
                isAnimating = false
                onComplete()
            }

            start()
        }
    }

    /**
     * Animate new gems appearing
     */
    fun animateAppear(positions: List<Position>, onComplete: () -> Unit) {
        if (positions.isEmpty()) {
            onComplete()
            return
        }

        isAnimating = true

        positions.forEach { pos ->
            val anim = getAnimation(pos.row, pos.col)
            anim.scale = 0f
            anim.alpha = 0f
        }

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            interpolator = OvershootInterpolator()

            addUpdateListener { animator ->
                val progress = animator.animatedValue as Float
                positions.forEach { pos ->
                    val anim = getAnimation(pos.row, pos.col)
                    anim.scale = progress
                    anim.alpha = progress
                }
            }

            doOnEnd {
                positions.forEach { pos ->
                    val anim = getAnimation(pos.row, pos.col)
                    anim.scale = 1f
                    anim.alpha = 1f
                }
                isAnimating = false
                onComplete()
            }

            start()
        }
    }

    /**
     * Clear all animations
     */
    fun clear() {
        gemAnimations.clear()
        isAnimating = false
    }
}

/**
 * Extension function for ValueAnimator doOnEnd
 */
private inline fun ValueAnimator.doOnEnd(crossinline action: () -> Unit) {
    addListener(object : android.animation.Animator.AnimatorListener {
        override fun onAnimationStart(animation: android.animation.Animator) {}
        override fun onAnimationEnd(animation: android.animation.Animator) { action() }
        override fun onAnimationCancel(animation: android.animation.Animator) {}
        override fun onAnimationRepeat(animation: android.animation.Animator) {}
    })
}
