package com.example.libRecipe.activites

import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.libRecipe.R
import com.example.libRecipe.Utlis.ThemeManager
import com.example.libRecipe.databinding.ActivityHomePageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePage : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private lateinit var navController: NavController
    private lateinit var themeManager: ThemeManager
    private lateinit var themeSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize ThemeManager and apply the current theme
        themeManager = ThemeManager(this)
        themeManager.applyTheme()

        // Initialize the theme toggle Switch
        themeSwitch = binding.themeSwitch

        // Dynamically set the Switch state based on saved preference
        val sharedPreferences = getSharedPreferences(ThemeManager.PREFERENCES_NAME, MODE_PRIVATE)
        themeSwitch.isChecked = sharedPreferences.getBoolean(ThemeManager.KEY_THEME_MODE, false)

        // Set listener for theme toggle
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            themeManager.toggleTheme() // Toggle the theme preference
            recreate() // Recreate the activity to apply the new theme
        }

        // Set up Navigation Component
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.recipesFragment
            )
        )

        // Handle "Add Recipe" button click
        binding.buttonAddRecipe.setOnClickListener {
            val intent = Intent(this, AddRecipeActivity::class.java)
            startActivity(intent)
        }

        // Handle "Favorites" button click
        binding.buttonFavorites.setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
