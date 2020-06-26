package com.myniprojects.fuelmanager.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.databinding.CarRecyclerBinding

class CarRecyclerAdapter(private val clickListener: CarListener) :
        ListAdapter<Car, CarRecyclerAdapter.ViewHolder>(
            CarDiffCallback()
        )
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder.from(
            parent
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.bind(getItem(position)!!, clickListener)
    }


    class ViewHolder private constructor(private val binding: CarRecyclerBinding) :
            RecyclerView.ViewHolder(binding.root)
    {

        companion object
        {
            fun from(parent: ViewGroup): ViewHolder
            {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CarRecyclerBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding
                )
            }
        }


        fun bind(
            car: Car,
            clickListener: CarListener
        )
        {
            binding.car = car
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

    }


}

class CarDiffCallback : DiffUtil.ItemCallback<Car>()
{
    override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean
    {
        return oldItem.carID == newItem.carID
    }

    override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean
    {
        return oldItem == newItem
    }

}


class CarListener(val clickListener: (carId: Long) -> Unit)
{
    fun onClick(car: Car) = clickListener(car.carID)
}