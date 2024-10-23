package com.lyentech.lib.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import androidx.core.content.FileProvider
import com.lyentech.lib.global.common.UiHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.*

object DevApiUtils {
    /**
     * @param context     上下文
     * @param packageName 应用包名
     * @return 是否已安装
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        val pinfo = packageManager.getInstalledPackages(0)
        if (pinfo != null) {
            for (i in pinfo.indices) {
                val pn = pinfo[i].packageName
                if (pn == packageName) {
                    return true
                }
            }
        }
        return false
    }


    /**
     * @param context     上下文
     * @param packageName 应用包名
     */
    fun startApp(context: Context, packageName: String?) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName!!)
        context.startActivity(launchIntent)
    }


    /**
     * @param context 上下文
     * @param apkFile 安装文件全路径
     */
    fun install(context: Context, apkFile: File?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val b = context.packageManager.canRequestPackageInstalls()
            if (b) {
                startInstall(context, apkFile)
            } else { //跳转到应用授权安装第三方应用权限
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                context.startActivity(intent)
            }
        } else {
            startInstall(context, apkFile)
        }
    }

    fun startInstall(context: Context, apkFile: File?) {
        val intent1 = Intent(Intent.ACTION_VIEW)
        val data: Uri
        val type = "application/vnd.android.package-archive"
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(apkFile)
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } else {
            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val authority = context.packageName + ".fileProvider"
            data = FileProvider.getUriForFile(context, authority, apkFile!!)
        }
        intent1.setDataAndType(data, type)
        context.startActivity(intent1)
    }

    fun installApk(context: Activity, path: String?) {
        val file = File(path)
        if (file.exists()) {
            val installApkIntent = Intent()
            installApkIntent.action = Intent.ACTION_VIEW
            installApkIntent.addCategory(Intent.CATEGORY_DEFAULT)
            installApkIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            //适配8.0需要有权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val hasInstallPermission = context.packageManager.canRequestPackageInstalls()
                if (hasInstallPermission) {
                    //安装应用
                    installApkIntent.setDataAndType(
                        FileProvider.getUriForFile(
                            context,
                            UiHelper.getPackageName() + ".fileProvider",
                            file
                        ), "application/vnd.android.package-archive"
                    )
                    installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    if (context.packageManager.queryIntentActivities(
                            installApkIntent,
                            0
                        ).size > 0
                    ) {
                        context.startActivity(installApkIntent)
                    }
                } else {
                    //跳转至“安装未知应用”权限界面，引导用户开启权限
                    val selfPackageUri = Uri.parse("package:" + UiHelper.getPackageName())
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, selfPackageUri)
                    context.startActivityForResult(intent, 1)
                }
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    installApkIntent.setDataAndType(
                        FileProvider.getUriForFile(
                            context,
                            UiHelper.getPackageName() + ".fileProvider",
                            file
                        ), "application/vnd.android.package-archive"
                    )
                    installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    installApkIntent.setDataAndType(
                        Uri.fromFile(file),
                        "application/vnd.android.package-archive"
                    )
                }
                if (context.packageManager.queryIntentActivities(installApkIntent, 0).size > 0) {
                    context.startActivity(installApkIntent)
                }
            }
        }
    }

    /**
     * @param context     上下文
     * @param packageName 应用包名
     */
    fun unInstall(context: Context, packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$packageName")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * @author by jason-何伟杰，2022/10/10
     * des:根据apk路径获取 应用的信息-版本号、包名
     */
    fun getApkInfo(context: Context, apkPath: String?): JSONObject? {
        val pm = context.packageManager
        val pi = pm.getPackageArchiveInfo(apkPath!!, PackageManager.GET_ACTIVITIES)
        if (pi != null) {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("packageName", pi.packageName)
                jsonObject.put("versionName", pi.versionName)
                jsonObject.put("versionCode", pi.versionCode)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonObject
        }
        return null
    }

    /**
     * @param packageName 包名
     * @return 应用版本名
     */
    fun getVersionName(packageName: String?): String? {
        val packageManager: PackageManager = UiHelper.getContext().getPackageManager()
        try {
            val packageInfo = packageManager.getPackageInfo(packageName!!, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "1.0.0"
    }

    /**
     * @param packageName 包名
     * @return 应用版本号
     */
    fun getVersionCode(packageName: String?): Int {
        val packageManager: PackageManager = UiHelper.getContext().getPackageManager()
        try {
            val packageInfo = packageManager.getPackageInfo(packageName!!, 0)
            return packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 1
    }

    // 获取CPU最大频率（单位KHZ）
    // "/system/bin/cat" 命令行
    // "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
    fun getMaxCpuFreq(): String? {
        var result = ""
        val cmd: ProcessBuilder
        try {
            val args = arrayOf(
                "/system/bin/cat",
                "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"
            )
            cmd = ProcessBuilder(*args)
            val process = cmd.start()
            val `in` = process.inputStream
            val re = ByteArray(24)
            while (`in`.read(re) != -1) {
                result = result + String(re)
            }
            `in`.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
            result = "N/A"
        }
        return result.trim { it <= ' ' }
    }

    // 获取CPU最小频率（单位KHZ）
    fun getMinCpuFreq(): String? {
        var result = ""
        val cmd: ProcessBuilder
        try {
            val args = arrayOf(
                "/system/bin/cat",
                "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"
            )
            cmd = ProcessBuilder(*args)
            val process = cmd.start()
            val `in` = process.inputStream
            val re = ByteArray(24)
            while (`in`.read(re) != -1) {
                result = result + String(re)
            }
            `in`.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
            result = "N/A"
        }
        return result.trim { it <= ' ' }
    }

    // 实时获取CPU当前频率（单位KHZ）
    fun getCurCpuFreq(): String? {
        var result = "N/A"
        try {
            val fr = FileReader(
                "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
            )
            val br = BufferedReader(fr)
            val text = br.readLine()
            result = text.trim { it <= ' ' }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    // 获取CPU名字
    fun getCpuName(): String? {
        try {
            val fr = FileReader("/proc/cpuinfo")
            val br = BufferedReader(fr)
            val text = br.readLine()
            val array = text.split(":\\s+".toRegex(), 2).toTypedArray()
            for (i in array.indices) {
            }
            return array[1]
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * @return 获得SD卡总大小 不包括系统占的大小
     */
    fun getSDTotalSize(): Long {
        val path = Environment.getExternalStorageDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val totalBlocks = stat.blockCount.toLong()
//        return Formatter.formatFileSize(UiHelper.getContext(), blockSize * totalBlocks)
        return blockSize * totalBlocks
    }

    /**
     * @return 获得sd卡剩余容量，即可用大小
     */
    fun getSDAvailableSize(): Long {
        val path = Environment.getExternalStorageDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val availableBlocks = stat.availableBlocks.toLong()
//        return Formatter.formatFileSize(UiHelper.getContext(), blockSize * availableBlocks)
        return blockSize * availableBlocks
    }
}