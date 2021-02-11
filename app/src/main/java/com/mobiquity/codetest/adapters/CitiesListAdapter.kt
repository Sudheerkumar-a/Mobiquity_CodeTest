package com.mobiquity.codetest.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobiquity.codetest.R
import com.mobiquity.codetest.activities.CityDetailsActivity
import com.mobiquity.codetest.database.CitiesInfo
import kotlinx.android.synthetic.main.list_item_cities.view.*


class CitiesListAdapter(
    private val mContext: Context,
    private val cities: List<CitiesInfo>,
    private val adapterListener: AdapterListener
) :
    RecyclerView.Adapter<CitiesListAdapter.MyView>() {

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(context: Context, data: CitiesInfo) {
            run {
                itemView.setOnClickListener(View.OnClickListener {
                    var intent = Intent(context, CityDetailsActivity::class.java);
                    intent.putExtra("city", data.cityName)
                    intent.putExtra("lat", data.latitude)
                    intent.putExtra("lang", data.longitude)
                    context.startActivity(intent)
                })
                itemView.title.text = data.cityName
                itemView.address.text = data.cityAddress
                itemView.imgDelete.setOnClickListener(View.OnClickListener {
                    adapterListener.onDelete(data)
                })
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
                R.layout.list_item_cities,
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
            holder.bindItems(mContext, cities.get(position))
        }
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    interface AdapterListener {
        fun onDelete(citiesInfo: CitiesInfo);
    }
}