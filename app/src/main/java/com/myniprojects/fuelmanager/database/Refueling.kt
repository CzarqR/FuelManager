package com.myniprojects.fuelmanager.database

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.myniprojects.fuelmanager.R
import com.myniprojects.fuelmanager.utils.Log
import com.myniprojects.fuelmanager.utils.getDate
import java.util.*

@Entity(
    tableName = "refueling_table", foreignKeys = [ForeignKey(
        entity = Car::class,
        parentColumns = arrayOf("carID"),
        childColumns = arrayOf("carID"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Refueling(
    @PrimaryKey(autoGenerate = true)
    val refuelingID: Long = 0L,

    @ColumnInfo(name = "carID")
    val carID: Long = 0L,

    @ColumnInfo(name = "litres")
    val litres: Double = 0.0,

    @ColumnInfo(name = "price")
    val price: Double = 0.0,

    @ColumnInfo(name = "place")
    val place: String = "",

    @ColumnInfo(name = "previous_tank_state")
    val tankState: Byte = 0,

    @ColumnInfo(name = "previous_odometer_reading")
    val odometerReading: Double = 0.0,

    @ColumnInfo(name = "comment")
    val comment: String = "",

    @ColumnInfo(name = "date_time")
    val dateTimeMillis: Long = 0
)
{

    companion object
    {
        @StringRes
        fun validateData(
            litres: String,
            price: String,
            tankState: String,
            odometerReading: String
        ): Int
        {
            when
            {
                litres.isEmpty() ->
                {
                    return R.string.volume_empty
                }
                litres.toDoubleOrNull() == null ->
                {
                    return R.string.volume_wrong_format
                }
                price.isEmpty() ->
                {
                    return R.string.price_empty
                }
                price.toDoubleOrNull() == null ->
                {
                    return R.string.price_wrong_format
                }
                odometerReading.isEmpty() ->
                {
                    return R.string.odometer_reading_empty
                }
                odometerReading.toDoubleOrNull() == null ->
                {
                    return R.string.odometer_reading_wrong_format
                }
                tankState.isEmpty() ->
                {
                    return R.string.tank_state_empty
                }
                tankState.toByteOrNull() == null || (tankState.toByte() < 0 || tankState.toByte() > 100.toByte()) ->
                {
                    return R.string.tank_state_outside_range
                }
                else ->
                {
                    return R.string.succes_code
                }
            }

        }
    }

    val dateTimeLongString: String
        get()
        {
            val x = getDate(this.dateTimeMillis, "dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            Log.d("milis: $dateTimeMillis. date: $x")

            return x
        }

    val dateTimeChartString: String
        get() = getDate(this.dateTimeMillis, "dd/MM/yy HH:mm", Locale.getDefault())

    val totalPrice: Double
        get() = price * litres
}