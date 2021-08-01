package com.example.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import android.widget.Toast.makeText
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.activity.connection.CheckConnection
import com.example.foodistaan.R
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.lang.Exception
import java.lang.RuntimeException

class LoginActivity : AppCompatActivity() {

    lateinit var txtForgotPassword : TextView
    lateinit var txtSignUp : TextView
    lateinit var edtPhoneNumber : EditText
    lateinit var edtPassword : EditText
    lateinit var btnLogin : Button
    lateinit var loginRelativeLayout: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_register_file), Context.MODE_PRIVATE)

        //if user is logged in then they don't get to login again they will logged in until they themselves logged them out from the app
        isLoggedIn()

        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtSignUp = findViewById(R.id.txtSignUp)

        loginRelativeLayout = findViewById(R.id.loginRelativeLayout)


        btnLogin.setOnClickListener {

            var phoneNumber = edtPhoneNumber.text.toString()
            var password = edtPassword.text.toString()


            val queue = Volley.newRequestQueue(this@LoginActivity)
            val url = "http://13.235.250.119/v2/login/fetch_result"
            val params = JSONObject()

            if (CheckConnection().checkConnectivity(this)){
                //Internet is available
                params.put("mobile_number",phoneNumber)
                params.put("password",password)


                val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, params,
                        Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")

                                if (success){

                                   var values = data.getJSONObject("data")

                                        sharedPreferences.edit().putString("user_id",values.getString("user_id")).apply()
                                        sharedPreferences.edit().putString("name",values.getString("name")).apply()
                                        sharedPreferences.edit().putString("mobile_number",values.getString("mobile_number")).apply()
                                        sharedPreferences.edit().putString("email",values.getString("email")).apply()
                                        sharedPreferences.edit().putString("address",values.getString("address")).apply()

                                        //when user is logged in
                                        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()

                                    val intent = Intent(this@LoginActivity , MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    Snackbar.make(loginRelativeLayout,"Incorrect Credentials",Snackbar.LENGTH_LONG).show()
                                }

                            } catch (e: Exception) {
                                Toast.makeText(this@LoginActivity,"some error occurred", Toast.LENGTH_SHORT).show()
                            }

                        },
                        Response.ErrorListener {
                            makeText(this@LoginActivity, "Volley Error Occurred", Toast.LENGTH_SHORT).show()
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

            else{
                //Internet is not Available
                Snackbar.make(btnLogin,"Check Your Connection",Snackbar.LENGTH_SHORT).show()

                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")
                dialog.setCancelable(false)
                dialog.setPositiveButton("Open Settings"){text , listener ->
                    //opening settings to make the connection available
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()

                }
                dialog.setNegativeButton("Exit"){text,listener ->
                    //exiting the app
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()
            }
        }

        txtForgotPassword.setOnClickListener{
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
        txtSignUp.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun isLoggedIn(){
        var checkLogin = sharedPreferences.getBoolean("isLoggedIn",false)
        if (checkLogin){
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}