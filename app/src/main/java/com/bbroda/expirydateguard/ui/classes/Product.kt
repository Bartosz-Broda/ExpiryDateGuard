package com.bbroda.expirydateguard.ui.classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Product(
    @PrimaryKey (autoGenerate = true) val uid: Int,
    @ColumnInfo (name = "product name") var name: String?,
    @ColumnInfo (name = "product type") var type: String?,
    @ColumnInfo (name = "expiryDate") var expiryDate: LocalDate,
    @ColumnInfo (name = "ingredients_text") var ingredients: String?,
    @ColumnInfo (name = "nutriments") var nutriments: String?,
    @ColumnInfo (name = "imageUrl") var imageUrl: String?
    //Kiedyś może będzie obrazek
    // var image: Image?
)