package com.bbroda.expirydateguard.ui.classes.recipedatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey (autoGenerate = true) val uid: Int,
    @ColumnInfo (name = "label") var name: String?,
    @ColumnInfo (name = "image") var imageUrl: String?,
    @ColumnInfo (name = "calories") var calories: Float?,
    @ColumnInfo (name = "healthLabels") var healthLabel: List<String>?,
    @ColumnInfo (name = "ingredientLines") var ingredients: List<String>?,

    @ColumnInfo (name="source") val source: String?,
    @ColumnInfo (name="url") val url: String?,
    @ColumnInfo (name="dietLabels") val dietLabels: List<String>?,
    @ColumnInfo (name="cautions") val cautions: List<String>?,
    @ColumnInfo (name="mealType") val mealType: List<String>?,
    @ColumnInfo (name="time") val time: String?
)
