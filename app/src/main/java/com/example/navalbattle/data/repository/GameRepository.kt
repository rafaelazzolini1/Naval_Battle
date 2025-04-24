package com.naval.battle.data.repository

import android.util.Log
import com.example.navalbattle.data.model.GameState
import com.example.navalbattle.data.model.Move
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GameRepository(
    private val firestore: FirebaseFirestore,
    private val realtimeDatabase: FirebaseDatabase
) {
    companion object {
        private const val TAG = "GameRepository"
    }

    /**
     * Saves a game move to the Realtime Database under the user's game session.
     *
     * @param userId The authenticated user's ID
     * @param gameId The unique ID of the game session
     * @param move The move data to save
     * @param shipSunk The name of the ship sunk, if any
     * @param turnNumber The turn number of the move
     * @return Result<Unit> indicating success or failure
     */
    suspend fun saveMove(
        userId: String,
        gameId: String,
        move: Move,
        shipSunk: String?,
        turnNumber: Int
    ): Result<Unit> {
        return try {
            val moveData = hashMapOf(
                "row" to move.row,
                "col" to move.col,
                "result" to move.result.name,
                "isPlayerMove" to move.isPlayerMove,
                "shipSunk" to shipSunk,
                "timestamp" to System.currentTimeMillis(),
                "turnNumber" to turnNumber
            )
            Log.d(TAG, "Saving move to Realtime Database: userId=$userId, gameId=$gameId, move=$moveData")
            realtimeDatabase.getReference("games/$userId/$gameId/moves")
                .push()
                .setValue(moveData)
                .await()
            Log.d(TAG, "Move saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving move: ${e.message}", e)
            Result.failure(e)
        }
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
    suspend fun saveGame(
        userId: String,
        gameState: GameState,
        playerScore: Int,
        aiScore: Int,
        winner: String?,
        winnerScore: Int
    ): Result<Unit> {
        return try {
            val gameData = hashMapOf(
                "moves" to gameState.moves,
                "playerScore" to playerScore,
                "aiScore" to aiScore,
                "winner" to (winner ?: "None"),
                "winnerScore" to winnerScore,
                "timestamp" to System.currentTimeMillis()
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