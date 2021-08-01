package com.example.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
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
import java.lang.Exception

class NewPasswordActivity : AppCompatActivity() {

    lateinit var relativeLayoutNewPassword: RelativeLayout
    lateinit var edtOtp: TextInputEditText
    lateinit var edtConfirmPassword: TextInputEditText
    lateinit var edtNewPassword: TextInputEditText
    lateinit var btnSubmit: Button
    lateinit var mobile_number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        relativeLayoutNewPassword = findViewById(R.id.relativeLayoutNewPassword)
        edtOtp = findViewById(R.id.edtOTP)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        if (intent!=null){
            mobile_number= intent.getStringExtra("mobile_number").toString()
        }

        btnSubmit.setOnClickListener {

            val queue =Volley.newRequestQueue(this@NewPasswordActivity)
            val params= JSONObject()
            val url = "http://13.235.250.119/v2/reset_password/fetch_result"

            var otp = edtOtp.text.toString()
            var password = edtNewPassword.text.toString()
            var confirmPassword = edtConfirmPassword.text.toString()

            if (CheckConnection().checkConnectivity(this)){
                params.put("mobile_number",mobile_number)
                params.put("password",password)
                params.put("otp",otp)

                val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, params,
                        Response.Listener {
                            if (password.length>=4 && password.equals(confirmPassword)){
                                try {

                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    val successMessage = data.getString("successMessage")

                                    if (success) {

                                        val intent = Intent(this@NewPasswordActivity,LoginActivity::class.java)
                                        startActivity(intent)
                                        Snackbar.make(relativeLayoutNewPassword,successMessage,Snackbar.LENGTH_LONG).show()
                                        finish()

                                    }else{
                                        Snackbar.make(relativeLayoutNewPassword,"Enter Correct OTP!!!",Snackbar.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(this, "some error occurred", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Snackbar.make(relativeLayoutNewPassword,"Enter correct OTP and password of minimum length 4",Snackbar.LENGTH_LONG).show()
                            }
                                          },
                        Response.ErrorListener {
                            Toast.makeText(this, "volley error occurred", Toast.LENGTH_SHORT).show()
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
                Snackbar.make(relativeLayoutNewPassword,"Check Your Connection",Snackbar.LENGTH_SHORT).show()

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
    }

    override fun onBackPressed() {
        val intent = Intent(this@NewPasswordActivity,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}