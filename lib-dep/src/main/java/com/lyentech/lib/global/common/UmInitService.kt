package com.lyentech.lib.global.common

import com.umeng.commonsdk.UMConfigure

object UmInitService {

    //preInit()不会立刻上报设备信息，用在隐私授权后必须再init()
    fun preInit(){
        UMConfigure.preInit(CoreApplicationProvider.appContext,
            "690078468560e34872d7c98d","Umeng")
    }

    fun init(isDebug: Boolean = false) {
        UMConfigure.init(
            CoreApplicationProvider.appContext,
           "690078468560e34872d7c98d" , "Umeng", UMConfigure.DEVICE_TYPE_PHONE, ""
        )
        UMConfigure.setLogEnabled(isDebug)
    }
}