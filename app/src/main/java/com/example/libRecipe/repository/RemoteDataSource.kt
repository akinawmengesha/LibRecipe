package com.example.libRecipe.repository

import com.example.libRecipe.models.FoodRecipe
import com.example.libRecipe.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {


    suspend fun getRecipesFromServer(): Response<FoodRecipe> {
        return apiService.getRecipes()
    }


}