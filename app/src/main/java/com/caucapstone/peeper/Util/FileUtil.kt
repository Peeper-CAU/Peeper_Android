package com.caucapstone.peeper.Util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object FileUtil {
    private var fileBuffer = byteArrayOf(0)
    private var fileCounter = 1
    private var fileObject: File? = null
    private var filePath: File? = null
    private var fileStream: FileOutputStream? = null

    fun initFile(uid: String, context: Context) {
        Log.i("Uploadutil", "Initializing New File")
        if(fileObject == null) {
            filePath = context.filesDir
            fileObject = File(getFileFullPath(uid))
        }
        if(fileStream == null) {
            fileStream = FileOutputStream(fileObject)
        }
        Log.i("Uploadutil", String.format("Initialized File %s", getFileFullPath(uid)))
    }

    fun closeFile(uid: String) {
        Log.i("Uploadutil", "Closing File")
        if(fileStream != null){
            UploadUtil.uploadFile(getFileFullPath(uid))
            fileCounter++
            fileStream!!.close()
            fileStream = null
        }
        Log.i("Uploadutil", String.format("Closing File %s", getFileFullPath(uid)))
    }

    fun appendData(data: ByteArray) {
        Log.i("Uploadutil", String.format("Appending '%s' to File", data.toString()))
        data.forEach { buf ->
            fileBuffer += buf
        }
        Log.i("Uploadutil", "Appended data to File")
    }

    private fun getFileFullPath(uid: String): String {
        return String.format(Locale.getDefault(), "%s/%s-%d.wav", filePath!!.absolutePath, uid, fileCounter)
    }

    private fun writeFileData() {
        if(fileStream != null){
            fileStream!!.write(fileBuffer)
            fileStream!!.flush()
        }
    }

    private fun writeFileHeader(filename: String) {

    }
}