package com.hhoangphuoc.speakingcashflow.data

import com.google.firebase.Timestamp
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val type: TransactionType,
    val categoryId: String,
    val date: Timestamp,
    val createdAt: Timestamp = Timestamp.now(),
    val note: String? = null
) {
    fun getCategory(): Category {
        return Category.findById(categoryId)
    }

    fun getFormattedAmount(): String {
        return String.format("%.2f €", amount)
    }
}

enum class TransactionType {
    INCOME,
    EXPENSE
}