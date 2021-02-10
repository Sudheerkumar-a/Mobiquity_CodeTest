package com.mobiquity.codetest.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CitiesDao {
    @Query("SELECT * FROM citiesinfo")
    fun getAll(): List<CitiesInfo>

    @Query("SELECT * FROM citiesinfo WHERE cid = :cityId")
    fun getCityInfoById(cityId: Int): CitiesInfo

    @Insert
    fun insertAll(vararg citiesInfo: CitiesInfo)

    @Delete
    fun delete(citiesInfo: CitiesInfo)
}