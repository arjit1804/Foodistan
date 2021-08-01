package com.example.activity.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.activity.adapter.FaqAdapter
import com.example.activity.connection.CheckConnection
import com.example.activity.model.Faqs
import com.example.foodistaan.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FAQFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var allResRelativeLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var faqRecycler: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var faqAdapter:FaqAdapter

    val faqList = arrayListOf<Faqs>(
        Faqs("Describe your project?","This is a fully functional food cart app in which we can order the foods from different different " +
                "restaurants."),
        Faqs("What other features are in the app?","You can add or remove the items while ordering and after placing the orders " +
                "the orders will be saved in your previous order history page where you can see all your orders."),
        Faqs("Is there any functionality so that user can save their favourites restaurant in app somewhere?",
        "Yes, we provided the favourites section so that when you press on the heart button the restaurant will add in your favourites list " +
                "which you can access from the navigation view."),
        Faqs("What did you used in your app to save all the things?","We used sharedpreferences and Room persistence Library to save some" +
                " small data in your local database for your benefits."),
        Faqs("What other things did you used to make app look good?","We used material designs so that we can give the styles some view" +
                ", and also we used a LOTTIE ANIMATIONS.")
    )

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
        val view = inflater.inflate(R.layout.fragment_f_a_q, container, false)

        faqRecycler = view.findViewById(R.id.faqRecycler)
        layoutManager = LinearLayoutManager(activity)

        allResRelativeLayout = view.findViewById(R.id.allResRelativeLayout)
        progressBar = view.findViewById(R.id.progressbar)

        allResRelativeLayout.visibility = View.VISIBLE

        if (CheckConnection().checkConnectivity(activity as Context)){
            allResRelativeLayout.visibility = View.GONE

            faqAdapter = FaqAdapter(activity as Context,faqList)
            faqRecycler.adapter = faqAdapter
            faqRecycler.layoutManager = layoutManager

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
            FAQFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}