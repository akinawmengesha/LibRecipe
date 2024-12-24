package com.example.libRecipe.network

import com.example.libRecipe.model.User
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BackEndApi {
    @FormUrlEncoded
    @POST("auth/login")
    fun LOGIN(@Field("email") email: String, @Field("password") password: String): Call<User>


}

