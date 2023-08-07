package com.bbroda.expirydateguard.ui.classes.productdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Product::class], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class ProductsDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        private var INSTANCE: ProductsDatabase? = null
        fun getDatabase(context: Context): ProductsDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context, ProductsDatabase::class.java, "product_database")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}