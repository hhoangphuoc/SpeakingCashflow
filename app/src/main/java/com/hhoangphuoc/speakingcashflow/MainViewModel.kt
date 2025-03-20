package com.hhoangphuoc.speakingcashflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import com.hhoangphuoc.speakingcashflow.data.Transaction
import com.hhoangphuoc.speakingcashflow.data.TransactionType
import com.hhoangphuoc.speakingcashflow.repository.TransactionRepository
import com.hhoangphuoc.speakingcashflow.service.GeminiService
import com.hhoangphuoc.speakingcashflow.service.VoiceRecognitionService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val voiceRecognitionService: VoiceRecognitionService,
//    private val geminiService: GeminiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Initial)
    val uiState: StateFlow<MainUiState> = _uiState

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _calendarViewMode = MutableStateFlow(CalendarViewMode.MONTH)
    val calendarViewMode: StateFlow<CalendarViewMode> = _calendarViewMode

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _transactionError = MutableStateFlow<String?>(null)
    val transactionError: StateFlow<String?> = _transactionError

    init {
        loadTransactionsForCurrentMonth()
    }

    fun startVoiceRecognition() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Recording

            try {
                val voiceInput = voiceRecognitionService.startListening()
                    .catch { e ->
                        _uiState.value = MainUiState.Error(e.message ?: "Voice recognition failed")
                    }
                    .first() //OUTPUT OF VOICE RECOGNITION -  The voice input from the user

                _uiState.value = MainUiState.Processing

                /** This part calling GeminiService to process the voice input
                 * and convert it to a transaction object
                 */
//                geminiService.processVoiceInput(voiceInput)
//                    .onSuccess { transaction ->
//                        transactionRepository.addTransaction(transaction)
//                            .onSuccess {
//                                _uiState.value = MainUiState.Success
//                                loadTransactions()
//                            }
//                            .onFailure { e ->
//                                _uiState.value = MainUiState.Error(e.message ?: "Failed to save transaction")
//                            }
//                    }
//                    .onFailure { e ->
//                        _uiState.value = MainUiState.Error(e.message ?: "Failed to process voice input")
//                    }
                //TRY DEFAULT TRANSACTION FIRST
                transactionRepository.addTransaction(Transaction(
                    amount = 100.0,
                    type = TransactionType.INCOME,
                    categoryId = "salary",
                    date = Timestamp.now()
                    ))
                    .onSuccess {
                        //if transaction is added successfully, load all transactions
                        _uiState.value = MainUiState.Success
                        loadTransactions()
                    }
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun stopVoiceRecognition() {
        voiceRecognitionService.stopListening()
    }

    fun setCalendarViewMode(mode: CalendarViewMode) {
        _calendarViewMode.value = mode
        loadTransactions()
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadTransactions()
    }

    private fun loadTransactions() {
        when (calendarViewMode.value) {
            CalendarViewMode.MONTH -> loadTransactionsForCurrentMonth()
            CalendarViewMode.WEEK -> loadTransactionsForCurrentWeek()
            CalendarViewMode.DAY -> loadTransactionsForSelectedDay()
        }
    }

    private fun deleteTransaction(transaction: Transaction) {
        //TODO: Implement delete transaction
//        viewModelScope.launch {
//            transactionRepository.deleteTransaction(transaction.id)
//                .onSuccess {
//                    loadTransactions()
//                }
//                .onFailure { e ->
//                    _transactionError.value = e.message ?: "Failed to delete transaction"
//                }
//        }
    }

    private fun loadTransactionsForCurrentMonth() {
        viewModelScope.launch {
            val year = selectedDate.value.year
            val month = selectedDate.value.monthValue - 1 // Calendar months are 0-based

            transactionRepository.getTransactionsByMonth(year, month)
                .collect { transactions ->
                    _transactions.value = transactions
                }
        }
    }

    private fun loadTransactionsForCurrentWeek() {
        viewModelScope.launch {
            val date = selectedDate.value
            val startOfWeek = date.minusDays(date.dayOfWeek.value.toLong() - 1)
            val endOfWeek = startOfWeek.plusDays(6)

            val startTimestamp = Timestamp(Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            val endTimestamp = Timestamp(Date.from(endOfWeek.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1)))

            transactionRepository.getTransactions(startTimestamp, endTimestamp)
                .collect { transactions ->
                    _transactions.value = transactions
                }
        }
    }

    private fun loadTransactionsForSelectedDay() {
        viewModelScope.launch {
            val date = selectedDate.value

            val startOfDay = Timestamp(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            val endOfDay = Timestamp(Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1)))

            transactionRepository.getTransactions(startOfDay, endOfDay)
                .collect { transactions ->
                    _transactions.value = transactions
                }
        }
    }

}

sealed class MainUiState {
    data object Initial : MainUiState()
    data object Recording : MainUiState()
    data object Processing : MainUiState()
    data object Success : MainUiState()
    data object TransactionAdded : MainUiState()
    data class Error(val message: String) : MainUiState()
}

enum class CalendarViewMode {
    MONTH, WEEK, DAY
}