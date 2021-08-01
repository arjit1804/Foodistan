package com.example.activity.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ CartEntity::class , FavouritesEntity::class ], version = 2)
abstract class CartDataBase : RoomDatabase() {

    abstract fun cartDao() : CartDao

    abstract fun favouritesDao() : FavouritesDao

}