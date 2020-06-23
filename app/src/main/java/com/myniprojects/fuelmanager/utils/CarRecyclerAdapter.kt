package com.myniprojects.fuelmanager.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.model.CarIcon

class CarRecyclerAdapter : RecyclerView.Adapter<CarRecyclerAdapter.ViewHolder>()
{
    var data = listOf<Car>()
        set(value)
        {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = data[position]
        holder.bind(item)
    }


    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val carTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val carIcon: ImageView = itemView.findViewById(R.id.imgCar)
        private val carEngine: TextView = itemView.findViewById(R.id.txtEngine)
        private val carFuel: TextView = itemView.findViewById(R.id.txtFuel)

        companion object
        {
            fun from(parent: ViewGroup): ViewHolder
            {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.car_recycler, parent, false)
                return ViewHolder(view)
            }
        }


        fun bind(
            item: Car
        )
        {
            carIcon.setImageResource(CarIcon.cars[item.iconID.toInt()].icon)
            carTitle.text = "${item.brand} ${item.model}"
            carEngine.text = item.engine
            carFuel.text = item.fuelType
        }

    }


}