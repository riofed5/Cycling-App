package com.example.finalproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [(CyclingData::class)],
    version = 1
)
abstract class CyclingDatabase: RoomDatabase() {
    abstract fun cyclingDataDao(): CyclingDataDao

    /* one and only one instance, similar to static in Java */
    companion object {
        private var Instance: CyclingDatabase? = null

        @Synchronized
        fun get(context: Context): CyclingDatabase {
            if (Instance == null) {
                Instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        CyclingDatabase::class.java, "cyclingdata.db"
                    ).build()
            }
            return Instance!!
        }
    }
}