package com.example.dailytaskplanner.utils

import java.security.MessageDigest
import kotlin.experimental.and

fun String.toHex(pass: String): String {
    val md = MessageDigest.getInstance("SHA-1")
    md.update(pass.toByteArray())
    val bytes = md.digest(this.toByteArray())
    val sb = StringBuilder()
    for (element in bytes) {
        sb.append(((element and 0xff.toByte()) + 0x100).toString(16).substring(1))
    }
    return sb.toString()
}

fun String.toHex(): String {
    val array = toByteArray()
    val var1 = CharArray(array.size shl 1)
    var var2 = 0

    array.forEach {
        val var4: Int = (it and 255.toByte()).toInt()
        var1[var2++] = Constants.HEX_LOWERCASE[var4 ushr 4]
        var1[var2++] = Constants.HEX_LOWERCASE[var4 and 15]
    }
    return String(var1)
}