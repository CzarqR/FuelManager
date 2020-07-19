package com.myniprojects.fuelmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
