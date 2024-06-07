package com.caucapstone.peeper.Util

import android.util.Log
import com.caucapstone.peeper.BuildConfig
import java.io.DataInput
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets

object UploadUtil {
    private const val SERVER_PORT = BuildConfig.SERVER_PORT
    private const val SERVER_URL = BuildConfig.SERVER_URL
    private var serverSocket: Socket? = null
    private var serverStream: OutputStream? = null

    fun initSocket() {
        Log.i("Uploadutil", "Initializing Socket")
        if(serverSocket == null || serverSocket!!.isClosed) {
            serverSocket = Socket(SERVER_URL, SERVER_PORT.toInt())
        }

        if(serverStream == null) {
            serverStream = serverSocket!!.getOutputStream()
        }
        Log.i("Uploadutil", "Initialized Socket")
    }

    fun closeSocket() {
        Log.i("Uploadutil", "Closing Socket")
        if(serverStream != null) {
            serverStream!!.close()
            serverStream = null
        }

        if(serverSocket != null) {
            if(serverSocket!!.isClosed == false) {
                serverSocket!!.close()
            }
            serverSocket = null
        }
        Log.i("Uploadutil", "Closed Socket")
    }

    fun uploadUID(uid: String){
        Log.i("Uploadutil", String.format("Uploading UID %s", uid))
        if(serverStream != null && serverSocket != null){
            val serverStreamWrite = OutputStreamWriter(serverStream, StandardCharsets.UTF_8)
            serverStreamWrite.write(uid)
        }
        Log.i("Uploadutil", String.format("Uploaded UID %s", uid))
    }

    fun uploadFile(filename: String) {
        Log.i("Uploadutil", String.format("Uploading File %s", filename))
        if(serverSocket != null && serverStream != null){
            val fileInputStream = DataInputStream(FileInputStream(File(filename)))
            val fileOutputStream = DataOutputStream(serverStream)

            var buffer = ByteArray(1024)
            while(fileInputStream.read(buffer) > 0){
                fileOutputStream.write(buffer)
                fileOutputStream.flush()
            }

            fileOutputStream.close()
            Log.i("Uploadutil", String.format("Uploaded File %s", filename))
        }
    }
}