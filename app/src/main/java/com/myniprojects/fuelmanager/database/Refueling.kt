package com.myniprojects.fuelmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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

    @ColumnInfo(name = "previous_state")
    val previousState: Byte = 0,

    @ColumnInfo(name = "comment")
    val comment: String = ""
)