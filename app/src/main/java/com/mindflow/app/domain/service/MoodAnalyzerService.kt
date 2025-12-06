package com.mindflow.app.domain.service

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.mindflow.app.data.local.entity.MoodCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Result from mood analysis
 */
data class MoodAnalysisResult(
    val emoji: String,
    val sentiment: String,
    val score: Int,
    val insight: String,
    val moodCategory: MoodCategory
)

/**
 * Service for analyzing mood from journal text using Gemini AI.
 * Falls back to local keyword analysis if AI is unavailable.
 */
class MoodAnalyzerService(
    private val apiKey: String? = null
) {
    
    private val generativeModel: GenerativeModel? = apiKey?.let {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = it,
            generationConfig = generationConfig {
                temperature = 0.7f
                maxOutputTokens = 500
            }
        )
    }
    
    /**
     * Analyze the journal text and return mood analysis.
     * Uses Gemini AI if available, otherwise falls back to local analysis.
     */
    suspend fun analyze(text: String): MoodAnalysisResult {
        // Try AI analysis first
        if (generativeModel != null && apiKey?.isNotBlank() == true) {
            try {
                return analyzeWithGemini(text)
            } catch (e: Exception) {
                // Fall back to local analysis on error
                e.printStackTrace()
            }
        }
        
        // Fallback to local keyword-based analysis
        return analyzeLocally(text)
    }
    
    /**
     * Analyze using Gemini AI
     */
    private suspend fun analyzeWithGemini(text: String): MoodAnalysisResult = withContext(Dispatchers.IO) {
        val prompt = """
            Analyze the following journal entry and provide a mood analysis.
            
            Journal Entry:
            "$text"
            
            Respond ONLY with a valid JSON object in this exact format (no markdown, no extra text):
            {
                "emoji": "single emoji representing the mood",
                "sentiment": "one or two word mood description (e.g., Happy, Anxious, Stressed, Sad, Neutral, Excited, Calm)",
                "score": number from 0-100 where 0 is very negative and 100 is very positive,
                "insight": "a brief, empathetic insight about the entry in 1-2 sentences, addressing the person directly"
            }
        """.trimIndent()
        
        val response = generativeModel!!.generateContent(prompt)
        val responseText = response.text?.trim() ?: throw Exception("Empty response")
        
        // Clean the response (remove markdown if present)
        val jsonText = responseText
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        
        val json = JSONObject(jsonText)
        
        val emoji = json.optString("emoji", "😐")
        val sentiment = json.optString("sentiment", "Neutral")
        val score = json.optInt("score", 50).coerceIn(0, 100)
        val insight = json.optString("insight", "Thank you for sharing your thoughts.")
        
        val moodCategory = MoodCategory.fromScoreAndSentiment(score, sentiment)
        
        MoodAnalysisResult(
            emoji = emoji,
            sentiment = sentiment,
            score = score,
            insight = insight,
            moodCategory = moodCategory
        )
    }
    
    /**
     * Local keyword-based analysis (fallback)
     */
    private fun analyzeLocally(text: String): MoodAnalysisResult {
        val lowerText = text.lowercase()
        val words = lowerText.split(Regex("\\W+"))
        
        // Count keyword matches
        val positiveCount = words.count { it in positiveKeywords }
        val negativeCount = words.count { it in negativeKeywords }
        val stressCount = words.count { it in stressKeywords }
        
        val score: Int
        val sentiment: String
        val emoji: String
        val insight: String
        
        when {
            stressCount > 0 && stressCount >= positiveCount -> {
                score = maxOf(15, 50 - (stressCount * 10) + (positiveCount * 5))
                sentiment = "Anxious"
                emoji = "😰"
                insight = generateStressInsight(lowerText)
            }
            negativeCount > positiveCount && negativeCount > stressCount -> {
                score = maxOf(20, 45 - (negativeCount * 8) + (positiveCount * 5))
                sentiment = "Sad"
                emoji = "😢"
                insight = generateSadInsight(lowerText)
            }
            positiveCount > negativeCount && positiveCount > stressCount -> {
                score = minOf(100, 70 + (positiveCount * 5) - (negativeCount * 3))
                sentiment = when {
                    score >= 85 -> "Excellent"
                    score >= 70 -> "Good"
                    else -> "Positive"
                }
                emoji = when {
                    score >= 85 -> "😄"
                    score >= 70 -> "😊"
                    else -> "🙂"
                }
                insight = generatePositiveInsight(score)
            }
            else -> {
                score = 50 + (positiveCount * 3) - (negativeCount * 3) - (stressCount * 2)
                sentiment = "Neutral"
                emoji = "😐"
                insight = generateNeutralInsight(text)
            }
        }
        
        val moodCategory = MoodCategory.fromScoreAndSentiment(score.coerceIn(0, 100), sentiment)
        
        return MoodAnalysisResult(
            emoji = emoji,
            sentiment = sentiment,
            score = score.coerceIn(0, 100),
            insight = insight,
            moodCategory = moodCategory
        )
    }
    
    private fun generateStressInsight(text: String): String {
        return when {
            text.contains("exam") || text.contains("test") ->
                "Academic pressure can be overwhelming. Remember to take breaks and breathe."
            text.contains("work") || text.contains("job") ->
                "Work stress is challenging. Consider setting boundaries and taking short breaks."
            text.contains("deadline") ->
                "Time pressure affects us all. Try breaking tasks into smaller, manageable pieces."
            else ->
                "You're experiencing some stress. Consider taking a moment to relax and practice deep breathing."
        }
    }
    
    private fun generateSadInsight(text: String): String {
        return when {
            text.contains("lonely") || text.contains("alone") ->
                "Feelings of loneliness can be difficult. Consider reaching out to someone you trust."
            text.contains("miss") ->
                "It's okay to miss people or things. Allow yourself to feel these emotions."
            else ->
                "You're going through a difficult time. Remember that it's okay to not be okay."
        }
    }
    
    private fun generatePositiveInsight(score: Int): String {
        return when {
            score >= 85 ->
                "You're feeling great today! Keep nurturing the things that bring you joy."
            else ->
                "You're in a good place today. Keep up the positive momentum!"
        }
    }
    
    private fun generateNeutralInsight(text: String): String {
        return when {
            text.length < 50 ->
                "Consider writing more about your day to get a deeper analysis."
            else ->
                "You're having a balanced day. Small positive actions can boost your mood."
        }
    }
    
    companion object {
        private val positiveKeywords = setOf(
            "happy", "great", "amazing", "wonderful", "excited", "love", "joy",
            "fantastic", "awesome", "good", "grateful", "blessed", "peaceful",
            "relaxed", "content", "proud", "accomplished", "energetic", "motivated"
        )
        
        private val negativeKeywords = setOf(
            "sad", "depressed", "unhappy", "miserable", "down", "blue", "lonely",
            "hopeless", "empty", "grief", "crying", "tears"
        )
        
        private val stressKeywords = setOf(
            "stress", "stressed", "anxious", "anxiety", "worried", "nervous",
            "overwhelmed", "pressure", "panic", "tense", "exhausted", "tired",
            "deadline", "exam", "work", "busy", "rush"
        )
    }
}
