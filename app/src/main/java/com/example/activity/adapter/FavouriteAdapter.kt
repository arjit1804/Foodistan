package com.example.activity.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.activity.RestaurantDetailsActivity
import com.example.activity.database.FavouritesEntity
import com.example.foodistaan.R
import com.squareup.picasso.Picasso

class FavouriteAdapter(val context: Context, val list: List<FavouritesEntity>)
    : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {


    class FavouriteViewHolder(view:View):RecyclerView.ViewHolder(view){

        val imgRecyclerRestaurant : ImageView = view.findViewById(R.id.imgRecyclerFavourite)
        val txtRecyclerRestaurantFoodName : TextView = view.findViewById(R.id.txtRecyclerFavouriteFoodName)
        val txtRecyclerRestaurantFoodPrice : TextView = view.findViewById(R.id.txtRecyclerFavouriteFoodPrice)
        //val imgRecyclerRestaurantFavFood : ImageView = view.findViewById(R.id.imgRecyclerRestaurantFavFood)
        val txtRecyclerRestaurantRating : TextView = view.findViewById(R.id.txtRecyclerFavouriteRating)
        val llRecyclerRestaurantRow : LinearLayout = view.findViewById(R.id.llRecyclerFavouriteRow)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favourite_single_row,parent,false)

        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = list[position]
        holder.txtRecyclerRestaurantFoodName.text = restaurant.name
        holder.txtRecyclerRestaurantFoodPrice.text = restaurant.cost
        holder.txtRecyclerRestaurantRating.text = restaurant.rating
        Picasso.get().load(restaurant.imageUrl).error(R.drawable.food_cart_logo).into(holder.imgRecyclerRestaurant)

        holder.llRecyclerRestaurantRow.setOnClickListener {

            val intent = Intent( context , RestaurantDetailsActivity::class.java)
            intent.putExtra("id",restaurant.id)
            intent.putExtra("name",restaurant.name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}