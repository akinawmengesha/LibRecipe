package com.example.libRecipe.repository

import com.example.libRecipe.roomDB.entity.FavoritesEntity
import kotlinx.coroutines.flow.Flow

interface FavRepository {
    fun getFavouriteFromRoom(): Flow<List<FavoritesEntity>>
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) // Define method to delete a specific favorite
    suspend fun deleteAllFavoriteRecipes() // Define method to delete all favorites
}
