package com.mindflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindflow.app.config.AppConfig
import com.mindflow.app.data.local.entity.MoodEntryEntity
import com.mindflow.app.data.repository.MoodRepository
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Data class for mood trend (used in bar chart)
 */
data class MoodTrendItem(
    val dayLabel: String,
    val score: Int,
    val date: LocalDate
)

/**
 * Data class for word frequency in word cloud
 */
data class WordFrequency(
    val word: String,
    val count: Int,
    val size: Int,
    val colorHex: Long
)

/**
 * UI State for Insights Screen
 */
data class InsightsUiState(
    val isLoading: Boolean = true,
    val weeklyTrend: List<MoodTrendItem> = emptyList(),
    val averageScore: Int? = null,
    val totalEntries: Int = 0,
    val wordCloud: List<WordFrequency> = emptyList(),
    val aiSummary: String = "",
    val recommendations: List<Recommendation> = emptyList(),
    val isGeneratingAiSummary: Boolean = false
)

data class Recommendation(
    val emoji: String,
    val text: String,
    val bgColorHex: Long
)

/**
 * ViewModel for Insights Screen with Gemini AI integration
 */
class InsightsViewModel(
    private val repository: MoodRepository,
    private val apiKey: String = AppConfig.GEMINI_API_KEY
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()
    
    // Gemini AI model for generating insights
    private val generativeModel: GenerativeModel? = if (apiKey.isNotBlank() && apiKey != "YOUR_API_KEY_HERE") {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
    } else null
    
    init {
        loadInsights()
    }
    
    private fun loadInsights() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.getRecentEntries(7).collect { entries ->
                val weeklyTrend = generateWeeklyTrend(entries)
                val wordCloud = generateWordCloud(entries)
                val avgScore = entries.takeIf { it.isNotEmpty() }
                    ?.map { it.score }
                    ?.average()
                    ?.toInt()
                
                // Generate basic summary first, then try AI
                val basicSummary = generateBasicSummary(entries, avgScore)
                val recommendations = generateRecommendations(entries)
                
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        weeklyTrend = weeklyTrend,
                        averageScore = avgScore,
                        totalEntries = entries.size,
                        wordCloud = wordCloud,
                        aiSummary = basicSummary,
                        recommendations = recommendations
                    )
                }
                
                // Try to get AI-powered summary if API key is configured
                if (generativeModel != null && entries.isNotEmpty()) {
                    generateAiSummary(entries)
                }
            }
        }
    }
    
    /**
     * Generate AI-powered weekly summary using Gemini
     */
    private suspend fun generateAiSummary(entries: List<MoodEntryEntity>) {
        if (generativeModel == null) return
        
        _uiState.update { it.copy(isGeneratingAiSummary = true) }
        
        try {
            val entrySummary = entries.joinToString("\n") { entry ->
                "- ${entry.date}: Score ${entry.score}/100, Mood: ${entry.sentiment}, Journal: \"${entry.journalText.take(100)}...\""
            }
            
            val prompt = """
                You are a compassionate mental wellness coach analyzing a user's mood journal entries from the past week.
                
                Here are the entries:
                $entrySummary
                
                Please provide a brief, warm, and supportive summary (2-3 sentences) that:
                1. Acknowledges their emotional journey this week
                2. Identifies any patterns you notice
                3. Offers one piece of encouragement or gentle suggestion
                
                Keep the tone conversational and empathetic. Do not use bullet points or numbered lists.
                Respond in plain text only.
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            val aiSummary = response.text?.trim() ?: return
            
            _uiState.update { state ->
                state.copy(
                    aiSummary = aiSummary,
                    isGeneratingAiSummary = false
                )
            }
        } catch (e: Exception) {
            // Keep basic summary on error
            _uiState.update { it.copy(isGeneratingAiSummary = false) }
        }
    }
    
    private fun generateWeeklyTrend(entries: List<MoodEntryEntity>): List<MoodTrendItem> {
        val today = LocalDate.now()
        val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        
        return (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val entry = entries.find { it.date == date }
            val dayOfWeek = date.dayOfWeek.value - 1 // 0 = Monday
            
            MoodTrendItem(
                dayLabel = dayLabels[dayOfWeek],
                score = entry?.score ?: 0,
                date = date
            )
        }
    }
    
    private fun generateWordCloud(entries: List<MoodEntryEntity>): List<WordFrequency> {
        if (entries.isEmpty()) return emptyList()
        
        // Extract words from all journal entries
        val allText = entries.joinToString(" ") { it.journalText }
        val words = allText.lowercase()
            .split(Regex("\\W+"))
            .filter { it.length > 3 }
            .filterNot { it in stopWords }
        
        // Count word frequency
        val wordCounts = words.groupingBy { it }.eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(12)
        
        if (wordCounts.isEmpty()) return emptyList()
        
        val maxCount = wordCounts.maxOf { it.value }
        
        return wordCounts.mapIndexed { index, (word, count) ->
            val normalizedSize = (count.toFloat() / maxCount * 20 + 12).toInt()
            WordFrequency(
                word = word,
                count = count,
                size = normalizedSize.coerceIn(12, 32),
                colorHex = wordCloudColors[index % wordCloudColors.size]
            )
        }
    }
    
    private fun generateBasicSummary(entries: List<MoodEntryEntity>, avgScore: Int?): String {
        if (entries.isEmpty()) {
            return "Start tracking your mood to get personalized insights and patterns."
        }
        
        val scores = entries.map { it.score }
        val trend = if (scores.size >= 2) {
            val firstHalf = scores.take(scores.size / 2).average()
            val secondHalf = scores.drop(scores.size / 2).average()
            when {
                secondHalf > firstHalf + 5 -> "improving"
                secondHalf < firstHalf - 5 -> "declining"
                else -> "stable"
            }
        } else "not enough data"
        
        val sentiments = entries.map { it.sentiment.lowercase() }
        val dominantMood = sentiments.groupingBy { it }.eachCount()
            .maxByOrNull { it.value }?.key ?: "mixed"
        
        return when {
            avgScore != null && avgScore >= 70 && trend == "improving" ->
                "Great week! Your mood has been $trend with an average score of $avgScore. You've been predominantly feeling $dominantMood. Keep up the positive habits!"
            
            avgScore != null && avgScore >= 70 ->
                "Your mood has been mostly positive this week with an average score of $avgScore. Consistency is key to maintaining this well-being."
            
            avgScore != null && avgScore < 40 ->
                "This week has been challenging with an average mood score of $avgScore. Remember that difficult periods are temporary. Consider reaching out to someone you trust."
            
            trend == "declining" ->
                "Your mood has been $trend this week. Pay attention to what might be causing stress and try to incorporate more self-care activities."
            
            else ->
                "Your mood has been $trend this week with some variation. Focus on activities that boost your energy and well-being."
        }
    }
    
    private fun generateRecommendations(entries: List<MoodEntryEntity>): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()
        
        if (entries.isEmpty()) {
            recommendations.add(
                Recommendation(
                    emoji = "📝",
                    text = "Start journaling daily to track your mood patterns",
                    bgColorHex = 0xFFE8EAF6
                )
            )
            return recommendations
        }
        
        val avgScore = entries.map { it.score }.average()
        val hasStress = entries.any { it.sentiment.lowercase().contains("stress") || it.sentiment.lowercase().contains("anxious") }
        val hasSadness = entries.any { it.sentiment.lowercase().contains("sad") }
        
        if (hasStress) {
            recommendations.add(
                Recommendation(
                    emoji = "🧘",
                    text = "Try 5-minute meditation breaks to manage stress",
                    bgColorHex = 0xFFEDE7F6
                )
            )
        }
        
        if (avgScore < 50) {
            recommendations.add(
                Recommendation(
                    emoji = "🚶",
                    text = "A short walk can help improve your mood",
                    bgColorHex = 0xFFE8F5E9
                )
            )
        }
        
        if (hasSadness) {
            recommendations.add(
                Recommendation(
                    emoji = "👥",
                    text = "Consider connecting with friends or family",
                    bgColorHex = 0xFFFFF3E0
                )
            )
        }
        
        recommendations.add(
            Recommendation(
                emoji = "😴",
                text = "Maintain consistent sleep schedule for better mood stability",
                bgColorHex = 0xFFE3F2FD
            )
        )
        
        return recommendations.take(3)
    }
    
    fun refresh() {
        loadInsights()
    }
    
    companion object {
        private val stopWords = setOf(
            "the", "and", "is", "it", "to", "of", "in", "for", "on", "with",
            "was", "that", "have", "this", "but", "are", "not", "you", "all",
            "can", "had", "her", "she", "will", "one", "been", "has", "when",
            "who", "their", "said", "each", "which", "they", "were", "then",
            "very", "just", "about", "like", "really", "feel", "feeling",
            "today", "going", "think", "know", "want", "need", "time", "some"
        )
        
        private val wordCloudColors = listOf(
            0xFFEF5350, // Red
            0xFFAB47BC, // Purple
            0xFF5C6BC0, // Indigo
            0xFF42A5F5, // Blue
            0xFF26A69A, // Teal
            0xFF66BB6A, // Green
            0xFFFFA726, // Orange
            0xFFEC407A, // Pink
            0xFF7E57C2, // Deep Purple
            0xFF8D6E63, // Brown
            0xFF78909C, // Blue Grey
            0xFFD4E157  // Lime
        )
    }
}
