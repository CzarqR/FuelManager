package com.myniprojects.fuelmanager.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.databinding.CarRecyclerBinding

class CarRecyclerAdapter : ListAdapter<Car, CarRecyclerAdapter.ViewHolder>(CarDiffCallback())
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = getItem(
            position
        )
        holder.bind(item)
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
                return ViewHolder(binding)
            }
        }


        fun bind(
            car: Car
        )
        {
            binding.car = car
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