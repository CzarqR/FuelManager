package com.myniprojects.fuelmanager.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CarDAO
{
    @Insert
    fun insert(car: Car)

    @Update
    fun update(car: Car)

    @Query("SELECT * FROM car_table WHERE carID = :key")
    fun get(key: Long): Car

    @Delete
    fun delete(car: Car)

    @Query("SELECT * FROM car_table ORDER BY carID ASC")
    fun getAll(): LiveData<List<Car>>

}