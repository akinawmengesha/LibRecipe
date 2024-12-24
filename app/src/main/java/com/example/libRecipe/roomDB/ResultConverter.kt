package com.example.libRecipe.roomDB

import android.util.Log
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.libRecipe.models.ResultListing
import com.google.gson.Gson

@ProvidedTypeConverter
class ResultConverter {
    @TypeConverter
    fun toDetailList(value: String): ResultListing {
        Log.d("DBINFO_FROMJSON", "Extracted>>${value}") /* just for demonstration */
        return Gson().fromJson(value, ResultListing::class.java)
    }

    @TypeConverter
    fun fromDetailList(value: ResultListing): String {
        return Gson().toJson(value)
    }
}