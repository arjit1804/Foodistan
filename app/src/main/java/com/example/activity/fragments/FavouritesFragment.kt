package com.example.activity.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.airbnb.lottie.LottieAnimationView
import com.example.activity.adapter.FavouriteAdapter
import com.example.activity.connection.CheckConnection
import com.example.activity.database.CartDataBase
import com.example.activity.database.FavouritesEntity
import com.example.foodistaan.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FavouritesFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerFavorite:RecyclerView
    lateinit var favouriteAdapter: FavouriteAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var animation: LottieAnimationView
    lateinit var txtNoFav:TextView


    lateinit var allResRelativeLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var dbFavRestaurantList = listOf<FavouritesEntity>()

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
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        allResRelativeLayout = view.findViewById(R.id.allResRelativeLayout)
        progressBar = view.findViewById(R.id.progressbar)

        allResRelativeLayout.visibility = View.VISIBLE


        animation = view.findViewById(R.id.favAnimation)
        txtNoFav = view.findViewById(R.id.txtNoFav)

        recyclerFavorite = view.findViewById(R.id.recyclerFavourite)
        layoutManager = LinearLayoutManager(activity)


        val checkingList = RetrieveFavouriteRestaurant(activity as Context).execute().get()

        if(CheckConnection().checkConnectivity(activity as Context)){
            allResRelativeLayout.visibility = View.GONE

            if (checkingList.isEmpty()){
                animation.visibility =View.VISIBLE
                txtNoFav.visibility =View.VISIBLE

            }else{

                animation.visibility =View.GONE
                txtNoFav.visibility =View.GONE

                dbFavRestaurantList = RetrieveFavouriteRestaurant(activity as Context).execute().get()

                favouriteAdapter = FavouriteAdapter(activity as Context , dbFavRestaurantList)
                recyclerFavorite.adapter = favouriteAdapter
                recyclerFavorite.layoutManager = layoutManager

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

    class RetrieveFavouriteRestaurant(val context: Context): AsyncTask<Void,Void,List<FavouritesEntity>>(){

        override fun doInBackground(vararg p0: Void?): List<FavouritesEntity> {


            val db = Room.databaseBuilder(context,CartDataBase::class.java,"cart-db").build()

            return db.favouritesDao().getAllFavRestaurants()


        }

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}