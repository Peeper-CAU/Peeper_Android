package com.caucapstone.peeper.Util

import com.caucapstone.peeper.BuildConfig
import java.io.OutputStream
import java.net.Socket

object UploadUtil {
    private const val SERVER_PORT = BuildConfig.SERVER_PORT
    private const val SERVER_URL = BuildConfig.SERVER_URL
    private var serverSocket: Socket? = null
    private var serverStream: OutputStream? = null

    fun initSocket() {
        if(serverSocket == null || serverSocket!!.isClosed) {
            serverSocket = Socket(SERVER_URL, SERVER_PORT.toInt())
        }

        if(serverStream == null) {
            serverStream = serverSocket!!.getOutputStream()
        }
    }

    fun closeSocket() {
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
    }

    fun uploadFile() {

    }
}