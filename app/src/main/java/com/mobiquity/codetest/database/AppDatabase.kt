package com.mobiquity.codetest.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(CitiesInfo::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun citiesDao(): CitiesDao
}