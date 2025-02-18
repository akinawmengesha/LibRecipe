package com.example.libRecipe.activites

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libRecipe.R
import com.example.libRecipe.adapter.RecipesAdapter
import com.example.libRecipe.viewmodel.RecipesViewModel
import com.example.libRecipe.Utlis.ThemeManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val postViewModel: RecipesViewModel by viewModels() // Get ViewModel

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

        // Use observe instead of collect if you're working with LiveData
        postViewModel.readRecipes.observe(this) { recipes ->
            postAdapter.setData(recipes)
        }
    }

    private fun setUi() {
        // Initialize the RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView)
        postAdapter = RecipesAdapter(postViewModel) // Only pass ViewModel

        recyclerView.apply {
            setHasFixedSize(true) // Makes the RecyclerView more efficient
            layoutManager = LinearLayoutManager(this@MainActivity) // LinearLayout for vertical scrolling
            adapter = postAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // Inflate the options menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_activity -> {
                startActivity(Intent(this, FavoriteActivity::class.java)) // Navigate to FavoriteActivity
                return true
            }
            R.id.themeSwitch -> {
                themeManager.toggleTheme()
                recreate() // Recreate activity to apply the new theme
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
