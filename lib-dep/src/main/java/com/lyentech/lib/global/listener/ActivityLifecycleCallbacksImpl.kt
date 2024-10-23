package com.lyentech.lib.global.listener

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.lyentech.lib.utils.ActivityStackUtils

/**
 * @author by jason-何伟杰，2022/9/30
 * des:监听所有Activity的生命周期变化,包括sdk三方库的Activity
 */
interface ActivityLifecycleCallbacksImpl : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
//        printD("onActivityCreated--$activity")
        ActivityStackUtils.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
//        printI("onActivityStarted--$activity")
    }

    override fun onActivityResumed(activity: Activity) {
//        printD("onActivityResumed--$activity")
    }

    override fun onActivityPaused(activity: Activity) {
//        printI("onActivityPaused--$activity")
    }

    override fun onActivityStopped(activity: Activity) {
//        printI("onActivityStopped--$activity")
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
//        printD("onActivityDestroyed--$activity")
        ActivityStackUtils.del(activity)
    }
}