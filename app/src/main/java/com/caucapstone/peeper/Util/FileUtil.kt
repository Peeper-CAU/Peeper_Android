package com.caucapstone.peeper.Util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object FileUtil {
    private var audioSize = 0
    private var fileCounter = 1
    private var fileObject: File? = null
    private var filePath: File? = null
    private var fileStream: FileOutputStream? = null

    fun initFile(uid: String, context: Context) {
        if(fileObject == null) {
            filePath = context.filesDir
            fileObject = File(getFileFullPath(uid))
        }
        if(fileStream == null) {
            fileStream = FileOutputStream(fileObject)
        }
    }

    fun closeFile(uid: String) {
        if(fileStream != null){
            UploadUtil.uploadFile(getFileFullPath(uid))
            fileCounter++
            fileStream!!.close()
            fileStream = null
        }
    }

    fun appendData(data: ByteArray) {
        if(fileStream != null){
            fileStream!!.write(data, audioSize, data.size)
            audioSize += data.size
        }
    }

    private fun getFileFullPath(uid: String): String {
        return String.format(Locale.getDefault(), "%s/%s-%d.wav", filePath!!.absolutePath, uid, fileCounter)
    }

    private fun writeFileHeader(filename: String) {

    }
}