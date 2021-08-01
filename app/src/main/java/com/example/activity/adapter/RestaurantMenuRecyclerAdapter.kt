package com.example.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.activity.model.FoodItem
import com.example.foodistaan.R

class RestaurantMenuRecyclerAdapter(val context : Context, val menuList: ArrayList<FoodItem>, private val listener : OnItemClickListener) :
        RecyclerView.Adapter<RestaurantMenuRecyclerAdapter.RestaurantDetailsViewHolder>(){

    //for clickListener
    companion object{
        var isCartEmpty = true
    }


    class RestaurantDetailsViewHolder(view: View):RecyclerView.ViewHolder(view){

        val txtMenuFoodName : TextView = view.findViewById(R.id.txtMenuFoodName)
        val txtPrice : TextView = view.findViewById(R.id.txtPrice)
        val btnAddToCart : Button = view.findViewById(R.id.btnAddToCart)
        val btnRemoveFromCart : Button = view.findViewById(R.id.btnRemoveFromCart)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantDetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurant_menu_rows,parent,false)

        return RestaurantDetailsViewHolder(view)
    }

    //for clickListener
    interface OnItemClickListener{
        fun onAddItemClick(foodItem: FoodItem)
        fun onRemoveItemClick(foodItem: FoodItem)
    }

    override fun onBindViewHolder(holder: RestaurantDetailsViewHolder, position: Int) {
        val menuObject = menuList[position]
        holder.txtMenuFoodName.text = menuObject.name
        holder.txtPrice.text = menuObject.cost_for_one.toString()

        holder.btnAddToCart.setOnClickListener {
            holder.btnRemoveFromCart.visibility = View.VISIBLE
            holder.btnAddToCart.visibility =View.GONE

            //for clickListener
            listener.onAddItemClick(menuObject)
        }

        holder.btnRemoveFromCart.setOnClickListener {
            holder.btnAddToCart.visibility = View.VISIBLE
            holder.btnRemoveFromCart.visibility = View.GONE

            //for clickListener
            listener.onRemoveItemClick(menuObject)
        }


    }

    override fun getItemCount(): Int {
        return menuList.size
    }


    //for clickListener
    override fun getItemViewType(position: Int): Int {
        return position
    }
}