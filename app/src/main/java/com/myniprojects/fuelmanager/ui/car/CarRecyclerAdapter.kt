package com.myniprojects.fuelmanager.ui.car

import android.annotation.SuppressLint
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
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.databinding.CarRecyclerBinding
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class CarRecyclerAdapter(private val clickListener: CarListener, maxSelect: Int, panelSize: Int) :
        ListAdapter<Car, CarRecyclerAdapter.ViewHolder>(
            CarDiffCallback()
        )
{
    companion object
    {
        var maxSize: Int = 8 // 8 colors in array for different colors
            private set

        var PANEL_SIZE = 125
    }

    init
    {
        maxSize = maxSelect
        PANEL_SIZE = panelSize
    }

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
            parent,
            _selectedCars,
            clickListener
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.bind(getItem(position)!!, clickListener)
    }


    class ViewHolder private constructor(
        private val binding: CarRecyclerBinding,
        private val selectedCars: MutableLiveData<ArrayList<Long>>,
        private val carListener: CarListener
    ) :
            RecyclerView.ViewHolder(binding.root), View.OnTouchListener
    {

        private var xStart = 0F
        private var lastY = 0F
        private var yStart = 0F
        private val handler: Handler = Handler()
        private var isLongClickCanceled = false
        private var wasLongClicked = false
        private var startScrolling = false
        private var status = 0
            set(value)
            {
                field = when
                {
                    value > 0 ->
                    {
                        min(value, PANEL_SIZE)
                    }
                    value < 0 ->
                    {
                        max(value, -PANEL_SIZE)
                    }
                    else ->
                    {
                        value
                    }
                }
                setSizes()
            }

        companion object
        {
            fun from(
                parent: ViewGroup,
                selectedCar: MutableLiveData<ArrayList<Long>>,
                carListener: CarListener
            ): ViewHolder
            {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CarRecyclerBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding, selectedCar, carListener
                )
            }

            private const val LONG_CLICK_TIME = 550L
            private const val CLICK_DISTANCE = 75
            //private const val PANEL_SIZE = 240
        }


        private val isCarSelected: Boolean
            get()
            {
                return selectedCars.value!!.contains(binding.car!!.carID)
            }

        private val isPlaceToAdd: Boolean
            get()
            {
                return selectedCars.value!!.size < maxSize
            }


        private fun addCar()
        {
            selectedCars.value!!.add(binding.car!!.carID)
            selectedCars.value = selectedCars.value
        }

        private fun removeCar()
        {
            selectedCars.value!!.remove(binding.car!!.carID)
            selectedCars.value = selectedCars.value
        }


        @SuppressLint("ClickableViewAccessibility")
        fun bind(
            car: Car,
            carListener: CarListener
        )
        {
            binding.car = car
            binding.clickListener = carListener

            binding.rootCL.setOnLongClickListener {
                carListener.clickLongListener(car.carID)
                true
            }

            binding.rootCL.setOnTouchListener(this)

            status = if (isCarSelected) //car was selected, show right panel
            {
                -PANEL_SIZE
            }
            else
            {
                0
            }

            binding.executePendingBindings()
        }

        private val leftPanel = binding.rootCL.getChildAt(0)
        private val rightPanel = binding.rootCL.getChildAt(1)
        private val centerPanel = binding.rootCL.getChildAt(2)

        private fun setSizes()
        {
            when
            {
                status == 0 -> // center
                {
                    leftPanel.layoutParams.width = 1
                    rightPanel.layoutParams.width = 1
                }
                status > 0 -> //right
                {
                    leftPanel.layoutParams.width = status
                    rightPanel.layoutParams.width = 1
                }
                else -> //left
                {
                    leftPanel.layoutParams.width = 1
                    rightPanel.layoutParams.width = -status
                }
            }

            leftPanel.requestLayout()
            rightPanel.requestLayout()

            centerPanel.setBackgroundResource(R.drawable.gradient_car)
            leftPanel.setBackgroundResource(R.drawable.gradient_car_delete)
            rightPanel.setBackgroundResource(R.drawable.gradient_select)
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
                        xStart = event.x
                        yStart = event.y
                        isLongClickCanceled = false
                        wasLongClicked = false
                        startScrolling = false
                        handler.postDelayed({ //long click
                            wasLongClicked = true
                            v.performLongClick()
                        }, LONG_CLICK_TIME)
                    }
                    MotionEvent.ACTION_UP ->
                    {
                        handler.removeCallbacksAndMessages(null)
                        if (!startScrolling && !isLongClickCanceled && !wasLongClicked)
                        {
                            if ((event.eventTime - event.downTime) < LONG_CLICK_TIME) //click
                            {
                                if (status == 0)
                                {
                                    v.performClick()
                                }
                                else
                                {
                                    status = 0
                                    removeCar()
                                }
                            }
                        }
                        else if (!startScrolling)
                        {
                            when
                            {
                                status > (PANEL_SIZE / 2) -> //show left
                                {
                                    status = PANEL_SIZE
                                    removeCar()
                                }
                                status < -(PANEL_SIZE / 2) -> //show right
                                {

                                    if (isPlaceToAdd)//car can be added
                                    {
                                        status = -PANEL_SIZE
                                        addCar()
                                    }
                                    else
                                    {
                                        status = 0
                                        carListener.cannotSelectCar()
                                    }
                                }
                                else ->
                                {
                                    status = 0
                                    removeCar()
                                }
                            }
                        }
                    }
                    MotionEvent.ACTION_MOVE ->
                    {
                        if (startScrolling)
                        {
                            carListener.scroll((lastY - event.rawY).toInt())
                            lastY = event.rawY
                        }
                        else
                        {
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
                                        }
                                        else if (deltaX < 0)
                                        {
                                            status = (deltaX + CLICK_DISTANCE)
                                        }

                                    }
                                }
                                else if (abs(yStart - event.y) > CLICK_DISTANCE)
                                {
                                    lastY = event.rawY
                                    startScrolling = true
                                    handler.removeCallbacksAndMessages(null)
                                }
                                else if (!isLongClickCanceled && abs(xStart - event.x) >= CLICK_DISTANCE)
                                {
                                    isLongClickCanceled = true
                                    handler.removeCallbacksAndMessages(null)
                                }
                            }
                        }

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
    val clickListener: (carId: Long) -> Unit,
    val clickLongListener: (carId: Long) -> Unit,
    val clickDeleteListener: (carId: Long) -> Unit,
    val scroll: (dy: Int) -> Unit,
    val cannotSelectCar: () -> Unit
)
{
    fun onClick(car: Car) = clickListener(car.carID)
    fun onDeleteClick(car: Car) = clickDeleteListener(car.carID)
}


