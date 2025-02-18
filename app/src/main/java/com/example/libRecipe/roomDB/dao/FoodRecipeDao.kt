package com.example.libRecipe.roomDB.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.libRecipe.models.ResultListing
import com.example.libRecipe.roomDB.entity.FavoritesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodRecipeDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(resultListing: List<ResultListing>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(resultListing: List<ResultListing>)

    // Safely insert the recipes if the list is not null
    @Transaction
    suspend fun insertRecipesSafely(resultListing: List<ResultListing>?) {
        resultListing?.let {
            insertRecipes(it) // Only insert if the list is not null
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(model: FavoritesEntity)

    @Query("SELECT * FROM favorite_recipes_table")
    fun getAllFavouriteData(): Flow<List<FavoritesEntity>>

    @Query("SELECT EXISTS (SELECT 1 FROM favorite_recipes_table WHERE recipeId = :id)")
    fun isFavorite(id: Int): Boolean
    // Method to delete a favorite recipe
    @Delete
    suspend fun delete(favoritesEntity: FavoritesEntity)


    // Method to delete all favorite recipes
    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavorites()

    @Insert
    suspend fun insertRecipeResultListing(resultListing: ResultListing)

    @Query("DELETE FROM favorite_recipes_table WHERE recipeId = :id")
    fun deleteData(id: Int)

    @Query("DELETE FROM recipes_table")
    fun deleteAllData()

    @Query("SELECT * FROM favorite_recipes_table") // Replace with the correct condition
    fun getFavoriteRecipes(): LiveData<List<ResultListing>>

    @Query("SELECT COUNT (recipeId) FROM recipes_table")
    fun getRowCount(): Int

    @Query("SELECT * FROM recipes_table ORDER BY recipeId ASC LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(limit: Int, offset: Int): List<ResultListing>

    @Query("SELECT * FROM recipes_table ORDER BY AUTOid ASC")
    fun readRecipes(): Flow<List<ResultListing>>

    @Query("SELECT * FROM recipes_table ORDER BY recipeId ASC")
    suspend fun getAllRecipesFromDB(): List<ResultListing> // New method to fetch all recipes

    @Query("SELECT * FROM favorite_recipes_table ORDER BY recipeId ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("SELECT EXISTS (SELECT 1 FROM favorite_recipes_table WHERE recipeId = :recipeId)")
    fun isFavoriteLive(recipeId: Int): LiveData<Boolean>

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()
    @Delete
    suspend fun deleteFavorite(favorite: FavoritesEntity)

    @Query("SELECT * FROM favorite_recipes_table ORDER BY AUTOid ASC")
    fun getAllFavorites(): Flow<List<FavoritesEntity>>



}