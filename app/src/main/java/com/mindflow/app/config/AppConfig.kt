package com.mindflow.app.config

/**
 * Configuration for API keys and app settings.
 * 
 * ⚠️ IMPORTANT: For production, use BuildConfig fields or encrypted storage.
 * Never commit API keys to version control!
 * 
 * To get a Gemini API key:
 * 1. Go to https://makersuite.google.com/app/apikey
 * 2. Create a new API key
 * 3. Replace the empty string below with your key
 */
object AppConfig {
    
    /**
     * Gemini API Key
     * 
     * Set this to your API key to enable AI-powered mood analysis.
     * If empty or null, the app will use local keyword-based analysis.
     */
    const val GEMINI_API_KEY: String = "" // Add your API key here or use BuildConfig
    
    /**
     * Check if AI features are enabled
     */
    val isAiEnabled: Boolean
        get() = GEMINI_API_KEY.isNotBlank()
}
