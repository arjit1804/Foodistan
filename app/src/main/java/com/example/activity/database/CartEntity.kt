package com.example.activity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cartItems")
data class CartEntity(
    //restaurant_name and user_id will pick from the all restaurant page and login page
    @PrimaryKey(autoGenerate = true) val resId: Int?,
    @ColumnInfo(name = "food_items") val foodItems: String
)