package com.mindflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mindflow.app.config.AppConfig
import com.mindflow.app.data.repository.MoodRepository
import com.mindflow.app.domain.service.MoodAnalyzerService

/**
 * Factory for creating ViewModels with repository and API dependencies
 */
class ViewModelFactory(
    private val repository: MoodRepository,
    private val geminiApiKey: String = AppConfig.GEMINI_API_KEY
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DailyEntryViewModel::class.java) -> {
                val analyzerService = MoodAnalyzerService(
                    apiKey = geminiApiKey.takeIf { it.isNotBlank() && it != "YOUR_API_KEY_HERE" }
                )
                DailyEntryViewModel(repository, analyzerService) as T
            }
            modelClass.isAssignableFrom(CalendarViewModel::class.java) -> {
                CalendarViewModel(repository) as T
            }
            modelClass.isAssignableFrom(InsightsViewModel::class.java) -> {
                InsightsViewModel(repository, geminiApiKey) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
