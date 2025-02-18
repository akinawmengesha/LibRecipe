package com.example.libRecipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libRecipe.roomDB.dao.FoodRecipeDao
import com.example.libRecipe.roomDB.entity.FavoritesEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavViewModel @Inject constructor(
    private val foodRecipeDao: FoodRecipeDao
) : ViewModel() {

    // Get all favorite recipes
    fun getFullListFav(): Flow<List<FavoritesEntity>> {
        return foodRecipeDao.getAllFavorites()
    }

    // Delete a single favorite recipe
    fun deleteFavoriteRecipe(favorite: FavoritesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            foodRecipeDao.deleteFavorite(favorite)
        }
    }

    // Delete all favorite recipes
    fun deleteAllFavoriteRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            foodRecipeDao.deleteAllFavorites()
        }
    }
}
