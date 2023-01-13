package com.example.project3.models.helpers

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor

class Formatters {
    fun format(num: Double): String {
        val df = DecimalFormat("##.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(num)
    }

    fun formatToName(num: Double): String {
        return floor(num).toInt().toString()
    }

    fun getFileExtension(uri: Uri, context: Context): String? {
        val cr = context.contentResolver
        val map = MimeTypeMap.getSingleton()
        return map.getExtensionFromMimeType(cr?.getType(uri))
    }

    fun getName(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.now().format(formatter)
    }

}