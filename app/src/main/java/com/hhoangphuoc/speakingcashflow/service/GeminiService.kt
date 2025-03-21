package com.hhoangphuoc.speakingcashflow.service

import com.hhoangphuoc.speakingcashflow.data.Transaction
import com.hhoangphuoc.speakingcashflow.data.Category
import com.hhoangphuoc.speakingcashflow.data.TransactionType

// firebase and vertex ai
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

import java.util.Date
import javax.inject.Singleton
import javax.inject.Inject

import org.json.JSONObject

/**
 * A Singleton of a Gemini Service with Firebase Vertex AI
 * This contains the logic for processing voice input and generating transactions
 * Firebase Vertex AI will handle the text input and convert it to a transaction object
 */
@Singleton
class GeminiService @Inject constructor() {
    private val generativeModel = Firebase.vertexAI.generativeModel("gemini-1.5-flash")
    
    private val db = Firebase.firestore
    
    suspend fun processVoiceInput(input: String): Result<Transaction> = withContext(Dispatchers.IO) {
        try {
            val categoryList = Category.DEFAULT_CATEGORIES.joinToString("\n") {
                "- ${it.name} (${it.type.name.lowercase()})"
            }

            val prompt = """
                Extract transaction details from the following voice input. 
                Return a JSON object with the following fields:
                - amount: number (positive)
                - type: "INCOME" or "EXPENSE"
                - categoryId: string (one of the category IDs listed below)
                - date: string (in ISO format, use current date if not specified)
                - note: string (optional)
                
                Available categories:
                $categoryList
                
                The currency is EUR (€).
                
                Voice input: "$input"
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val responseText = response.text ?: throw IllegalStateException("Empty response from Gemini")

            val json = JSONObject(responseText)

            val transaction = Transaction(
                amount = json.getDouble("amount"),
                type = TransactionType.valueOf(json.getString("type")),
                categoryId = json.getString("categoryId"),
                date = Timestamp(Date(json.optString("date", Date().toString()))),
                note = if (json.has("note")) json.getString("note") else null
            )

            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveTransaction(transaction: Transaction): Result<String> = withContext(Dispatchers.IO) {
        try {
            val docRef = db.collection("transactions").document()
            val transactionMap = mapOf(
                "id" to docRef.id,
                "amount" to transaction.amount,
                "type" to transaction.type.name,
                "categoryId" to transaction.categoryId,
                "date" to transaction.date,
                "note" to transaction.note
            )
            
            docRef.set(transactionMap).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}