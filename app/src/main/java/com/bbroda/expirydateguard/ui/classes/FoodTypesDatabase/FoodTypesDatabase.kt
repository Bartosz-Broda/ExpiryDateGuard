package com.bbroda.expirydateguard.ui.classes.FoodTypesDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bbroda.expirydateguard.ui.classes.productdatabase.LocalDateConverter

@Database(entities = [Type::class], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class FoodTypesDatabase : RoomDatabase() {

    abstract fun typesDao(): FoodTypesDao

    companion object {
        private var INSTANCE: FoodTypesDatabase? = null
        fun getDatabase(context: Context): FoodTypesDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context, FoodTypesDatabase::class.java, "food_database")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}