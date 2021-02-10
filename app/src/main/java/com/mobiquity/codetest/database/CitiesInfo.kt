package com.mobiquity.codetest.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CitiesInfo(
    @ColumnInfo(name = "city_name") val cityName: String?,
    @ColumnInfo(name = "city_address") val cityAddress: String?,
    @ColumnInfo(name = "city_lat") val latitude: Double?,
    @ColumnInfo(name = "city_lang") val longitude: Double?
){
    @PrimaryKey(autoGenerate = true)
    var cid: Int = 0
}