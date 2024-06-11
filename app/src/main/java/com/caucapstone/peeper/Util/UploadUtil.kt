package com.caucapstone.peeper.Util

import android.util.Log
import com.caucapstone.peeper.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString.Companion.toByteString
import okio.IOException
import java.io.File
import java.io.FileInputStream

object UploadUtil {
    private const val SERVER_URL = BuildConfig.SERVER_URL
    private var socketClient: OkHttpClient? = null
    private var serverSocket: WebSocket? = null

    fun initSocket() {
        Log.i("Uploadutil", "Initializing Socket")
        val request = Request.Builder().url(SERVER_URL).build()
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                Log.i("Uploadutil", "WebSocket Opened")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e("Uploadutil", "WebSocket Failure: " + t.message)
            }
        }
        socketClient = OkHttpClient()
        serverSocket = socketClient!!.newWebSocket(request, listener)
        Log.i("Uploadutil", "Initialized Socket")
    }

    fun closeSocket() {
        Log.i("Uploadutil", "Closing Socket")
        socketClient!!.dispatcher.executorService.shutdown()
        socketClient = null
        serverSocket?.close(1000, "Closing WebSocket")
        serverSocket = null
        Log.i("Uploadutil", "Closed Socket")
    }

    fun uploadUID(uid: String){
        Log.i("Uploadutil", String.format("Uploading UID %s", uid))
        serverSocket?.send(uid)
        Log.i("Uploadutil", String.format("Uploaded UID %s", uid))
    }

    fun uploadFile(fileName: String) {
        Log.i("Uploadutil", String.format("Uploading File %s", fileName))
        val file = File(fileName)
        val fileInputStream = FileInputStream(file)
        val buffer = ByteArray(file.length().toInt())
        try {
            fileInputStream.read(buffer)
            val byteString = buffer.toByteString(0, buffer.size)
            serverSocket?.send(byteString)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fileInputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Log.i("Uploadutil", String.format("Uploaded File %s", fileName))
    }
}