package com.example.finalproject.DataController

import androidx.room.*
import com.example.finalproject.DataController.CyclingData

@Dao
interface CyclingDataDao {
    @Query("SELECT * from cyclingdata")
    fun getAll(): List<CyclingData>

    @Query("SELECT * FROM cyclingdata WHERE cyclingdata.uid = :userId")
    fun getDateData(userId: Long): CyclingData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cyclingData: CyclingData): Long

    @Update
    fun update(cyclingData: CyclingData)
}