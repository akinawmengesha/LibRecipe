package com.example.libRecipe.roomDB.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.libRecipe.models.ResultListing

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val recipeId: Int = 0,
    val title: String,
    val summary: String,
    val ingredients: String,
    val instructions: String,
    val aggregateLikes: Int,
    val cheap: Boolean,
    val dairyFree: Boolean,
    val glutenFree: Boolean,
    val image: String,
    val readyInMinutes: Int,
    val sourceName: String,
    val sourceUrl: String,
    val vegan: Boolean,
    val vegetarian: Boolean,
    val veryHealthy: Boolean
) {
    // Convert RecipeEntity to ResultListing
    fun toResultListing(): ResultListing {
        return ResultListing(
            recipeId = this.recipeId,
            aggregateLikes = this.aggregateLikes,
            cheap = this.cheap,
            dairyFree = this.dairyFree,
            glutenFree = this.glutenFree,
            image = this.image,
            readyInMinutes = this.readyInMinutes,
            sourceName = this.sourceName,
            sourceUrl = this.sourceUrl,
            summary = this.summary,
            title = this.title,
            vegan = this.vegan,
            vegetarian = this.vegetarian,
            veryHealthy = this.veryHealthy
        )
    }
}
