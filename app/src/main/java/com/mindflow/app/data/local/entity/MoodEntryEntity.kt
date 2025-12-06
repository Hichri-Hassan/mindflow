package com.mindflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entity representing a mood entry in the database.
 * Each entry contains the user's journal text and the AI analysis results.
 */
@Entity(tableName = "mood_entries")
data class MoodEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // User input
    val journalText: String,
    
    // AI Analysis results
    val emoji: String,
    val sentiment: String,
    val score: Int, // 0-100
    val insight: String,
    
    // Mood category for calendar coloring
    val moodCategory: MoodCategory,
    
    // Timestamps
    val date: LocalDate,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Mood categories for visual representation
 */
enum class MoodCategory(val colorHex: Long) {
    EXCELLENT(0xFFA5D6A7),  // Green - Score 80-100
    GOOD(0xFFC5E1A5),       // Light Green - Score 60-79
    NEUTRAL(0xFFFFF59D),    // Yellow - Score 40-59
    LOW(0xFFFFCC80),        // Orange - Score 20-39
    STRESSED(0xFFEF9A9A),   // Red - Score 0-19 or stressed sentiment
    SAD(0xFF90CAF9);        // Blue - Sad sentiment
    
    companion object {
        fun fromScoreAndSentiment(score: Int, sentiment: String): MoodCategory {
            return when {
                sentiment.lowercase().contains("sad") -> SAD
                sentiment.lowercase().contains("stress") || 
                sentiment.lowercase().contains("anxious") -> STRESSED
                score >= 80 -> EXCELLENT
                score >= 60 -> GOOD
                score >= 40 -> NEUTRAL
                score >= 20 -> LOW
                else -> STRESSED
            }
        }
    }
}
