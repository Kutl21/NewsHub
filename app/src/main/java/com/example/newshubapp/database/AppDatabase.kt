package com.example.newshubapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newshubapp.data.models.Article

@Database(
    entities = [Article::class],
    version = 2,              // ✅ BUMPED from 1 → 2 (schema changed for GNews)
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "newshub_database"
                )
                    .fallbackToDestructiveMigration()  // ✅ Wipes old DB if schema changed
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}