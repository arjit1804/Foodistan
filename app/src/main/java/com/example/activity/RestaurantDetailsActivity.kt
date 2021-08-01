package com.example.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.activity.adapter.RestaurantMenuRecyclerAdapter
import com.example.activity.connection.CheckConnection
import com.example.activity.database.CartDataBase
import com.example.activity.database.CartEntity
import com.example.activity.model.FoodItem
import com.example.foodistaan.R
import com.google.gson.Gson
import java.lang.Exception

class RestaurantDetailsActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    lateinit var recyclerMenu : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantMenuRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences



    private var menuList = arrayListOf<FoodItem>()

    var orderList = arrayListOf<FoodItem>()

    //for recycler button clicks
    companion object{
        @SuppressLint("StaticFieldLeak")

        lateinit var goToCart : Button

        var resId : Int? = 0
        var resName : String? = ""


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)


        goToCart = findViewById<Button>(R.id.btnGoToCart)
        goToCart.visibility = View.GONE

        sharedPreferences = getSharedPreferences(getString(R.string.preference_register_file),Context.MODE_PRIVATE)

        toolbar = findViewById(R.id.toolbar)
        recyclerMenu = findViewById(R.id.recyclerMenu)
        layoutManager = LinearLayoutManager(this)

        if (intent!=null){
            resId = intent.getIntExtra("id",0)
            resName = intent.getStringExtra("name")
            setupToolbar()
        }else{
            supportActionBar?.title = "Menu"
        }

        goToCart.setOnClickListener {
            proceedToCart()
        }


        val queue = Volley.newRequestQueue(this@RestaurantDetailsActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/${resId}"

        if (CheckConnection().checkConnectivity(this@RestaurantDetailsActivity)){

          val jsonRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                  Response.Listener {

                      try {
                          val data = it.getJSONObject("data")
                          val success = data.getBoolean("success")

                          if (success) {
                              val dataArray = data.getJSONArray("data")
                              for (i in 0 until dataArray.length()) {
                                  val restaurantMenuJsonObject = dataArray.getJSONObject(i)
                                  val foodItem = FoodItem(
                                          restaurantMenuJsonObject.getString("id"),
                                          restaurantMenuJsonObject.getString("name"),
                                          restaurantMenuJsonObject.getString("cost_for_one").toInt()
                                  )
                                  menuList.add(foodItem)

                                  //for recycler button clicks
                                  recyclerAdapter = RestaurantMenuRecyclerAdapter(this@RestaurantDetailsActivity, menuList,
                                      object :RestaurantMenuRecyclerAdapter.OnItemClickListener{

                                          //for recycler button clicks
                                          override fun onAddItemClick(foodItem: FoodItem) {
                                              orderList.add(foodItem)

                                              if (orderList.size > 0) {
                                                  goToCart.visibility = View.VISIBLE
                                                  RestaurantMenuRecyclerAdapter.isCartEmpty = false
                                              }
                                          }

                                          //for recycler button clicks
                                          override fun onRemoveItemClick(foodItem: FoodItem) {
                                              orderList.remove(foodItem)
                                              if (orderList.isEmpty()) {
                                                  goToCart.visibility = View.GONE
                                                  RestaurantMenuRecyclerAdapter.isCartEmpty = true
                                              }
                                          }
                                      })

                                  recyclerMenu.adapter = recyclerAdapter
                                  recyclerMenu.layoutManager = layoutManager

                              }
                          }
                          else {
                              Toast.makeText(this, "some error occured", Toast.LENGTH_SHORT).show()
                          }
                      }

                      catch (e: Exception) {
                          Toast.makeText(this, "some Exception occurred", Toast.LENGTH_SHORT).show()
                      }

                  },
                  Response.ErrorListener {

                      Toast.makeText(this, "volley error occurredd", Toast.LENGTH_SHORT).show()
                  }
          )
          {
              override fun getHeaders(): MutableMap<String, String> {
                  val headers = HashMap<String,String>()
                  headers["Content-type"] = "application/json"
                  headers["token"] = "a503a953d714c2"
                  return headers
              }
          }
            queue.add(jsonRequest)

        }else{
            Toast.makeText(this@RestaurantDetailsActivity, "internet not available", Toast.LENGTH_SHORT).show()
        }

    }

    private fun proceedToCart() {

        val gson = Gson()

        val foodItems = gson.toJson(orderList)

        val async = ItemsOfCart(this@RestaurantDetailsActivity, resId, foodItems, 1).execute()
        val result = async.get()
        if (result) {
            val data = Bundle()
            data.putString("resId", resId.toString())
            data.putString("resName", resName as String)
            val intent = Intent(this@RestaurantDetailsActivity, CartActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
            //finish()
        } else {
            Toast.makeText(this@RestaurantDetailsActivity, "Some unexpected error", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun setupToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = resName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home){
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }


    //context is given to know from which part of the activity/fragments the request are appealing
    class ItemsOfCart(val context: Context, val restaurantId: Int?, val foodItems:String, val mode: Int) : AsyncTask<Void,Void,Boolean>(){

        val db = Room.databaseBuilder(context, CartDataBase::class.java , "cart-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode){
                1 -> {
                    //adding the items
                    db.cartDao().InsertFoodItem(CartEntity(restaurantId,foodItems))
                    db.close()
                    return true
                }

                2-> {
                    //deleting the items
                    db.cartDao().DeleteFoodItem(CartEntity(restaurantId,foodItems))
                    db.close()
                    return true

                     }
            }

            return false

        }
    }

}