package com.example.libRecipe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libRecipe.R
import com.example.libRecipe.adapter.FavoriteRecipesAdapter
import com.example.libRecipe.databinding.FragmentFavoriteRecipesBinding
import com.example.libRecipe.roomDB.database.FoodDatabase
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.libRecipe.viewmodel.FavViewModel

@AndroidEntryPoint
class FavoriteRecipesFragment : androidx.fragment.app.Fragment() {

    // Use FavViewModel instead of RecipesViewModel
    private val favViewModel: FavViewModel by viewModels()

    private val mAdapter: FavoriteRecipesAdapter by lazy {
        FavoriteRecipesAdapter(
            requireActivity(),
            favViewModel  // Pass FavViewModel to the adapter
        )
    }

    private var _binding: FragmentFavoriteRecipesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var db: FoodDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteRecipesBinding.inflate(inflater, container, false)

        // Set up options menu
        setHasOptionsMenu(true)
        setUpRecyclerView(binding.favoriteRecipesRecyclerView)
        readDatabase()

        // Set up toolbar
        binding.toolbar.inflateMenu(R.menu.favorite_recipes_menu)
        binding.toolbar.title = "Favorite List"
        binding.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.deleteAll_favorite_recipes_menu -> {
                    // Call a function in the ViewModel to delete all favorites
                    favViewModel.deleteAllFavoriteRecipes()
                    Snackbar.make(binding.root, "All Recipes removed", Snackbar.LENGTH_SHORT)
                        .setAction("Okay") {}.show()
                    binding.favoriteRecipesRecyclerView.visibility = View.GONE
                }
            }
            true
        }

        return binding.root
    }

    private fun setUpRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            // Observe changes in favorite recipes using the correct ViewModel
            favViewModel.getFullListFav().collect { database ->
                if (database.isNotEmpty()) {
                    Log.d("FavoriteRecipesFragment", "readDatabase called!")
                    // Pass only the list of data (FavoritesEntity) to the adapter
                    mAdapter.setData(database)  // Pass only the List<FavoritesEntity> to setData
                    binding.favoriteRecipesRecyclerView.visibility = View.VISIBLE
                    binding.noDataImageView.visibility = View.GONE
                    binding.noDataTextView.visibility = View.GONE
                } else {
                    binding.favoriteRecipesRecyclerView.visibility = View.GONE
                    binding.noDataImageView.visibility = View.VISIBLE
                    binding.noDataTextView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear binding to prevent memory leaks
        _binding = null
    }
}
