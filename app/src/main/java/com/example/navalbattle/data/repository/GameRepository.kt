package com.naval.battle.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.navalbattle.data.model.GameState
import com.example.navalbattle.data.model.Move
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.util.UUID

class GameRepository(
    private val firestore: FirebaseFirestore,
    private val database: FirebaseDatabase
) {
    companion object {
        private const val TAG = "GameRepository"
    }

    /**
     * Saves a single move to the Realtime Database in real-time.
     *
     * @param userId The authenticated user's ID
     * @param matchId The unique ID of the match
     * @param move The move to save
     * @return Result<Unit> indicating success or failure
     */
    suspend fun saveMove(
        userId: String,
        matchId: String,
        move: Move
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Saving move to Realtime Database: userId=$userId, matchId=$matchId, move=$move")
            val moveId = database.getReference("games")
                .child(userId)
                .child("matches")
                .child(matchId)
                .child("moves")
                .push()
                .key ?: throw IllegalStateException("Failed to generate moveId")

            database.getReference("games")
                .child(userId)
                .child("matches")
                .child(matchId)
                .child("moves")
                .child(moveId)
                .setValue(move)
                .await()

            Log.d(TAG, "Move saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving move: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Saves the game summary to Firestore when the game ends, excluding moves.
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
    suspend fun saveGameSummary(
        userId: String,
        gameState: GameState,
        playerScore: Int,
        aiScore: Int,
        winner: String?,
        winnerScore: Int
    ): Result<Unit> {
        return try {
            val totalMoves = gameState.moves.size
            val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "unknown@example.com"
            val gameData = hashMapOf(
                "winner" to (winner ?: "None"),
                "winnerScore" to winnerScore,
                "timestamp" to Instant.now().toString(),
                "totalMoves" to totalMoves,
                "playerScore" to playerScore,
                "aiScore" to aiScore,
                "userEmail" to userEmail
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