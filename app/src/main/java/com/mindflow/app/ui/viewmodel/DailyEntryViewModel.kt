package com.mindflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindflow.app.config.AppConfig
import com.mindflow.app.data.repository.MoodRepository
import com.mindflow.app.domain.service.MoodAnalysisResult
import com.mindflow.app.domain.service.MoodAnalyzerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for Daily Entry Screen
 */
data class DailyEntryUiState(
    val journalText: String = "",
    val isAnalyzing: Boolean = false,
    val isSaving: Boolean = false,
    val analysisResult: MoodAnalysisResult? = null,
    val isSaved: Boolean = false,
    val error: String? = null,
    val isAiEnabled: Boolean = AppConfig.isAiEnabled
)

/**
 * ViewModel for Daily Entry Screen
 */
class DailyEntryViewModel(
    private val repository: MoodRepository,
    private val analyzerService: MoodAnalyzerService = MoodAnalyzerService(
        apiKey = AppConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() }
    )
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DailyEntryUiState())
    val uiState: StateFlow<DailyEntryUiState> = _uiState.asStateFlow()
    
    init {
        loadExistingEntry()
    }
    
    private fun loadExistingEntry() {
        viewModelScope.launch {
            val existingEntry = repository.getTodayEntry()
            if (existingEntry != null) {
                _uiState.update { state ->
                    state.copy(
                        journalText = existingEntry.journalText,
                        analysisResult = MoodAnalysisResult(
                            emoji = existingEntry.emoji,
                            sentiment = existingEntry.sentiment,
                            score = existingEntry.score,
                            insight = existingEntry.insight,
                            moodCategory = existingEntry.moodCategory
                        )
                    )
                }
            }
        }
    }
    
    fun updateJournalText(text: String) {
        _uiState.update { it.copy(journalText = text, error = null) }
    }
    
    fun analyzeEntry() {
        val text = _uiState.value.journalText.trim()
        if (text.isBlank()) {
            _uiState.update { it.copy(error = "Please write something first") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true, error = null) }
            
            try {
                // Call AI service (Gemini if available, otherwise offline)
                val result = analyzerService.analyze(text)
                _uiState.update { state ->
                    state.copy(
                        isAnalyzing = false,
                        analysisResult = result
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isAnalyzing = false,
                        error = "Analysis failed: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun saveEntry(): Boolean {
        val analysis = _uiState.value.analysisResult ?: return false
        val text = _uiState.value.journalText.trim()
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            try {
                repository.saveEntry(
                    journalText = text,
                    emoji = analysis.emoji,
                    sentiment = analysis.sentiment,
                    score = analysis.score,
                    insight = analysis.insight
                )
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isSaving = false,
                        error = "Failed to save: ${e.message}"
                    )
                }
            }
        }
        return true
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun reset() {
        _uiState.value = DailyEntryUiState()
    }
}
