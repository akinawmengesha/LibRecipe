package com.example.libRecipe.network

import com.example.libRecipe.models.FoodRecipe
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
   /* @GET(NetworkingConstants.URL_REPOSITORIES)
    suspend fun getAllPosterImages(): List<PostEntity>*/

    @GET(NetworkingConstants.URL_REPOSITORIES)
    suspend fun getRecipes(): Response<FoodRecipe>
}