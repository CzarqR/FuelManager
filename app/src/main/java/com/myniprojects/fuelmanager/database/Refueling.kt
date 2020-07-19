package com.myniprojects.fuelmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
    val previousTankState: Byte = 0,

    @ColumnInfo(name = "previous_odometer_reading")
    val previousOdometerReading: Double = 0.0,

    @ColumnInfo(name = "comment")
    val comment: String = "",

    @ColumnInfo(name = "date_time")
    val dateTimeMillis: Long = 0
)
{
    val dateTimeLongString: String
        get() = getDate(this.dateTimeMillis, "dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    val dateTimeChartString: String
        get() = getDate(this.dateTimeMillis, "dd/MM/yy HH:mm", Locale.getDefault())
}