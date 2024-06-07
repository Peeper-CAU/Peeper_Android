package com.caucapstone.peeper.Util

import java.util.Locale

object FileUtil {
    private var fileCounter = 1

    fun initFile(uid: String) {

    }

    fun closeFile(uid: String) {

    }

    private fun getFileName(uid: String): String {
        return String.format(Locale.getDefault(), "%s-%d.wav", uid, fileCounter)
    }

    private fun writeFileHeader(filename: String) {

    }
}