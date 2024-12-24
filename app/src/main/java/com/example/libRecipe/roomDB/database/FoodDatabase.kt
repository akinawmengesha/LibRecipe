package com.example.libRecipe.roomDB.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.libRecipe.models.ResultEntity
import com.example.libRecipe.models.ResultListing // Ensure ResultListing is correctly annotated with @Entity
import com.example.libRecipe.roomDB.dao.FoodRecipeDao
import com.example.libRecipe.roomDB.entity.FavoritesEntity
import com.example.libRecipe.roomDB.entity.RecipeEntity

@Database(
    entities = [
        FavoritesEntity::class,   // Entity for favorites
        RecipeEntity::class,      // Entity for recipes
        ResultEntity::class,      // Additional result-related entity
        ResultListing::class      // Entity for listing results
    ],
    version = 1,
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {

    // Provide access to the DAO
    abstract fun getFoodRecipeDao(): FoodRecipeDao
}
