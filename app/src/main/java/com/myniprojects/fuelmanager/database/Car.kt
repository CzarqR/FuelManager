package com.myniprojects.fuelmanager.database

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.myniprojects.fuelmanager.R

@Entity(tableName = "car_table")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val carID: Long = 0L,

    @ColumnInfo(name = "brand")
    val brand: String = "test",

    @ColumnInfo(name = "model")
    val model: String = "1",

    @ColumnInfo(name = "engine")
    val engine: String = "2",

    @ColumnInfo(name = "fuel_type")
    val fuelType: String = "3",

    @ColumnInfo(name = "tank_size")
    val tankSize: Double = 0.0,

    @ColumnInfo(name = "icon_id")
    val iconID: Byte = 0

)
{
    companion object
    {
        @StringRes
        fun validateData(
            brand: String,
            model: String,
            tankSize: String
        ): Int
        {
            return when
            {
                brand.isEmpty() ->
                {
                    R.string.brand_cannot_empty
                }
                model.isEmpty() ->
                {
                    R.string.model_cannot_empty
                }
                tankSize.isEmpty() ->
                {
                    R.string.tank_size_cannot_empty
                }
                tankSize.toDoubleOrNull() == null ->
                {
                    R.string.tank_size_wrong_format
                }
                else ->
                {
                    R.string.success_code
                }
            }
        }
    }
}
