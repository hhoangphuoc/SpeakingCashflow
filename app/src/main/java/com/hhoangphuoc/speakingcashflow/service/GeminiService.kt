package com.hhoangphuoc.speakingcashflow.service

import com.hhoangphuoc.speakingcashflow.data.Transaction
import com.hhoangphuoc.speakingcashflow.data.Category
import com.hhoangphuoc.speakingcashflow.data.TransactionType

//import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.Timestamp //firebase timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.util.Date
import javax.inject.Singleton
import javax.inject.Inject

import org.json.JSONObject //to convert response to JSON object
/**
 * A Singleton of a Gemini Service, when it connected with Gemini API
 * This contains the logic for processing voice input and generating transactions
 * Gemini will handle the text input and convert it to a transaction object
 */
@Singleton
class GeminiService @Inject constructor() {
    private val model = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

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
                ${categoryList}
                
                The currency is EUR (€).
                
                Voice input: "$input"
            """.trimIndent()

            val response = model.generateContent(prompt).text
            val json = JSONObject(response)

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
}