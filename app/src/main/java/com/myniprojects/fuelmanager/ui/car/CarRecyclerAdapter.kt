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

    private fun selectCar(carID: Long, add: Boolean)
    {
        if (add)
        {
            _selectedCars.value!!.add(carID)
            _selectedCars.value = _selectedCars.value
        }
        else
        {
            _selectedCars.value!!.remove(carID)
            _selectedCars.value = _selectedCars.value
        }

    }

    init
    {
        _selectedCars.value = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder.from(
            parent,
            { carID, add -> selectCar(carID, add) },
            { dy -> clickListener.scroll(dy) })
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.bind(getItem(position)!!, clickListener)
    }



    class ViewHolder private constructor(
        private val binding: CarRecyclerBinding,
        private val selectCar: (carID: Long, add: Boolean) -> Unit,
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
                selectCar: (carID: Long, add: Boolean) -> Unit,
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

                            val leftPanel = (v as ViewGroup).getChildAt(0)
                            val rightPanel = v.getChildAt(1)

                            val paramsLP = leftPanel.layoutParams
                            val paramsRP = rightPanel.layoutParams

                            paramsLP.width = 1
                            paramsRP.width = 1

                            leftPanel.layoutParams = paramsLP
                            rightPanel.layoutParams = paramsRP

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
                                    val leftPanel = (v as ViewGroup).getChildAt(0)
                                    val rightPanel = v.getChildAt(1)

                                    val paramsLP = leftPanel.layoutParams
                                    val paramsRP = rightPanel.layoutParams

                                    paramsLP.width = 1
                                    paramsRP.width = 1

                                    leftPanel.layoutParams = paramsLP
                                    rightPanel.layoutParams = paramsRP

                                    status = 0
                                    selectCar(binding.car!!.carID, false)
                                }
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
                                    selectCar(binding.car!!.carID, false)
                                }
                                status < -(PANEL_SIZE / 2) ->
                                {
                                    paramsRP.width = PANEL_SIZE
                                    paramsLP.width = 1
                                    selectCar(binding.car!!.carID, true)
                                }
                                else ->
                                {
                                    paramsLP.width = 1
                                    paramsRP.width = 1
                                    status = 0
                                    selectCar(binding.car!!.carID, false)
                                }
                            }

                            leftPanel.layoutParams = paramsLP
                            rightPanel.layoutParams = paramsRP
                        }
                    }

                    MotionEvent.ACTION_MOVE ->
                    {
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
                                        val leftPanel = (v as ViewGroup).getChildAt(0)
                                        val rightPanel = v.getChildAt(1)

                                        val paramsLP = leftPanel.layoutParams
                                        val paramsRP = rightPanel.layoutParams

                                        if (deltaX > 0)
                                        {
                                            status = (deltaX - CLICK_DISTANCE)
                                            paramsLP.width = min(status, PANEL_SIZE)
                                            paramsRP.width = 1
                                        }
                                        else if (deltaX < 0)
                                        {
                                            status = (deltaX + CLICK_DISTANCE)
                                            paramsRP.width = min(-status, PANEL_SIZE)
                                            paramsLP.width = 1
                                        }

                                        leftPanel.layoutParams = paramsLP
                                        rightPanel.layoutParams = paramsRP
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


