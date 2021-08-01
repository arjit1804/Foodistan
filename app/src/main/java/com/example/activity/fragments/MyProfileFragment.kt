package com.example.activity.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.foodistaan.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var txtNameFrag :TextView
    lateinit var txtEmailFrag :TextView
    lateinit var txtMobileNumFrag :TextView
    lateinit var txtAddressFrag :TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        sharedPreferences = (activity as FragmentActivity).getSharedPreferences(getString(R.string.preference_register_file),Context.MODE_PRIVATE)

        txtNameFrag = view.findViewById(R.id.txtNameFrag)
        txtEmailFrag=view.findViewById(R.id.txtEmailFrag)
        txtAddressFrag=view.findViewById(R.id.txtAddressFrag)
        txtMobileNumFrag=view.findViewById(R.id.txtMobileNumFrag)

        linearLayout = view.findViewById(R.id.profileLinearLayout)

        var name = txtNameFrag.text
        var email = txtEmailFrag.text
        var address = txtAddressFrag.text
        var number = txtMobileNumFrag.text

        name = sharedPreferences.getString("name","no name")
        email = sharedPreferences.getString("email","no email")
        address = sharedPreferences.getString("address","no address")
        number = sharedPreferences.getString("mobile_number","no number")



        txtNameFrag.text = name
        txtEmailFrag.text = email
        txtAddressFrag.text = address
        txtMobileNumFrag.text = number



        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MyProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }



}