package com.mindflow.app.data.repository

import com.mindflow.app.data.local.dao.MoodEntryDao
import com.mindflow.app.data.local.entity.MoodCategory
import com.mindflow.app.data.local.entity.MoodEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository for managing mood entries.
 * Single source of truth for mood data.
 */
class MoodRepository(
    private val moodEntryDao: MoodEntryDao
) {
    // Observe all entries
    val allEntries: Flow<List<MoodEntryEntity>> = moodEntryDao.observeAll()
    
    // Observe total count
    val totalEntriesCount: Flow<Int> = moodEntryDao.observeTotalCount()
    
    /**
     * Save or update a mood entry for a specific date.
     * If an entry already exists for that date, it will be updated.
     */
    suspend fun saveEntry(
        journalText: String,
        emoji: String,
        sentiment: String,
        score: Int,
        insight: String,
        date: LocalDate = LocalDate.now()
    ): Long {
        val moodCategory = MoodCategory.fromScoreAndSentiment(score, sentiment)
        
        // Check if entry exists for this date
        val existingEntry = moodEntryDao.getByDate(date)
        
        return if (existingEntry != null) {
            // Update existing entry
            val updatedEntry = existingEntry.copy(
                journalText = journalText,
                emoji = emoji,
                sentiment = sentiment,
                score = score,
                insight = insight,
                moodCategory = moodCategory,
                updatedAt = java.time.LocalDateTime.now()
            )
            moodEntryDao.update(updatedEntry)
            existingEntry.id
        } else {
            // Create new entry
            val newEntry = MoodEntryEntity(
                journalText = journalText,
                emoji = emoji,
                sentiment = sentiment,
                score = score,
                insight = insight,
                moodCategory = moodCategory,
                date = date
            )
            moodEntryDao.insert(newEntry)
        }
    }
    
    /**
     * Get entry for a specific date
     */
    suspend fun getEntryForDate(date: LocalDate): MoodEntryEntity? {
        return moodEntryDao.getByDate(date)
    }
    
    /**
     * Observe entry for a specific date
     */
    fun observeEntryForDate(date: LocalDate): Flow<MoodEntryEntity?> {
        return moodEntryDao.observeByDate(date)
    }
    
    /**
     * Get entries for a date range (useful for calendar view)
     */
    fun getEntriesForMonth(year: Int, month: Int): Flow<List<MoodEntryEntity>> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.plusMonths(1).minusDays(1)
        return moodEntryDao.observeByDateRange(startDate, endDate)
    }
    
    /**
     * Get entries for the last N days
     */
    fun getRecentEntries(days: Int): Flow<List<MoodEntryEntity>> {
        val since = LocalDate.now().minusDays(days.toLong())
        return moodEntryDao.observeEntriesSince(since)
    }
    
    /**
     * Check if there's an entry for today
     */
    suspend fun hasTodayEntry(): Boolean {
        return moodEntryDao.hasEntryForDate(LocalDate.now())
    }
    
    /**
     * Get today's entry
     */
    suspend fun getTodayEntry(): MoodEntryEntity? {
        return moodEntryDao.getByDate(LocalDate.now())
    }
    
    /**
     * Observe today's entry
     */
    fun observeTodayEntry(): Flow<MoodEntryEntity?> {
        return moodEntryDao.observeByDate(LocalDate.now())
    }
    
    /**
     * Calculate current streak
     */
    suspend fun calculateStreak(): Int {
        val today = LocalDate.now()
        var streak = 0
        var currentDate = today
        
        // Simple streak calculation: count consecutive days backwards from today
        while (moodEntryDao.hasEntryForDate(currentDate)) {
            streak++
            currentDate = currentDate.minusDays(1)
        }
        
        return streak
    }
    
    /**
     * Get average mood score for last N days
     */
    suspend fun getAverageScore(days: Int): Double? {
        val startDate = LocalDate.now().minusDays(days.toLong())
        val endDate = LocalDate.now()
        return moodEntryDao.getAverageScoreForRange(startDate, endDate)
    }
    
    /**
     * Observe average score since a date
     */
    fun observeAverageScore(since: LocalDate): Flow<Double?> {
        return moodEntryDao.observeAverageScore(since)
    }
    
    /**
     * Delete an entry
     */
    suspend fun deleteEntry(entry: MoodEntryEntity) {
        moodEntryDao.delete(entry)
    }
}
