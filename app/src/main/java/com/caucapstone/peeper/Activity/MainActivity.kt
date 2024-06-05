package com.caucapstone.peeper.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caucapstone.peeper.R
import com.caucapstone.peeper.Service.BluetoothService
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnStart = findViewById<MaterialButton>(R.id.main_btn_start)
        val btnStop = findViewById<MaterialButton>(R.id.main_btn_stop)
        btnStart.setOnClickListener(btnListener)
        btnStop.setOnClickListener(btnListener)
    }

    private val btnListener = View.OnClickListener { btn ->
        when(btn.id) {
            R.id.main_btn_start -> startService(Intent(this, BluetoothService::class.java))
            R.id.main_btn_stop -> stopService(Intent(this, BluetoothService::class.java))
        }
    }
}