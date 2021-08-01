package com.example.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.activity.model.FoodItem
import com.example.activity.model.OrderHistory
import com.example.foodistaan.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(val context:Context , val orderHistoryList: ArrayList<OrderHistory>): RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view:View):RecyclerView.ViewHolder(view){

        val txtRestaurantName:TextView = view.findViewById(R.id.txtRestaurantName)
        val txtDate:TextView = view.findViewById(R.id.txtDate)
        val recyclerResHistoryItems:RecyclerView = view.findViewById(R.id.recyclerResHistoryItems)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_row,parent,false)

        return OrderHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {

        val orderList = orderHistoryList[position]
        holder.txtRestaurantName.text = orderList.restaurant_name
        holder.txtDate.text = formatDate(orderList.date)
        setupRecycler(holder.recyclerResHistoryItems, orderList)

    }

    private fun setupRecycler(recyclerResHistoryItems: RecyclerView, orderList: OrderHistory) {
        val orderItems = ArrayList<FoodItem>()
        for (i in 0 until orderList.food_items.length()){
            val foodJson = orderList.food_items.getJSONObject(i)
            orderItems.add(
                FoodItem(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost").toInt()
                )
            )
        }

        val orderHistoryFoodAdapter = CartAdapter(context,orderItems)
        val layoutManager = LinearLayoutManager(context)
        recyclerResHistoryItems.adapter = orderHistoryFoodAdapter
        recyclerResHistoryItems.layoutManager = layoutManager

    }


    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

}