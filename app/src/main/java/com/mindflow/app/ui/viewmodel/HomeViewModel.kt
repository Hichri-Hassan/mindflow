package com.mindflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindflow.app.data.local.entity.MoodEntryEntity
import com.mindflow.app.data.repository.MoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * UI State for Home Screen
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val todayEntry: MoodEntryEntity? = null,
    val hasTodayEntry: Boolean = false,
    val currentStreak: Int = 0,
    val totalEntries: Int = 0,
    val averageScore: Int? = null,
    val last7DaysCount: Int = 0,
    val formattedDate: String = "",
    val greeting: String = "Welcome back!"
)

/**
 * ViewModel for Home Screen
 */
class HomeViewModel(
    private val repository: MoodRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            // Format today's date
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.ENGLISH)
            val formattedDate = today.format(formatter)
            
            // Generate greeting based on time of day
            val greeting = getGreeting()
            
            _uiState.update { it.copy(formattedDate = formattedDate, greeting = greeting) }
            
            // Observe today's entry and total count
            repository.observeTodayEntry()
                .combine(repository.getRecentEntries(7)) { todayEntry, recentEntries ->
                    Pair(todayEntry, recentEntries)
                }
                .combine(repository.totalEntriesCount) { pair, totalCount ->
                    Triple(pair.first, pair.second, totalCount)
                }
                .collect { (todayEntry, recentEntries, totalCount) ->
                    val streak = repository.calculateStreak()
                    val avgScore = repository.getAverageScore(7)
                    
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            todayEntry = todayEntry,
                            hasTodayEntry = todayEntry != null,
                            currentStreak = streak,
                            totalEntries = totalCount,
                            averageScore = avgScore?.toInt(),
                            last7DaysCount = recentEntries.size
                        )
                    }
                }
        }
    }
    
    private fun getGreeting(): String {
        val hour = LocalTime.now().hour
        return when {
            hour < 12 -> "Good morning! ☀️"
            hour < 17 -> "Good afternoon! 🌤️"
            else -> "Good evening! 🌙"
        }
    }
    
    fun refresh() {
        loadHomeData()
    }
}
