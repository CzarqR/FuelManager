package com.myniprojects.fuelmanager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RefuelingDAO
{
    @Insert
    fun insert(refueling: Refueling)

    @Update
    fun update(refueling: Refueling)

    @Query("SELECT * FROM refueling_table WHERE refuelingID = :key")
    fun get(key: Long): LiveData<Refueling>

    @Query("DELETE FROM refueling_table WHERE refuelingID = :refuelingID")
    fun delete(refuelingID: Long)

    @Query("SELECT * FROM refueling_table ORDER BY refuelingID DESC")
    fun getAll(): LiveData<List<Refueling>>

    @Query("SELECT * FROM refueling_table WHERE carID = :carID ORDER BY refuelingID DESC")
    fun getAll(carID: Long): LiveData<List<Refueling>>

    @Query("SELECT * FROM refueling_table WHERE carID IN (:carID) ORDER BY refuelingID DESC")
    fun getAll(carID: LongArray): LiveData<List<Refueling>>

    @Query("SELECT * FROM refueling_table WHERE refuelingID = :refuelingID")
    fun getOne(refuelingID: Long): LiveData<Refueling>

    @Query("SELECT * FROM refueling_table order by date_time ASC")
    fun getAllNotObservable(): List<Refueling>

    @Query("SELECT * FROM refueling_table WHERE carID IN (:carID) order by date_time ASC")
    fun getAllNotObservable(carID: LongArray): List<Refueling>

}