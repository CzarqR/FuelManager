package com.myniprojects.fuelmanager.ui.refueling

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.database.Car
import com.myniprojects.fuelmanager.database.CarDAO
import com.myniprojects.fuelmanager.database.Refueling
import com.myniprojects.fuelmanager.database.RefuelingDAO
import com.myniprojects.fuelmanager.utils.Log
import kotlinx.coroutines.*

class RefuelingFragmentVM(
    private val databaseRefueling: RefuelingDAO,
    private val databaseCar: CarDAO,
    private var carID: LongArray,
    application: Application
) :
        AndroidViewModel(application)
{
    lateinit var refueling: LiveData<List<Refueling>>
    lateinit var cars: LiveData<List<Car>>

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val type: Boolean //true 1 car
        get() = carID.size == 1

    fun setCarId(carID: LongArray)
    {
        this.carID = carID
        refueling = databaseRefueling.getAll(carID)
        cars = databaseCar.get(carID)
    }


    init
    {
        Log.d("VM car created. CarID: ${carID[0]}")
    }


    private suspend fun insertRefueling(refueling: Refueling)
    {
        withContext(Dispatchers.IO) {
            databaseRefueling.insert(refueling)
        }
    }

    fun addRefueling(
        litres: Double,
        price: Double,
        state: Byte,
        place: String,
        comment: String,
        odometerReading: Double,
        selectedCar: Int,
        dateTime: Long = -1L
    )
    {
        uiScope.launch {
            insertRefueling(
                Refueling(
                    carID = carID[selectedCar],
                    litres = litres,
                    price = price,
                    previousTankState = state,
                    previousOdometerReading = odometerReading,
                    place = place,
                    comment = comment,
                    dateTimeMillis = if (dateTime == -1L) System.currentTimeMillis() else dateTime
                )
            )
        }
    }


    override fun onCleared()
    {
        super.onCleared()
        Log.d("VM refueling destroyed")
        viewModelJob.cancel()
    }

    // region navigation

    private val _navigateToRefueling = MutableLiveData<Long>()
    val navigateToRefueling
        get() = _navigateToRefueling

    fun refuelingClicked(refuelingID: Long)
    {
        refueling.value!!.forEach {
            Log.d("Car: $it")
        }

        _navigateToRefueling.value = refuelingID
    }

    fun refuelingNavigated()
    {
        _navigateToRefueling.value = null
    }

    fun canShow(): Boolean
    {
        if (refueling.value != null)
        {
            return refueling.value!!.size > 2
        }
        return false
    }

    // endregion


    // region chart

    val chartFuelCost: Cartesian
        get()
        {
            Log.d("Fuel cost")
            val cartesian = AnyChart.line()

            cartesian.animation(true)

            cartesian.padding(10, 20, 5, 20)

            cartesian.crosshair().enabled(true)
            cartesian.crosshair().yLabel(true)

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

            cartesian.title(getApplication<Application>().getString(R.string.chart_title))

            cartesian.yAxis(0).title(getApplication<Application>().getString(R.string.y_axis_title))
            cartesian.xAxis(0).labels().padding(5, 5, 5, 5)

            val seriesData = ArrayList<DataEntry>()

            refueling.value!!.forEach {
                seriesData.add(
                    ValueDataEntry(
                        it.dateTimeChartString,
                        it.price
                    )
                )
            }


            val set = Set.instantiate()
            set.data(seriesData)
            val series1Mapping = set.mapAs("{ x: 'x', value: 'value'}")

            val series1 = cartesian.line(series1Mapping)
            series1.name(
                getApplication<Application>().getString(
                    R.string.car_title,
                    cars.value!![0].brand,
                    cars.value!![0].model
                )
            )
            series1.hovered().markers().enabled(true)
            series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4.0)
            series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_TOP)
                .offsetX(5.0)
                .offsetY(5.0)

            return cartesian
        }


    val chartFuelEfficiency: Cartesian
        get()
        {
            val cartesian = AnyChart.line()

            cartesian.animation(true)

            cartesian.padding(10, 20, 5, 20)

            cartesian.crosshair().enabled(true)
            cartesian.crosshair().yLabel(true)

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

            cartesian.title(getApplication<Application>().getString(R.string.fuel_efficiency))

            cartesian.yAxis(0)
                .title(getApplication<Application>().getString(R.string.y_axis_title_efficiency))
            cartesian.xAxis(0).labels().padding(5, 5, 5, 5)

            val seriesData = ArrayList<DataEntry>()

            val tankSize = cars.value!![0].tankSize

            with(refueling.value!!) {
                for (i in (size - 1) downTo 1)
                {
                    val afterRefTankState =
                        this[i].previousTankState + this[i].litres * 100 / tankSize
                    val endState = this[i - 1].previousTankState
                    val usedTank = afterRefTankState - endState

                    val usedLitres = usedTank * tankSize / 100


                    val distance =
                        this[i - 1].previousOdometerReading - this[i].previousOdometerReading

                    if (distance <= 0)
                    {
                        Log.d("Refueling at $i skipped. Distance was $distance")
                        continue
                    }

                    if (usedLitres <= 0)
                    {
                        Log.d("Refueling at $i skipped. Used litres was $usedLitres")
                        continue
                    }

                    val efficiency = distance / usedLitres


                    Log.d("after $afterRefTankState")
                    Log.d("end $endState")
                    Log.d("used $usedTank")
                    Log.d("ul $usedLitres")
                    Log.d("dist $distance")
                    Log.d("ref at $i eq $efficiency")


                    seriesData.add(
                        ValueDataEntry(
                            this[i].dateTimeChartString,
                            efficiency
                        )
                    )

                }

            }


            val set = Set.instantiate()
            set.data(seriesData)
            val series1Mapping = set.mapAs("{ x: 'x', value: 'value'}")

            val series1 = cartesian.line(series1Mapping)
            series1.name(
                getApplication<Application>().getString(
                    R.string.car_title,
                    cars.value!![0].brand,
                    cars.value!![0].model
                )
            )
            series1.hovered().markers().enabled(true)
            series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4.0)
            series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_TOP)
                .offsetX(5.0)
                .offsetY(5.0)

            return cartesian
        }


    // endregion

}