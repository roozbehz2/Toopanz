package com.roozbeh.toopan.utility

import android.util.Log
import java.io.File

class FileManager {

    companion object {

        /**
         * Check the file name is unique
         *
         * @param nameNewFolder The name of new folder
         * @param fileLocation  file path
         * @return file file with unique name
         */
        fun generateFileName(nameNewFolder: String, fileLocation: String?): File {
            var name = nameNewFolder
            var file1 = File(fileLocation, "$name.jpg")
            var num = 0
            while (file1.exists()) {
                num++
                name = "$nameNewFolder($num)"
                file1 = File(fileLocation, "$name.jpg")
            }
            Log.e("rrr", "generateFileName: " + name )
            return file1
        }

    }
}