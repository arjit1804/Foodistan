package com.example.activity.model

import org.json.JSONArray

data class OrderHistory(
    val order_id: String,
    val restaurant_name:String,
    val date:String,
    val food_items: JSONArray
)