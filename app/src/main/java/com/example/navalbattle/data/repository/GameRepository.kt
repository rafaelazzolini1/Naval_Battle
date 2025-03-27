package com.naval.battle.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.navalbattle.data.model.GameState
import kotlinx.coroutines.tasks.await

class GameRepository(private val db: FirebaseFirestore) {

    suspend fun saveGame(userId: String, gameState: GameState, playerScore: Int, aiScore: Int, winner: String?, winnerScore: Int): Result<Unit> {
        return try {
            val gameData = hashMapOf(
                "moves" to gameState.moves,
                "playerScore" to playerScore,
                "aiScore" to aiScore,
                "winner" to (winner ?: "None"),
                "winnerScore" to winnerScore,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("games").document(userId).set(gameData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}