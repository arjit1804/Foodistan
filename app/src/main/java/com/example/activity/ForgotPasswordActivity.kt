package com.example.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
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


class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var btnNext : Button
    lateinit var edtMobileNum : TextInputEditText
    lateinit var edtEmailAddress : TextInputEditText
    lateinit var linearLayoutForgotPassword : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        btnNext = findViewById(R.id.btnNext)
        edtMobileNum = findViewById(R.id.edtMobileNum)
        edtEmailAddress = findViewById(R.id.edtEmailAddress)
        linearLayoutForgotPassword = findViewById(R.id.linearLayoutForgotPassword)

        val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        val params = JSONObject()

        btnNext.setOnClickListener{

            if (CheckConnection().checkConnectivity(this)){


                var mobile_number = edtMobileNum.text.toString()
                var email = edtEmailAddress.text.toString()

                params.put("mobile_number",mobile_number)
                params.put("email",email)

                val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, params,
                        Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")

                                if (success) {
                                    val intent = Intent(this@ForgotPasswordActivity, NewPasswordActivity::class.java)
                                    intent.putExtra("mobile_number", mobile_number)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Snackbar.make(linearLayoutForgotPassword, "Please enter Registered Details", Snackbar.LENGTH_LONG).show()
                                }

                            } catch (e: Exception) {
                                Toast.makeText(this, "some error occurred", Toast.LENGTH_SHORT).show()
                            }

                        },
                        Response.ErrorListener {

                            Toast.makeText(this@ForgotPasswordActivity, "volley error occurred", Toast.LENGTH_SHORT).show()

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


            }else{
                Snackbar.make(linearLayoutForgotPassword,"Check Your Connection",Snackbar.LENGTH_SHORT).show()
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
        val intent = Intent(this@ForgotPasswordActivity,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}