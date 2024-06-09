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
        Log.i("FileUtil", "Initializing New File")
        if(fileObject == null) {
            filePath = context.filesDir
            fileObject = File(getFileFullPath(uid))
        }
        if(fileStream == null) {
            fileStream = FileOutputStream(fileObject)
        }
        Log.i("FileUtil", String.format("Initialized File %s", getFileFullPath(uid)))
    }

    fun closeFile(uid: String): String {
        Log.i("FileUtil", "Closing File")
        val fileName = getFileFullPath(uid)
        if(fileStream != null) {
            writeFileHeader()
            writeFileData()
            fileCounter++
            fileObject = null
            fileStream!!.close()
            fileStream = null
        }
        Log.i("FileUtil", String.format("Closing File %s", fileName))
        return fileName
    }

    fun appendData(data: ByteArray) {
        Log.i("FileUtil", String.format("Appending '%s' to File", data.toString()))
        data.forEach { buf ->
            fileBuffer += buf
        }
        Log.i("FileUtil", "Appended data to File")
    }

    fun getFileFullPath(uid: String): String {
        return String.format(Locale.getDefault(), "%s/%s-%d.wav", filePath!!.absolutePath, uid, fileCounter)
    }

    private fun writeFileData() {
        Log.i("FileUtil", "Writing Data to File")
        if(fileStream != null) {
            fileStream!!.write(fileBuffer)
            fileStream!!.flush()
        }
        Log.i("FileUtil", "Writing Data to File Done")
    }

    private fun writeFileHeader() {
        Log.i("FileUtil", "Writing Header to File")
        if(fileStream != null) {
            val sampleRate = 16000.toLong() // 16000Hz
            val channels = 1                // Mono Channel
            val bitsPerSample = 8           // PCM Bit Size
            val byteRate = sampleRate * channels * bitsPerSample / 8
            val blockAlign = channels * bitsPerSample / 8
            
            val headerBuffer = ByteArray(44)
            // 1. RIFF
            headerBuffer[0] = 'R'.code.toByte()
            headerBuffer[1] = 'I'.code.toByte()
            headerBuffer[2] = 'F'.code.toByte()
            headerBuffer[3] = 'F'.code.toByte()
            // 2. File Size
            headerBuffer[4] = ((fileBuffer.size + 36).toLong() and 0xffL).toByte()
            headerBuffer[5] = ((fileBuffer.size + 36).toLong() shr 8 and 0xffL).toByte()
            headerBuffer[6] = ((fileBuffer.size + 36).toLong() shr 16 and 0xffL).toByte()
            headerBuffer[7] = ((fileBuffer.size + 36).toLong() shr 24 and 0xffL).toByte()
            // 3. WAVE
            headerBuffer[8] = 'W'.code.toByte()
            headerBuffer[9] = 'A'.code.toByte()
            headerBuffer[10] = 'V'.code.toByte()
            headerBuffer[11] = 'E'.code.toByte()
            // 4. 'fmt '
            headerBuffer[12] = 'f'.code.toByte()
            headerBuffer[13] = 'm'.code.toByte()
            headerBuffer[14] = 't'.code.toByte()
            headerBuffer[15] = ' '.code.toByte()
            // 5. Fixed Value 16
            headerBuffer[16] = 16
            headerBuffer[17] = 0
            headerBuffer[18] = 0
            headerBuffer[19] = 0
            // 6. Data Format : PCM(1)
            headerBuffer[20] = 1
            headerBuffer[21] = 0
            // 7. Channel Size
            headerBuffer[22] = channels.toByte()
            headerBuffer[23] = 0
            // 8. Sample Rate
            headerBuffer[24] = (sampleRate and 0xffL).toByte()
            headerBuffer[25] = (sampleRate shr 8 and 0xffL).toByte()
            headerBuffer[26] = (sampleRate shr 16 and 0xffL).toByte()
            headerBuffer[27] = (sampleRate shr 24 and 0xffL).toByte()
            // 9. Byte Rate
            headerBuffer[28] = (byteRate and 0xffL).toByte()
            headerBuffer[29] = (byteRate shr 8 and 0xffL).toByte()
            headerBuffer[30] = (byteRate shr 16 and 0xffL).toByte()
            headerBuffer[31] = (byteRate shr 24 and 0xffL).toByte()
            // 10. Block Size
            headerBuffer[32] = blockAlign.toByte()
            headerBuffer[33] = 0
            // 11. Bit Depth
            headerBuffer[34] = bitsPerSample.toByte()
            headerBuffer[35] = 0
            // 12.'data'
            headerBuffer[36] = 'd'.code.toByte()
            headerBuffer[37] = 'a'.code.toByte()
            headerBuffer[38] = 't'.code.toByte()
            headerBuffer[39] = 'a'.code.toByte()
            // 13. PCM Data Size(File Size - Header Size)
            headerBuffer[40] = (fileBuffer.size.toLong() and 0xffL).toByte()
            headerBuffer[41] = (fileBuffer.size.toLong() shr 8 and 0xffL).toByte()
            headerBuffer[42] = (fileBuffer.size.toLong() shr 16 and 0xffL).toByte()
            headerBuffer[43] = (fileBuffer.size.toLong() shr 24 and 0xffL).toByte()
            fileStream!!.write(headerBuffer, 0, 44)
        }
        Log.i("FileUtil", "Writing Header to File Done")
    }
}