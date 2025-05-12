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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import java.time.Instant
import java.util.UUID

class GameRepository(
    private val firestore: FirebaseFirestore,
    private val database: FirebaseDatabase
) {
    companion object {
        private const val TAG = "GameRepository"
    }

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

            Log.d(TAG, "🎯 Comparando winner=$winner com userId=$userId")

            // Envio do e-mail se o jogador for o vencedor
            if (winner === "Player") {
                val json = """
                    {
                      "to": "$userEmail",
                      "subject": "Parabéns! Você venceu o Naval Battle!",
                      "html": "<h2>Você venceu!</h2><p>Pontuação: $winnerScore</p><p>Total de movimentos: $totalMoves</p>"
                    }
                """.trimIndent()

                val client = OkHttpClient()
                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    json
                )

                val request = Request.Builder()
                    .url("https://naval-battle-xxsc.onrender.com/send-email")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e(TAG, "Erro ao enviar e-mail: ${e.message}", e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.d(TAG, "Resposta do envio de e-mail: ${response.code}")
                    }
                })
            }

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
