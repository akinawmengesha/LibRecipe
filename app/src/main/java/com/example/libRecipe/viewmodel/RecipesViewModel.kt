package com.example.libRecipe.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.libRecipe.Utlis.DataStoreRepository
import com.example.libRecipe.Utlis.NetworkResult
import com.example.libRecipe.models.FoodRecipe
import com.example.libRecipe.models.ResultListing
import com.example.libRecipe.pagining.MainPagingSource
import com.example.libRecipe.repository.RecipesRepository
import com.example.libRecipe.roomDB.dao.FoodRecipeDao
import com.example.libRecipe.roomDB.entity.FavoritesEntity
import com.example.libRecipe.roomDB.entity.RecipeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val postRepository: RecipesRepository,
    private val foodRecipeDao: FoodRecipeDao,
    private val dataStoreRepository: DataStoreRepository,
) : AndroidViewModel(application) {

    // Network Status Management
    var networkStatus = false
    var backOnline = false
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    // Retrofit API Call
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    fun getRecipes() = viewModelScope.launch {
        getRecipesSafeCall()
    }

    private suspend fun getRecipesSafeCall() {
        recipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = postRepository.remote.getRecipesFromServer()
                recipesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value?.data?.results
                if (foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }
            } catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error("Ops! Recipes not found...")
            }
        } else {
            recipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    /** Room Database Operations **/

    // Observing recipes in Room
    val readRecipes: LiveData<List<ResultListing>> = postRepository.local.readRecipes().asLiveData()
    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> = postRepository.local.readFavoriteRecipes().asLiveData()

    // Offline caching of recipes
    private fun offlineCacheRecipes(foodRecipe: List<ResultListing>?) {
        insertRecipes(foodRecipe)
    }


    // Insert multiple recipes into Room
    private fun insertRecipes(recipesEntity: List<ResultListing>?) =
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.local.insertRecipes(recipesEntity) // Bulk insert
        }

    // Handling API Response
    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.body()?.results.isNullOrEmpty() -> {
                NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    // Check if the device has internet connection
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    // Manage network status and data persistence
    private fun saveBackOnline(backOnline: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.saveBackOnline(backOnline)
    }

    // Notify user about network status changes
    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if (networkStatus && backOnline) {
            Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
            saveBackOnline(false)
        }
    }

    // Fetch favorite recipes
    fun getFavoriteRecipes(): LiveData<List<ResultListing>> {
        return foodRecipeDao.getFavoriteRecipes() // Get favorite recipes from the DAO
    }

    /** I/O operations for managing favorite recipes **/
    fun deleteAllFavoriteRecipes() = viewModelScope.launch(Dispatchers.IO) {
        postRepository.local.deleteAllFavoriteRecipes()
    }

    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.local.insertFavoriteRecipes(favoritesEntity)
        }

    // Check if a recipe is marked as favorite
    suspend fun isFavorite(recipeId: Int): Boolean? = withContext(Dispatchers.IO) {
        return@withContext postRepository.local.isFavorite(recipeId)
    }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.local.deleteFavoriteRecipe(favoritesEntity)
        }

    /** Pagination with Flow for lazy loading of recipes **/
    val getDataFromRoomWithOffset = Pager(
        PagingConfig(
            pageSize = 5,
            enablePlaceholders = false,
            initialLoadSize = 5
        )
    ) {
        MainPagingSource(foodRecipeDao)
    }.flow.cachedIn(viewModelScope)
}
