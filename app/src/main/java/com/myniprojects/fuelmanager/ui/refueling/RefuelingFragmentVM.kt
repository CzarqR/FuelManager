package com.myniprojects.fuelmanager.ui.refueling

import android.app.Application
import androidx.annotation.StringRes
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
import com.myniprojects.fuelmanager.ui.chart.ChartType
import com.myniprojects.fuelmanager.utils.Log
import kotlinx.coroutines.*

class RefuelingFragmentVM(
    private val databaseRefueling: RefuelingDAO,
    private val databaseCar: CarDAO,
    private var _carID: LongArray,
    application: Application
) :
        AndroidViewModel(application)
{
    lateinit var refueling: LiveData<List<Refueling>>
    lateinit var cars: LiveData<List<Car>>

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val type: Boolean //true 1 car
        get() = _carID.size == 1

    val carID: LongArray
        get() = _carID

    lateinit var chartType: ChartType
        private set

    fun setCarId(carID: LongArray)
    {
        this._carID = carID
        refueling = databaseRefueling.getAll(carID)
        cars = databaseCar.get(carID)
    }


    init
    {
        Log.d("VM car created. CarID: ${_carID[0]}")
    }


    private suspend fun insertRefueling(refueling: Refueling)
    {
        withContext(Dispatchers.IO) {
            databaseRefueling.insert(refueling)
        }
    }

    @StringRes
    fun addRefueling(
        litres: String,
        price: String,
        state: String,
        place: String,
        comment: String,
        odometerReading: String,
        selectedCar: Int,
        dateTime: Long = -1L
    ): Int
    {

        val result = Refueling.validateData(litres, price, state, odometerReading)

        if (result == R.string.succes_code)
        {
            uiScope.launch {
                insertRefueling(
                    Refueling(
                        carID = _carID[selectedCar],
                        litres = litres.toDouble(),
                        price = price.toDouble(),
                        tankState = state.toByte(),
                        odometerReading = odometerReading.toDouble(),
                        place = place,
                        comment = comment,
                        dateTimeMillis = if (dateTime == -1L) System.currentTimeMillis() else dateTime
                    )
                )

            }
            return R.string.refueling_added
        }
        return result
    }


    override fun onCleared()
    {
        super.onCleared()
        Log.d("VM refueling destroyed")
        viewModelJob.cancel()
    }

    // region navigation

    private val _navigateToRefueling = MutableLiveData<Long>()
    val navigateToRefueling: LiveData<Long>
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

    fun goToChart(chartType: ChartType)
    {
        this.chartType = chartType
        _navigateToChart.postValue(chartType)
    }

    private val _navigateToChart = MutableLiveData<ChartType>()
    val navigateToChart: LiveData<ChartType>
        get() = _navigateToChart

    fun chartNavigated()
    {
        _navigateToChart.value = null
    }

    // endregion


    // region chart

    val chartFuelCost: Cartesian
        get()
        {
            Log.d("Fuel cost")
            val cartesian = AnyChart.line()

            with(cartesian)
            {
                animation(true)
                padding(10, 20, 5, 20)

                crosshair().enabled(true)
                crosshair().yLabel(true)

                tooltip().positionMode(TooltipPositionMode.POINT)

                title(getApplication<Application>().getString(R.string.chart_title))

                yAxis(0).title(getApplication<Application>().getString(R.string.y_axis_title))
                xAxis(0).labels().padding(5, 5, 5, 5)

            }


            val seriesData = ArrayList<DataEntry>()

            with(refueling.value!!) {
                for (i in (size - 1) downTo 1)
                {
                    seriesData.add(
                        ValueDataEntry(
                            this[i].dateTimeLongString,
                            this[i].price
                        )
                    )
                }
            }


            val set = Set.instantiate()
            set.data(seriesData)
            val series1Mapping = set.mapAs("{ x: 'x', value: 'value'}")

            val series1 = cartesian.line(series1Mapping)

            with(series1)
            {
                name(
                    getApplication<Application>().getString(
                        R.string.car_title,
                        cars.value!![0].brand,
                        cars.value!![0].model
                    )
                )
                hovered().markers().enabled(true)
                hovered().markers()
                    .type(MarkerType.CIRCLE)
                    .size(4.0)
                tooltip()
                    .position("right")
                    .anchor(Anchor.LEFT_TOP)
                    .offsetX(5.0)
                    .offsetY(5.0)
            }



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
                        this[i].tankState + this[i].litres * 100 / tankSize
                    val endState = this[i - 1].tankState
                    val usedTank = afterRefTankState - endState

                    val usedLitres = usedTank * tankSize / 100


                    val distance =
                        this[i - 1].odometerReading - this[i].odometerReading

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