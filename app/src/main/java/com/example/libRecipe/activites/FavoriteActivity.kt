package com.example.libRecipe.activites

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libRecipe.R
import com.example.libRecipe.adapter.FavoriteRecipesAdapter
import com.example.libRecipe.databinding.ActivityFavoriteBinding
import com.example.libRecipe.roomDB.database.FoodDatabase
import com.example.libRecipe.viewmodel.FavViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteActivity : androidx.appcompat.app.AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var postAdapter: FavoriteRecipesAdapter
    private val favViewModel: FavViewModel by viewModels()  // Hilt ViewModel
    private lateinit var progressBar: ProgressBar

    @Inject
    lateinit var db: FoodDatabase  // Inject FoodDatabase using Hilt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using ViewBinding
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)  // Use binding.root here

        // Set title of the activity
        title = "Favorite Listing"

        // Initialize UI components and RecyclerView
        setUi()

        // Show progress bar before data is loaded
        showProgress()

        // Fetch and display the favorite recipes
        lifecycleScope.launch {
            favViewModel.getFullListFav().collect { data ->
                // Hide the progress bar when data is received
                goneProgress()

                // If no data is available, hide the RecyclerView and show an empty view
                if (data.isNullOrEmpty()) {
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    // Hide the empty view and show the RecyclerView
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    // Pass the data and FoodRecipeDao to the adapter
                    postAdapter.setData(data, db.getFoodRecipeDao())
                }
            }
        }
    }

    // Set up the RecyclerView and adapter
    private fun setUi() {
        // Initialize the adapter
        postAdapter = FavoriteRecipesAdapter(this, favViewModel)

        // Set up RecyclerView with fixed size and layout manager
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
            adapter = postAdapter
        }
    }

    // Show a progress bar while data is loading
    private fun showProgress() {
        // Initialize a layout for the progress bar
        val layout = RelativeLayout(this)
        progressBar = ProgressBar(this, null, androidx.appcompat.R.attr.progressBarStyle)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE
        val params = RelativeLayout.LayoutParams(100, 100)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        layout.addView(progressBar, params)

        // Add the progress bar layout to the root view
        binding.root.addView(layout)
    }

    // Hide the progress bar when data is ready
    private fun goneProgress() {
        progressBar.visibility = View.GONE
    }
}
