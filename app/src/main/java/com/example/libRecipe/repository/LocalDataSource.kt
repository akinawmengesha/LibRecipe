package com.example.libRecipe.repository

import android.util.Log
import com.example.libRecipe.models.ResultListing
import com.example.libRecipe.roomDB.dao.FoodRecipeDao
import com.example.libRecipe.roomDB.entity.FavoritesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDAO: FoodRecipeDao,
) {
    fun readRecipes(): Flow<List<ResultListing>> {
        return recipesDAO.readRecipes()
    }

    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return recipesDAO.readFavoriteRecipes()
    }

    suspend fun insertRecipes(recipesEntity: List<ResultListing>?) {
        // Check if recipesEntity is not null before calling the DAO method
        recipesEntity?.let {
            recipesDAO.insertRecipes(it) // Insert non-null list
            it.forEach { recipe ->
                Log.e("TAG_ROOMDATABASE", "insertRecipes: $recipe")
            }
        } ?: run {
            Log.e("TAG_ROOMDATABASE", "insertRecipes: recipesEntity is null")
        }
    }




    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity) {
       recipesDAO.insertFavorite(favoritesEntity)
    }
  suspend fun isFavorite(recipeId:Int) : Boolean? {
     return  recipesDAO.isFavorite(recipeId)
    }


    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDAO.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteAllFavoriteRecipes() {
        recipesDAO.deleteAllFavoriteRecipes()
    }
}