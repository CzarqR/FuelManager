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
import com.myniprojects.fuelmanager.utils.getDate
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

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

    // endregion


    // region chart

    val chart: Cartesian
        get()
        {
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

            val locale = Locale.getDefault()
            refueling.value!!.forEach {
                seriesData.add(
                    ValueDataEntry(
                        getDate(it.dateTimeMillis, "dd/MM/yy HH:mm", locale),
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


            cartesian.legend().enabled(true)
            cartesian.legend().fontSize(13.0)
            cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)

            return cartesian
        }


    private class CustomDataEntry internal constructor(
        x: String?,
        value: Number?,
        value2: Number?,
        value3: Number?
    ) :
            ValueDataEntry(x, value)
    {
        init
        {
            setValue("value2", value2)
            setValue("value3", value3)
        }
    }


    // endregion

}