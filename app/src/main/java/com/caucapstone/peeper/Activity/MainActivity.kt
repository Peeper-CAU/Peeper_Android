package com.caucapstone.peeper.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caucapstone.peeper.R
import com.caucapstone.peeper.Service.BluetoothService
import com.caucapstone.peeper.Util.FileUtil
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
        val btnTestFile = findViewById<MaterialButton>(R.id.main_btn_test_file)
        val btnTestUpload = findViewById<MaterialButton>(R.id.main_btn_test_upload)
        btnStart.setOnClickListener(btnListener)
        btnStop.setOnClickListener(btnListener)
        btnTestFile.setOnClickListener(btnListener)
        btnTestUpload.setOnClickListener(btnListener)
    }

    private val btnListener = View.OnClickListener { btn ->
        when(btn.id) {
            R.id.main_btn_start -> startService(Intent(this, BluetoothService::class.java))
            R.id.main_btn_stop -> stopService(Intent(this, BluetoothService::class.java))
            R.id.main_btn_test_file -> testFile()
            R.id.main_btn_test_upload -> testUpload()
        }
    }

    private fun testFile() {
        val testBytes = byteArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1)
        val testUID = "TEST_UID"
        Log.d("Util Test", "File Test Started!!")
        Log.d("Util Test", "Initializing File!!")
        FileUtil.initFile(testUID, applicationContext)
        Log.d("Util Test", "Initialized File!!")

        Log.d("Util Test", "Writing Bytes to File!!")
        FileUtil.appendData(testBytes)
        Log.d("Util Test", "Wrote to File!!")

        Log.d("Util Test", "Closing File!!")
        FileUtil.closeFile(testUID)
        Log.d("Util Test", "Closed File!!")
        Log.d("Util Test", "File Test Done!!")
    }

    private fun testUpload() {

    }
}