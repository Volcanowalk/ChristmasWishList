package com.example.christmaswishlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/*
    Preconfigured SQL queries

    Data Access Object for the Room database
 */
@Dao
interface ChristmasGiftDAO {

    @Query("SELECT * from christmasgift")
    fun getAll(): List<ChristmasGift>

    //Room generates an implementation that inserts the parameter into the database
    @Insert
    fun addGift(gift : ChristmasGift)
}