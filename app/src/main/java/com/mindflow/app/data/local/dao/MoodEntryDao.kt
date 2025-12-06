package com.mindflow.app.data.local.dao

import androidx.room.*
import com.mindflow.app.data.local.entity.MoodEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for mood entries
 */
@Dao
interface MoodEntryDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MoodEntryEntity): Long
    
    @Update
    suspend fun update(entry: MoodEntryEntity)
    
    @Delete
    suspend fun delete(entry: MoodEntryEntity)
    
    // Query operations
    @Query("SELECT * FROM mood_entries WHERE id = :id")
    suspend fun getById(id: Long): MoodEntryEntity?
    
    @Query("SELECT * FROM mood_entries WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: LocalDate): MoodEntryEntity?
    
    @Query("SELECT * FROM mood_entries WHERE date = :date LIMIT 1")
    fun observeByDate(date: LocalDate): Flow<MoodEntryEntity?>
    
    @Query("SELECT * FROM mood_entries ORDER BY date DESC")
    fun observeAll(): Flow<List<MoodEntryEntity>>
    
    @Query("SELECT * FROM mood_entries ORDER BY date DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<MoodEntryEntity>>
    
    // Date range queries for calendar
    @Query("SELECT * FROM mood_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun observeByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<MoodEntryEntity>>
    
    // Statistics queries
    @Query("SELECT COUNT(*) FROM mood_entries")
    fun observeTotalCount(): Flow<Int>
    
    @Query("SELECT AVG(score) FROM mood_entries WHERE date >= :since")
    fun observeAverageScore(since: LocalDate): Flow<Double?>
    
    @Query("SELECT AVG(score) FROM mood_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getAverageScoreForRange(startDate: LocalDate, endDate: LocalDate): Double?
    
    // Streak calculation - get dates to calculate streak in code
    @Query("SELECT date FROM mood_entries WHERE date <= :today ORDER BY date DESC")
    suspend fun getDatesForStreak(today: LocalDate): List<LocalDate>
    
    // Check if entry exists for today
    @Query("SELECT EXISTS(SELECT 1 FROM mood_entries WHERE date = :date)")
    suspend fun hasEntryForDate(date: LocalDate): Boolean
    
    // Get entries for last N days
    @Query("SELECT * FROM mood_entries WHERE date >= :since ORDER BY date DESC")
    fun observeEntriesSince(since: LocalDate): Flow<List<MoodEntryEntity>>
}
