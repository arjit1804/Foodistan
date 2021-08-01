@file:Suppress("DEPRECATION")

package com.example.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.activity.RestaurantDetailsActivity.Companion.resId
import com.example.activity.adapter.CartAdapter
import com.example.activity.adapter.RestaurantMenuRecyclerAdapter
import com.example.activity.database.CartDataBase
import com.example.activity.database.CartEntity
import com.example.activity.model.FoodItem
import com.example.foodistaan.R
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class CartActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerCartAdapter :  CartAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    private var orderList = arrayListOf<FoodItem>()
    lateinit var btnConfirmOrder : Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_register_file), Context.MODE_PRIVATE)

        toolbar = findViewById(R.id.toolbar)

        layoutManager = LinearLayoutManager(this@CartActivity)

        setupToolbar()

        cartList()

        confirmOrder()

    }
    fun setupToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun cartList(){

        recyclerView = findViewById(R.id.recyclerCart)

        val dbList = GetItemsFromDBAsync(this).execute().get()

        for (elements in dbList){
            orderList.addAll(
                Gson().fromJson(elements.foodItems,Array<FoodItem>::class.java).asList()
            )
        }

        if (orderList.isEmpty()){
            Toast.makeText(this, "orderList is empty", Toast.LENGTH_SHORT).show()
        }

            recyclerCartAdapter = CartAdapter(this@CartActivity,orderList)
            recyclerView.adapter = recyclerCartAdapter
            recyclerView.layoutManager = layoutManager
            recyclerView.itemAnimator = DefaultItemAnimator()

    }

    fun confirmOrder(){

        btnConfirmOrder = findViewById(R.id.btnConfirmOrder)

        var sum = 0

        for (i in 0 until orderList.size){
            sum += orderList[i].cost_for_one
        }

        btnConfirmOrder.text = "Place order For Rs. $sum"

        btnConfirmOrder.setOnClickListener {

            sendServerRequest()
        }
    }

    private fun sendServerRequest() {

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        val params = JSONObject()

        //calculating the order total
        var sum = 0

        for (i in 0 until orderList.size){
            sum += orderList[i].cost_for_one as Int
        }



        val foodArray = JSONArray()
        for (i in 0 until orderList.size){
            val foodId = JSONObject()
            foodId.put("food_item_id",orderList[i].id)
            foodArray.put(i,foodId)
        }

        Log.d("tag",foodArray.toString(4))

        params.put("user_id",sharedPreferences.getString("user_id",null) as String)
        params.put("restaurant_id", resId.toString())
        params.put("total_cost", sum.toString())
        params.put("food",foodArray)

        //making the post request
        val jsonRequest = object : JsonObjectRequest(
            Method.POST, url, params,
            Response.Listener {



                try {

                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {


                        //clearing the db
                        ClearDBAsync(applicationContext, resId.toString()).execute().get()
                        RestaurantMenuRecyclerAdapter.isCartEmpty = true

                        //sending to the confirmation page
                            val intent = Intent(this@CartActivity, OkOrderActivity::class.java)
                            startActivity(intent)
                            finish()

                    }
                    else {
                        Toast.makeText(this, "false", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this, "some Exception occurred", Toast.LENGTH_SHORT).show()
                }

            },
            Response.ErrorListener {

                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "a503a953d714c2"
                return headers
            }
        }
        queue.add(jsonRequest)
    }

    class GetItemsFromDBAsync( context: Context):AsyncTask<Void,Void,List<CartEntity>>(){

        val db = Room.databaseBuilder(context,CartDataBase::class.java,"cart-db").build()

        override fun doInBackground(vararg p0: Void?): List<CartEntity> {

            return db.cartDao().getAllFoodItems()

        }
    }

    class ClearDBAsync(context: Context, val resId:String):AsyncTask<Void,Void,Boolean>(){

        val db = Room.databaseBuilder(context,CartDataBase::class.java,"cart-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            db.cartDao().deleteFoodItems(resId)
            db.close()
            return true
        }

    }
//
//    override fun onSupportNavigateUp(): Boolean {
//
//        onBackPressed()
//
//        return true
//    }

    override fun onBackPressed() {



        ClearDBAsync(applicationContext, resId.toString()).execute().get()


        super.onBackPressed()

    }



}