package com.lyentech.lib.global.common

import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.format.Formatter
import android.util.Log
import com.lyentech.lib.utils.printD
import com.lyentech.lib.utils.printE
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.InputStream
import java.math.BigDecimal
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


/**
 * @author by jason-何伟杰，2022/12/19
 * des:全局调用
 */
object GlobalCode {

    @JvmField
    var TOKEN: String? = null

    var HOST: String = "https://www.baidu.com"


    @JvmStatic //出现协程的完成、取消异常不弹提示
    fun isJobErr(err: String?): Boolean {
        err?.let {
            if (err.contains("was cancelled") || err.contains("has completed normally")) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun getCurProcessName(context: Context): String? {
        val pid = Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in activityManager
            .runningAppProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName
            }
        }
        return null
    }

    @JvmStatic
    fun getTotalRam(): String? {
        val path = "/proc/meminfo"
        var ramMemorySize: String? = null
        var totalRam = 0
        try {
            val fileReader = FileReader(path)
            val br = BufferedReader(fileReader, 4096)
            ramMemorySize = br.readLine().split("\\s+").get(1)
            br.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (ramMemorySize != null) {
            totalRam =
                Math.ceil(java.lang.Float.valueOf(ramMemorySize) / (1024 * 1024).toDouble()).toInt()
        }
        return totalRam.toString() + "GB"
    }

    fun toRequestBody(value: String): RequestBody {
//        return RequestBody.create(MediaType.parse("text/plain"), value)
        return value.toRequestBody("text/plain".toMediaType())
    }

    @JvmStatic //当前APP的内存使用情况
    fun getRam(context: Context): String {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        //最大分配内存
        val memory = activityManager!!.memoryClass
        println("memory: $memory")
        val mi1 = MemoryInfo()
        activityManager.getMemoryInfo(mi1)
        println("aviMemory=${mi1.availMem} ${mi1.totalMem}")
        val aviMem = getKeepValue((mi1.availMem * 1.0 / (1024 * 1024 * 1024)))
        val totalMem = getKeepValue(mi1.totalMem * 1.0 / (1000 * 1000 * 1000))
        println("for-$aviMem $totalMem")

        val mi = ActivityManager.MemoryInfo()
        val devMem = Formatter.formatFileSize(context, mi.totalMem)
        val avaiMem = Formatter.formatFileSize(context, mi.availMem)
        //最大分配内存获取方法2
        val maxMemory = (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024)).toFloat()
        //当前分配的总内存
        val totalMemory = (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024)).toFloat()
        //剩余内存
        val freeMemory = (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024)).toFloat()
        println("maxMemory: $maxMemory")
        println("totalMemory: $totalMemory")
        println("freeMemory: $freeMemory")
