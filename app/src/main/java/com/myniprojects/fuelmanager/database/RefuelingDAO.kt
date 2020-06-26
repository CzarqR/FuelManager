package com.myniprojects.fuelmanager.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RefuelingDAO
{
    @Insert
    fun insert(refueling: Refueling)

    @Update
    fun update(refueling: Refueling)

    @Query("SELECT * FROM refueling_table WHERE refuelingID = :key")
    fun get(key: Long): Refueling

    @Delete
    fun delete(refueling: Refueling)

    @Query("SELECT * FROM refueling_table ORDER BY refuelingID DESC")
    fun getAll(): LiveData<List<Refueling>>

    @Query("SELECT * FROM refueling_table WHERE carID = :carID ORDER BY refuelingID DESC")
    fun getAll(carID: Long): LiveData<List<Refueling>>

}