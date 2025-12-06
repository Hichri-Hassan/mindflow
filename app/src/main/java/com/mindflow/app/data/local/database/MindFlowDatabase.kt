package com.mindflow.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mindflow.app.data.local.converter.Converters
import com.mindflow.app.data.local.dao.MoodEntryDao
import com.mindflow.app.data.local.entity.MoodEntryEntity

/**
 * Main Room database for MindFlow app
 */
@Database(
    entities = [MoodEntryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MindFlowDatabase : RoomDatabase() {
    
    abstract fun moodEntryDao(): MoodEntryDao
    
    companion object {
        private const val DATABASE_NAME = "mindflow_database"
        
        @Volatile
        private var INSTANCE: MindFlowDatabase? = null
        
        fun getInstance(context: Context): MindFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        
        private fun buildDatabase(context: Context): MindFlowDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MindFlowDatabase::class.java,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
        }
    }
}
