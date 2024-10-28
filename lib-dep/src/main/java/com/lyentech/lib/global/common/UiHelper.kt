package com.lyentech.lib.global.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.hjq.window.EasyWindow
import com.lyentech.lib.R
import com.lyentech.lib.base.uiTask
import com.lyentech.lib.utils.ActivityStackUtils
import com.lyentech.lib.utils.ScreenUtils
import com.lyentech.lib.utils.printD
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.set

@SuppressLint("StaticFieldLeak")
object UiHelper {

    //防止一些通知类view拼命弹
    var lastShowMiss = hashMapOf<String, Long>()

    @JvmStatic
    fun switch2Aty(activity: Activity, cls: Class<*>?, isClearAll: Boolean = false) {
        try {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(activity, cls)
                if (isClearAll)
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                activity.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun switch2Aty(activity: Context, cls: Class<*>?, isClearAll: Boolean = false) {
        try {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(activity, cls)
                if (isClearAll)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                activity.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun checkLastShow(type: String): Boolean {
        if (lastShowMiss[type] == null || lastShowMiss[type] == 0L) {
            lastShowMiss[type] = System.currentTimeMillis()
            return true
        }
        if (System.currentTimeMillis() - lastShowMiss[type]!! > 3000) {
            lastShowMiss[type] = System.currentTimeMillis()
            return true
        }
        return false
    }

    @JvmStatic
    fun toast(text: String) {
        if (checkLastShow("toast")) {
            try {
                ActivityStackUtils.getCurAty().uiTask {
                    toastMsg(ActivityStackUtils.getCurAty(), text)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun toastMsg(activity: FragmentActivity, text: String) {
        val toast = EasyWindow<EasyWindow<*>>(activity)
            .setContentView(R.layout.toast_msg)
            .setDuration(1000)
            .setGravity(Gravity.CENTER)
            .setYOffset(ScreenUtils.dp2px(getContext(), 115.0f))
        toast.setText(R.id.tvMsg, text)
        toast.show()
    }

    @JvmStatic
    fun toast(resId: Int) {
        if (checkLastShow("toast")) {
            try {
                ActivityStackUtils.getCurAty().uiTask {
                    toastMsg(
                        ActivityStackUtils.getCurAty(),
                        ActivityStackUtils.getCurAty().getString(resId)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun toastWarn(text: String) {
        try {
            ActivityStackUtils.getCurAty().uiTask {
                toastWarning(ActivityStackUtils.getCurAty(), text)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toastWarning(activity: FragmentActivity, text: String) {
        val toast = EasyWindow<EasyWindow<*>>(activity)
            .setContentView(R.layout.toast_warn)
            .setDuration(1000)
            .setGravity(Gravity.CENTER)
//            .setYOffset(ScreenUtils.dp2px(activity, 115.0f))
        toast.setText(R.id.tvMsg, text)
        toast.show()
    }

    @JvmStatic
    fun toastWarn(resId: Int) {
        try {
            ActivityStackUtils.getCurAty().uiTask {
                toastWarn(ActivityStackUtils.getCurAty().getString(resId))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getContext(): Context {
        return CoreApplicationProvider.appContext
    }

    @JvmStatic
    fun getPoint2Value(value: Double): String {
        var bd = BigDecimal(value)
        bd = bd.setScale(2, RoundingMode.HALF_UP)
        return bd.toString()
    }

    @JvmStatic
    fun getCurrentDate(pattern: String = "yyyyMMdd"): String {
        val format = SimpleDateFormat(pattern, Locale.CHINA)
        return format.format(Date())
    }

    @JvmStatic
    fun getVersionName(): String {
        val packageManager: PackageManager = getContext().packageManager
        try {
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(getContext().packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "1.0.0"
    }

    @JvmStatic
    fun getVersionCode(): Long {
        val packageManager: PackageManager = getContext().packageManager
        try {
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(getContext().packageName, 0)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                return packageInfo.versionCode.toLong()
            } else {
                return packageInfo.longVersionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 1
    }

    @JvmStatic
    fun getPackageName(): String {
        val packageManager = getContext().packageManager
        val packageInfo = packageManager.getPackageInfo(getContext().packageName, 0)
        return packageInfo.packageName
    }

    @JvmStatic
    fun reformatVersion(version: String): Long {
        var v: Long = 0
        try {
            v = version.toLong()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return v
    }

    /** @return 跳转应用设置界面*/
    @JvmStatic
    fun switch2SettingInfo() {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS")
            intent.setData(Uri.fromParts("package", getPackageName(), null))
        } else {
            intent.setAction(Intent.ACTION_VIEW)
            intent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails")
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName())
        }
        getContext().startActivity(intent)
    }

    @JvmStatic
    fun getResource(): Resources {
        return getContext().resources
    }

    @JvmStatic
    fun getString(resId: Int): String {
        return getResource().getString(resId)
    }

    //保留一位小数
    @JvmStatic
    fun getFloatStr(data: Float): String {
        return String.format("%.2f", data)
    }

    private val APP_NAME_CACHE = mutableMapOf<String, String>()
    fun getAppName(pm: PackageManager): String {
        val pInfo = pm.getPackageInfo(getContext().packageName, 0)
        if (APP_NAME_CACHE.containsKey(pInfo.packageName)) {
            return APP_NAME_CACHE.get(pInfo.packageName) + ""
        }
        val label = pInfo.applicationInfo.loadLabel(pm).toString()
        APP_NAME_CACHE[pInfo.packageName] = label
        return label
    }

    /**
     * get set
     * 给view添加一个上次触发时间的属性（用来屏蔽连击操作）
     */
    @JvmStatic
    private var <T : View>T.triggerLastTime: Long
        get() = if (getTag(R.id.triggerLastTimeKey) != null) getTag(R.id.triggerLastTimeKey) as Long else 0
        set(value) {
            setTag(R.id.triggerLastTimeKey, value)
        }

    /**
     * get set
     * 给view添加一个延迟的属性（用来屏蔽连击操作）
     */
    @JvmStatic
    private var <T : View> T.triggerDelay: Long
        get() = if (getTag(R.id.triggerDelayKey) != null) getTag(R.id.triggerDelayKey) as Long else -1
        set(value) {
            setTag(R.id.triggerDelayKey, value)
        }

    /**
     * 判断时间是否满足再次点击的要求（控制点击）
     */
    private fun <T : View> T.clickEnable(): Boolean {
        var clickable = false
        val currentClickTime = System.currentTimeMillis()
        if (currentClickTime - triggerLastTime >= triggerDelay) {
            clickable = true
        }
        triggerLastTime = currentClickTime
        return clickable
    }

    /***
     * 带延迟过滤点击事件的 View 扩展
     * @param delay Long 延迟时间，默认500毫秒
     * @param block: (T) -> Unit 函数
     * @return Unit
     */
    fun <T : View> T.clickFilter(delay: Long = 500, block: (T) -> Unit) {
        triggerDelay = delay
        setOnClickListener {
            if (clickEnable()) {
                block(this)
            }
        }
    }

    /**
     * @return 得到string.xml中的字符串，带点位符
     */
    @JvmStatic
    fun getString(id: Int, vararg formatArgs: Any?): String? {
        return getResource().getString(id, *formatArgs)
    }

    /** @return 得到颜色值 */
    @JvmStatic
    fun getColor(colorId: Int): Int {
        return ContextCompat.getColor(getContext(), colorId)
    }

    @JvmStatic
    fun hideKeyboard(context: Context, etInput: EditText?) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(etInput?.windowToken, 0)
        }
    }


    /**  应用白名单 @return <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />*/
    @JvmStatic
    fun switch2WhiteList() {
        val intent = Intent()
        try {
            printD("当前设备型号为:${Build.MANUFACTURER}")
            var componentName: ComponentName? = null
            val brand = Build.BRAND
            when (brand.toLowerCase()) {
                "samsung" -> {
                    componentName = ComponentName(
                        "com.samsung.android.sm",
                        "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity"
                    )
                }

                "huawei", "honor" -> {
                    componentName = ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                        //com.huawei.systemmanager.optimize.bootstart.BootStartActivity
                    )
                }

                "xiaomi" -> {
                    componentName = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                }

                "vivo" -> {
                    componentName = ComponentName(
                        "com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                    )
                }

                "oppo" -> {
                    componentName = ComponentName(
                        "com.coloros.oppoguardelf",
                        "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity"
                    )
                }

                "360" -> {
                    componentName = ComponentName(
                        "com.yulong.android.coolsafe",
                        "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity"
                    )
                }

                "meizu" -> {
                    componentName =
                        ComponentName("com.meizu.safe", "com.meizu.safe.permission.SmartBGActivity")
                }

                "oneplus" -> {
                    componentName = ComponentName(
                        "com.oneplus.security",
                        "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
                    )
                }

                "ulong" -> {    //360未测
                    componentName = ComponentName(
                        "com.yulong.android.coolsafe",
                        ".ui.activity.autorun.AutoRunListActivity"
                    )
                }
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (null != componentName) {
                intent.component = componentName
            } else {
                intent.action = Settings.ACTION_SETTINGS
            }
        } catch (e: Exception) {
            intent.action = Settings.ACTION_SETTINGS
        } finally {
            getContext().startActivity(intent)
        }
    }


    @JvmStatic
    fun initSDPermission(activity: FragmentActivity, doneRun: Runnable? = null) {
        val requestList = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestList.add(Manifest.permission.READ_MEDIA_IMAGES)
            requestList.add(Manifest.permission.READ_MEDIA_AUDIO)
            requestList.add(Manifest.permission.READ_MEDIA_VIDEO)
            requestList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            requestList.add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { //android 11 全局权限
                requestList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE) //弹窗的特殊权限
            }
            requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        requestList.add(Manifest.permission.CAMERA)
        PermissionX.init(activity).permissions(requestList)
            .onExplainRequestReason { scope, deniedList ->
                val msg = "同意以下权限使用："
                scope.showRequestReasonDialog(deniedList, msg, "同意", "取消")
            }.request { allGranted, grantedList, deniedList ->
//                printList(grantedList,"granted")
//                printList(deniedList,"denied")
                if (allGranted) {
                    doneRun?.run()
                } else {
                    toast(text = "未授权！")
                }
            }
    }

    /** @return 开启通知管理*/
    @JvmStatic
    fun checkNotifyPermission(context: Context) {
        val isOpen = NotificationManagerCompat.from(context).areNotificationsEnabled()
        if (!isOpen) {
            AlertDialog.Builder(context)
                .setNegativeButton("取消") { _, _ -> "" }
                .setPositiveButton("去开启") { _, _ ->
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName()) //8.0
                            intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo.uid)
                            context.startActivity(intent)
                        }

                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            intent.putExtra("app_package", getPackageName()) //7.0
                            intent.putExtra("app_uid", context.applicationInfo.uid)
                            context.startActivity(intent)
                        }

                        Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT -> {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            intent.data = Uri.parse("package:${getPackageName()}")
                            context.startActivity(intent)
                        }
                    }
                }
                .setCancelable(true)
                .setMessage("开启通知服务").show()
        }
    }

    @JvmStatic
    fun showTip(c: Context, txt: String, run: Runnable? = null, cancelRun: Runnable? = null) {
        AlertDialog.Builder(c)
            .setMessage(txt + "")
            .setNegativeButton(
                c.getString(R.string.dep_tip_cancel)
            ) { dialog, which ->
                cancelRun?.run()
                dialog?.dismiss()
            }.setPositiveButton(
                c.getString(R.string.dep_tip_done)
            ) { dialog, which ->
                run?.run()
                dialog?.dismiss()
            }.show()
    }


    @JvmStatic
    fun subFormatUrl(url: String): String {
        var arr = url.split("?")
        return if (arr.size > 1) {
            arr[0].substring((arr[0].lastIndexOf('/') + 1))
        } else {
            url.substring(url.lastIndexOf('/') + 1)
        }
    }
}