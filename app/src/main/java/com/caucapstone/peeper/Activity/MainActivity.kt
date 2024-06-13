package com.caucapstone.peeper.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caucapstone.peeper.R
import com.caucapstone.peeper.Util.FileUtil
import com.caucapstone.peeper.Util.UploadUtil
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private var fileName = ""
    private var isRecording = false

    val testUID = "TEST_UID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermissions()) {
                requestBluetoothPermissions.launch(permissionArray)
            }
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
            R.id.main_btn_start -> startRecord()
            R.id.main_btn_stop -> stopRecord()
            R.id.main_btn_test_file -> testFile()
            R.id.main_btn_test_upload -> testUpload()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecord() {
        val recordChannel = AudioFormat.CHANNEL_IN_MONO
        val recordEncoding = AudioFormat.ENCODING_PCM_8BIT
        val recordSampleRate = 44100

        val recordByteSize = AudioRecord.getMinBufferSize(recordSampleRate, recordChannel, recordEncoding)
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            recordSampleRate,
            recordChannel,
            recordEncoding,
            recordByteSize
        )

        FileUtil.initFile(testUID, applicationContext)

        val audioBuffer = ByteArray(recordByteSize)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                audioRecord.startRecording()
                isRecording = true
                while(isRecording) {
                    val readResult = audioRecord.read(audioBuffer, 0, recordByteSize)
                    if(readResult == AudioRecord.ERROR_INVALID_OPERATION ||
                        readResult == AudioRecord.ERROR_BAD_VALUE)
                        continue
                    try {
                        FileUtil.writeFile(audioBuffer, recordByteSize)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                audioRecord.stop()
                fileName = FileUtil.closeFile(testUID, recordByteSize)

                UploadUtil.uploadUID(testUID)
                UploadUtil.uploadFile(fileName)
            }
        }
    }

    private fun stopRecord() {
        isRecording = false
    }

    private fun testFile() {
        val testBytes = byteArrayOf(0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1)
        Log.d("Util Test", "File Test Started!!")
        Log.d("Util Test", "Initializing File!!")
        FileUtil.initFile(testUID, applicationContext)
        Log.d("Util Test", "Initialized File!!")

        Log.d("Util Test", "Writing Bytes to File!!")
//        FileUtil.appendData(testBytes)
        Log.d("Util Test", "Wrote to File!!")

        Log.d("Util Test", "Closing File!!")
//        fileName = FileUtil.closeFile(testUID)
        Log.d("Util Test", "Closed File!!")
        Log.d("Util Test", "File Test Done!!")
    }

    private fun testUpload() {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        Log.d("Util Test", "Upload Test Started!!")

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                Log.d("Util Test", "Initializing Socket!!")
                UploadUtil.initSocket()
                Log.d("Util Test", "Initialized Socket!!")

                Log.d("Util Test", "Uploading UID!!")
                UploadUtil.uploadUID(testUID)
                Log.d("Util Test", "Uploaded UID!!")

                Log.d("Util Test", "Uploading File!!")
                UploadUtil.uploadFile(fileName)
                Log.d("Util Test", "Uploaded File!!")

                Log.d("Util Test", "Closing Socket!!")
                UploadUtil.closeSocket()
                Log.d("Util Test", "Closed Socket!!")
            }
        }
        Log.d("Util Test", "Upload Test Done!!")
    }

    private val permissionArray = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    private fun hasPermissions(): Boolean {
        return permissionArray.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private val requestBluetoothPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var allPermissionsGranted = true
            permissions.entries.forEach {
                if (!it.value) {
                    allPermissionsGranted = false
                }
            }
    }
}