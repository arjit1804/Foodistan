package com.example.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.activity.connection.CheckConnection
import com.example.foodistaan.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import kotlin.toString as toString

class RegisterActivity : AppCompatActivity() {

    lateinit var edtRegisterName : TextInputEditText
    lateinit var edtRegisterEmailAddress : TextInputEditText
    lateinit var edtRegisterMobileNum : TextInputEditText
    lateinit var edtRegisterDeliveryAddress : TextInputEditText
    lateinit var edtRegisterPassword : TextInputEditText
    lateinit var edtRegisterConfirmPassword : TextInputEditText
    lateinit var btnRegister : Button
    lateinit var txtInvalidName : TextView
    lateinit var txtInvalidNumber : TextView
    lateinit var txtInvalidPassword : TextView
    lateinit var txtInvalidConformation : TextView
    lateinit var txtInvalidEmail : TextView
    lateinit var txtInvalidLocation : TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var relativeLayout: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //for saving the information given by the user in the local device
        sharedPreferences = getSharedPreferences(getString(R.string.preference_register_file), Context.MODE_PRIVATE)

        edtRegisterName = findViewById(R.id.edtRegisterName)
        edtRegisterEmailAddress = findViewById(R.id.edtRegisterEmailAddress)
        edtRegisterMobileNum = findViewById(R.id.edtRegisterMobileNum)
        edtRegisterDeliveryAddress = findViewById(R.id.edtRegisterDeliveryAddress)
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword)
        edtRegisterConfirmPassword = findViewById(R.id.edtRegisterConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        txtInvalidName = findViewById(R.id.txtInvalidName)
        txtInvalidName.visibility = View.GONE
        txtInvalidNumber = findViewById(R.id.txtInvalidNumber)
        txtInvalidNumber.visibility = View.GONE
        txtInvalidPassword = findViewById(R.id.txtInvalidPassword)
        txtInvalidPassword.visibility = View.GONE
        txtInvalidConformation = findViewById(R.id.txtIncorrectConfirmation)
        txtInvalidConformation.visibility = View.GONE
        txtInvalidEmail = findViewById(R.id.txtInvalidEmail)
        txtInvalidEmail.visibility = View.GONE
        txtInvalidLocation = findViewById(R.id.txtInvalidLocation)
        txtInvalidLocation.visibility = View.GONE

        relativeLayout = findViewById(R.id.relativeLayout)




        btnRegister.setOnClickListener {

            var name = edtRegisterName.text.toString()
            var email = edtRegisterEmailAddress.text.toString()
            var mobileNumber = edtRegisterMobileNum.text.toString()
            var deliveryAddress = edtRegisterDeliveryAddress.text.toString()
            var password = edtRegisterPassword.text.toString()
            var confirmPassword = edtRegisterConfirmPassword.text.toString()

            val queue = Volley.newRequestQueue(this@RegisterActivity)
            val url = "http://13.235.250.119/v2/register/fetch_result"
            val params = JSONObject()

            if (CheckConnection().checkConnectivity(this)){
                //internet is available
                if(name.isNotEmpty() && name.length>=3 && email.isNotEmpty() && mobileNumber.isNotEmpty() && mobileNumber.length==10
                        && deliveryAddress.isNotEmpty() && password.isNotEmpty() && password.length>=4 && confirmPassword.equals(password)){

                    //Toast.makeText(this@RegisterActivity, "true", Toast.LENGTH_SHORT).show()
                    checkInfo(name,email,mobileNumber,deliveryAddress,password,confirmPassword)


                    params.put("name",name)
                    params.put("mobile_number",mobileNumber)
                    params.put("password",password)
                    params.put("address",deliveryAddress)
                    params.put("email",email)

                    val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, params,
                            //to make this request happen we also have to make a http request

                            Response.Listener {
                                //here responses are handled
                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    if (success) {
                                        Toast.makeText(this@RegisterActivity, "registeration done", Toast.LENGTH_SHORT).show()

                                        var values = data.getJSONObject("data")

                                        sharedPreferences.edit().putString("user_id",values.getString("user_id")).apply()
                                        sharedPreferences.edit().putString("name",values.getString("name")).apply()
                                        sharedPreferences.edit().putString("mobile_number",values.getString("mobile_number")).apply()
                                        sharedPreferences.edit().putString("email",values.getString("email")).apply()
                                        sharedPreferences.edit().putString("address",values.getString("address")).apply()
//

                                        val intent = Intent(this@RegisterActivity,MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this@RegisterActivity, "not done", Toast.LENGTH_SHORT).show()
                                    }

                                } catch (e: Exception) {
                                    Toast.makeText(this@RegisterActivity, "some exception occurred", Toast.LENGTH_SHORT).show()
                                    println(e)

                                }

                            },

                            Response.ErrorListener {
                                Toast.makeText(this@RegisterActivity, "volley error occurred", Toast.LENGTH_SHORT).show()

                            })

                    //sending the headers to get the data
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
                    checkInfo(name, email, mobileNumber, deliveryAddress, password, confirmPassword)
                }
            }
            else{
                //internet is not available
                Snackbar.make(relativeLayout, "Internet is Not Available", Snackbar.LENGTH_LONG)
                        .show()
                val dialog = android.app.AlertDialog.Builder(this)
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
    }

    fun checkInfo(name: String, email: String, mobileNumber: String, deliveryAddress: String, password: String, confirmPassword: String) {
        if (name.length<3 || name.isEmpty()){
            txtInvalidName.visibility=View.VISIBLE
        }else{
            txtInvalidName.visibility=View.GONE
        }
        if (email.isEmpty()){
            txtInvalidEmail.visibility=View.VISIBLE
        }else{
            txtInvalidEmail.visibility=View.GONE
        }
        if (mobileNumber.length!=10 || mobileNumber.isEmpty()){
            txtInvalidNumber.visibility=View.VISIBLE
        }else{
            txtInvalidNumber.visibility=View.GONE
        }
        if (deliveryAddress.isEmpty()){
            txtInvalidLocation.visibility=View.VISIBLE
        }else{
            txtInvalidLocation.visibility=View.GONE
        }
        if (password.length<4 || password.isEmpty()){
            txtInvalidPassword.visibility=View.VISIBLE
        }else{
            txtInvalidPassword.visibility=View.GONE
        }
        if (!(confirmPassword.equals(password))){
            txtInvalidConformation.visibility=View.VISIBLE
        }else{
            txtInvalidConformation.visibility=View.GONE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@RegisterActivity,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}