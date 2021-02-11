package com.mobiquity.codetest.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.callhistory.network.ApiService
import com.callhistory.network.RetrofitBuilder
import com.google.gson.JsonObject
import com.mobiquity.codetest.R
import com.mobiquity.codetest.adapters.CityForecastAdapter
import com.mobiquity.codetest.pojo.CitiesForecastInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_citydetails.*
import kotlinx.android.synthetic.main.activity_main.toolbar

class CityDetailsActivity : AppCompatActivity() {

    var apiService: ApiService? = null
    var disposable = CompositeDisposable();

    companion object {
        @JvmStatic
        val BASE_URL = "http://api.openweathermap.org/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_citydetails)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        toolbar.title = intent.getStringExtra("city");
        var layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        listWeather.setLayoutManager(layoutManager)
        apiService = RetrofitBuilder.create(BASE_URL)
        var subscribe =
            apiService!!.getWeatherReport(
                intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra(
                    "lang",
                    0.0
                ), "fae7190d7e6433ec3a45285ffcf55c86", "metric"
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ res: JsonObject? ->
                    res?.let {
                        if (res?.get("cod").asInt == 200) {
                            var listCitiesForecastInfo = ArrayList<CitiesForecastInfo>()
                            var list = res?.getAsJsonArray("list")
                            for (i in 0 until list.size()) {
                                var data = list.get(i).asJsonObject
                                var citiesForecastInfo = CitiesForecastInfo()
                                citiesForecastInfo.dt_txt = data.get("dt_txt").asString
                                var dataTemp = data.get("main").asJsonObject
                                citiesForecastInfo.temp = dataTemp.get("temp").asFloat
                                citiesForecastInfo.temp_min =
                                    dataTemp.get("temp_min").asFloat
                                citiesForecastInfo.temp_max =
                                    dataTemp.get("temp_max").asFloat
                                citiesForecastInfo.feels_like =
                                    dataTemp.get("feels_like").asFloat
                                citiesForecastInfo.humidity =
                                    dataTemp.get("humidity").asInt
                                var dataWeatherList = data.get("weather").asJsonArray
                                if (dataWeatherList.size() > 0) {
                                    var dataWeather = dataWeatherList.get(0).asJsonObject
                                    citiesForecastInfo.weather = dataWeather.get("main").asString
                                    citiesForecastInfo.weathersub =
                                        dataWeather.get("description").asString
                                }
                                listCitiesForecastInfo.add(citiesForecastInfo)
                            }
                            Handler(Looper.getMainLooper()).post(Runnable {
                                listWeather.adapter =
                                    CityForecastAdapter(applicationContext, listCitiesForecastInfo)
                            })
                        }
                    }
                }, { error ->
                    Log.i("error", "" + error.message)
                })

        disposable.add(subscribe)
    }

}