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
import com.myniprojects.fuelmanager.utils.Log
import kotlin.math.abs
import kotlin.math.min


class CarRecyclerAdapter(private val clickListener: CarListener, private val maxSelect: Int) :
        ListAdapter<Car, CarRecyclerAdapter.ViewHolder>(
            CarDiffCallback()
        )
{

    private val _selectedCars: MutableLiveData<ArrayList<Long>> = MutableLiveData()
    val selectedCars: LiveData<ArrayList<Long>>
        get() = _selectedCars

    //if car can be added return true
    private fun selectCar(carID: Long, add: Boolean): Boolean
    {
        Log.d("SelectCar. Id : $carID. add: $add")
        if (add && !canAdd(carID))
        {
            return false
        }
        else
        {
            if (add && canAdd(carID))
            {
                _selectedCars.value!!.add(carID)
                _selectedCars.value = _selectedCars.value
            }
            else
            {
                _selectedCars.value!!.remove(carID)
                _selectedCars.value = _selectedCars.value
            }
            return true
        }

    }

    fun canAdd(id: Long): Boolean
    {
        val x = selectedCars.value!!.size <= maxSelect
        Log.d("Can add $id : $_selectedCars.value!!.contains(id)}. Size is lower : $x")
        val y = !_selectedCars.value!!.contains(id) && selectedCars.value!!.size <= maxSelect
        Log.d("Return $y")
        return y
    }

    init
    {
        _selectedCars.value = ArrayList()
        Log.d("Max Select size = $maxSelect")

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        Log.d("Create VH")
        return ViewHolder.from(
            parent,
            { carID, add -> selectCar(carID, add) },
            { dy -> clickListener.scroll(dy) })
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        Log.d("Bind VH at pos : $position")
        holder.bind(getItem(position)!!, clickListener)
    }


    class ViewHolder private constructor(
        private val binding: CarRecyclerBinding,
        private val selectCar: (carID: Long, add: Boolean) -> Boolean,
        private val scroll: (dy: Int) -> Unit
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

        companion object
        {
            fun from(
                parent: ViewGroup,
                selectCar: (carID: Long, add: Boolean) -> Boolean,
                scroll: (dy: Int) -> Unit
            ): ViewHolder
            {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CarRecyclerBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding, selectCar, scroll
                )
            }

            private const val LONG_CLICK_TIME = 700L
            private const val CLICK_DISTANCE = 75
            private const val PANEL_SIZE = 125
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


            binding.executePendingBindings()
        }

        private val leftPanel = binding.rootCL.getChildAt(0)
        private val rightPanel = binding.rootCL.getChildAt(1)
        private val centerPanel = binding.rootCL.getChildAt(2)

        private fun setSizes(lW: Int, rW: Int)
        {
            Log.d("$lW  $rW")
            leftPanel.layoutParams.width = lW
            rightPanel.layoutParams.width = rW

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
                        Log.d("Down")
                        xStart = event.x
                        yStart = event.y
                        isLongClickCanceled = false
                        wasLongClicked = false
                        startScrolling = false
                        handler.postDelayed({ //long click
                            wasLongClicked = true
                            setSizes(1, 1)
                            v.performLongClick()
                        }, LONG_CLICK_TIME)
                    }
                    MotionEvent.ACTION_UP ->
                    {
                        Log.d("Up")
                        handler.removeCallbacksAndMessages(null)
                        if (!startScrolling && !isLongClickCanceled && !wasLongClicked)
                        {
                            if ((event.eventTime - event.downTime) < LONG_CLICK_TIME) //click
                            {
                                Log.d("Status $status")
                                if (status == 0)
                                {
                                    v.performClick()
                                }
                                else
                                {
                                    setSizes(1, 1)
                                    status = 0
                                    selectCar(binding.car!!.carID, false)
                                }
                            }
                        }
                        else if (startScrolling)
                        {
                            Log.d("Up  after scrolling")
                        }
                        else
                        {
                            Log.d("Up after operation")

                            when
                            {
                                status > (PANEL_SIZE / 2) ->
                                {
                                    setSizes(PANEL_SIZE, 1)
                                    selectCar(binding.car!!.carID, false)
                                }
                                status < -(PANEL_SIZE / 2) ->
                                {

                                    if (!selectCar(
                                            binding.car!!.carID,
                                            true
                                        )
                                    ) //cars cannot be added
                                    {
                                        setSizes(1, 1)
                                    }
                                    else //car was added
                                    {
                                        setSizes(1, PANEL_SIZE)

                                    }

                                }
                                else ->
                                {
                                    setSizes(1, 1)
                                    status = 0
                                    selectCar(binding.car!!.carID, false)
                                }
                            }
                        }
                    }
                    MotionEvent.ACTION_MOVE ->
                    {
                        Log.d("Move")
                        if (startScrolling)
                        {
                            scroll((lastY - event.rawY).toInt())
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
                                            setSizes(min(status, PANEL_SIZE), 1)
                                        }
                                        else if (deltaX < 0)
                                        {
                                            status = (deltaX + CLICK_DISTANCE)
                                            setSizes(1, min(-status, PANEL_SIZE))
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
    val scroll: (dy: Int) -> Unit
)
{
    fun onClick(car: Car) = clickListener(car.carID)
    fun onDeleteClick(car: Car) = clickDeleteListener(car.carID)
}


