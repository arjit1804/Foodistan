package com.example.activity.fragments

import android.app.Activity
import android.app.AlertDialog

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.activity.adapter.AllRestaurantsRecyclerAdapter
import com.example.activity.connection.CheckConnection
import com.example.activity.model.Restaurant
import com.example.foodistaan.R
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap
import kotlin.math.cos

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AllRestaurantsFragments : androidx.fragment.app.Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var allRestaurantFrame : FrameLayout

    lateinit var recyclerAllRestaurants : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter: AllRestaurantsRecyclerAdapter

    lateinit var allResRelativeLayout:RelativeLayout
    lateinit var progressBar: ProgressBar

    val restaurantList = arrayListOf<Restaurant>()

    val costComparator = Comparator<Restaurant>{res1,res2 ->
        res1.cost_for_one.compareTo(res2.cost_for_one,true)
    }

    val ratingComparator = Comparator<Restaurant>{res1,res2 ->
        res1.rating.compareTo(res2.rating,true)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_all_restaurants_fragments, container, false)


        setHasOptionsMenu(true)

        recyclerAllRestaurants = view.findViewById(R.id.recyclerAllRestaurants)
        layoutManager = LinearLayoutManager(activity)
        allRestaurantFrame = view.findViewById(R.id.allRestaurantFrame)

        allResRelativeLayout = view.findViewById(R.id.allResRelativeLayout)
        progressBar = view.findViewById(R.id.progressbar)

        allResRelativeLayout.visibility = View.VISIBLE


        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (CheckConnection().checkConnectivity(activity as Context)){

            try {
                val jsonRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                        Response.Listener {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            allResRelativeLayout.visibility = View.GONE

                            if (success) {
                                val dataArray = data.getJSONArray("data")

                                for (i in 0 until dataArray.length()) {
                                    val restaurantJsonObject = dataArray.getJSONObject(i)
                                    val restaurantObject = Restaurant(
                                            restaurantJsonObject.getString("id").toInt(),
                                            restaurantJsonObject.getString("name"),
                                            restaurantJsonObject.getString("rating"),
                                            restaurantJsonObject.getString("cost_for_one"),
                                            restaurantJsonObject.getString("image_url")
                                    )
                                    restaurantList.add(restaurantObject)
                                    recyclerAdapter =
                                            AllRestaurantsRecyclerAdapter(activity as Context, restaurantList)
                                    recyclerAllRestaurants.adapter = recyclerAdapter
                                    recyclerAllRestaurants.layoutManager = layoutManager
                                }
                            } else {
                                Toast.makeText(activity, "some error occurred", Toast.LENGTH_SHORT).show()
                            }
                        },
                        Response.ErrorListener {

                            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT)
                                    .show()

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

            }catch (e:Exception){
                Toast.makeText(activity, "some exception occurred", Toast.LENGTH_SHORT).show()
            }

        }else{

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Open Settings"){text , listener ->
                //opening settings to make the connection available
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()

            }
            dialog.setNegativeButton("Exit"){text,listener ->
                //exiting the app
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }



        return view
    }




    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllRestaurantsFragments().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id){
            R.id.highToLow -> {
                Collections.sort(restaurantList,costComparator)
                restaurantList.reverse()
            }
            R.id.lowToHigh -> {
                Collections.sort(restaurantList, costComparator)
            }
            R.id.rating -> {
                Collections.sort(restaurantList,ratingComparator)
                restaurantList.reverse()
            }
        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}