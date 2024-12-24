package com.example.libRecipe.activites

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.libRecipe.R
import com.example.libRecipe.adapter.RecipesAdapter
import com.example.libRecipe.viewmodel.RecipesViewModel
import com.example.libRecipe.Utlis.RecyclerViewHelper
import com.example.libRecipe.Utlis.ThemeManager
import com.example.libRecipe.roomDB.dao.FoodRecipeDao
import com.example.libRecipe.roomDB.database.FoodDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // ViewModel for managing recipe data
    private val postViewModel: RecipesViewModel by viewModels()

    // Injecting DAO and Database using Dagger Hilt
    @Inject lateinit var foodRecipeDao: FoodRecipeDao
    @Inject lateinit var db: FoodDatabase

    // UI components
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: RecipesAdapter
    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ThemeManager and apply the current theme before setting the layout
        themeManager = ThemeManager(this)
        themeManager.applyTheme()

        setContentView(R.layout.activity_main)

        // Set up the UI elements
        setUi()

        // Observe the list of recipes from ViewModel and update the adapter
        postViewModel.readRecipes.observe(this, { recipes ->
            recipes?.let {
                postAdapter.setData(it) // Update adapter with new data
            }
        })
    }

    private fun setUi() {
        // Initialize the RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView)
        postAdapter = RecipesAdapter(postViewModel, foodRecipeDao)

        // Set up RecyclerView with helper class
        RecyclerViewHelper.setUpRecyclerView(this, recyclerView, postAdapter)

        // Perform I/O operation to observe data and update UI
        lifecycleScope.launch(Dispatchers.IO) {
            RecyclerViewHelper.observeData(postViewModel, postAdapter, foodRecipeDao, postViewModel, db)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // Inflate the options menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item clicks from options menu
        when (item.itemId) {
            R.id.new_activity -> {
                startActivity(Intent(this, FavoriteActivity::class.java)) // Navigate to FavoriteActivity
                return true
            }
            R.id.themeSwitch -> {
                // Toggle the theme when the user selects the toggle option
                themeManager.toggleTheme()
                recreate()  // Recreate the activity to apply the new theme
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        // Override back button press to show confirmation dialog
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        // Display a confirmation dialog when the user presses back
        AlertDialog.Builder(this)
            .setMessage("Do you want to exit the application?")
            .setPositiveButton("Yes") { _, _ ->
                appExit() // Exit the app
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun appExit() {
        // Exit the app and return to home screen
        finish()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
