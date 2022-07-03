package com.example.christmaswishlist

import androidx.room.Database
import androidx.room.RoomDatabase

/*
    Data Access Object for the ChrimstmasGift class
 */
@Database(entities = [ChristmasGift::class], version = 1)
abstract class GiftAppDatabase : RoomDatabase() {
    abstract fun giftDAO() : ChristmasGiftDAO
}