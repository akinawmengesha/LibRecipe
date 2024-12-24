package com.example.libRecipe.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.example.libRecipe.R
import com.example.libRecipe.Utlis.RecipesDiffUtil
import com.example.libRecipe.activites.RecipeDetailActivity
import com.example.libRecipe.databinding.RecipesRowLayoutBinding
import com.example.libRecipe.models.ResultListing
import com.example.libRecipe.roomDB.dao.FoodRecipeDao
import com.example.libRecipe.roomDB.entity.FavoritesEntity
import com.example.libRecipe.viewmodel.RecipesViewModel
import com.google.android.material.snackbar.Snackbar
import org.jsoup.Jsoup

class RecipesAdapter(
    private val mainViewModel: RecipesViewModel,  // Injected ViewModel
    private val favoriteDao: FoodRecipeDao,      // Injected DAO
) : androidx.recyclerview.widget.RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {

    private var recipes = emptyList<ResultListing>()  // Holds the list of recipes
    private lateinit var mcontext: Context  // Context for the adapter

    class MyViewHolder(private val binding: RecipesRowLayoutBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

        fun bind(
            result: ResultListing,
            mainViewModel: RecipesViewModel,
            favoriteDao: FoodRecipeDao,
            context: Context
        ) {
            binding.titleTextView.text = result.title
            binding.clockTextView.text = result.readyInMinutes.toString() + " min"
            binding.recipeImageView.load(result.image) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
            applyVeganColor(binding.leafTextView, result.vegan)
            applyVeganColor(binding.leafImageView, result.vegan)
            parseHtml(binding.descriptionTextView, result.summary)

            // Handle Favorites
            if (favoriteDao.isFavorite(result.recipeId)) {
                binding.heartImageView.load(R.drawable.ic_favorite)
                binding.heartImageView.setOnClickListener {
                    val favoritesEntity = FavoritesEntity(
                        0,
                        result.aggregateLikes,
                        result.cheap,
                        result.dairyFree,
                        result.glutenFree,
                        result.recipeId,
                        result.image,
                        result.readyInMinutes,
                        result.sourceName,
                        result.sourceUrl,
                        result.summary,
                        result.title,
                        result.vegan,
                        result.vegetarian,
                        result.veryHealthy
                    )
                    mainViewModel.deleteFavoriteRecipe(favoritesEntity)
                    showSnackBar("Removed from Favorite")
                    binding.heartImageView.load(R.drawable.ic_favorite_border)
                }
            } else {
                binding.heartImageView.load(R.drawable.ic_favorite_border)
                binding.heartImageView.setOnClickListener {
                    val favoritesEntity = FavoritesEntity(
                        0,
                        result.aggregateLikes,
                        result.cheap,
                        result.dairyFree,
                        result.glutenFree,
                        result.recipeId,
                        result.image,
                        result.readyInMinutes,
                        result.sourceName,
                        result.sourceUrl,
                        result.summary,
                        result.title,
                        result.vegan,
                        result.vegetarian,
                        result.veryHealthy
                    )
                    mainViewModel.insertFavoriteRecipe(favoritesEntity)
                    showSnackBar("Added to Favorite")
                    binding.heartImageView.load(R.drawable.ic_favorite)
                }
            }

            // Set up onClickListener for navigating to RecipeDetailActivity
            itemView.setOnClickListener {
                val intent = Intent(context, RecipeDetailActivity::class.java)
                intent.putExtra("RECIPE_DETAIL", result)  // Passing the ResultListing object
                context.startActivity(intent)
            }
        }

        private fun showSnackBar(message: String) {
            Snackbar.make(this.binding.cardView, message, Snackbar.LENGTH_SHORT)
                .setAction("Okay") {}
                .show()
        }

        private fun parseHtml(textView: TextView, description: String?) {
            if (description != null) {
                val desc = Jsoup.parse(description).text()
                textView.text = desc
            }
        }

        private fun applyVeganColor(view: View, vegan: Boolean) {
            if (vegan) {
                when (view) {
                    is TextView -> {
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }

                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        mcontext = holder.itemView.context
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe, mainViewModel, favoriteDao, mcontext)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setData(foodRecipe: List<ResultListing>) {
        val recipesDiffUtil = RecipesDiffUtil(recipes, foodRecipe)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = foodRecipe
        diffUtilResult.dispatchUpdatesTo(this)
    }
}
