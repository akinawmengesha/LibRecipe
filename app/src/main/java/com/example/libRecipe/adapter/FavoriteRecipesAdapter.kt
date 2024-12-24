package com.example.libRecipe.adapter

import android.content.Intent
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.libRecipe.R
import com.example.libRecipe.Utlis.RecipesDiffUtil
import com.example.libRecipe.activites.RecipeDetailActivity
import com.example.libRecipe.databinding.FavoriteRecipeRowLayoutBinding
import com.example.libRecipe.roomDB.dao.FoodRecipeDao
import com.example.libRecipe.roomDB.entity.FavoritesEntity
import com.example.libRecipe.viewmodel.FavViewModel
import org.jsoup.Jsoup

class FavoriteRecipesAdapter(
    private val requireActivity: FragmentActivity,
    private val favViewModel: FavViewModel
) : RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>(), ActionMode.Callback {

    private var multiSelection = false
    private lateinit var mActionMode: ActionMode
    private val selectedRecipes = arrayListOf<FavoritesEntity>()
    private val myViewHolders = arrayListOf<MyViewHolder>()
    private var favoriteRecipes = emptyList<FavoritesEntity>()

    inner class MyViewHolder(val binding: FavoriteRecipeRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: FavoritesEntity) {
            binding.apply {
                favoriteTitleTextView.text = recipe.title
                favoriteRecipeImageView.load(recipe.image) {
                    crossfade(600)
                    error(R.drawable.ic_error_placeholder)
                }
                applyVeganColor(favoriteLeafImageView, recipe.vegan)
                favoriteDescriptionTextView.text = parseHtml(recipe.summary)
            }
        }

        private fun applyVeganColor(view: View, vegan: Boolean) {
            val color = if (vegan) R.color.green else R.color.color1
            when (view) {
                is TextView -> view.setTextColor(ContextCompat.getColor(view.context, color))
                is ImageView -> view.setColorFilter(ContextCompat.getColor(view.context, color))
            }
        }

        private fun parseHtml(description: String?): String {
            return Jsoup.parse(description).text()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavoriteRecipeRowLayoutBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolders.add(holder)
        val recipe = favoriteRecipes[position]
        holder.bind(recipe)

        holder.binding.favoriteHeartImageView.setOnClickListener {
            favViewModel.deleteFavoriteRecipe(recipe)
            Toast.makeText(requireActivity, "Removed from Favorites", Toast.LENGTH_SHORT).show()
        }

        holder.binding.favoriteRecipesRowLayout.apply {
            setOnClickListener {
                if (multiSelection) applySelection(holder, recipe) else openRecipeDetails(recipe)
            }
            setOnLongClickListener {
                if (!multiSelection) {
                    multiSelection = true
                    requireActivity.startActionMode(this@FavoriteRecipesAdapter)
                }
                applySelection(holder, recipe)
                true
            }
        }

        saveItemStateOnScroll(recipe, holder)
    }

    override fun getItemCount(): Int = favoriteRecipes.size

    private fun saveItemStateOnScroll(recipe: FavoritesEntity, holder: MyViewHolder) {
        if (selectedRecipes.contains(recipe)) {
            changeItemStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
        } else {
            changeItemStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
    }

    private fun applySelection(holder: MyViewHolder, recipe: FavoritesEntity) {
        if (selectedRecipes.contains(recipe)) {
            selectedRecipes.remove(recipe)
            changeItemStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        } else {
            selectedRecipes.add(recipe)
            changeItemStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
        }
        applyActionModeTitle()
    }

    private fun changeItemStyle(holder: MyViewHolder, backgroundColor: Int, strokeColor: Int) {
        holder.binding.favoriteRecipesRowLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.binding.favoriteRowCardView.strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    private fun applyActionModeTitle() {
        when (selectedRecipes.size) {
            0 -> {
                mActionMode.finish()
                multiSelection = false
            }
            1 -> mActionMode.title = "${selectedRecipes.size} item selected"
            else -> mActionMode.title = "${selectedRecipes.size} items selected"
        }
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorites_contextual_menu, menu)
        mActionMode = actionMode!!
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean = true

    override fun onActionItemClicked(actionMode: ActionMode?, menuItem: MenuItem?): Boolean {
        if (menuItem?.itemId == R.id.delete_favorite_recipe_menu) {
            selectedRecipes.forEach { favViewModel.deleteFavoriteRecipe(it) }
            Toast.makeText(requireActivity, "${selectedRecipes.size} recipes removed.", Toast.LENGTH_SHORT).show()
            selectedRecipes.clear()
            actionMode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        myViewHolders.forEach { changeItemStyle(it, R.color.cardBackgroundColor, R.color.strokeColor) }
        multiSelection = false
        selectedRecipes.clear()
    }

    fun setData(newRecipes: List<FavoritesEntity>, foodRecipeDao: FoodRecipeDao) {
        val diffUtil = RecipesDiffUtil(favoriteRecipes, newRecipes)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        favoriteRecipes = newRecipes
        diffResult.dispatchUpdatesTo(this)
    }

    private fun openRecipeDetails(recipe: FavoritesEntity) {
       // val intent = Intent(requireActivity, RecipeDetailActivity::class.java)
        //requireActivity.startActivity(intent)
    }
}
