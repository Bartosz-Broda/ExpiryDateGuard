package com.bbroda.expirydateguard.ui.classes.productdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Product::class], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class ProductsInListDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        private var INSTANCE: ProductsInListDatabase? = null
        fun getDatabase(context: Context): ProductsInListDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context, ProductsInListDatabase::class.java, "product_in_meal_database")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}