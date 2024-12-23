package com.lyentech.lib.global.common

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import androidx.multidex.MultiDex
import com.lyentech.lib.global.http.NetworkApi
import com.lyentech.lib.global.listener.ActivityLifecycleCallbacksImpl
import com.lyentech.lib.utils.MMKVUtils
import com.lyentech.lib.utils.printD
import com.lyentech.lib.widget.loading.GLoading
import com.lyentech.lib.widget.loading.LoadProgressAdapter
import java.io.File

/**
 * @author by jason-何伟杰，2022/12/8
 * des:共享Application
 */
open class CoreApplicationProvider : Application() {
    companion object {
        // 全局共享的 Application
        lateinit var appContext: Application

        /****私有目录，卸载被删除*****/
        @JvmStatic // /data/user/0/com.lyentech.vehicle/cache
        fun getAppCacheDir(): File {
            return appContext.cacheDir
        }

        @JvmStatic
        fun getImageCacheDir(): File? {
            return appContext.getExternalFilesDir("_img")
        }

        @JvmStatic
        fun getDownLoadDir(): File? {
            return appContext.getExternalFilesDir("_downLoad")
        }

        @JvmStatic
        fun getMainCacheDir(): File? {
            return appContext.getExternalFilesDir("_dep")
        }

        /****公有目录，要权限****/
        @JvmStatic
        fun getGlobalDir(): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {//android10开始分区存储
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + "gree_vehicle" + File.separator
            } else {
                Environment.getExternalStorageDirectory().absolutePath + File.separator + "gree_vehicle" + File.separator
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (isAppMainProcess(this)) {
            appContext = this
            registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksImpl {})
            initApp()//确保只在主进程初始化
        }
        printD("Application执行次数》》 $this")
    }

    /*模块的初始化保证在主进程中*/
    private fun isAppMainProcess(context: Context): Boolean {
        if (TextUtils.equals(GlobalCode.getCurProcessName(context), packageName)) {
            return true
        }
        return false
    }

    /*适合基础库的初始化，会回调到所有模块*/
    open fun initApp() {
        GLoading.initDefault(LoadProgressAdapter())
        MMKVUtils.setSavePath(getMainCacheDir()?.absolutePath, "dep", mode = MODE_PRIVATE)
        NetworkApi(appContext).registerCallback()

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this) //dex分包
    }

    override fun onTerminate() {
        super.onTerminate()
        NetworkApi.instance?.unregisterCallback()
    }
}