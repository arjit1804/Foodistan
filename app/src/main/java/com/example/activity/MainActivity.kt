package com.example.activity
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu

import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.activity.fragments.*
import com.example.foodistaan.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtUserName : TextView
    lateinit var txtUserMobNum : TextView

    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_register_file),Context.MODE_PRIVATE)

        txtUserName = findViewById(R.id.txtUserName)
        txtUserMobNum = findViewById(R.id.txtUserMobileNum)

        txtUserName.text = sharedPreferences.getString("name","name")
        txtUserMobNum.text = sharedPreferences.getString("mobile_number","1234567890")




        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)


        setupToolbar()
        inHome()

        //setting the hamBurger icon
        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        //adding the click Listeners on the menu item of the navigation view
        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem!=null){
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){
                R.id.homes -> {
                    inHome()
                   drawerLayout.closeDrawers()


                }
                R.id.myProfile -> {

                    supportFragmentManager.beginTransaction()
                            .replace(R.id.frameLayout , MyProfileFragment())
                            .commit()

                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()

                }
                R.id.favouriteRestaurant -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout,FavouritesFragment())
                        .commit()
                    supportActionBar?.title = "Favourite Restaurant"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout,OrderHistoryFragment())
                        .commit()

                    supportActionBar?.title="My Previous Orders"
                    drawerLayout.closeDrawers()

                }
                R.id.faqs -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout,FAQFragment())
                        .commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()

                }
                R.id.logOut -> {

                    //signing out the user
                    var builder = MaterialAlertDialogBuilder(this@MainActivity)

                        builder.setTitle("LOG OUT!!!")
                        .setMessage("Are You Sure You Want To Log Out")
                        .setNeutralButton("CANCEL") { dialog, which ->
                            // Respond to neutral button press
                        }
                        .setNegativeButton("NO") { dialog, which ->
                            // Respond to negative button press
                        }
                        .setPositiveButton("YES") { dialog, which ->
                            // Respond to positive button press
                            val intent = Intent(this@MainActivity,LoginActivity::class.java)
                            sharedPreferences.edit().clear().apply()
                            startActivity(intent)
                            finish()
                        }
                        .setCancelable(false)
                        .show()

                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setupToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //on clicking the hamBurger icon navigation view slides up
        val id = item.itemId


        if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    fun inHome(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout , AllRestaurantsFragments())
            .commit()

        supportActionBar?.title = "All Restaurants"

        navigationView.setCheckedItem(R.id.homes)
        drawerLayout.closeDrawers()


    }



    override fun onBackPressed() {

        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)

        when (frag)
        {

            !is AllRestaurantsFragments -> inHome()

            else ->  ActivityCompat.finishAffinity(this@MainActivity)

        }


    }

}