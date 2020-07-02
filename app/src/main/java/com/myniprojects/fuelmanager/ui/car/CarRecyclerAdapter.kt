package com.myniprojects.fuelmanager.ui.car

import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
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
import kotlin.math.abs
import kotlin.math.min


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
            RecyclerView.ViewHolder(binding.root), View.OnTouchListener
    {
        private var xStart = 0F
        private val handler: Handler = Handler()
        private val LONG_CLICK_TIME = 1000L
        private val CLICK_DISTANCE = 75
        private val PANEL_SIZE = 125
        private var isLongClickCanceled = false
        private var wasLongClicked = false

        private var status = 0

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

            binding.rootCL.setOnClickListener {
//                if (selectedCars.value!!.size == 0)
//                {
//                    Log.d("Empty")
//                    clickListener.onClick(car)
//                }
//                else
//                {
//                    Log.d("Not empty")
//                    selectCar()
//                }
                Log.d("Click")
            }


            binding.rootCL.setOnLongClickListener {
//                selectCar()
                Log.d("Long Click")
                true
            }

            binding.rootCL.setOnTouchListener(this)

            binding.linLayDelete.setOnClickListener {
                Log.d("Delete click")
            }

            binding.linLaySelect.setOnClickListener {
                Log.d("Select click")
            }

            binding.executePendingBindings()


        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean
        {
            if (v != null && event != null)
            {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action)
                {
                    MotionEvent.ACTION_DOWN ->
                    {
                        Log.d("Down")
                        xStart = event.x
                        isLongClickCanceled = false
                        wasLongClicked = false
                        handler.postDelayed({ //long click
                            wasLongClicked = true
                            v.performLongClick()
                        }, LONG_CLICK_TIME)
                        xStart = event.x
                    }
                    MotionEvent.ACTION_UP ->
                    {
                        Log.d("Up")
                        handler.removeCallbacksAndMessages(null)
                        val deltaX = event.x - xStart
                        if (abs(deltaX) < CLICK_DISTANCE)
                        {
                            if ((event.eventTime - event.downTime) < LONG_CLICK_TIME) //click
                            {
                                v.performClick()
                            }
                        }
                        else
                        {
                            val leftPanel = (v as ViewGroup).getChildAt(0)
                            val rightPanel = v.getChildAt(1)

                            val paramsLP = leftPanel.layoutParams
                            val paramsRP = rightPanel.layoutParams

                            when
                            {
                                status > (PANEL_SIZE / 2) ->
                                {
                                    paramsLP.width = PANEL_SIZE
                                    paramsRP.width = 1
                                }
                                status < -(PANEL_SIZE / 2) ->
                                {
                                    paramsRP.width = PANEL_SIZE
                                    paramsLP.width = 1
                                }
                                else ->
                                {
                                    paramsLP.width = 1
                                    paramsRP.width = 1
                                }
                            }

                            leftPanel.layoutParams = paramsLP
                            rightPanel.layoutParams = paramsRP
                        }
                    }

                    MotionEvent.ACTION_MOVE ->
                    {
                        Log.d("START")
                        Log.d("Pos start $xStart")
                        Log.d("Pos ${event.x}")
                        Log.d("DELTA TO INT ${(event.x - xStart).toInt()}")
                        if (!wasLongClicked)
                        {

                            if (isLongClickCanceled)
                            {
                                val deltaX = (event.x - xStart).toInt()

                                if (abs(deltaX) > 75)
                                {
                                    if (deltaX > 0)
                                    {
                                        status = (deltaX - CLICK_DISTANCE)
                                        Log.d("status $status")

                                        val leftPanel = (v as ViewGroup).getChildAt(0)
                                        val rightPanel = v.getChildAt(1)

                                        val paramsLP = leftPanel.layoutParams
                                        val paramsRP = rightPanel.layoutParams

                                        paramsLP.width = min(status, PANEL_SIZE)
                                        paramsRP.width = 1

                                        leftPanel.layoutParams = paramsLP
                                        rightPanel.layoutParams = paramsRP
                                    }
                                    else if (deltaX < 0)
                                    {
                                        status = (deltaX + CLICK_DISTANCE)
                                        Log.d("status $status")
                                        val rightPanel = (v as ViewGroup).getChildAt(1)
                                        val leftPanel = v.getChildAt(0)

                                        val paramsLP = leftPanel.layoutParams
                                        val paramsRP = rightPanel.layoutParams

                                        paramsRP.width = min(-status, PANEL_SIZE)
                                        paramsLP.width = 1

                                        leftPanel.layoutParams = paramsLP
                                        rightPanel.layoutParams = paramsRP
                                    }
                                }
                            }

                            if (!isLongClickCanceled && abs(xStart - event.x) >= CLICK_DISTANCE)
                            {
                                Log.d("Cancel long click")
                                isLongClickCanceled = true
                                handler.removeCallbacksAndMessages(null)
                            }
                        }
                        Log.d("END")

                    }
                }
            }
            return true
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
