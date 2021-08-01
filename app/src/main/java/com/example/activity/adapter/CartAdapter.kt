package com.example.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.activity.database.CartEntity
import com.example.activity.model.FoodItem
import com.example.foodistaan.R

class CartAdapter(val context: Context, val cartList: ArrayList<FoodItem>): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val txtFoodItem : TextView = view.findViewById(R.id.txtFoodItem)
        val txtFoodCost : TextView = view.findViewById(R.id.txtFoodCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_food_orders_row, parent ,false)

        return CartViewHolder(view)
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartList = cartList[position]
        holder.txtFoodItem.text = cartList.name
        holder.txtFoodCost.text = "Rs. ${cartList.cost_for_one.toString()}"
    }

    override fun getItemCount(): Int {
        return cartList.size
    }
}