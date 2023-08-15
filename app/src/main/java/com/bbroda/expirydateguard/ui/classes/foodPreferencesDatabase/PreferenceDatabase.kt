package com.bbroda.expirydateguard.ui.classes.foodPreferencesDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bbroda.expirydateguard.ui.classes.productdatabase.LocalDateConverter

@Database(entities = [Preference::class], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class PreferenceDatabase : RoomDatabase() {

    abstract fun preferenceDao(): PreferenceDao

    companion object {
        private var INSTANCE: PreferenceDatabase? = null
        fun getDatabase(context: Context): PreferenceDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context, PreferenceDatabase::class.java, "preference_database")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}