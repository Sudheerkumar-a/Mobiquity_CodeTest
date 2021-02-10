package com.ziraff.hrcursor.ui.Attendance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobiquity.codetest.R
import com.mobiquity.codetest.database.CitiesInfo
import kotlinx.android.synthetic.main.list_item_cities.view.*


class CitiesListAdapter(private val mContext: Context, private val cities: List<CitiesInfo>) :
    RecyclerView.Adapter<CitiesListAdapter.MyView>() {

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(context: Context, data: CitiesInfo) {
            run {
                itemView.title.text = data.cityName
                itemView.address.text = data.cityAddress
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

}