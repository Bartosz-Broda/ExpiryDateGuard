package com.bbroda.expirydateguard.ui.classes.foodPreferencesDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Preference(
    @PrimaryKey (autoGenerate = true) val uid: Int,
    @ColumnInfo (name = "apiLlabel") var apiLabel: String,
    @ColumnInfo (name = "stringID") var stringID: Int,
    @ColumnInfo (name = "isChecked") var isChecked: Boolean

)
