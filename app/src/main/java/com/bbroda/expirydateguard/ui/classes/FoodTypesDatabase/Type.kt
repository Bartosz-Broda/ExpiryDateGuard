package com.bbroda.expirydateguard.ui.classes.FoodTypesDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Type(
    @PrimaryKey (autoGenerate = true) val uid: Int,
    @ColumnInfo (name = "typeLabelPol") var typeLabelPol: String,
    @ColumnInfo (name = "stringID") var stringID: Int,
    @ColumnInfo (name = "typeLabelEn") var typeLabelEn: String

)
