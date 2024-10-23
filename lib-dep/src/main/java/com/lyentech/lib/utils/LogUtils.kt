package com.lyentech.lib.utils

import android.util.Log

/**
 * @author by jason-何伟杰，2022/11/25
 * des:日志打印
 */
internal var openLog = true

const val TAG = "dep"

fun setLogEnable(enable: Boolean = true) {
    openLog = enable
}

fun printI(message: String = "", key: String = TAG) {
    if (openLog && !isEmpty(message)) {
        Log.i(key, message)
    }
}

fun printV(message: String?, key: String = TAG) {
    if (openLog && !isEmpty(message))
        Log.v(key, message!!)
}

fun printD(message: String, key: String = TAG) {
    if (openLog && !isEmpty(message))
        printLog(message, key)
}

fun printW(message: String, key: String = TAG) {
    if (openLog && !isEmpty(message)) {
        Log.w(key, message)
    }
}

fun printE(message: String, key: String = TAG) {
    if (openLog && !isEmpty(message)) {
        Log.e(key, message)
    }
}

fun printE(throwable: Throwable, key: String = TAG) {
    if (openLog) {
        printE(Log.getStackTraceString(throwable), key)
    }
}

fun printLog(txt: Any?, tag: String = TAG) {
    if (!openLog) return
    val msg = txt.toString()
    if (msg.isEmpty()) return
    var log = msg
    val segmentSize = 3 * 1024
    val length = log.length
    if (length <= segmentSize) {
        Log.d(
            tag,
            "Method:{${Throwable().stackTrace[1].methodName}}-${Throwable().stackTrace[1].lineNumber}::$log"
        )
    } else {
        while (log.length > segmentSize) {
            val logContent = log.substring(0, segmentSize)
            log = log.replace(logContent, "")
            Log.d(tag, "\n$logContent")
        }
        Log.d(tag, "\n$log")
    }
}

fun <T> printList(mutableList: List<T>?, des: String? = "") {
    if (mutableList == null || mutableList.isEmpty()) {
        printD(message = "printList is null>>>")
    } else {
        printD(message = "printList>>$des>${mutableList.size} ${Thread.currentThread().name}")
        mutableList.forEach {
            printD(message = "printItem>>$it ")
        }
    }
}

//严格判空
fun isEmpty(s: String?): Boolean {
    if (null == s) {
        println("LogUtils_txt is null>>>")
        return true
    }
    if (s.isEmpty()) {
        println("LogUtils_txt is null>>>")
        return true
    }
    if (s.trim { it <= ' ' }.isEmpty()) {
        println("LogUtils_txt is ' '>>>")
        return true
    }
    return false
}