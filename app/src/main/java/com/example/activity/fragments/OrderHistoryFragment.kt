package com.example.activity.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.activity.adapter.OrderHistoryAdapter
import com.example.activity.connection.CheckConnection
import com.example.activity.model.OrderHistory
import com.example.foodistaan.R
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var orderHistoryAdapter: OrderHistoryAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var animation: LottieAnimationView

    lateinit var allResRelativeLayout: RelativeLayout
    lateinit var progressBar: ProgressBar


    var orderHistoryList = ArrayList<OrderHistory>()
    private var user_id = ""

    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)


        allResRelativeLayout = view.findViewById(R.id.allResRelativeLayout)
        progressBar = view.findViewById(R.id.progressbar)

        allResRelativeLayout.visibility = View.VISIBLE

        animation = view.findViewById(R.id.animation)

        sharedPreferences = (activity as FragmentActivity).
                getSharedPreferences(getString(R.string.preference_register_file),Context.MODE_PRIVATE)

        layoutManager = LinearLayoutManager(activity)
        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)

        val queue = Volley.newRequestQueue(activity as Context)
        user_id = sharedPreferences.getString("user_id","user_id") as String
        val url = "http://13.235.250.119/v2/orders/fetch_result/$user_id"


        //checking connection before making the requests
        if (CheckConnection().checkConnectivity(activity as Context)){

            val jsonRequest = object: JsonObjectRequest(Request.Method.GET,url,null,
                Response.Listener {
                                 try {
                                 allResRelativeLayout.visibility = View.GONE
                                 val data = it.getJSONObject("data")
                                 val success = data.getBoolean("success")
                                 if (success){

                                     val resArray = data.getJSONArray("data")

                                     if (resArray.length()==0){
                                         animation.visibility = View.VISIBLE
                                     }
                                     else{
                                         animation.visibility = View.GONE
                                         for (i in 0 until resArray.length()){
                                             val orderObject = resArray.getJSONObject(i)
                                             val foodItems = orderObject.getJSONArray("food_items")
                                             val orderDetails = OrderHistory(
                                                 orderObject.getString("order_id"),
                                                 orderObject.getString("restaurant_name"),
                                                 orderObject.getString("order_placed_at"),
                                                 foodItems
                                             )
                                             orderHistoryList.add(orderDetails)
                                             if (orderHistoryList.isEmpty()){
                                                 animation.visibility = View.VISIBLE
                                             }else{
                                                 animation.visibility=View.GONE
                                                 orderHistoryAdapter = OrderHistoryAdapter(activity as Context , orderHistoryList)
                                                 recyclerOrderHistory.layoutManager = layoutManager
                                                 recyclerOrderHistory.adapter = orderHistoryAdapter

                                             }

                                         }

                                     }
                                 }
                                 else{
                                     Toast.makeText(activity as Context, "response error occurred", Toast.LENGTH_SHORT)
                                         .show()    
                                 }

                                 } catch (e:Exception){
                                     Toast.makeText(
                                         activity as Context,
                                         "Exception Occurred",
                                         Toast.LENGTH_SHORT
                                     ).show()

                                 }

                },
                Response.ErrorListener {
                    Toast.makeText(activity as Context, it.message, Toast.LENGTH_SHORT)
                        .show()

                })
            {
                override fun getHeaders(): MutableMap<String, String>
                {
                    val headers = HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "a503a953d714c2"
                    return headers
                }
            }

            queue.add(jsonRequest)

        }
        else{
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
            OrderHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}