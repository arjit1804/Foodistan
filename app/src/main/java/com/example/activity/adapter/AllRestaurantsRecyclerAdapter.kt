package com.example.activity.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.activity.RestaurantDetailsActivity
import com.example.activity.database.CartDataBase
import com.example.activity.database.FavouritesEntity
import com.example.activity.model.Restaurant
import com.example.foodistaan.R
import com.squareup.picasso.Picasso

class AllRestaurantsRecyclerAdapter(val context: Context , val infoList :ArrayList<Restaurant> ) :

    RecyclerView.Adapter<AllRestaurantsRecyclerAdapter.AllRestaurantsViewHolder>(){

    class AllRestaurantsViewHolder(view : View): RecyclerView.ViewHolder(view){

        val imgRecyclerRestaurant : ImageView = view.findViewById(R.id.imgRecyclerRestaurant)
        val txtRecyclerRestaurantFoodName : TextView = view.findViewById(R.id.txtRecyclerRestaurantFoodName)
        val txtRecyclerRestaurantFoodPrice : TextView = view.findViewById(R.id.txtRecyclerRestaurantFoodPrice)
        val imgRecyclerRestaurantFavFood : ImageView = view.findViewById(R.id.imgRecyclerRestaurantFavFood)
        val txtRecyclerRestaurantRating : TextView = view.findViewById(R.id.txtRecyclerRestaurantRating)
        val llRecyclerRestaurantRow : LinearLayout = view.findViewById(R.id.llRecyclerRestaurantRow)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRestaurantsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_all_restaurants_row, parent , false)

        return AllRestaurantsViewHolder(view)

    }

    override fun onBindViewHolder(holder: AllRestaurantsViewHolder, position: Int) {

        val restaurant = infoList[position]
        holder.txtRecyclerRestaurantFoodName.text = restaurant.name
        holder.txtRecyclerRestaurantFoodPrice.text = restaurant.cost_for_one
        holder.txtRecyclerRestaurantRating.text = restaurant.rating
        Picasso.get().load(restaurant.image_url).into(holder.imgRecyclerRestaurant)

        holder.llRecyclerRestaurantRow.setOnClickListener {

            val intent = Intent( context , RestaurantDetailsActivity::class.java)
            intent.putExtra("id",restaurant.id)
            intent.putExtra("name",restaurant.name)
            context.startActivity(intent)
        }


        //doing this to see which restaurant is in favourites list and which isn't
        val listOfFavourites = GetAllFavAsync(context).execute().get()

        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(restaurant.id.toString())){

            holder.imgRecyclerRestaurantFavFood.setImageResource(R.drawable.colour_heart)

        }
        else{

            holder.imgRecyclerRestaurantFavFood.setImageResource(R.drawable.uncolour_heart)

        }


        holder.imgRecyclerRestaurantFavFood.setOnClickListener {
            val favouritesEntity = FavouritesEntity(
                restaurant.id,
                restaurant.name,
                restaurant.cost_for_one,
                restaurant.rating,
                restaurant.image_url
            )

            if (!DBAsyncTask(context,favouritesEntity,1).execute().get()){

                val async = DBAsyncTask(context,favouritesEntity,2).execute()
                val result = async.get()
                if (result){
                    holder.imgRecyclerRestaurantFavFood.setImageResource(R.drawable.colour_heart)
                }
            }
            else{
                val async = DBAsyncTask(context,favouritesEntity,3).execute()
                val result = async.get()

                if (result){
                    holder.imgRecyclerRestaurantFavFood.setImageResource(R.drawable.uncolour_heart)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    class DBAsyncTask(val context: Context,val favouritesEntity: FavouritesEntity,val mode:Int):
        AsyncTask<Void,Void,Boolean>(){

        val db = Room.databaseBuilder(context,CartDataBase::class.java,"cart-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {

            when(mode)
            {
                1 -> {
                    val res: FavouritesEntity? =  db.favouritesDao().getRestaurantById(favouritesEntity.id.toString())
                    db.close()
                    return res!=null
                }

                2 -> {
                    db.favouritesDao().insertFavRestaurant(favouritesEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.favouritesDao().deleteFavRestaurant(favouritesEntity)
                    db.close()
                    return true
                }
            }

            return false

        }

    }

    class GetAllFavAsync(context:Context):AsyncTask<Void,Void,List<String>>(){

        val db = Room.databaseBuilder(context,CartDataBase::class.java,"cart-db").build()

        override fun doInBackground(vararg p0: Void?): List<String> {

            val list = db.favouritesDao().getAllFavRestaurants()
            val listOfId = arrayListOf<String>()
            for (i in list){
                listOfId.add(i.id.toString())

            }

            return listOfId

        }

    }

}