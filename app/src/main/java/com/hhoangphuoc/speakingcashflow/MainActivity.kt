package com.hhoangphuoc.speakingcashflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hhoangphuoc.speakingcashflow.ui.theme.SpeakingCashflowTheme
import kotlinx.coroutines.delay
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpeakingCashflowTheme {
                MainScreen()
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
            }
        }
    }
}

@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val transactionError by viewModel.transactionError.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val calendarViewMode by viewModel.calendarViewMode.collectAsState()

    var showSuccessMessage by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is MainUiState.TransactionAdded) {
            showSuccessMessage = true
            delay(3000)
            showSuccessMessage = false
        }
    }

    Scaffold(
        floatingActionButton = {
            RecordButton(
                isRecording = isRecording,
                onClick = {
                    if (isRecording) {
                        viewModel.stopVoiceRecognition()
                    } else {
                        viewModel.startVoiceRecognition()
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Calendar View
                CashflowCalendarView(
                    transactions = transactions,
                    selectedDate = selectedDate,
                    viewMode = calendarViewMode,
                    onDateSelected = { viewModel.setSelectedDate(it) },
                    onViewModeChanged = { viewModel.setCalendarViewMode(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status Indicator
                StatusIndicator(
                    isRecording = isRecording,
                    isProcessing = isProcessing,
                    showSuccessMessage = showSuccessMessage,
                    errorMessage = transactionError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Transactions List
                if (transactions.isEmpty()) {
                    EmptyTransactionsView(
                        selectedDate = selectedDate,
                        viewMode = calendarViewMode
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(transactions) { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                onDeleteClick = { viewModel.deleteTransaction(transaction) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTransactionsView(selectedDate: LocalDate, viewMode: CalendarViewMode) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (viewMode) {
                CalendarViewMode.MONTH -> "No transactions this month"
                CalendarViewMode.WEEK -> "No transactions this week"
                CalendarViewMode.DAY -> "No transactions on ${selectedDate.dayOfMonth} ${selectedDate.month.name.lowercase().capitalize()}"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    SpeakingCashflowTheme {
//        MainScreen()
////        Greeting("Android")
//    }
//}