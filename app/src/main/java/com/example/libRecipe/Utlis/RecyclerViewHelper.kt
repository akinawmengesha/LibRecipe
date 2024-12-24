package com.example.libRecipe.Utlis

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libRecipe.adapter.RecipesAdapter
import com.example.libRecipe.models.ResultListing
import com.example.libRecipe.roomDB.dao.FoodRecipeDao
import com.example.libRecipe.viewmodel.RecipesViewModel
import com.example.libRecipe.roomDB.database.FoodDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecyclerViewHelper {

    companion object {
        // This function sets up the RecyclerView with the adapter and layout
        fun setUpRecyclerView(
            context: Context,
            recyclerView: RecyclerView,
            postAdapter: RecipesAdapter // Ensure it's the correct type here
        ) {
            recyclerView.apply {
                setHasFixedSize(true) // Makes the RecyclerView more efficient
                layoutManager = LinearLayoutManager(context) // LinearLayout for vertical scrolling
                adapter = postAdapter // Sets the provided adapter
            }
        }

        // This function observes the LiveData and updates the adapter with the new data
        suspend fun observeData(
            postViewModel: RecipesViewModel,
            postAdapter: RecipesAdapter,
            foodRecipeDao: FoodRecipeDao,
            recipesViewModel: RecipesViewModel,
            db: FoodDatabase
        ) {
            // Perform I/O operation in background thread
            val recipes: List<ResultListing> = withContext(Dispatchers.IO) {
                foodRecipeDao.getAllRecipesFromDB() // Fetch data from the database in the background
            }

            // Switch to main thread to update the UI
            withContext(Dispatchers.Main) {
                // Make sure we have valid recipes data
                if (recipes.isNotEmpty()) {
                    // Update the adapter with the fetched data
                    postAdapter.setData(
                        recipes  // Pass the fetched recipes to the adapter
                    )
                }
            }
        }
    }
}
