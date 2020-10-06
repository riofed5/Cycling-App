package com.example.finalproject.DataController

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CyclingData(
    @PrimaryKey
    val uid: Long,
    val date: Long,
    val averageSpeed: Float,
    val highestSpeed: Float,
    val distanceTraveled: Float
) {
    override fun toString(): String {
        return "(${uid}) Date: ${date}, average speed ${averageSpeed}. Distance: $distanceTraveled"
    }
}