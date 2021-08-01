package com.example.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.foodistaan.R

class OkOrderActivity : AppCompatActivity() {

    lateinit var btnOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ok_order)

        btnOk = findViewById(R.id.btnOk)

        btnOk.setOnClickListener {
            val intent = Intent(this@OkOrderActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this@OkOrderActivity,MainActivity::class.java)
        startActivity(intent)
        finish()

    }
}