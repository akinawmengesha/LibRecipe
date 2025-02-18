package com.example.libRecipe.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.libRecipe.roomDB.entity.FavoritesEntity
import com.example.libRecipe.viewmodel.RecipesViewModel
import com.google.android.material.snackbar.Snackbar
import org.jsoup.Jsoup
import java.io.File

class RecipesAdapter(
    private val mainViewModel: RecipesViewModel
) : androidx.recyclerview.widget.RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {

    private var recipes = emptyList<ResultListing>()
    private lateinit var mcontext: Context

    class MyViewHolder(private val binding: RecipesRowLayoutBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

        fun bind(result: ResultListing, mainViewModel: RecipesViewModel, context: Context) {
            binding.titleTextView.text = result.title
            binding.clockTextView.text = "${result.readyInMinutes} min"

            if (result.image.startsWith("http")) {
                binding.recipeImageView.load(result.image) {
                    crossfade(600)
                    error(R.drawable.ic_error_placeholder)
                }
            } else {
                val file = File(result.image)
                if (file.exists()) {
                    binding.recipeImageView.load(Uri.fromFile(file)) {
                        crossfade(600)
                        error(R.drawable.ic_error_placeholder)
                    }
                } else {
                    binding.recipeImageView.setImageResource(R.drawable.ic_error_placeholder)
                }
            }

            applyVeganColor(binding.leafTextView, result.vegan)
            applyVeganColor(binding.leafImageView, result.vegan)
            parseHtml(binding.descriptionTextView, result.summary)

            mainViewModel.isRecipeFavorite(result.recipeId).observeForever { isFavorite ->
                if (isFavorite) {
                    binding.heartImageView.load(R.drawable.ic_favorite)
                    binding.heartImageView.setOnClickListener {
                        mainViewModel.deleteFavoriteRecipe(createFavoriteEntity(result))
                        showSnackBar("Removed from Favorite")
                        binding.heartImageView.load(R.drawable.ic_favorite_border)
                    }
                } else {
                    binding.heartImageView.load(R.drawable.ic_favorite_border)
                    binding.heartImageView.setOnClickListener {
                        mainViewModel.insertFavoriteRecipe(createFavoriteEntity(result))
                        showSnackBar("Added to Favorite")
                        binding.heartImageView.load(R.drawable.ic_favorite)
                    }
                }
            }

            itemView.setOnClickListener {
                val intent = Intent(context, RecipeDetailActivity::class.java)
                intent.putExtra("RECIPE_DETAIL", result)
                context.startActivity(intent)
            }
        }

        private fun createFavoriteEntity(result: ResultListing): FavoritesEntity {
            return FavoritesEntity(
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
        }

        private fun showSnackBar(message: String) {
            Snackbar.make(binding.cardView, message, Snackbar.LENGTH_SHORT)
                .setAction("Okay") {}
                .show()
        }

        private fun parseHtml(textView: TextView, description: String?) {
            textView.text = description?.let { Jsoup.parse(it).text() }
        }

        private fun applyVeganColor(view: View, vegan: Boolean) {
            val color = if (vegan) R.color.green else R.color.colour4
            when (view) {
                is TextView -> view.setTextColor(ContextCompat.getColor(view.context, color))
                is ImageView -> view.setColorFilter(ContextCompat.getColor(view.context, color))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        mcontext = holder.itemView.context
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe, mainViewModel, mcontext)
    }

    override fun getItemCount() = recipes.size

    fun setData(foodRecipe: List<ResultListing>) {
        val diffUtilResult = DiffUtil.calculateDiff(RecipesDiffUtil(recipes, foodRecipe))
        recipes = foodRecipe
        diffUtilResult.dispatchUpdatesTo(this)
    }
}