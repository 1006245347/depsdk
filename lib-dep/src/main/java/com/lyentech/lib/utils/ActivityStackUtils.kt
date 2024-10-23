package com.lyentech.lib.utils

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.system.exitProcess

/**
 * @author jason-何伟杰，2020-01-05
 * des:应用中所有Activity的管理器，可用于一键杀死所有Activity。
 */
object ActivityStackUtils {
    private var activityStack :Stack<Activity>?= Stack<Activity>()

    @JvmStatic
     fun getActivityStack(): Stack<Activity>? {
        return activityStack
    }

    @JvmStatic
    fun add(weakRefActivity: Activity?) {
        weakRefActivity?.let {
            getActivityStack()?.push(it)
        }
    }

    @JvmStatic
    fun del(weakRefActivity: Activity?) {
        weakRefActivity?.let {
            //应该是先把Stack的内容清掉，再finish；假如先finish,那么存在Stack的Aty还能正常finish?
            getActivityStack()?.remove(it)
            it.finish() //这个需要吗,还有执行顺序有无泄漏
        }
    }

    @JvmStatic
    fun getCurAty(): FragmentActivity { //有崩溃的情况
        if (activityStack == null) {
            activityStack =Stack()
        }
        return getActivityStack()?.lastElement() as FragmentActivity
    }

    //关闭其他Activity
    @JvmStatic
    fun <T> finishOther(cls: Class<T>) {
        for (aty in getActivityStack()!!) {
            if (aty::class.java != cls) {
                del(aty)
                break
            }
        }
    }

    @JvmStatic
    fun <T> findAty(cls: Class<T>): Activity? {
        for (aty in getActivityStack()!!) {
            if (aty::class.java == cls) {
                return aty
            }
        }
        return null
    }

    @JvmStatic
    fun <T> finishActivity(weakRefActivity: Activity) {
        if (getActivityStack()!!.size > 0 && getActivityStack()?.search(weakRefActivity) != -1) {
            del(weakRefActivity)
        }
    }

    @JvmStatic
    fun clearAllActivity() {
        if (getActivityStack()?.isNotEmpty()!!) {
            while (!activityStack!!.empty()) {
                activityStack!!.pop()?.finish()
            }
        }
    }

    @JvmStatic
    fun exitApp() {
        clearAllActivity()
        exitProcess(0)
    }
}