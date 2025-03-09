package com.hhoangphuoc.speakingcashflow.data

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val type: TransactionType
) {
    companion object {
        val DEFAULT_CATEGORIES = listOf(
            // Income categories
            Category("salary", "Salary", "money", "#4CAF50", TransactionType.INCOME),
            Category("freelance", "Freelance", "work", "#8BC34A", TransactionType.INCOME),
            Category("investment", "Investment", "trending_up", "#00BCD4", TransactionType.INCOME),
            Category("gift", "Gift", "card_giftcard", "#9C27B0", TransactionType.INCOME),
            Category("other_income", "Other Income", "add", "#2196F3", TransactionType.INCOME),

            // Expense categories
            Category("food", "Food & Dining", "restaurant", "#FF5722", TransactionType.EXPENSE),
            Category("groceries", "Groceries", "shopping_cart", "#FF9800", TransactionType.EXPENSE),
            Category("transport", "Transport", "directions_car", "#795548", TransactionType.EXPENSE),
            Category("shopping", "Shopping", "shopping_bag", "#E91E63", TransactionType.EXPENSE),
            Category("entertainment", "Entertainment", "movie", "#673AB7", TransactionType.EXPENSE),
            Category("health", "Health", "medical_services", "#F44336", TransactionType.EXPENSE),
            Category("travel", "Travel", "flight", "#3F51B5", TransactionType.EXPENSE),
            Category("education", "Education", "school", "#009688", TransactionType.EXPENSE),
            Category("bills", "Bills & Utilities", "receipt", "#607D8B", TransactionType.EXPENSE),
            Category("other_expense", "Other Expense", "more_horiz", "#9E9E9E", TransactionType.EXPENSE)
        )

        fun findById(id: String): Category {
            return DEFAULT_CATEGORIES.find { it.id == id }
                ?: DEFAULT_CATEGORIES.last { it.type == TransactionType.EXPENSE }
        }

        fun getByType(type: TransactionType): List<Category> {
            return DEFAULT_CATEGORIES.filter { it.type == type }
        }
    }
}
