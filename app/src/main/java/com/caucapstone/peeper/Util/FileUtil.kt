package com.caucapstone.peeper.Util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale

object FileUtil {
    private var fileCounter = 1
    private var fileObject: File? = null
    private var filePath: File? = null
    private var fileStream: FileOutputStream? = null

    fun initFile(uid: String, context: Context) {
        Log.i("FileUtil", "Initializing New PCM File")
        if(fileObject == null) {
            filePath = context.filesDir
            fileObject = File(getFileFullPath(uid, true))
        }
        if(fileStream == null) {
            fileStream = FileOutputStream(fileObject)
        }
        Log.i("FileUtil", String.format("Initialized File %s", getFileFullPath(uid, true)))
    }

    fun closeFile(uid: String, bitSize: Int): String {
        Log.i("FileUtil", "Closing File")
        Log.i("FileUtil", "Converting PCM to WAV")

        val resFile = File(getFileFullPath(uid, false))
        convertPCMtoWAV(fileObject!!, resFile, bitSize)

        val pcmFileName = getFileFullPath(uid, true)
        val wavFileName = getFileFullPath(uid, false)
        if(fileStream != null) {
            fileCounter++
            fileObject = null
            fileStream!!.close()
            fileStream = null
        }

        Log.i("FileUtil", String.format("Closing File %s", pcmFileName))
        return wavFileName
    }

    fun writeFile(buffer: ByteArray, bitSize: Int) {
        if(fileStream != null) {
            fileStream!!.write(buffer, 0, bitSize)
        }
    }

    fun getFileFullPath(uid: String, is_pcm: Boolean): String {
        return String.format(Locale.getDefault(), "%s/%s-%d.${if(is_pcm) "pcm" else "wav"}", filePath!!.absolutePath, uid, fileCounter)
    }

    @Throws(IOException::class)
    fun convertPCMtoWAV(
        pcmFile: File,
        wavFile: File,
        bufferSize: Int,
        sampleRate: Int = 44100,
        channels: Int = 1,
        bitsPerSample: Int = 8
    ) {
        val pcmData = ByteArray(bufferSize)

        FileInputStream(pcmFile).use { fis ->
            FileOutputStream(wavFile).use { fos ->
                // WAV 파일 헤더 작성
                writeWavHeader(fos, pcmFile.length(), sampleRate, channels, bitsPerSample)

                // PCM 데이터 읽고 WAV 파일에 쓰기
                var bytesRead: Int
                while (fis.read(pcmData).also { bytesRead = it } != -1) {
                    fos.write(pcmData, 0, bytesRead)
                }
            }
        }
    }

    private fun writeWavHeader(
        outputStream: OutputStream,
        pcmDataLength: Long,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ) {
        val totalDataLength = pcmDataLength + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8

        val header = ByteArray(44)
        // RIFF
        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLength and 0xff).toByte()
        header[5] = (totalDataLength shr 8 and 0xff).toByte()
        header[6] = (totalDataLength shr 16 and 0xff).toByte()
        header[7] = (totalDataLength shr 24 and 0xff).toByte()

        // Wave
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        header[12] = 'f'.toByte()
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0

        // Format
        header[20] = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = blockAlign.toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0

        // Data Chunk
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (pcmDataLength and 0xff).toByte()
        header[41] = (pcmDataLength shr 8 and 0xff).toByte()
        header[42] = (pcmDataLength shr 16 and 0xff).toByte()
        header[43] = (pcmDataLength shr 24 and 0xff).toByte()

        outputStream.write(header, 0, 44)
    }
}