package com.mobiquity.codetest.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobiquity.codetest.R
import com.mobiquity.codetest.pojo.CitiesForecastInfo
import kotlinx.android.synthetic.main.city_forecast.view.*
import java.text.SimpleDateFormat
import java.util.*


class CityForecastAdapter(
    private val mContext: Context,
    private val listForecastInfo: List<CitiesForecastInfo>
) :
    RecyclerView.Adapter<CityForecastAdapter.MyView>() {

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(context: Context, data: CitiesForecastInfo) {
            run {
                itemView.txtDate.text = SimpleDateFormat(
                    "dd MMMM yyyy hh:mm a",
                    Locale.US
                ).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data.dt_txt))
                itemView.txtMinTemp.text = data.temp_min.toString() + "\u00B0"
                itemView.txtMaxTemp.text = data.temp_max.toString() + "\u00B0"
                itemView.txtTemp.text = data.temp.toString() + "\u2103"
                itemView.txtFeelsLike.text = "feels like:" + data.feels_like.toString() + "\u00B0"
                itemView.txtWeather.text = data.weather
                itemView.txtWeatherSub.text = data.weathersub
            }
        }

    }

    // Override onCreateViewHolder which deals
    // with the inflation of the card layout
    // as an item for the RecyclerView.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyView {

        // Inflate item.xml using LayoutInflator
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.city_forecast,
                parent,
                false
            )

        // return itemView
        return MyView(itemView)
    }

    // Override onBindViewHolder which deals
    // with the setting of different data
    // and methods related to clicks on
    // particular items of the RecyclerView.
    override fun onBindViewHolder(
        holder: MyView,
        position: Int
    ) {
        mContext?.let {
            holder.bindItems(mContext, listForecastInfo.get(position))
        }
    }

    override fun getItemCount(): Int {
        return listForecastInfo.size
    }

}