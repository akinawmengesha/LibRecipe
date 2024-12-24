package com.example.libRecipe.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "recipes_table")
data class ResultListing(
    @PrimaryKey(autoGenerate = true)
    var AUTOid: Int = 0,
    @SerializedName("aggregateLikes")
    val aggregateLikes: Int,
    @SerializedName("cheap")
    val cheap: Boolean,
    @SerializedName("dairyFree")
    val dairyFree: Boolean,
    @SerializedName("glutenFree")
    val glutenFree: Boolean,
    @SerializedName("id")
    var recipeId: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int,
    @SerializedName("sourceName")
    val sourceName: String?,
    @SerializedName("sourceUrl")
    val sourceUrl: String,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("vegan")
    val vegan: Boolean,
    @SerializedName("vegetarian")
    val vegetarian: Boolean,
    @SerializedName("veryHealthy")
    val veryHealthy: Boolean
) : Parcelable {

    constructor(parcel: Parcel) : this(
        AUTOid = parcel.readInt(),
        aggregateLikes = parcel.readInt(),
        cheap = parcel.readByte() != 0.toByte(),
        dairyFree = parcel.readByte() != 0.toByte(),
        glutenFree = parcel.readByte() != 0.toByte(),
        recipeId = parcel.readInt(),
        image = parcel.readString() ?: "",
        readyInMinutes = parcel.readInt(),
        sourceName = parcel.readString(),
        sourceUrl = parcel.readString() ?: "",
        summary = parcel.readString() ?: "",
        title = parcel.readString() ?: "",
        vegan = parcel.readByte() != 0.toByte(),
        vegetarian = parcel.readByte() != 0.toByte(),
        veryHealthy = parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(AUTOid)
        parcel.writeInt(aggregateLikes)
        parcel.writeByte(if (cheap) 1 else 0)
        parcel.writeByte(if (dairyFree) 1 else 0)
        parcel.writeByte(if (glutenFree) 1 else 0)
        parcel.writeInt(recipeId)
        parcel.writeString(image)
        parcel.writeInt(readyInMinutes)
        parcel.writeString(sourceName)
        parcel.writeString(sourceUrl)
        parcel.writeString(summary)
        parcel.writeString(title)
        parcel.writeByte(if (vegan) 1 else 0)
        parcel.writeByte(if (vegetarian) 1 else 0)
        parcel.writeByte(if (veryHealthy) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ResultListing> {
        override fun createFromParcel(parcel: Parcel): ResultListing {
            return ResultListing(parcel)
        }

        override fun newArray(size: Int): Array<ResultListing?> {
            return arrayOfNulls(size)
        }
    }
}
