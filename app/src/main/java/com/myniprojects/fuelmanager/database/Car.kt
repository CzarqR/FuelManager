package com.myniprojects.fuelmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car_table")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val carID: Long = 1L,

    @ColumnInfo(name = "brand")
    val brand: String = "",

    @ColumnInfo(name = "model")
    val model: String = "",

    @ColumnInfo(name = "engine")
    val engine: String = "",

    @ColumnInfo(name = "fuel_type")
    val fuelType: String = ""
)
