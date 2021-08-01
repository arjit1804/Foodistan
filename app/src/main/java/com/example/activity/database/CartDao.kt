package com.example.activity.database

import androidx.room.*

@Dao
interface CartDao {

    @Insert
    fun InsertFoodItem(cartEntity : CartEntity)

    @Delete
    fun DeleteFoodItem(cartEntity: CartEntity)

    @Query("SELECT * FROM cartItems")
    fun getAllFoodItems(): List<CartEntity>

    @Query("DELETE FROM cartItems WHERE resId = :resId")
    fun deleteFoodItems(resId: String)


}