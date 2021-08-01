package com.example.activity.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavouritesDao {

    @Insert
    fun insertFavRestaurant(favouriteSEntity:FavouritesEntity)

    @Delete
    fun deleteFavRestaurant(favouriteSEntity: FavouritesEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllFavRestaurants():List<FavouritesEntity>

    @Query("SELECT * FROM restaurants WHERE id = :id")
    fun getRestaurantById(id: String): FavouritesEntity

}