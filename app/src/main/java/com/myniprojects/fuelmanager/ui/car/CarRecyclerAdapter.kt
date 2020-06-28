package com.myniprojects.fuelmanager.ui.car

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.databinding.CarRecyclerBinding
import com.myniprojects.fuelmanager.utils.Log


class CarRecyclerAdapter(private val clickListener: CarListener) :
        ListAdapter<Car, CarRecyclerAdapter.ViewHolder>(
            CarDiffCallback()
        )
{

    private val _selectedCars: MutableLiveData<ArrayList<Long>> = MutableLiveData()
    val selectedCars: LiveData<ArrayList<Long>>
        get() = _selectedCars

    init
    {
        _selectedCars.value = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder.from(
            parent
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.bind(getItem(position)!!, clickListener, _selectedCars)
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
            clickListener: CarListener,
            selectedCars: MutableLiveData<ArrayList<Long>>
        )
        {
            fun selectCar()
            {
                with(binding.chBoxSelect)
                {
                    isChecked = !isChecked
                    visibility = if (isChecked)
                    {
                        selectedCars.value!!.add(car.carID)
                        View.VISIBLE
                    }
                    else
                    {
                        selectedCars.value!!.remove(car.carID)
                        View.GONE
                    }
                    selectedCars.value = selectedCars.value
                }
            }

            binding.car = car

            binding.carBackground.setOnClickListener {
                if (selectedCars.value!!.size == 0)
                {
                    Log.d("Empty")
                    clickListener.onClick(car)
                }
                else
                {
                    Log.d("Not empty")
                    selectCar()
                }
            }

            binding.carBackground.setOnLongClickListener {
                selectCar()
                true
            }
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


class CarListener(
    val clickListener: (carId: Long) -> Unit
)
{
    fun onClick(car: Car) = clickListener(car.carID)
}
