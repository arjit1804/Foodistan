package com.example.activity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class FavouritesEntity (
    @PrimaryKey val id:Int,
    @ColumnInfo(name="name")val name:String,
    @ColumnInfo(name="cost")val cost:String,
    @ColumnInfo(name="rating")val rating:String,
    @ColumnInfo(name="image_url")val imageUrl:String
    )