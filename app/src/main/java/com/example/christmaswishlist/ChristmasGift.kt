package com.example.christmaswishlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
    Data Model Class for the Room database
 */
@Entity
data class ChristmasGift(
    @PrimaryKey(autoGenerate = true) val giftID : Int,
    @ColumnInfo(name = "name") val giftName : String?,
    @ColumnInfo(name = "description") val giftDescription : String?,
    @ColumnInfo(name = "image_uri") val giftImageUri : String?
)