package com.mindflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindflow.app.data.local.entity.MoodEntryEntity
import com.mindflow.app.data.repository.MoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * UI State for Calendar Screen
 */
data class CalendarUiState(
    val isLoading: Boolean = true,
    val currentMonth: YearMonth = YearMonth.now(),
    val formattedMonth: String = "",
    val entriesMap: Map<Int, MoodEntryEntity> = emptyMap(), // day of month -> entry
    val selectedDay: Int? = null,
    val selectedEntry: MoodEntryEntity? = null
)

/**
 * ViewModel for Mood Calendar Screen
 */
class CalendarViewModel(
    private val repository: MoodRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()
    
    init {
        loadCurrentMonth()
    }
    
    private fun loadCurrentMonth() {
        loadMonth(_uiState.value.currentMonth)
    }
    
    private fun loadMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
            val formattedMonth = yearMonth.format(formatter)
            
            repository.getEntriesForMonth(yearMonth.year, yearMonth.monthValue)
                .collect { entries ->
                    val entriesMap = entries.associateBy { it.date.dayOfMonth }
                    
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            currentMonth = yearMonth,
                            formattedMonth = formattedMonth,
                            entriesMap = entriesMap,
                            selectedEntry = state.selectedDay?.let { entriesMap[it] }
                        )
                    }
                }
        }
    }
    
    fun selectDay(day: Int?) {
        _uiState.update { state ->
            state.copy(
                selectedDay = if (state.selectedDay == day) null else day,
                selectedEntry = if (state.selectedDay == day) null else state.entriesMap[day]
            )
        }
    }
    
    fun goToPreviousMonth() {
        val previousMonth = _uiState.value.currentMonth.minusMonths(1)
        _uiState.update { it.copy(selectedDay = null, selectedEntry = null) }
        loadMonth(previousMonth)
    }
    
    fun goToNextMonth() {
        val nextMonth = _uiState.value.currentMonth.plusMonths(1)
        // Don't allow going beyond current month
        if (nextMonth <= YearMonth.now()) {
            _uiState.update { it.copy(selectedDay = null, selectedEntry = null) }
            loadMonth(nextMonth)
        }
    }
    
    fun goToCurrentMonth() {
        _uiState.update { it.copy(selectedDay = null, selectedEntry = null) }
        loadMonth(YearMonth.now())
    }
    
    fun getDaysInMonth(): Int = _uiState.value.currentMonth.lengthOfMonth()
    
    fun getFirstDayOfWeek(): Int {
        // Returns 0 for Sunday, 1 for Monday, etc.
        val firstDay = _uiState.value.currentMonth.atDay(1).dayOfWeek
        return (firstDay.value % 7) // Convert to Sunday = 0 format
    }
    
    fun canGoToNextMonth(): Boolean {
        return _uiState.value.currentMonth < YearMonth.now()
    }
}