//        return "$devMem/$avaiMem"
//        return  "${Formatter.formatFileSize(context,aviMem.toLong())}/${Formatter.formatFileSize(context,totalMem.toLong())}"
        return "$aviMem Gb/$totalMem Gb"
    }

    fun getSDAvailableSize(): String {
        val path = Environment.getExternalStorageDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        //        GlobalCode.printLog("size=" + blockSize + " " + availableBlocks + " " + blockSize * availableBlocks);
        return Formatter.formatFileSize(
            UiHelper.getContext(),
            blockSize * availableBlocks
        )
    }

    fun getRandomValue(value: Float): Float {
        var y1: Float = 0f
        val maxRange = 10
        val max = 8
        val min = 1
        val random = (Math.random() * (max - min) + min).toFloat()
        y1 = random * 3 + value
//        for (i in 0 until maxRange) {
//            y1 = (sin(random * (i * Math.PI / 180)) + i * 5 * 0.1).toFloat()+value
//            y1 = cos(random * (i * Math.PI / 180)) + i * 3 * 0.01

//        }
        return y1
    }


    fun intToIp(ip: Int): String {
        val strIp: String =
            ((ip and 0xFF).toString() + "." + (ip shr 8 and 0xFF) + "." + (ip shr 16 and 0xFF)
                    + "." + (ip shr 24 and 0xFF))
        return strIp
    }

    @JvmStatic
    fun getWifiHostAddress(): String? {
        val en = NetworkInterface.getNetworkInterfaces()
        for (item in en) {
            if (item.name.contains("wlan")) {
                val ei = item.inetAddresses
                for (i in ei) {
                    if (!i.isLoopbackAddress && i.address.size == 4) {
                        return i.hostAddress
                    }
                }
            }
        }
        return null
    }

    fun getWifiHostAddress2(context: Context) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val wifiIp = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
            printD("wifi1=$wifiIp")
        } else {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.run {
                activeNetwork?.let { network ->
                    (getNetworkCapabilities(network)?.transportInfo as? WifiInfo)?.let { wifiInfo ->
                        val wifiIp = Formatter.formatIpAddress(wifiInfo.ipAddress)
                        printD("wifi2=$wifiIp")
                    }
                }
            }
        }
    }

    /**转为字符串文本*/
    @JvmStatic
    fun formatTime(time: String): String =
        formatTimeStamp(time).let {
            DateUtils.getRelativeTimeSpanString(it).toString()
        }

    /**转为时间戳*/
    @JvmStatic
    fun formatTimeStamp(time: String): Long =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
            .let {
                it.timeZone = TimeZone.getTimeZone("GMT+8")
                it.parse(time).time
            }

    @JvmStatic
    fun date2Str(date: Date = Date(), pattern: String = "yyyy MM dd HH:mm:ss"): String {
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    @JvmStatic
    fun long2DateStr(date: Long, pattern: String = "yyyyMMddHHmmss"): String {
        return SimpleDateFormat(pattern).format(date)
    }

    //保留两位小数
    @JvmStatic
    fun getKeepValue(value: Double?, match: String = "%.2f"): String {
        value?.let {
            try {
                return String.format(match, value.toDouble())
            } catch (e: Exception) {
                e.printStackTrace()
                return value.toString()
            }
        }
        return ""
    }

    @JvmStatic
    fun isChinese(): Boolean {
        val language = Locale.getDefault().language
        val country = Locale.getDefault().country
        if (language == "zh") {
            when (country) {
                "TW" -> {
                    //繁体 台湾
                }

                "HK" -> { //繁体 香港
                }

                "MO" -> { //繁体 澳门
                }

                "CN" -> {
                    //简体大陆
                }
            }
            return true
        } else {
            return false
        }
    }

    /** 根据它的名字启动一个应用*/
    @JvmStatic
    fun launchApp(context: Context, label: String) {
        if (TextUtils.isEmpty(label)) {
            UiHelper.toast(text = "Can't not open application")
            return
        }
        try {
            var packageName = ""
            val pm = context.packageManager
            val packList = pm.getInstalledPackages(0)
            for (i in packList.indices) {
                val info = packList[i]
                if (pm.getApplicationLabel(info.applicationInfo).toString() == label) {
                    packageName = info.applicationInfo.packageName
                    break
                }
            }
            if (TextUtils.isEmpty(packageName)) {
                UiHelper.toast(text = "没有安装应用 $label")
                return
            }
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            context.startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun refreshDir(file: File) {
        MediaScannerConnection.scanFile(
            UiHelper.getContext(),
            arrayOf(file.absolutePath),
            null,
            null
        )
    }

    @JvmStatic
    fun createImagePath(): String {
        return CoreApplicationProvider.getImageCacheDir()?.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
    }

    /**
     *  /data/xx/old.jpg
     *  /data/yy/
     *  new.jpg
     * */
    @JvmStatic
    fun copyNewFileAndRename(oldPath: String, newDirPath: String, newFileName: String) {
        //需要复制到的路径，以及图片的新命名+格式
        val result = File(newDirPath + newFileName)
        //判断该文件夹是否存在,不存在则新增
        if (!result.parentFile.exists()) {
            result.parentFile.mkdirs()
        }
        //需要复制的原图的路径+图片名+ .png(这是该图片的格式)
        val input = FileInputStream(oldPath)
        val out = FileOutputStream(result)
        //一个容量，相当于打水的桶，可以自定义大小
        val buffer = ByteArray(100)
        var hasRead = 0
        while (input.read(buffer).also { hasRead = it } > 0) {
            //0：表示每次从0开始
            out.write(buffer, 0, hasRead)
        }
        println(result.absolutePath)
        input.close()
        out.close()
    }

    /** 相对文件名，文件夹*/
    @JvmStatic
    fun setUniqueFileName(newFileName: String, fileDir: String): String? {
        var fileName = newFileName
        val f = File(fileDir)
        if (f.exists()) { //判断路径是否存在
            val files = f.listFiles()
            val hashSet: HashSet<String> = HashSet()
            for (file in files) {
                if (file.isFile) {
                    val name = file.name
                    hashSet.add(name)
                }
            }
            var a = 1
            while (true) {
                if (a != 1) {
                    val split: List<String> = fileName.split("\\.")
                    fileName = split[0] + "(" + a + ")." + split[1]
                }
                if (!hashSet.contains(fileName)) {
                    return fileName
                } else {
                    a++
                }
            }
        }
        return null
    }

    @JvmStatic //返回文件夹下的文件名
    fun getFilesAllName(path: String): List<String>? {
        val file = File(path)
        val files = file.listFiles()
        if (files == null) {
            printD("空目录>>")
            return null
        }
        val s: MutableList<String> = ArrayList()
        for (i in files.indices) {
            s.add(files[i].absolutePath)
        }
        return s
    }

    @JvmStatic
    fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            if (fileList != null) for (aFileList in fileList) {
                size = if (aFileList.isDirectory) {
                    size + getFolderSize(aFileList)
                } else {
                    size + aFileList.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    @JvmStatic
    fun getFormatFileSize(size: Double, m: Int = 1000): String { //系统的计算是1000，实际是1024
        val kiloByte = size / m
        if (kiloByte < 1) {
            return size.toString() + "Byte"
        }
        val megaByte = kiloByte / m
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }
        val gigaByte = megaByte / m
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }
        val teraBytes = gigaByte / m
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }

}