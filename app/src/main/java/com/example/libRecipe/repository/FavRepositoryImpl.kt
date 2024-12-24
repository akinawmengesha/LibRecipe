package com.example.libRecipe.repository

import com.example.libRecipe.roomDB.dao.FoodRecipeDao
import com.example.libRecipe.roomDB.entity.FavoritesEntity
import javax.inject.Inject

class FavRepositoryImpl @Inject constructor(
    private val foodRecipeDao: FoodRecipeDao
) : FavRepository {

    override fun getFavouriteFromRoom() = foodRecipeDao.getAllFavouriteData()

    // Implementing the deleteAllFavoriteRecipes method
    override suspend fun deleteAllFavoriteRecipes() {
        foodRecipeDao.deleteAllFavoriteRecipes() // DAO method to delete all favorites
    }

    // Implementing the deleteFavoriteRecipe method
    override suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        foodRecipeDao.deleteFavoriteRecipe(favoritesEntity) // DAO method to delete specific favorite
    }
}
