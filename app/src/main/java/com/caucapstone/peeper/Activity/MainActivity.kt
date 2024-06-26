package com.caucapstone.peeper.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caucapstone.peeper.R
import com.caucapstone.peeper.Service.FCMService
import com.caucapstone.peeper.Util.FileUtil
import com.caucapstone.peeper.Util.UploadUtil
import com.google.android.material.button.MaterialButton
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.Duration
import java.time.Instant

class MainActivity : AppCompatActivity() {
    private var btnRecord: MaterialButton? = null
    private var tvStatus: TextView? = null

    private var fileName = ""
    private var isRecording = false

    val testUID = "DEFAULT_UID"

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

        btnRecord = findViewById<MaterialButton>(R.id.main_btn_record)
        btnRecord!!.setOnClickListener(btnListener)

        tvStatus = findViewById(R.id.main_tv_status)

        startService(Intent(this, FCMService::class.java))
        FirebaseMessaging.getInstance().subscribeToTopic(testUID)
    }

    private val btnListener = View.OnClickListener { btn ->
        when(btn.id) {
            R.id.main_btn_record -> if(isRecording) {
                btnRecord!!.icon = resources.getDrawable(R.drawable.ic_mic)
                tvStatus!!.text = "아래 버튼을 눌러 음성 녹음을 시작하세요."
                stopRecord()
            } else {
                btnRecord!!.icon = resources.getDrawable(R.drawable.ic_uploading)
                tvStatus!!.text = "음성 녹음 데이터를 업로드하는 중입니다."
                startRecord()
            }
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
                var startTimestamp = Instant.now()

                audioRecord.startRecording()
                isRecording = true
                while(isRecording) {
                    val curTimestamp = Instant.now()
                    val curDuration = Duration.between(startTimestamp, curTimestamp).toMillis()
                    if(curDuration >= 5000) {
                        fileName = FileUtil.closeFile(testUID, recordByteSize)

                        UploadUtil.initSocket()
                        UploadUtil.uploadUID(testUID)
                        UploadUtil.uploadFile(fileName)
                        UploadUtil.closeSocket()

                        FileUtil.initFile(testUID, applicationContext)

                        startTimestamp = Instant.now()
                    }

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
            }
        }
    }

    private fun stopRecord() {
        isRecording = false
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