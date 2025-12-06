package com.mindflow.app

import android.app.Application
import com.mindflow.app.data.local.database.MindFlowDatabase
import com.mindflow.app.data.repository.MoodRepository

/**
 * Application class for MindFlow
 * Initializes database and repository
 */
class MindFlowApplication : Application() {
    
    // Database instance
    val database: MindFlowDatabase by lazy {
        MindFlowDatabase.getInstance(this)
    }
    
    // Repository instance
    val moodRepository: MoodRepository by lazy {
        MoodRepository(database.moodEntryDao())
    }
}
