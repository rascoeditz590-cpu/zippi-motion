package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.KeyframeDao
import com.example.data.local.dao.LayerDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.entity.KeyframeEntity
import com.example.data.local.entity.LayerEntity
import com.example.data.local.entity.ProjectEntity

@Database(
    entities = [
        ProjectEntity::class,
        LayerEntity::class,
        KeyframeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun layerDao(): LayerDao
    abstract fun keyframeDao(): KeyframeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zippi_motion.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
