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
     * ⚠️ FOR CONTEST EVALUATION: API key included for judges to test AI features.
     * In production, this should be in BuildConfig or encrypted storage.
     * 
     * If you want to use your own key:
     * 1. Go to https://makersuite.google.com/app/apikey
     * 2. Create a new API key
     * 3. Replace the key below
     */
    const val GEMINI_API_KEY: String = "AIzaSyDUvs4RUmbq2IKF_eRuV_SGN4vNDbKLTXk"
    
    /**
     * Check if AI features are enabled
     */
    val isAiEnabled: Boolean
        get() = GEMINI_API_KEY.isNotBlank()
}
