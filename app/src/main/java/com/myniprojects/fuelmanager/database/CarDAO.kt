package com.myniprojects.fuelmanager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CarDAO
{
    @Insert
    fun insert(car: Car)

    @Update
    fun update(car: Car)

    @Query("SELECT * FROM car_table WHERE carID = :key")
    fun get(key: Long): Car


    @Query("SELECT * FROM car_table WHERE carID IN (:keys)")
    fun get(keys: LongArray): LiveData<List<Car>>

    @Query("DELETE FROM car_table WHERE carID = :carID")
    fun delete(carID: Long)

    @Query("SELECT * FROM car_table ORDER BY carID ASC")
    fun getAll(): LiveData<List<Car>>

}