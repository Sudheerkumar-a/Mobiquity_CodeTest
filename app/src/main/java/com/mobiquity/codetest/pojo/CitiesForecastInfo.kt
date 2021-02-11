package com.mobiquity.codetest.pojo

import com.google.gson.annotations.SerializedName

class CitiesForecastInfo {
    @SerializedName("temp")
    var temp = 0f

    @SerializedName("feels_like")
    var feels_like = 0f

    @SerializedName("temp_min")
    var temp_min = 0f

    @SerializedName("temp_max")
    var temp_max = 0f

    @SerializedName("weather")
    var weather = ""

    @SerializedName("weather_sub")
    var weathersub = ""

    @SerializedName("dt_txt")
    var dt_txt = ""

    @SerializedName("humidity")
    var humidity = 0
}