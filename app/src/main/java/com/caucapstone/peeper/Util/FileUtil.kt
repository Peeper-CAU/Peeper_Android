package com.caucapstone.peeper.Util

import java.io.FileOutputStream
import java.util.Locale

object FileUtil {
    private var audioSize = 0
    private var fileCounter = 1
    private var fileStream: FileOutputStream? = null

    fun initFile(uid: String) {
        if(fileStream == null){
            fileStream = FileOutputStream(getFileName(uid))
        }
    }

    fun closeFile(uid: String) {
        if(fileStream != null){
            // TODO : Upload Function from UploadUtil
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

    private fun getFileName(uid: String): String {
        return String.format(Locale.getDefault(), "%s-%d.wav", uid, fileCounter)
    }

    private fun writeFileHeader(filename: String) {

    }
}