package com.hhoangphuoc.speakingcashflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val voiceRecognitionService: VoiceRecognitionService,
    private val geminiService: GeminiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Initial)
    val uiState: StateFlow<MainUiState> = _uiState

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _calendarViewMode = MutableStateFlow(CalendarViewMode.MONTH)
    val calendarViewMode: StateFlow<CalendarViewMode> = _calendarViewMode

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

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
                    .first()

                _uiState.value = MainUiState.Processing

                geminiService.processVoiceInput(voiceInput)
                    .onSuccess { transaction ->
                        transactionRepository.addTransaction(transaction)
                            .onSuccess {
                                _uiState.value = MainUiState.Success
                                loadTransactions()
                            }
                            .onFailure { e ->
                                _uiState.value = MainUiState.Error(e.message ?: "Failed to save transaction")
                            }
                    }
                    .onFailure { e ->
                        _uiState.value = MainUiState.Error(e.message ?: "Failed to process voice input")
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
    data class Error(val message: String) : MainUiState()
}

enum class CalendarViewMode {
    MONTH, WEEK, DAY
}