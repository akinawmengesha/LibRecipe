package com.example.libRecipe.Utlis

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager(private val context: Context) {

    companion object {
        // Change visibility to 'internal' or 'public'
        const val PREFERENCES_NAME = "app_preferences"
        const val KEY_THEME_MODE = "theme_mode"
    }

    // Apply theme based on saved preference
    fun applyTheme() {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean(KEY_THEME_MODE, false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    // Toggle the theme mode and save the preference
    fun toggleTheme() {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val currentMode = sharedPreferences.getBoolean(KEY_THEME_MODE, false)

        // Save the new theme preference
        sharedPreferences.edit().putBoolean(KEY_THEME_MODE, !currentMode).apply()

        // Apply the new theme
        applyTheme()
    }
}
