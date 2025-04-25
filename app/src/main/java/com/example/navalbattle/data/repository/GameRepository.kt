package com.naval.battle.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.navalbattle.data.model.GameState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.util.UUID

class GameRepository(
    private val firestore: FirebaseFirestore

) {
    companion object {
        private const val TAG = "GameRepository"
    }

    /**
     * Saves the game summary to Firestore when the game ends.
     *
     * @param userId The authenticated user's ID
     * @param gameState The final game state
     * @param playerScore The player's score
     * @param aiScore The AI's score
     * @param winner The winner of the game
     * @param winnerScore The winner's score
     * @return Result<Unit> indicating success or failure
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveGame(
        userId: String,
        gameState: GameState,
        playerScore: Int,
        aiScore: Int,
        winner: String?,
        winnerScore: Int
    ): Result<Unit> {
        return try {
            val totalMoves = gameState.moves.size
            val gameData = hashMapOf(
                "winner" to (winner ?: "None"),
                "winnerScore" to winnerScore,
                "timestamp" to Instant.now().toString(),
                "moves" to gameState.moves,
                "totalMoves" to totalMoves,
                "playerScore" to playerScore,
                "aiScore" to aiScore
            )
            Log.d(TAG, "Saving game summary to Firestore: userId=$userId, gameData=$gameData")
            firestore.collection("games")
                .document(userId)
                .collection("matches")
                .document(UUID.randomUUID().toString())
                .set(gameData)
                .await()
            Log.d(TAG, "Game summary saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving game summary: ${e.message}", e)
            Result.failure(e)
        }
    }
}