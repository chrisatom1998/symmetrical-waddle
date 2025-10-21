package com.example.gemmatch.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gemmatch.R
import com.example.gemmatch.game.GameView

/**
 * Main activity for the Gem Match game
 */
class MainActivity : AppCompatActivity() {

    private lateinit var gameView: GameView
    private lateinit var scoreText: TextView
    private lateinit var movesText: TextView
    private lateinit var newGameButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        gameView = findViewById(R.id.gameView)
        scoreText = findViewById(R.id.scoreText)
        movesText = findViewById(R.id.movesText)
        newGameButton = findViewById(R.id.newGameButton)

        // Set up game state listener
        gameView.onGameStateChanged = { score, moves ->
            updateUI(score, moves)
        }

        // Set up new game button
        newGameButton.setOnClickListener {
            gameView.resetGame()
        }

        // Initialize UI
        updateUI(0, 0)
    }

    private fun updateUI(score: Int, moves: Int) {
        scoreText.text = score.toString()
        movesText.text = moves.toString()
    }
}
